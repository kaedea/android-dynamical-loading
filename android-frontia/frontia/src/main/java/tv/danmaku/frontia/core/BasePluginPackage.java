package tv.danmaku.frontia.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;
import dalvik.system.DexClassLoader;
import tv.danmaku.frontia.bridge.plugin.BaseBehaviour;
import tv.danmaku.frontia.core.error.IllegalPluginException;
import tv.danmaku.frontia.core.error.LoadPluginException;
import tv.danmaku.frontia.core.install.PluginInstaller;
import tv.danmaku.frontia.util.ApkUtil;
import tv.danmaku.frontia.util.PluginFileUtil;
import tv.danmaku.frontia.util.PluginLogUtil;

import java.io.File;
import java.io.IOException;

/**
 * Copyright (c) 2015 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 */
public abstract class BasePluginPackage {
    public static final String TAG = "BasePluginPackage";

    public String pluginPath;
    public String pluginPathInternal;
    public DexClassLoader classLoader;
    public AssetManager assetManager;
    public Resources resources;
    public String internalSoLibDir;
    public boolean isLoaded;
    public PackageInfo packageInfo;

    public BasePluginPackage(String pluginPath) {
        this.pluginPath = pluginPath;
        this.pluginPathInternal = pluginPath;
        this.isLoaded = false;
    }

    @Override
    public String toString() {
        if (packageInfo != null) {
            return packageInfo.toString();
        }
        return "BasePluginPackage{" +
                "pluginPath='" + pluginPath + '\'' +
                ", pluginPathInternal='" + pluginPathInternal + '\'' +
                '}';
    }

    /**
     * 安装并加载插件，具体实现根据需求的不同由不同的继承类完成
     *
     * @param context Context实例
     * @return 完成加载工作的实体类
     */
    public BasePluginPackage loadPlugin(Context context) throws LoadPluginException {
        return loadPlugin(context, installPlugin(context, pluginPath));
    }

    /**
     * 安装指定路径上的插件，其实就是把插件复制到特定的内部目录
     *
     * @param context Context实例
     * @return bool
     */
    protected String installPlugin(Context context, String packagePath) throws LoadPluginException {
        // 插件是否存在
        if (TextUtils.isEmpty(packagePath) || !(new File(packagePath).exists())) {
            throw new LoadPluginException("plugin file not exist");
        }
        // 获取PackageInfo
        if (this.packageInfo == null) {
            this.packageInfo = ApkUtil.getPackageInfo(context, packagePath);
        }
        if (this.packageInfo == null) {
            throw new LoadPluginException("can not get plugin info");
        }
        // 获取插件在DATA目录的安装路径（包名+版本+base-1.apk）
        pluginPathInternal = PluginInstaller.getInstance(context).getPluginInstallPath(packageInfo.packageName, String.valueOf(packageInfo.versionCode));
        PluginLogUtil.v(TAG, "[installPlugin]pluginPathInternal = " + pluginPathInternal);
        // 安装插件（拷贝插件到安装路径）
        File destApk = new File(pluginPathInternal);
        // TODO: 16/6/15 remove plugin installer here.
        if (destApk.exists() && PluginInstaller.getInstance(context).checkPluginValid(destApk.getAbsolutePath())) {
            // 插件已经安装，并且合法
            PluginLogUtil.d(TAG, "[installPlugin]目标插件已存在，检验插件合法性");
        } else {
            PluginLogUtil.d(TAG, "[installPlugin]安装目标插件（拷贝插件到安装路径）");
            try {
                PluginFileUtil.copyFile(packagePath, pluginPathInternal);
            } catch (IOException e) {
                e.printStackTrace();
                throw new LoadPluginException("copy plugin to install path fail", e);
            }
        }
        return pluginPathInternal;
    }

    /**
     * 加载指定路径上的插件，具体实现根据需求的不同由不同的继承类完成
     *
     * @param context     Context实例
     * @param packagePath 插件路径
     * @return 完成加载工作的实体类
     */
    public abstract BasePluginPackage loadPlugin(Context context, String packagePath) throws LoadPluginException;

    /**
     * 获取插件的行为接口，用于控制插件
     *
     * @param args 初始化插件行为接口需要用到的参数（这里用到了不定数参数，是否能优化？）
     * @return 行为接口
     */
    public abstract BaseBehaviour getPluginBehaviour(Object... args) throws IllegalPluginException;

    /**
     * 加载插件的一个类
     *
     * @param className 类名
     * @return class
     */
    public Class loadPluginClass(String className) throws IllegalPluginException {
        try {
            return ApkUtil.loadPluginClass(classLoader, className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalPluginException("load plugin class fail", e);
        }
    }

}
