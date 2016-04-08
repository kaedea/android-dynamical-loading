/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import moe.studio.frontia.ext.PluginError;
import moe.studio.downloader.DownloadRequest;
import moe.studio.downloader.SyncDownloadProcessorImpl;
import moe.studio.downloader.core.DownloadListener;
import moe.studio.downloader.core.DownloadProcessor;
import moe.studio.downloader.core.RetryPolicy.RetryPolicyImpl;
import moe.studio.frontia.Internals.ApkUtils;
import moe.studio.frontia.Internals.FileUtils;
import moe.studio.frontia.core.PluginRequest;
import moe.studio.frontia.core.PluginUpdater;
import moe.studio.frontia.update.LocalPluginInfo;
import moe.studio.frontia.update.RemotePluginInfo;

import static moe.studio.frontia.core.PluginRequest.State.CANCELED;
import static moe.studio.frontia.core.PluginRequest.State.UPD_NEED_DOWNLOAD;
import static moe.studio.frontia.core.PluginRequest.State.UPD_NEED_EXTRACT;
import static moe.studio.frontia.core.PluginRequest.State.UPD_NO_PLUGIN;
import static moe.studio.frontia.core.PluginRequest.State.UPD_REMOTE_INFO_FAIL;
import static moe.studio.frontia.core.PluginRequest.State.UPD_SUCCESS;
import static moe.studio.frontia.core.PluginRequest.State.UPD_UPDATE_PLUGIN_FAIL;

/**
 * 插件更新器
 * <p>
 * 之所以不叫“插件下载器”，是因为插件可以有许多获取的路径，不一定从服务器下载
 * 所以把获取目标插件的行为抽象成“更新插件”
 */
class PluginUpdaterImpl implements PluginUpdater {

    private static final String TAG = "plugin.update";

    private static final int RESPONSE_SUCCESS = 0;
    private static final int RESPONSE_ILLEGAL_ONLINE_PLUGIN = -1;

    private final Context mContext;

    PluginUpdaterImpl(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * ”更新插件“
     * 1. 根据更新状态执行插件的更新逻辑；
     * 2. 这里只更新从ASSETS解压插件或者从网络下载插件的逻辑；
     *
     * @param request 更新状态
     * @return 更新状态
     */
    @Override
    public PluginRequest updatePlugin(@NonNull PluginRequest request) {
        Logger.i(TAG, "Start update, id = " + request.getId());
        request.marker("Update");
        onPreUpdate(request);

        // Request remote plugin.
        requestPlugin(request);

        if (request.isCanceled()) {
            onCanceled(request);
            return request;
        }

        if (request.getState() == UPD_NEED_EXTRACT) {
            // Check capacity.
            try {
                request.getManager().getInstaller().checkCapacity();
            } catch (IOException e) {
                Logger.w(TAG, e);
                PluginError.UpdateError error = new PluginError.UpdateError(e, PluginError.ERROR_UPD_CAPACITY);
                onError(request, error);
                return request;
            }

            // Extract plugin from assets.
            File tempFile;
            try {
                tempFile = request.getManager().getInstaller().createTempFile(request.getId());
            } catch (IOException e) {
                Logger.v(TAG, "Can not get temp file, error = " + e.getLocalizedMessage());
                Logger.w(TAG, e);
                PluginError.UpdateError error = new PluginError.UpdateError(e, PluginError.ERROR_UPD_NO_TEMP);
                onError(request, error);
                return request;
            }

            int retry = 0;
            request.setRetry(request.getManager().getSetting().getRetryCount());

            while (true) {
                if (request.isCanceled()) {
                    onCanceled(request);
                    return request;
                }

                try {
                    FileUtils.copyFileFromAsset(mContext, request.getAssetsPath(), tempFile);
                    Logger.v(TAG, "Extract plugin from assets success.");
                    request.setPluginPath(tempFile.getAbsolutePath());
                    request.switchState(UPD_SUCCESS);
                    onPostUpdate(request);
                    return request;

                } catch (IOException e) {
                    Logger.w(TAG, e);
                    try {
                        request.retry();
                        Logger.v(TAG, "Extract fail, retry " + (retry++));
                        request.marker("Retry extract " + retry);
                    } catch (PluginError.RetryError retryError) {
                        Logger.v(TAG, "Extract plugin from assets fail, error = "
                                + e.toString());
                        PluginError.UpdateError error = new PluginError.UpdateError(e, PluginError.ERROR_UPD_EXTRACT);
                        onError(request, error);
                        return request;
                    }
                }
            }

        } else if (request.getState() == UPD_NEED_DOWNLOAD) {
            // Check capacity.
            try {
                request.getManager().getInstaller().checkCapacity();
            } catch (IOException e) {
                Logger.w(TAG, e);
                PluginError.UpdateError error = new PluginError.UpdateError(e, PluginError.ERROR_UPD_CAPACITY);
                onError(request, error);
                return request;
            }

            // Download plugin from online.
            File tempFile;
            try {
                tempFile = request.getManager().getInstaller().createTempFile(request.getId());
            } catch (IOException e) {
                Logger.v(TAG, "Can not get temp file, error = " + e.getLocalizedMessage());
                Logger.w(TAG, e);
                PluginError.UpdateError error = new PluginError.UpdateError(e, PluginError.ERROR_UPD_NO_TEMP);
                onError(request, error);
                return request;
            }

            try {
                downloadPlugin(request, tempFile);
                Logger.v(TAG, "Download plugin online success.");
                request.setPluginPath(tempFile.getAbsolutePath());
                request.switchState(UPD_SUCCESS);
                onPostUpdate(request);
                return request;

            } catch (PluginError.UpdateError error) {
                Logger.v(TAG, "Download plugin fail, error = " + error.getLocalizedMessage());
                Logger.w(TAG, error);
                request.markException(error);
                onError(request, error);
                return request;
            } catch (PluginError.CancelError e) {
                onCanceled(request);
                return request;
            }

        } else {
            onPostUpdate(request);
            return request;
        }
    }

    private void onPreUpdate(PluginRequest request) {
        Logger.i(TAG, "onPreUpdate state = " + request.getState());
        request.getManager().getCallback().preUpdate(request);
    }

    private void onCanceled(PluginRequest request) {
        Logger.i(TAG, "onCanceled state = " + request.getState());
        request.switchState(CANCELED);
        request.getManager().getCallback().onCancel(request);
    }

    private void onError(PluginRequest request, PluginError.UpdateError error) {
        Logger.i(TAG, "onError state = " + request.getState());
        request.switchState(UPD_UPDATE_PLUGIN_FAIL);
        request.markException(error);
        request.doUpdateFailPolicy(request, error);
        onPostUpdate(request);
    }

    private void onPostUpdate(PluginRequest request) {
        Logger.i(TAG, "onPostUpdate state = " + request.getState());
        request.getManager().getCallback().postUpdate(request);
    }

    /**
     * 请求插件信息:
     * 1. 需要实现获取远程插件信息的逻辑 getRemotePluginInfo；
     * 2. 需要实现获取本地插件信息的逻辑 getLocalPluginInfo；
     * 3. doUpdatePolicy 根据远程和本地插件的信息，计算出最优的更新策略；
     * 4. 需要实现获取不到远程插件信息时本地的更新策略 onGetRemotePluginFail；
     */
    @SuppressWarnings("unchecked")
    // Package-local for testcase only.
    PluginRequest requestPlugin(PluginRequest request) {
        Logger.d(TAG, "Request remote plugin info.");

        // Check clear existing plugins.
        if (request.isClearLocalPlugins()) {
            request.getManager().getInstaller().deletePlugins(request.requestPluginId());
        }

        // Get local existing plugin info.
        request.getLocalPluginInfo(request);
        List<LocalPluginInfo> localPlugins = request.getLocalPlugins();

        if (localPlugins != null && localPlugins.size() > 0) {
            LocalPluginInfo localPluginInfo = localPlugins.get(0);
            // Getting plugin installed path.
            String installPath = request.getManager().getInstaller().getInstallPath(localPluginInfo.pluginId,
                    String.valueOf(localPluginInfo.version));
            request.setPluginPath(installPath);
            request.setLocalPluginPath(installPath);
        }

        try {
            // Request remote plugin info.
            List<? extends RemotePluginInfo> plugins = request.requestRemotePluginInfo(mContext);
            request.setRemotePlugins(plugins);

            request.setId(request.requestPluginId());
            request.setClearLocalPlugins(request.requestClearLocalPlugins(mContext));

            if (request.isFromAssets()) {
                request.fromAssets(request.getAssetsPath(), request.getAssetsVersion());
            }

            if (TextUtils.isEmpty(request.getId())) {
                doUpdatePolicy(RESPONSE_ILLEGAL_ONLINE_PLUGIN, request);
                return request;
            }

            // Success.
            doUpdatePolicy(RESPONSE_SUCCESS, request);

        } catch (Exception e) {
            Logger.w(TAG, "Request remote plugin info fail, error = " + e.toString());
            Logger.w(TAG, e);
            request.switchState(UPD_REMOTE_INFO_FAIL);
            PluginError.UpdateError error = new PluginError.UpdateError(e, PluginError.ERROR_UPD_REQUEST);
            request.markException(error);
            request.onGetRemotePluginFail(request, error);
        }

        return request;
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    private void doUpdatePolicy(int responseCode, @NonNull PluginRequest request) {
        if (responseCode == RESPONSE_SUCCESS) {
            if (request.isFromAssets()) {
                // Using plugin from assets.
                Logger.v(TAG, "Using plugin from assets");

                String apkPath = request.getManager().getInstaller()
                        .getInstallPath(request.getId(),
                                String.valueOf(request.getAssetsVersion()));

                if (request.getManager().getInstaller().isInstalled(apkPath)) {
                    // The current version of plugin has been installed before.
                    request.switchState(UPD_SUCCESS);
                    request.setPluginPath(apkPath);

                } else {
                    // Should extract plugin form assets.
                    request.switchState(UPD_NEED_EXTRACT);
                    Logger.v(TAG, "Extract plugin from assets, path = " + request.getAssetsPath());
                }

            } else {
                // Using online plugin.
                Logger.v(TAG, "Using online plugin.");

                // Calculate the best remote plugin version.
                // (Latest version & APP_BUILD is meet.)
                List<? extends RemotePluginInfo> remotePluginInfoList = request.getRemotePlugins();
                RemotePluginInfo bestPlugin = null;
                int appBuild = Integer.MAX_VALUE;
                PackageInfo localPackageInfo = ApkUtils.getLocalPackageInfo(mContext);

                if (request.getManager().getSetting().isDebugMode() && localPackageInfo != null) {
                    appBuild = localPackageInfo.versionCode;
                }
                Logger.v(TAG, "App build = " + appBuild);

                // Get the best plugin version.
                if (remotePluginInfoList != null) {
                    for (RemotePluginInfo pluginInfo : remotePluginInfoList) {
                        if (pluginInfo.enable && pluginInfo.minAppBuild <= appBuild) {
                            bestPlugin = pluginInfo;
                            break;
                        }
                    }
                }

                if (bestPlugin == null) {
                    Logger.v(TAG, "No available plugin, abort.");
                    request.switchState(UPD_NO_PLUGIN);

                } else {
                    LocalPluginInfo bestLocalPlugin = chooseBestPluginFromLocal(
                            request.getLocalPlugins(), bestPlugin);
                    if (bestLocalPlugin == null) {
                        // No local best plugin, should download from remote.
                        Logger.v(TAG, "Download new plugin, version = "
                                + bestPlugin.version + ", url = "
                                + bestPlugin.downloadUrl);

                        request.switchState(UPD_NEED_DOWNLOAD);
                        request.setDownloadUrl(bestPlugin.downloadUrl);
                        request.setFileSize(bestPlugin.fileSize);
                        request.setForUpdate(bestPlugin.isForceUpdate);

                    } else {
                        // The best plugin version has been installed before.
                        Logger.v(TAG, "Use local plugin, version = " + bestLocalPlugin.version);
                        String apkPath = request.getManager().getInstaller().getInstallPath(bestLocalPlugin.pluginId,
                                String.valueOf(bestLocalPlugin.version));

                        request.switchState(UPD_SUCCESS);
                        request.setPluginPath(apkPath);
                    }
                }
            }

        } else if (responseCode == RESPONSE_ILLEGAL_ONLINE_PLUGIN) {
            Logger.v(TAG, "Request remote plugin info fail, illegal online plugin.");
            request.switchState(UPD_NO_PLUGIN);
            request.onGetRemotePluginFail(request, null);
        }
    }

    @Nullable
    private LocalPluginInfo chooseBestPluginFromLocal(List<LocalPluginInfo> localPlugins,
                                                      RemotePluginInfo bestPlugin) {
        if (localPlugins == null) {
            return null;
        }

        for (int i = 0; i < localPlugins.size(); i++) {
            LocalPluginInfo item = localPlugins.get(i);
            // Getting the latest version of plugin, which is not disabled.
            if (item.version == bestPlugin.version) {
                return item;
            }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    private void downloadPlugin(final PluginRequest request, File destFile)
            throws PluginError.UpdateError, PluginError.CancelError {

        // Using FileDownloader to complete the download task.
        final long fileSize = request.getFileSize();

        final String[] errorMsg = {null};

        DownloadRequest downloadRequest = new DownloadRequest(request.getDownloadUrl())
                .setContentLength(fileSize)
                .setDestFile(destFile)
                .setDeleteDestFileOnFailure(true)
                .setRetryPolicy(new RetryPolicyImpl(request.getManager().getSetting()
                        .getRetryCount()))
                .setListener(new DownloadListener() {
                    @Override
                    public void onComplete(DownloadRequest request) {
                        Logger.v(TAG, "Download complete, original fileSize = " + fileSize
                                + ", downloadedSize = " + request.getCurrentBytes());
                    }

                    @Override
                    public void onFailed(DownloadRequest request, int errorCode, String errorMessage) {
                        errorMsg[0] = errorMessage;
                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes,
                                           long downloadedBytes, int progress, long bytesPerSecond) {
                        // notify progress
                        if (fileSize > 0) {
                            Logger.v(TAG, "Notify progress  = " + progress);
                            request.getManager().getCallback().notifyProgress(request,
                                    (float) progress / 100F);
                        }
                    }

                    @Override
                    public boolean isCanceled() {
                        return request.isCanceled();
                    }

                });

        // Downloading asynchronously.
        DownloadProcessor processor = new SyncDownloadProcessorImpl();
        processor.attach(mContext);
        processor.add(downloadRequest);

        if (request.isCanceled()) {
            throw new PluginError.CancelError(PluginError.ERROR_UPD_CANCELED);

        } else if (!TextUtils.isEmpty(errorMsg[0])) {
            throw new PluginError.UpdateError(errorMsg[0], PluginError.ERROR_UPD_DOWNLOAD);
        }
    }
}
