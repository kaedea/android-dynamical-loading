package tv.danmaku.pluinlib;

import android.content.Context;

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
		this.classLoader = ApkHelper.createDexClassLoader(context, packagePath, internalSoLibDir);
		this.assetManager = ApkHelper.createAssetManager(packagePath);
		this.resources = ApkHelper.createResources(context, this.assetManager);
		return this;
	}


}
