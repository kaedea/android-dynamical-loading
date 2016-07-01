/*
 * Copyright (c) 2015-2016 BiliBili Inc.
 */

package me.kaede.frontia.core.load;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import me.kaede.frontia.bridge.plugin.BaseBehaviour;
import me.kaede.frontia.core.BasePluginPackage;
import me.kaede.frontia.core.BasePluginRequest;
import me.kaede.frontia.core.PluginManager;
import me.kaede.frontia.core.error.IllegalPluginException;
import me.kaede.frontia.core.error.LoadPluginException;
import me.kaede.frontia.util.ApkUtil;
import me.kaede.frontia.util.PluginFileUtil;
import me.kaede.frontia.util.PluginLogUtil;

/**
 * 插件加载器
 * Created by Kaede on 16/6/12.
 */
public class PluginLoader {
    public static final String TAG = "PluginLoader";
    private static volatile PluginLoader instance;

    public static PluginLoader getInstance(Context context) {
        if (instance == null) {
            instance = new PluginLoader(context);
        }
        return instance;
    }

    private Context mContext;
    private PluginManager mPluginManager;
    private Map<String, BasePluginPackage> mPackageHolder;

    protected PluginLoader(Context context) {
        mContext = context.getApplicationContext();
        mPackageHolder = new HashMap<>();
    }

    public PluginLoader attach(PluginManager pluginManager) {
        mPluginManager = pluginManager;
        return this;
    }

    /**
     * "加载插件"
     * @param pluginRequest 更新状态
     * @return 更新状态
     */
    public BasePluginRequest loadPlugin(@NonNull final BasePluginRequest pluginRequest) {
        PluginLogUtil.d(TAG, "[loadPlugin]");
        pluginRequest.marker("load");
        pluginRequest.preLoadPlugin(mContext, pluginRequest);
        if (pluginRequest.getState() == BasePluginRequest.REQUEST_ALREADY_TO_LOAD_PLUGIN) {
            String path = pluginRequest.getTargetPluginPath();
            if (path != null) {
                // 1 加载插件，需要判断是否取消;
                BasePluginPackage pluginPackage = pluginRequest.createPluginPackage(path);
                Exception exception = null;
                int retry = pluginRequest.retry;
                while (retry > 0) {
                    if (pluginRequest.updateHandler.isCanceled()) {
                        pluginRequest.onCancelRequest(mContext, pluginRequest);
                        return pluginRequest;
                    }
                    try {
                        pluginPackage = mPluginManager.getLoader().loadPlugin(pluginPackage);
                        break;
                    } catch (LoadPluginException e) {
                        retry--;
                        exception = e;
                        pluginRequest.marker("retry load " + (pluginRequest.retry - retry));
                        e.printStackTrace();
                    }
                }
                if (exception != null) {
                    // 1.1 加载插件失败
                    pluginRequest.switchState(BasePluginRequest.REQUEST_LOAD_PLUGIN_FAIL);
                    pluginRequest.markException(exception);
                } else {
                    // 1.2 加载插件成功回调，用于执行高层业务的插件初始化工作，需要判断是否取消;
                    pluginRequest.pluginPackage = pluginPackage;
                    pluginRequest.switchState(BasePluginRequest.REQUEST_LOAD_PLUGIN_SUCCESS);
                }
                if (pluginRequest.updateHandler.isCanceled()) {
                    pluginRequest.onCancelRequest(mContext, pluginRequest);
                } else {
                    pluginRequest.postLoadPlugin(mContext, pluginRequest);
                    if (pluginRequest.onFinishedListener != null) {
                        mPluginManager.getMainHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                pluginRequest.onFinishedListener.onFinished(mContext, pluginRequest);
                            }
                        });
                    }
                }
            } else {
                pluginRequest.switchState(BasePluginRequest.REQUEST_WTF);
            }
        }
        return pluginRequest;
    }

    public BasePluginPackage loadPlugin(BasePluginPackage basePluginPackage) throws LoadPluginException {
        PluginLogUtil.d(TAG, "[loadPlugin]");
        String pluginPath = basePluginPackage.pluginPath;
        if (TextUtils.isEmpty(pluginPath) || !(new File(pluginPath).exists())) {
            PluginLogUtil.w(TAG, "[loadPlugin]pluginPath not exist");
            throw new LoadPluginException("plugin file not exist");
        }
        // 1. 判断插件是否安装
        if (mPluginManager.getInstaller().isPluginInstalled(pluginPath)) {
            // 1.1 插件已经安装
            PluginLogUtil.d(TAG, "[loadPlugin]plugin installed");
            String installPath = mPluginManager.getInstaller().getPluginInstallPath(pluginPath);
            PackageInfo packageInfo = ApkUtil.getPackageInfo(mContext, installPath);
            if (packageInfo == null) {
                PluginLogUtil.w(TAG, "[loadPlugin][plugin installed]packageInfo = null");
                throw new LoadPluginException("can not get plugin info");
            }
            basePluginPackage.packageInfo = packageInfo;
            basePluginPackage.pluginPathInternal = installPath;
            // 1.2 检查是否已经加载到缓存，有则直接使用缓存
            PluginLogUtil.v(TAG, "[loadPlugin][plugin installed]get PluginPackage from holder = " + packageInfo.packageName);
            BasePluginPackage pluginPackage = getPluginPackage(packageInfo.packageName);
            if (pluginPackage != null) {
                PluginLogUtil.v(TAG, "[loadPlugin][plugin installed]hit");
                return pluginPackage;
            }
            PluginLogUtil.v(TAG, "[loadPlugin][plugin installed]no hit");
            // 1.3 加载安装路径上的插件
            PluginLogUtil.v(TAG, "[loadPlugin][plugin installed]load plugin from installed path");
            basePluginPackage = basePluginPackage.loadPlugin(mContext, basePluginPackage.pluginPathInternal);
            putPluginPackage(packageInfo.packageName, basePluginPackage);
            return basePluginPackage;
        }
        // 2.1 插件未安装
        PluginLogUtil.d(TAG, "[loadPlugin]plugin not installed");
        // 2.2 复制到内部临时路径
        if (!pluginPath.startsWith(mContext.getCacheDir().getAbsolutePath())) {
            String tempFilePath = mPluginManager.getInstaller().getTempPluginPath();
            PluginLogUtil.v(TAG, "[loadPlugin][plugin not installed]copy to internal cache dir = " + tempFilePath);
            try {
                PluginFileUtil.copyFile(pluginPath, tempFilePath);
                basePluginPackage.pluginPath = tempFilePath;
                pluginPath = tempFilePath;
            } catch (IOException e) {
                e.printStackTrace();
                new File(tempFilePath).delete();
                PluginLogUtil.w(TAG, "[loadPlugin][plugin not installed]copy to internal cache dir fail, " + pluginPath + " to " + tempFilePath);
                throw new LoadPluginException("copy plugin to temp dir fail");
            }
        }
        // 2.3 获取插件包信息
        PluginLogUtil.v(TAG, "[loadPlugin][plugin not installed]get PackageInfo");
        PackageInfo packageInfo = ApkUtil.getPackageInfo(mContext, pluginPath);
        if (packageInfo == null) {
            new File(pluginPath).delete();
            PluginLogUtil.w(TAG, "[loadPlugin][plugin not installed] packageInfo = null");
            throw new LoadPluginException("can not get plugin info");
        }
        basePluginPackage.packageInfo = packageInfo;
        // 2.4  签名校验
        PluginLogUtil.v(TAG, "[loadPlugin][plugin not installed]check valid");
        if (!mPluginManager.getInstaller().checkPluginValid(pluginPath)) {
            PluginLogUtil.w(TAG, "[loadPlugin][plugin not installed]check valid fail");
            new File(pluginPath).delete();
            throw new LoadPluginException("check plugin valid fail");
        }
        // 2.5 检查是否已经加载到缓存，有则直接使用缓存
        PluginLogUtil.v(TAG, "[loadPlugin][plugin not installed]get PluginPackage from holder : " + packageInfo.packageName);
        BasePluginPackage pluginPackage = getPluginPackage(packageInfo.packageName);
        if (pluginPackage != null) {
            PluginLogUtil.v(TAG, "[plugin not installed]hit");
            return pluginPackage;
        }
        PluginLogUtil.v(TAG, "[plugin not installed]no hit");
        // 2.6 加载指定路径上的插件（区别于加载安装路径上的插件）
        try {
            PluginLogUtil.v(TAG, "[plugin not installed]load plugin");
            basePluginPackage = basePluginPackage.loadPlugin(mContext);
            putPluginPackage(packageInfo.packageName, basePluginPackage);
        } finally {
            new File(pluginPath).delete();
        }
        return basePluginPackage;
    }

    public synchronized BasePluginPackage getPluginPackage(String packageName) {
        BasePluginPackage basePluginPackage = mPackageHolder.get(packageName);
        if (basePluginPackage != null && !basePluginPackage.isLoaded) {
            return null;
        }
        return basePluginPackage;
    }

    public synchronized void putPluginPackage(String packageName, BasePluginPackage basePluginPackage) {
        if (basePluginPackage != null && basePluginPackage.isLoaded) {
            mPackageHolder.put(packageName, basePluginPackage);
        }
    }

    public Class loadPluginClass(BasePluginPackage basePluginPackage, String className) throws IllegalPluginException {
        return basePluginPackage.loadPluginClass(className);
    }

    public BaseBehaviour getPluginBehaviour(BasePluginPackage basePluginPackage) throws IllegalPluginException {
        return basePluginPackage.getPluginBehaviour();
    }

}
