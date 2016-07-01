/*
 * Copyright (c) 2015-2016 BiliBili Inc.
 */

package tv.danmaku.frontia.core.update;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.os.WorkSource;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import tv.danmaku.frontia.core.PluginConstants;
import tv.danmaku.frontia.core.install.PluginInstaller;
import tv.danmaku.frontia.core.PluginManager;
import tv.danmaku.frontia.core.BasePluginRequest;
import tv.danmaku.frontia.core.error.CancelPluginException;
import tv.danmaku.frontia.core.error.UpdatePluginException;
import tv.danmaku.frontia.util.ApkUtil;
import tv.danmaku.frontia.util.PluginFileUtil;
import tv.danmaku.frontia.util.PluginLogUtil;

/**
 * 插件更新器，具体的插件更新逻辑由此类的继承类完成；
 * 之所以不叫“插件下载器”，是因为插件可以有许多获取的路径，不一定从服务器下载；
 * 所以把获取目标插件的行为抽象成“更新插件”；
 * Created by Kaede on 2016/4/22.
 */
public class PluginUpdater {
    public static final String TAG = "PluginUpdater";
    public static final int RESPONSE_SUCCESS = 0;
    public static final int RESPONSE_ILLEGAL_ONLINE_PLUGIN = -1;
    private static volatile PluginUpdater instance;

    public static PluginUpdater getInstance(Context context) {
        if (instance == null) {
            instance = new PluginUpdater(context);
        }
        return instance;
    }

    protected Context application;
    protected PluginManager pluginManager;

    protected PluginUpdater(Context context) {
        application = context.getApplicationContext();
    }

    public PluginUpdater attach(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
        return this;
    }

    /**
     * "请求插件信息"
     * 1. 需要实现获取远程插件信息的逻辑getRemotePluginInfo；
     * 2. 需要实现获取本地插件信息的逻辑getLocalPluginInfo；
     * 3. doUpdatePolicy根据远程和本地插件的信息，计算出最优的更新策略；
     * 4. 需要实现获取不到远程插件信息时本地的更新策略doIllegalRemotePluginInfoPolicy；
     *
     * @param pluginRequest 更新状态
     * @return 更新状态
     */
    public BasePluginRequest requestPlugin(@NonNull BasePluginRequest pluginRequest) {
        PluginLogUtil.d(TAG, "[requestPlugin]");
        pluginRequest.marker("request");
        if (pluginRequest.needClearLocalPlugin)
            pluginManager.getInstaller().deletePlugin(pluginRequest.pluginId);
        pluginRequest.getLocalPluginInfo(application, pluginRequest);
        if (pluginRequest.localPluginInfoList.size()>0){
            LocalPluginInfo localPluginInfo = pluginRequest.localPluginInfoList.get(0);
            // 保存本地可用的插件
            pluginRequest.targetRemotePluginPath = pluginRequest.targetLocalPluginPath = PluginInstaller.getInstance(application).getPluginInstallPath(localPluginInfo.pluginId, String.valueOf(localPluginInfo.version));
        }
        try {
            // 1 请求插件信息
            pluginRequest.getRemotePluginInfo(application, pluginRequest);
            // 2 请求成功
            if (TextUtils.isEmpty(pluginRequest.pluginId)) {
                // 2.1 插件ID为空，非法信息
                doUpdatePolicy(RESPONSE_ILLEGAL_ONLINE_PLUGIN, pluginRequest);
                return pluginRequest;
            }
            if (!pluginRequest.isAssetsPlugin && (pluginRequest.remotePluginInfoList == null || pluginRequest.remotePluginInfoList.size() == 0)) {
                // 2.2 插件列表为空，非法信息
                doUpdatePolicy(RESPONSE_ILLEGAL_ONLINE_PLUGIN, pluginRequest);
                return pluginRequest;
            }
            // 2.3 请求成功，响应结果可信
            doUpdatePolicy(RESPONSE_SUCCESS, pluginRequest);
        } catch (UpdatePluginException e) {
            e.printStackTrace();
            // 3 请求失败，无法获取插件信息
            PluginLogUtil.w(TAG, "[requestPlugin]require plugin info fail");
            pluginRequest.switchState(BasePluginRequest.REQUEST_REQUEST_PLUGIN_INFO_FAIL);
            pluginRequest.markException(e);
            pluginRequest.doIllegalRemotePluginPolicy(pluginRequest, e);
        }
        return pluginRequest;
    }

    protected void doUpdatePolicy(int responseCode, @NonNull BasePluginRequest pluginRequest) {
        if (responseCode == RESPONSE_SUCCESS) {
            // 1 查询成功
            if (pluginRequest.isAssetsPlugin) {
                // 1.1 使用Assets里的插件
                PluginLogUtil.v(TAG, "[doUpdatePolicy]onResponse use asset plugin");
                if (PluginManager.getInstance(application).getInstaller().isPluginInstalled(pluginRequest.pluginId, String.valueOf(pluginRequest.assetsPluginVersion))) {
                    // 1.1.1 插件存在而且合法
                    String pathPlugin = PluginManager.getInstance(application).getInstaller().getPluginInstallPath(pluginRequest.pluginId, String.valueOf(pluginRequest.assetsPluginVersion));
                    pluginRequest.switchState(BasePluginRequest.REQUEST_ALREADY_TO_LOAD_PLUGIN);
                    pluginRequest.targetRemotePluginPath = pathPlugin;
                } else {
                    // 1.1.2 加载Assets插件
                    pluginRequest.switchState(BasePluginRequest.REQUEST_NEED_EXTRACT_ASSETS_PLUGIN);
                    PluginLogUtil.v(TAG, "[doUpdatePolicy]extract plugin from assets : " + pluginRequest.assetsPath);
                }
            } else {
                // 1.2 使用网络插件
                PluginLogUtil.v(TAG, "[doUpdatePolicy]onResponse use online plugin");
                // 执行升级策略
                List<? extends RemotePluginInfo> remotePluginInfoList = pluginRequest.remotePluginInfoList;
                // 获取最佳的在线插件信息（版本最新，且最低APP_BUILD要求小于本APP版本）
                RemotePluginInfo targetRemotePluginInfo = null;
                int versionCode = Integer.MAX_VALUE;
                PackageInfo localPackageInfo = ApkUtil.getLocalPackageInfo(application);
                if (!PluginConstants.DEBUG && localPackageInfo != null) {
                    versionCode = localPackageInfo.versionCode;
                }
                PluginLogUtil.v(TAG, "[doUpdatePolicy]local versionCode = " + versionCode);
                for (RemotePluginInfo tencentVideoPluginInfo : remotePluginInfoList) {
                    if (tencentVideoPluginInfo.enable && tencentVideoPluginInfo.minAppBuild <= versionCode) {
                        targetRemotePluginInfo = tencentVideoPluginInfo;
                        break;
                    }
                }
                if (targetRemotePluginInfo == null) {
                    PluginLogUtil.v(TAG, "[doUpdatePolicy]no available plugin, abort");
                    pluginRequest.switchState(BasePluginRequest.REQUEST_NO_AVAILABLE_PLUGIN);
                } else {
                    // 1.2.2 有可用插件
                    LocalPluginInfo localTargetPluginInfo = chooseBestPluginFromLocal(pluginRequest.localPluginInfoList, targetRemotePluginInfo);
                    if (localTargetPluginInfo == null) {
                        // 1.2.2.1 本地没有可用插件
                        PluginLogUtil.v(TAG, "[doUpdatePolicy]download new plugin, version = " + targetRemotePluginInfo.version + " url = " + targetRemotePluginInfo.downloadLink);
                        pluginRequest.switchState(BasePluginRequest.REQUEST_NEED_DOWNLOAD_ONLINE_PLUGIN);
                        pluginRequest.downloadUrl = targetRemotePluginInfo.downloadLink;
                        pluginRequest.fileSize = targetRemotePluginInfo.fileSize;
                        pluginRequest.isForceUpdate = targetRemotePluginInfo.isForceUpdate;
                    } else {
                        // 1.2.2.2 本地有可用插件，并且为最新版本
                        // 使用本地插件
                        PluginLogUtil.v(TAG, "[doUpdatePolicy]use local plugin, version = " + localTargetPluginInfo.version);
                        String pathPlugin = PluginManager.getInstance(application).getInstaller().getPluginInstallPath(localTargetPluginInfo.pluginId, String.valueOf(localTargetPluginInfo.version));
                        pluginRequest.switchState(BasePluginRequest.REQUEST_ALREADY_TO_LOAD_PLUGIN);
                        pluginRequest.targetRemotePluginPath = pathPlugin;
                    }
                }
            }
        } else if (responseCode == RESPONSE_ILLEGAL_ONLINE_PLUGIN) {
            // 2 查询失败，插件信息非法
            PluginLogUtil.v(TAG, "[doUpdatePolicy]require plugin info fail, illegal online plugin");
            pluginRequest.switchState(BasePluginRequest.REQUEST_NO_AVAILABLE_PLUGIN);
            pluginRequest.doIllegalRemotePluginPolicy(pluginRequest, null);
        }
    }

    protected LocalPluginInfo chooseBestPluginFromLocal(List<LocalPluginInfo> localPluginInfoList, @NonNull RemotePluginInfo targetRemotePluginInfo) {
        for (int i = 0; i < localPluginInfoList.size(); i++) {
            LocalPluginInfo item = localPluginInfoList.get(i);
            // 本地插件为远程最新版本，且远程没有禁用
            if (item.version == targetRemotePluginInfo.version) {
                return item;
            }
        }
        return null;
    }

    /**
     * ”更新插件“
     * 1. 根据更新状态执行插件的更新逻辑；
     * 2. 这里只更新从ASSETS解压插件或者从网络下载插件的逻辑；
     *
     * @param pluginRequest 更新状态
     * @return 更新状态
     */
    public BasePluginRequest updatePlugin(@NonNull BasePluginRequest pluginRequest) {
        PluginLogUtil.d(TAG, "[updatePlugin]");
        pluginRequest.marker("update");
        if (pluginRequest.getState() == BasePluginRequest.REQUEST_NEED_EXTRACT_ASSETS_PLUGIN) {
            // 1 从ASSETS解压插件
            String tempFile = pluginManager.getInstaller().getTempPluginPath();
            int retry = pluginRequest.retry;
            Exception exception = null;
            while (retry > 0) {
                if (pluginRequest.updateHandler.isCanceled()) {
                    pluginRequest.onCancelRequest(application, pluginRequest);
                    return pluginRequest;
                }
                try {
                    PluginFileUtil.copyFileFromAsset(application, pluginRequest.assetsPath, tempFile);
                    pluginRequest.switchState(BasePluginRequest.REQUEST_ALREADY_TO_LOAD_PLUGIN);
                    pluginRequest.targetRemotePluginPath = tempFile;
                    PluginLogUtil.v(TAG, "[updatePlugin]extract plugin from assets success");
                    // 1.1 解压成功
                    break;
                } catch (IOException e) {
                    retry--;
                    PluginLogUtil.v(TAG, "[updatePlugin]extract retry = " +  (pluginRequest.retry - retry));
                    pluginRequest.marker("retry extract " + (pluginRequest.retry - retry));
                    e.printStackTrace();
                    exception = e;
                }
            }
            if (exception != null) {
                // 1.2 解压失败
                PluginLogUtil.v(TAG, "[updatePlugin]extract plugin from assets error");
                pluginRequest.switchState(BasePluginRequest.REQUEST_UPDATE_PLUGIN_FAIL);
                pluginRequest.markException(exception);
                pluginRequest.doUpdateFailPolicy(pluginRequest, new UpdatePluginException("extract plugin from assets fail", exception));
            }
        } else if (pluginRequest.getState() == BasePluginRequest.REQUEST_NEED_DOWNLOAD_ONLINE_PLUGIN) {
            // 2 下载在线插件
            String tempFile = pluginManager.getInstaller().getTempPluginPath();
            Exception exception = null;
            int retry = pluginRequest.retry;
            while (retry > 0) {
                try {
                    downloadPlugin(pluginRequest.downloadUrl, pluginRequest.fileSize, tempFile, pluginRequest.updateHandler);
                    pluginRequest.switchState(BasePluginRequest.REQUEST_ALREADY_TO_LOAD_PLUGIN);
                    pluginRequest.targetRemotePluginPath = tempFile;
                    PluginLogUtil.v(TAG, "[updatePlugin]download plugin online success");
                    // 2.1 下载成功
                    break;
                } catch (UpdatePluginException e) {
                    retry--;
                    PluginLogUtil.v(TAG, "[updatePlugin]extract retry = " + (pluginRequest.retry - retry));
                    pluginRequest.marker("retry download " + (pluginRequest.retry - retry));
                    e.printStackTrace();
                    exception = e;
                } catch (CancelPluginException e) {
                    pluginRequest.onCancelRequest(application, pluginRequest);
                    return pluginRequest;
                }
            }
            if (exception != null) {
                // 2.2 下载失败
                PluginLogUtil.v(TAG, "[updatePlugin]download plugin online fail");
                pluginRequest.switchState(BasePluginRequest.REQUEST_UPDATE_PLUGIN_FAIL);
                pluginRequest.markException(exception);
                pluginRequest.doUpdateFailPolicy(pluginRequest, new UpdatePluginException("download plugin online fail", exception));
            }
        }
        return pluginRequest;
    }

    protected void downloadPlugin(String downloadLink, long fileSize, String destPath, PluginUpdateHandler updateHandler) throws UpdatePluginException, CancelPluginException {
        // 电源锁
        PowerManager powerManager = (PowerManager) application.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.setWorkSource(new WorkSource());
        wakeLock.acquire();
        // WIFI锁
        WifiManager manager = (WifiManager) application.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiManager.WifiLock wifiLock = manager.createWifiLock("WIFI LOCK : " + TAG);
        wifiLock.acquire();
        // 下载实现
        HttpURLConnection connection = null;
        InputStream in = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        int downloadedSize = 0;
        try {
            URL url = new URL(downloadLink);
            PluginLogUtil.v(TAG, "[downloadPlugin]source file url = " + downloadLink);
            connection = (HttpURLConnection) url.openConnection();
            // 如果直接中CONNECTION读取文件的大小，只能使用POST；
            // 使用GET的话，得实现在服务器配置好ileSize；
            connection.setRequestMethod("GET");
            PluginLogUtil.v(TAG, "[downloadPlugin]method = " + connection.getRequestMethod());
            connection.setConnectTimeout(5 * 1000);
            connection.setReadTimeout(30 * 1000);
            // connection.setDoInput(true);
            // connection.setDoOutput(true);
            in = connection.getInputStream();
            bis = new BufferedInputStream(in);
            File file = new File(destPath);
            PluginLogUtil.v(TAG, "[downloadPlugin]dest file = " + file.getPath());
            file.getParentFile().mkdirs();
            file.createNewFile();
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            byte[] buffer = new byte[8 * 1024]; // 1MB Buffer
            int length;
            while ((length = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
                bos.flush();
                downloadedSize += length;
                // check cancel
                if (updateHandler.isCanceled()) {
                    throw new CancelPluginException("download is canceled");
                }
                // notify progress
                if (fileSize > 0){
                    // 取两位小数点后面两位
                    float progress = new BigDecimal((float)downloadedSize / fileSize).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    if (progress < 0f) progress = 0f;
                    if (progress > 1f) progress = 1f;
                    PluginLogUtil.v(TAG, "[downloadPlugin]notify progress  = " + progress);
                    updateHandler.notifyProgress(progress);
                }
            }
            PluginLogUtil.v(TAG, "[downloadPlugin]original filesize = " + fileSize + ", downloadedsize = " + downloadedSize);
            // 检验文件下载大小，删除不一致的文件
            /*if (fileSize != downloadedSize) {
                new File(destPath).delete();
                throw new UpdatePluginException("illegal downloaded file");
            }*/
        } catch (IOException e) {
            e.printStackTrace();
            throw new UpdatePluginException("download file error", e);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    PluginLogUtil.w(TAG, "[downloadPlugin]close fos error!");
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    PluginLogUtil.w(TAG, "[downloadPlugin]close bis error!");
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                PluginLogUtil.v(TAG, "[downloadPlugin]method = " + connection.getRequestMethod());
                connection.disconnect();
            }
            wakeLock.release();
            wifiLock.release();
        }
    }
}
