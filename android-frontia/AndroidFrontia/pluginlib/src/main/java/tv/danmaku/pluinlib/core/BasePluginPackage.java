package tv.danmaku.pluinlib.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import dalvik.system.DexClassLoader;

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

	public BasePluginPackage(PackageInfo packageInfo){
		this.packageInfo = packageInfo;
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

}
