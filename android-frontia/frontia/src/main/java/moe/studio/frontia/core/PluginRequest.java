/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import moe.studio.frontia.ext.PluginError;
import moe.studio.frontia.BuildConfig;
import moe.studio.frontia.ext.PluginListener;
import moe.studio.frontia.update.LocalPluginInfo;
import moe.studio.frontia.update.RemotePluginInfo;

import static org.apache.commons.io.FileUtils.deleteQuietly;
import static moe.studio.frontia.core.PluginRequest.State.CANCELED;
import static moe.studio.frontia.core.PluginRequest.State.UPD_NO_PLUGIN;
import static moe.studio.frontia.core.PluginRequest.State.UPD_SUCCESS;
import static moe.studio.frontia.core.PluginRequest.State.WTF;

/**
 * 插件任务请求实体类。
 */
@SuppressWarnings("WeakerAccess")
public abstract class PluginRequest<P extends Plugin> {

    private static final String TAG = "plugin.request";
    private static final int NO_VALUE = -2233;

    protected String mId;
    protected int mRetry;
    protected int mState;
    protected StringBuffer mLog;
    protected String mRemotePluginPath;
    protected String mLocalPluginPath;
    protected boolean mIsClearLocalPlugins;

    protected P mPlugin;
    protected PluginListener mListener;
    protected PluginManager mManager;
    protected List<Exception> mExceptions;

    // FOR ASSETS PLUGIN.
    protected int mAssetsVersion;
    protected String mAssetsPath;
    protected boolean mIsFromAssets;

    protected long mFileSize;
    // FOR ONLINE PLUGIN.
    protected String mDownloadUrl;
    protected boolean mIsForceUpdate;

    // PLUGIN INFO LIST.
    protected List<LocalPluginInfo> mLocalPlugins;
    protected List<? extends RemotePluginInfo> mRemotePlugins;

    private final byte[] mLock;

    public PluginRequest() {
        mState = WTF;
        mRetry = NO_VALUE;
        mLock = new byte[0];
        mLog = new StringBuffer(String.valueOf(mState));
    }

    public PluginRequest attach(PluginManager manager) {
        mManager = manager;
        return this;
    }

    public PluginManager getManager() {
        return mManager;
    }

    /**
     * 获取当前状态, 参考{@link State}
     */
    public int getState() {
        synchronized (mLock) {
            return mState;
        }
    }

    /**
     * 获取插件任务的状态转换记录, 参考{@link State}
     */
    public String getLog() {
        return mLog.toString();
    }

    /**
     * 切换状态, 参考{@link State}
     */
    public PluginRequest switchState(int state) {
        synchronized (mLock) {
            mState = state;
        }
        return marker(String.valueOf(state));
    }

    public void cancel() {
        synchronized (mLock) {
            switchState(CANCELED);
        }
    }

    public boolean isCanceled() {
        return mState == CANCELED;
    }

    /**
     * 记录Log
     */
    public PluginRequest marker(String log) {
        if (!TextUtils.isEmpty(log)) {
            mLog.append(" --> ").append(log);
        }
        return this;
    }

    /**
     * 获取异常列表
     */
    @Nullable
    public List<Exception> getExceptions() {
        return mExceptions;
    }

    /**
     * 记录异常
     */
    public PluginRequest markException(@NonNull Exception e) {
        if (mExceptions == null) {
            mExceptions = new ArrayList<>();
        }
        mExceptions.add(e);
        return marker(e.getLocalizedMessage());
    }

    /**
     * 重试
     *
     * @throws PluginError.RetryError 重试超标
     */
    public void retry() throws PluginError.RetryError {
        if (--mRetry < 0) {
            throw new PluginError.RetryError();
        }
    }

    /**
     * 设置重试次数
     */
    public void setRetry(int count) {
        if (count > 0) {
            mRetry = count;
        }
    }

    /**
     * 获取插件ID
     */
    @Nullable
    public String getId() {
        return  mId == null? requestPluginId() : mId;
    }

    /**
     * 设置插件ID
     */
    public void setId(String id) {
        mId = id;
    }

    /**
     * 是否清除本地已安装插件
     */
    public boolean isClearLocalPlugins() {
        return mIsClearLocalPlugins;
    }

    /**
     * 设置是否清除本地已安装插件
     */
    public void setClearLocalPlugins(boolean isClear) {
        mIsClearLocalPlugins = isClear;
    }

    /**
     * 获取插件路径
     */
    @Nullable
    public String getPluginPath() {
        if (!TextUtils.isEmpty(mRemotePluginPath)) {
            return mRemotePluginPath;
        }

        return mLocalPluginPath;
    }

    /**
     * 设置插件路径
     */
    public void setPluginPath(String path) {
        mRemotePluginPath = path;
    }

    /**
     * 获取插件本地路径(安装路径)
     */
    @Nullable
    public String getLocalPluginPath() {
        return mLocalPluginPath;
    }

    /**
     * 设置插件本地路径(安装路径)
     */
    public void setLocalPluginPath(String path) {
        mLocalPluginPath = path;
    }

    /**
     * 获取插件
     */
    @Nullable
    public P getPlugin() {
        return mPlugin;
    }

    /**
     * 设置插件
     */
    public void setPlugin(P plugin) {
        mPlugin = plugin;
    }

    /**
     * 获取插件请求任务监听器
     */
    @Nullable
    public PluginListener getListener() {
        return mListener;
    }

    /**
     * 设置插件请求任务监听器
     */
    public void setListener(PluginListener listener) {
        mListener = listener;
    }

    /**
     * 是否是内置插件(存放在Assets)
     */
    public boolean isFromAssets() {
        return false;
    }

    /**
     * 获取内置路径
     */
    public String getAssetsPath() {
        return null;
    }

    /**
     * 获取内置插件版本
     */
    public int getAssetsVersion() {
        return -1;
    }

    /**
     * 设置内置插件信息
     *
     * @param path    Assets路径
     * @param version 版本
     */
    public void fromAssets(String path, int version) {
        mIsFromAssets = true;
        mAssetsPath = path;
        mAssetsVersion = version;
    }

    /**
     * 获取插件下载URL
     */
    @Nullable
    public String getDownloadUrl() {
        return mDownloadUrl;
    }

    /**
     * 设置插件下载URL
     */
    public void setDownloadUrl(String url) {
        this.mDownloadUrl = url;
    }

    /**
     * 获取插件大小(弃用, 插件下载器能自动获取在线插件文件大小)
     */
    @Deprecated
    public long getFileSize() {
        return mFileSize;
    }

    /**
     * 设置插件大小(弃用, 插件下载器能自动获取在线插件文件大小)
     */
    @Deprecated
    public void setFileSize(long fileSize) {
        mFileSize = fileSize;
    }

    /**
     * 是否强制使用最新插件
     */
    public boolean forceUpdate() {
        return mIsForceUpdate;
    }

    /**
     * 设置是否强制使用最新插件
     * <p>
     * 在更新插件失败的情况下, 如果该项为true, 则不会使用本地以安装(如果有)的旧版本插件,
     * 结果就是导致插件加载失败。
     */
    public void setForUpdate(boolean force) {
        mIsForceUpdate = force;
    }

    /**
     * 当前插件本地已安装的所有版本信息
     */
    @Nullable
    public List<LocalPluginInfo> getLocalPlugins() {
        return mLocalPlugins;
    }

    /**
     * 设置当前插件本地已安装的所有版本信息
     */
    public void setLocalPlugins(List<LocalPluginInfo> infos) {
        mLocalPlugins = infos;
    }

    /**
     * 当前插件在线的所有版本信息
     */
    @Nullable
    public List<? extends RemotePluginInfo> getRemotePlugins() {
        return mRemotePlugins;
    }

    /**
     * 设置当前插件在线的所有版本信息
     */
    public void setRemotePlugins(List<? extends RemotePluginInfo> infos) {
        this.mRemotePlugins = infos;
    }

    /**
     * 获取当前插件在线的所有版本信息
     * (必须由用户继承并实现该接口, 因为我压根不知道你的在线插件长什么样。)
     *
     * @param context Context
     * @return 远程插件信息列表，如果获取失败，返回NULL。
     */
    public abstract List<? extends RemotePluginInfo> requestRemotePluginInfo(Context context) throws Exception;

    public abstract String requestPluginId();

    public abstract boolean requestClearLocalPlugins(Context context);

    /**
     * 获取当前插件本地已安装的所有版本信息
     */
    public void getLocalPluginInfo(@NonNull PluginRequest request) {
        String pluginId = getId();
        if (!TextUtils.isEmpty(pluginId)) {
            request.setLocalPlugins(getLocalPluginInfoById(pluginId));
        }
    }

    /**
     * 获取当前插件本地已安装的所有版本信息
     *
     * @param id 插件ID
     * @return 已安装的所有版本列表
     */
    protected List<LocalPluginInfo> getLocalPluginInfoById(@NonNull String id) {
        List<LocalPluginInfo> localPluginInfoList = new ArrayList<>();
        String pluginDir = mManager.getInstaller().getPluginPath(id);

        File file = new File(pluginDir);
        if (!file.exists()) {
            Log.d(TAG, "No local plugin, path = " + file.getAbsolutePath());
            return localPluginInfoList;
        }

        String[] versions = file.list();
        for (String version : versions) {
            if (TextUtils.isDigitsOnly(version)) {
                // Version can only be integer.
                if (mManager.getInstaller().isInstalled(id, version)) {
                    // Plugin has been already installed.
                    LocalPluginInfo item = new LocalPluginInfo();
                    item.pluginId = id;
                    item.version = Integer.valueOf(version);
                    item.isValid = true;
                    localPluginInfoList.add(item);
                }
            } else {
                // Delete invalid file.
                delete(new File(pluginDir + File.separator + version));
            }
        }

        Collections.sort(localPluginInfoList);

        // Dump existing plugin versions.
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "-");
            Log.v(TAG, "Found local plugin \"" + id + "\" :");
            for (LocalPluginInfo item : localPluginInfoList) {
                Log.v(TAG, "Version =  " + item.version + ", path = "
                        + mManager.getInstaller().getInstallPath(id,
                        String.valueOf(item.version)));
            }
            Log.v(TAG, "-");
        }
        return localPluginInfoList;
    }

    /**
     * 当无法获取远程插件信息时候执行的回调, 默认使用本地最优插件(如果有)。
     */
    @SuppressWarnings("UnusedParameters")
    public void onGetRemotePluginFail(@NonNull PluginRequest request, PluginError.UpdateError e) {
        request.setId(getId());
        useLocalAvailablePlugin(request);
    }

    /**
     * 当插件更新失败时候执行的回调, 默认使用本地最优插件(如果有)。
     * 如果远程插件信息里配置了强制升级, 则丢弃本地插件, 插件加载失败。
     */
    @SuppressWarnings("UnusedParameters")
    public void doUpdateFailPolicy(@NonNull PluginRequest request, PluginError.UpdateError e) {
        if (!request.forceUpdate()) {
            // Use local installed plugin if the current plugin version is not fore-update.
            useLocalAvailablePlugin(request);
        } else {
            // No available plugin.
            request.switchState(UPD_NO_PLUGIN);
        }
    }

    /**
     * 使用版本可用的插件
     */
    protected void useLocalAvailablePlugin(@NonNull PluginRequest request) {
        String path = request.getLocalPluginPath();
        if (!TextUtils.isEmpty(path)) {
            request.setPluginPath(path);
            request.switchState(UPD_SUCCESS);
        }
    }

    /**
     * 加载插件时, 使用的插件实体类型。
     * (必须由用户继承并实现该接口, 只有你才知道你需要什么用的插件不是吗。)
     */
    public abstract Plugin createPlugin(String path);

    private static boolean delete(File file) {
        return deleteQuietly(file);
    }

    /**
     * 状态码
     */
    public static class State {

        public static final int WTF = -1;                           // 初始状态
        public static final int CANCELED = -7;                      // 任务被取消

        public static final int UPD_REMOTE_INFO_FAIL = -2;          // 无法获取远程插件信息
        public static final int UPD_NO_PLUGIN = -3;                 // 没有插件可用(在线和本地均没有)
        public static final int UPD_UPDATE_PLUGIN_FAIL = -4;        // 插件更新失败
        public static final int UPD_NEED_EXTRACT = 2;               // 准备从ASSETS释放插件
        public static final int UPD_NEED_DOWNLOAD = 3;              // 准备下载插件
        public static final int UPD_SUCCESS = 1;                    // 插件更新完成、准备加载

        public static final int LOA_SUCCESS = 0;                    // 插件已经加载成功
        public static final int LOA_PLUGIN_FAIL = -5;               // 插件加载失败
    }
}
