package me.kaede.frontia.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import dalvik.system.DexClassLoader;

import java.lang.reflect.Method;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 */
public class ApkUtil {
    public static DexClassLoader createDexClassLoader(Context context, String dexPath, String optimizedDir, String nativeLibDir, boolean isDependent) {
        ClassLoader parentClassLoader;
        if (isDependent) {
            parentClassLoader = ClassLoader.getSystemClassLoader().getParent();
        } else {
            parentClassLoader = context.getClassLoader();
        }
        return new DexClassLoader(dexPath, optimizedDir, nativeLibDir, parentClassLoader);
    }

    public static AssetManager createAssetManager(String dexPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, dexPath);
            return assetManager;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Resources createResources(Context context, AssetManager assetManager) {
        Resources superRes = context.getResources();
        return new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
    }

    public static Class<?> loadPluginClass(ClassLoader classLoader, String className) throws ClassNotFoundException {
        Class<?> clazz;
        try {
            clazz = Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }

        return clazz;
    }

    public static PackageInfo getLocalPackageInfo(Context context) {
        return getLocalPackageInfo(context, 0);
    }

    public static PackageInfo getLocalPackageInfo(Context context,  int flag) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageInfo(context.getPackageName(), flag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pkgInfo;
    }

    public static PackageInfo getPackageInfo(Context context, String apkFilepath) {
        return getPackageInfo(context, apkFilepath, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
    }

    public static PackageInfo getPackageInfo(Context context, String apkFilepath, int flag) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageArchiveInfo(apkFilepath, flag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pkgInfo;
    }
}
