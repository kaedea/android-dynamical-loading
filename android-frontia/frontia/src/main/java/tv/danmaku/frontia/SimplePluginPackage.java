package tv.danmaku.frontia;

import android.content.Context;
import tv.danmaku.frontia.core.BasePluginPackage;
import tv.danmaku.frontia.core.PluginConstants;
import tv.danmaku.frontia.util.ApkUtil;
import tv.danmaku.frontia.util.PluginLogUtil;

import java.io.File;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 * <p/>
 * 简单的APK插件，用于一般的SDK插件化
 */
public abstract class SimplePluginPackage extends BasePluginPackage {
    public static final String TAG = "SimplePluginPackage";

    public SimplePluginPackage(String pluginPath) {
        super(pluginPath);
    }

    @Override
    public BasePluginPackage loadPlugin(Context context, String packagePath) {
        // 创建插件的ClassLoader
        PluginLogUtil.d(TAG, "[loadPlugin]创建ClassLoader");
        String apkParentDir = new File(packagePath).getParent();
        File optDir = new File(apkParentDir, PluginConstants.DIR_DALVIK_CACHE);
        optDir.mkdirs();
        File libDir = new File(apkParentDir, PluginConstants.DIR_NATIVE_LIB);
        libDir.mkdirs();
        internalSoLibDir = libDir.getAbsolutePath();
        this.classLoader = ApkUtil.createDexClassLoader(context, packagePath, optDir.getAbsolutePath(), internalSoLibDir, false);
        this.assetManager = ApkUtil.createAssetManager(packagePath);
        this.resources = ApkUtil.createResources(context, this.assetManager);
        this.isLoaded = true;
        return this;
    }
}
