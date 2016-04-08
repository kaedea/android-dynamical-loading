package tv.danmaku.pluinlib;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 */
public class PluginPackageFactory {

	public static BasePluginPackage createSimplePluginPackage(String packageName,String pluginPath){
		BasePluginPackage basePluginPackage = new BasePluginPackage(packageName);
		basePluginPackage.loadPlugin(pluginPath);
		return basePluginPackage;
	}
}
