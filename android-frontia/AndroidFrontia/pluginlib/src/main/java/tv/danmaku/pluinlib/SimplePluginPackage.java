package tv.danmaku.pluinlib;

import android.content.Context;
import tv.danmaku.pluinlib.bridge.plugin.BaseBehaviour;
import tv.danmaku.pluinlib.core.BasePluginPackage;
import tv.danmaku.pluinlib.util.ApkUtil;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 *
 * 简单的APK插件，用于一般的SDK插件化
 */
public abstract class SimplePluginPackage extends BasePluginPackage {

	public SimplePluginPackage(String pluginPath) {
		super(pluginPath);
	}

	@Override
	public BasePluginPackage loadPlugin(Context context, String packagePath) {
		this.classLoader = ApkUtil.createDexClassLoader(context, packagePath, internalSoLibDir);
		this.assetManager = ApkUtil.createAssetManager(packagePath);
		this.resources = ApkUtil.createResources(context, this.assetManager);
		return this;
	}

	@Override
	public abstract BaseBehaviour getPluginBehaviour(Object... args);
}
