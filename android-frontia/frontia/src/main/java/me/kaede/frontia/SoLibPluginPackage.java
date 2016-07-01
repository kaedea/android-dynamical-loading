package me.kaede.frontia;

import android.content.Context;
import android.text.TextUtils;
import me.kaede.frontia.core.BasePluginPackage;
import me.kaede.frontia.core.PluginConstants;
import me.kaede.frontia.core.error.LoadPluginException;
import me.kaede.frontia.util.ApkUtil;
import me.kaede.frontia.util.PluginFileUtil;
import me.kaede.frontia.util.PluginLogUtil;
import me.kaede.frontia.util.SoLibUtil;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 * <p/>
 * 带有SO库的APK插件，用于满足带有SO库的SDK插件化
 */
public abstract class SoLibPluginPackage extends BasePluginPackage {
    public static final String TAG = "SoLibPluginPackage";

    public SoLibPluginPackage(String pluginPath) {
        super(pluginPath);
    }

    @Override
    public BasePluginPackage loadPlugin(Context context, String packagePath) throws LoadPluginException {
        PluginLogUtil.d(TAG, "[loadPlugin]destApkPath = " + packagePath);
        // 插件是否存在
        if (TextUtils.isEmpty(packagePath) || !(new File(packagePath).exists())) {
            throw new LoadPluginException("can not get plugin file");
        }
        // 获取PackageInfo
        if (this.packageInfo == null) {
            this.packageInfo = ApkUtil.getPackageInfo(context, packagePath);
        }
        if (this.packageInfo == null) {
            throw new LoadPluginException("can not get plugin info");
        }
        PluginLogUtil.d(TAG, "[loadPlugin]install plugin so libs");
        File destApk = new File(packagePath);
        File apkParent = destApk.getParentFile();
        try {
            installSoLib(apkParent, packagePath);
        } catch (IOException e) {
            throw new LoadPluginException("install so libs error", e);
        }
        // 删除DEX优化的缓存文件
        File fileDexCache = new File(apkParent, PluginConstants.DIR_DALVIK_CACHE);
        if (fileDexCache.exists()) {
            PluginFileUtil.deleteAll(fileDexCache);
        }
        // 创建插件的ClassLoader
        PluginLogUtil.d(TAG, "[loadPlugin]create classloader");
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

    protected void installSoLib(File apkParent, String packagePath) throws IOException {
        // 解压SO库，并根据当前CPU的类型选择正确的SO库
        // TODO: 有必要每次都重新解压一边SO库吗，目前看起来这样做是比较便捷的
        PluginLogUtil.d(TAG, "[installSoLib]unzip so libs");
        File tempSoDir = new File(apkParent, PluginConstants.DIR_TEMP_SO);
        Set<String> soList = SoLibUtil.extractSoLib(packagePath, tempSoDir);
        if (soList != null) {
            for (String soName : soList) {
                SoLibUtil.copySoLib(tempSoDir, soName, apkParent.getAbsolutePath(), PluginConstants.DIR_NATIVE_LIB);
            }
            // 删掉临时文件
            PluginFileUtil.deleteAll(tempSoDir);
        }
    }

}
