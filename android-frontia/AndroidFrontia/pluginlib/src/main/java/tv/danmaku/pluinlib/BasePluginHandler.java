package tv.danmaku.pluinlib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 */
public abstract class BasePluginHandler implements IPluginHandler {

	Context context;
	Map<String, BasePluginPackage> packageHolder;

	public BasePluginHandler(Context context) {
		this.context = context.getApplicationContext();
		packageHolder = new HashMap<>();
	}

	@Override
	public int initPlugin(String pluginPath) {
		PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(pluginPath,
				PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
		if (packageInfo == null) {
			return -1;
		}
		BasePluginPackage basePluginPackage = PluginPackageFactory.createSimplePluginPackage(context, packageInfo.packageName, pluginPath);
		packageHolder.put(packageInfo.packageName, basePluginPackage);
		return 0;
	}

	@Override
	public BasePluginPackage getPluginPackage(String packageName) {
		return packageHolder.get(packageName);
	}

	@Override
	public Class loadPluginClass(BasePluginPackage basePluginPackage, String className) {
		return loadPluginClass(basePluginPackage.classLoader, className);
	}

	private Class<?> loadPluginClass(ClassLoader classLoader, String className) {
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className, true, classLoader);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return clazz;
	}
}
