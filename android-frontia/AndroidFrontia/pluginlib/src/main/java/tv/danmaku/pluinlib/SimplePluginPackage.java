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
		this.classLoader = createDexClassLoader(context, packagePath, internalSoLibDir);
		this.assetManager = createAssetManager(packagePath);
		this.resources = createResources(context, this.assetManager);
		return this;
	}


}
