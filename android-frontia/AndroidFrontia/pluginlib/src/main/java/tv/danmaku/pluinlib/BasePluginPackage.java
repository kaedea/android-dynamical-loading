package tv.danmaku.pluinlib;

import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import dalvik.system.DexClassLoader;

/**
 * Copyright (c) 2015 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 */
public class BasePluginPackage {

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

	public BasePluginPackage loadPlugin(String packagePath) {
		return null;
	}

	public boolean checkPluginValid(){
		return false;
	}
}
