package tv.danmaku.pluinlib;

import android.content.Context;
import android.content.pm.PackageInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 */
public class BasePluginHandler implements IPluginHandler {

	public static final String TAG = "BasePluginHandler";
	Context context;
	Map<String, BasePluginPackage> packageHolder;

	public BasePluginHandler(Context context) {
		this.context = context.getApplicationContext();
		packageHolder = new HashMap<>();
	}

	@Override
	public BasePluginPackage initPlugin(String pluginPath) {
		PackageInfo packageInfo = ApkHelper.getPackageInfo(context,pluginPath);
		/*PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(pluginPath,
				PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);*/
		if (packageInfo == null) {
			LogUtil.w(TAG,"packageInfo = null");
			return null;
		}
		BasePluginPackage basePluginPackage = PluginPackageFactory.createSimplePluginPackage(context, packageInfo.packageName, pluginPath);
		packageHolder.put(packageInfo.packageName, basePluginPackage);
		return basePluginPackage;
	}

	@Override
	public BasePluginPackage getPluginPackage(String packageName) {
		return packageHolder.get(packageName);
	}

	@Override
	public Class loadPluginClass(BasePluginPackage basePluginPackage, String className) {
		return ApkHelper.loadPluginClass(basePluginPackage.classLoader, className);
	}

}
