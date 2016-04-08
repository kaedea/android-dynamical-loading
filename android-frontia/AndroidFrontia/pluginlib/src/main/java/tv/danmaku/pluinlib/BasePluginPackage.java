package tv.danmaku.pluinlib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import dalvik.system.DexClassLoader;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Copyright (c) 2015 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 */
public abstract class BasePluginPackage {

	public String packageName;
	public String defaultActivity;
	public DexClassLoader classLoader;
	public AssetManager assetManager;
	public Resources resources;
	public boolean hasSoLib;
	public String internalSoLibDir;
	public boolean hasLoadedSoLib;
	public String md5;

	public PackageInfo packageInfo;

	public BasePluginPackage(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public String toString() {
		return "BasePluginPackage{" +
				"packageName='" + packageName + '\'' +
				'}';
	}

	public abstract BasePluginPackage loadPlugin(Context context, String packagePath);

	public boolean checkPluginValid() {
		return false;
	}

	public DexClassLoader createDexClassLoader(Context context, String dexPath, String internalSoLibDir) {
		File dexOutputDir = context.getDir("dex", Context.MODE_PRIVATE);
		String dexOutputPath = dexOutputDir.getAbsolutePath();
		return new DexClassLoader(dexPath, dexOutputPath, internalSoLibDir, context.getClassLoader());
	}

	public AssetManager createAssetManager(String dexPath) {
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

	public Resources createResources(Context context, AssetManager assetManager) {
		Resources superRes = context.getResources();
		return new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
	}
}
