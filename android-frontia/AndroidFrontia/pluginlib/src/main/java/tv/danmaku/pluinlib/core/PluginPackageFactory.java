package tv.danmaku.pluinlib.core;

import android.content.Context;
import tv.danmaku.pluinlib.util.LogUtil;
import tv.danmaku.pluinlib.SimplePluginPackage;
import tv.danmaku.pluinlib.SoLibPluginPackage;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 */
public class PluginPackageFactory {
	public static final String TAG = "PluginPackageFactory";
	public static BasePluginPackage createSimplePluginPackage(Context context, String packageName, String pluginPath) {
		BasePluginPackage basePluginPackage = new SimplePluginPackage(packageName);
		basePluginPackage.loadPlugin(context, pluginPath);
		return basePluginPackage;
	}

	public static BasePluginPackage createSoLibPluginPackage(Context context, String packageName, String pluginPath) {
		LogUtil.i(TAG, "[kaede] createSoLibPluginPackage");
		BasePluginPackage basePluginPackage = new SoLibPluginPackage(packageName);
		basePluginPackage.loadPlugin(context, pluginPath);
		return basePluginPackage;
	}
}
