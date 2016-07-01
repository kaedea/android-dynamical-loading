/*
 * Copyright (c) 2015-2016 BiliBili Inc.
 */

package me.kaede.frontia.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.kaede.frontia.core.error.UpdatePluginException;
import me.kaede.frontia.core.install.PluginInstaller;
import me.kaede.frontia.core.update.LocalPluginInfo;
import me.kaede.frontia.core.update.PluginUpdateHandler;
import me.kaede.frontia.core.update.RemotePluginInfo;
import me.kaede.frontia.util.PluginLogUtil;

/**
 * 插件任务请求实体类。
 * Created by Kaede on 16/6/3.
 */
public abstract class BasePluginRequest {
    public static final String TAG = "BasePluginRequest";
    public static final int REQUEST_CANCEL = -7;
    public static final int REQUEST_GET_BEHAVIOUR_FAIL = -6;
    public static final int REQUEST_LOAD_PLUGIN_FAIL = -5;
    public static final int REQUEST_UPDATE_PLUGIN_FAIL = -4;
    public static final int REQUEST_NO_AVAILABLE_PLUGIN = -3;
    public static final int REQUEST_REQUEST_PLUGIN_INFO_FAIL = -2;
    public static final int REQUEST_WTF = -1;
    public static final int REQUEST_LOAD_PLUGIN_SUCCESS = 0;
    public static final int REQUEST_ALREADY_TO_LOAD_PLUGIN = 1;
    public static final int REQUEST_NEED_EXTRACT_ASSETS_PLUGIN = 2;
    public static final int REQUEST_NEED_DOWNLOAD_ONLINE_PLUGIN = 3;

    private int mState;
    public StringBuffer stateLog;
    public boolean needClearLocalPlugin;
    public int retry;
    public String pluginId;
    public String targetRemotePluginPath; // 升级策略得到的最佳插件路径；
    public String targetLocalPluginPath; // 升级策略得到的本地可用插件路径；
    public BasePluginPackage pluginPackage;
    public PluginUpdateHandler updateHandler;
    public OnFinishedListener onFinishedListener;
    public List<Exception> exceptions;
    // ASSETS
    public boolean isAssetsPlugin;
    public String assetsPath;
    public int assetsPluginVersion;
    // ONLINE
    public String downloadUrl;
    public long fileSize;
    public boolean isForceUpdate;
    public List<RemotePluginInfo> remotePluginInfoList; // 远程插件列表，不同的业务可能需要不同的插件信息，所以这里使用了泛型；
    public List<LocalPluginInfo> localPluginInfoList; // 本地插件列表；

    public BasePluginRequest() {
        mState = REQUEST_WTF;
        stateLog = new StringBuffer(String.valueOf(mState));
        retry = 3;
        updateHandler = new PluginUpdateHandler();
    }

    public int getState() {
        return mState;
    }

    public BasePluginRequest switchState(int state) {
        this.mState = state;
        return marker(String.valueOf(state));
    }

    public BasePluginRequest marker(@NonNull String mark) {
        if (!TextUtils.isEmpty(mark))
            stateLog.append(" --> ").append(mark);
        return this;
    }

    public BasePluginRequest markException(@NonNull Exception exception) {
        if (exceptions == null) {
            exceptions = new ArrayList<>();
        }
        exceptions.add(exception);
        return marker(exception.getLocalizedMessage());
    }

    public String getStateLog() {
        return stateLog.toString();
    }

    public String getTargetPluginPath() {
        if (!TextUtils.isEmpty(targetRemotePluginPath))
            return targetRemotePluginPath;
        return targetLocalPluginPath;
    }

    public boolean isUpdateFail() {
        return mState == REQUEST_CANCEL
                || mState == REQUEST_REQUEST_PLUGIN_INFO_FAIL
                || mState == REQUEST_UPDATE_PLUGIN_FAIL
                || mState == REQUEST_LOAD_PLUGIN_FAIL
                || mState == REQUEST_GET_BEHAVIOUR_FAIL
                || mState == REQUEST_WTF;
    }

    public abstract void getRemotePluginInfo(Context context, @NonNull BasePluginRequest pluginRequest) throws UpdatePluginException;

    public void getLocalPluginInfo(Context context, @NonNull BasePluginRequest pluginRequest) {
        pluginRequest.localPluginInfoList = getLocalPluginInfoById(context, getLocalPluginId());
    }

    @NonNull
    public String getLocalPluginId(){
        return pluginId;
    }

    public List<LocalPluginInfo> getLocalPluginInfoById(@NonNull Context context, @NonNull String pluginId) {
        List<LocalPluginInfo> localPluginInfoList = new ArrayList<>();
        String pluginDir = PluginInstaller.getInstance(context).getPluginDir(pluginId);
        File file = new File(pluginDir);
        if (!file.exists()) {
            PluginLogUtil.d(TAG,"[getLocalPluginInfoById]no local plugin, filepath = " + file.getAbsolutePath());
            return localPluginInfoList;
        }
        String[] versions = file.list();
        for (String version : versions){
            if (TextUtils.isDigitsOnly(version)){
                // 版本文件夹只能是数字
                if (PluginInstaller.getInstance(context).isPluginInstalled(pluginId, version)){
                    // 插件版本已经安装,且合法
                    LocalPluginInfo item = new LocalPluginInfo();
                    item.pluginId = pluginId;
                    item.version = Integer.valueOf(version);
                    item.isValid = true;
                    localPluginInfoList.add(item);
                }
            } else {
                // 删除版本外文件
                new File(pluginDir + File.separator + version).delete();
            }
        }
        Collections.sort(localPluginInfoList);
        // 打印本地存在的所有插件版本
        if (PluginConstants.DEBUG){
            PluginLogUtil.d(TAG,"---------------- plugin " + pluginId + " installed ----------------");
            for (LocalPluginInfo item : localPluginInfoList){
                PluginLogUtil.v(TAG, "version : " + item.version + ", path : " + PluginInstaller.getInstance(context).getPluginInstallPath(pluginId, String.valueOf(item.version)));
            }
            PluginLogUtil.d(TAG,"---------------- plugin " + pluginId + " installed ----------------");
        }
        return localPluginInfoList;
    }

    protected void useLocalAvailablePlugin(@NonNull BasePluginRequest pluginRequest) {
        // 使用本地最优插件
        if (!TextUtils.isEmpty(pluginRequest.targetLocalPluginPath)) {
            pluginRequest.targetRemotePluginPath = pluginRequest.targetLocalPluginPath;
            pluginRequest.switchState(BasePluginRequest.REQUEST_ALREADY_TO_LOAD_PLUGIN);
        }
    }

    public void doIllegalRemotePluginPolicy(@NonNull BasePluginRequest pluginRequest, UpdatePluginException exception) {
        pluginRequest.pluginId = getLocalPluginId();
        useLocalAvailablePlugin(pluginRequest);
    }

    public void doUpdateFailPolicy(@NonNull BasePluginRequest pluginRequest, UpdatePluginException exception) {
        if (!pluginRequest.isForceUpdate) {
            // 非强制升级，使用本地可用插件
            useLocalAvailablePlugin(pluginRequest);
        } else {
            // 强制升级，无插件可用
            pluginRequest.switchState(BasePluginRequest.REQUEST_NO_AVAILABLE_PLUGIN);
        }
    }

    public void preLoadPlugin(Context context, BasePluginRequest pluginRequest) {

    }

    public abstract BasePluginPackage createPluginPackage(String pluginPath);

    public void postLoadPlugin(Context context, BasePluginRequest pluginRequest) {
    }

    public void onCancelRequest(Context context, BasePluginRequest pluginRequest) {
        pluginRequest.switchState(BasePluginRequest.REQUEST_CANCEL);
    }

    public void setOnFinishedListener(OnFinishedListener onFinishedListener) {
        this.onFinishedListener = onFinishedListener;
    }

    public interface OnFinishedListener {
        void onFinished(Context context, BasePluginRequest pluginRequest);
    }

}
