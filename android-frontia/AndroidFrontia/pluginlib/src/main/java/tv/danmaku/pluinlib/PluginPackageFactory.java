package tv.danmaku.pluinlib;

import android.content.Context;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 */
public class PluginPackageFactory {

	public static BasePluginPackage createSimplePluginPackage(Context context, String packageName, String pluginPath) {
		BasePluginPackage basePluginPackage = new SimplePluginPackage(packageName);
		basePluginPackage.loadPlugin(context, pluginPath);
		return basePluginPackage;
	}
}
