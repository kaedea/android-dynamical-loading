package tv.danmaku.pluinlib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import dalvik.system.DexClassLoader;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 */
public class ApkHelper {

	public static final String TAG = "ApkHelper";

	public static DexClassLoader createDexClassLoader(Context context, String dexPath, String internalSoLibDir) {
		LogUtil.i(TAG,"createDexClassLoader");
		File dexOutputDir = context.getDir("dex", Context.MODE_PRIVATE);
		String dexOutputPath = dexOutputDir.getAbsolutePath();
		return new DexClassLoader(dexPath, dexOutputPath, internalSoLibDir, context.getClassLoader());
	}

	public static AssetManager createAssetManager(String dexPath) {
		LogUtil.i(TAG,"createAssetManager");
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
		LogUtil.i(TAG,"createResources");
		Resources superRes = context.getResources();
		return new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
	}

	public static Class<?> loadPluginClass(ClassLoader classLoader, String className) {
		LogUtil.i(TAG,"loadPluginClass");
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className, true, classLoader);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return clazz;
	}

	public static PackageInfo getPackageInfo(Context context, String apkFilepath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pkgInfo = null;
		try {
			pkgInfo = pm.getPackageArchiveInfo(apkFilepath, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pkgInfo;
	}
}
