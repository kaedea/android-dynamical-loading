package tv.danmaku.pluinlib;

import android.annotation.SuppressLint;
import android.content.Context;
import tv.danmaku.pluinlib.bridge.plugin.BaseBehaviour;
import tv.danmaku.pluinlib.util.ApkUtil;
import tv.danmaku.pluinlib.core.BasePluginPackage;

import java.io.File;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 */
public class SimplePluginPackage extends BasePluginPackage {

	public SimplePluginPackage(String packageName) {
		super(packageName);
	}

	@Override
	public BasePluginPackage loadPlugin(Context context, String packagePath) {
		/*if (this.packageInfo==null){
			this.packageInfo = ApkHelper.getPackageInfo(context,packagePath);
		}*/

		this.classLoader = ApkUtil.createDexClassLoader(context, packagePath, internalSoLibDir);
		this.assetManager = ApkUtil.createAssetManager(packagePath);
		this.resources = ApkUtil.createResources(context, this.assetManager);
		return this;
	}

	@Override
	public BaseBehaviour getPluginBehaviour(Object... args) {
		return null;
	}

	@SuppressLint("SdCardPath")
	public static File getLibrayAbsDirectory(Context context) {
		String dataDir = context.getApplicationInfo().dataDir;

		File libDir = new File(dataDir, "lib");
		return libDir;
	}


}
