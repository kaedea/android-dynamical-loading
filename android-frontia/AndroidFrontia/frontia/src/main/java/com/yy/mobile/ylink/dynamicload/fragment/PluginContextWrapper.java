package com.yy.mobile.ylink.dynamicload.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContextWrapper;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import com.yy.mobile.ylink.dynamicload.core.DLPluginPackage;

/**
 * 插件项目专用Context
 * 在插件里开发应该优先使用该Context，不然可能错误使用了宿主的上下文环境，比如插件从res拿一个String，结果变成宿主里的String，或者直接找不到
 * Created by kaede on 2015/12/11.
 */
public class PluginContextWrapper extends ContextWrapper {
	DLPluginPackage pluginPackage;
	Activity activity;
	Resources.Theme theme;

	public PluginContextWrapper(Activity activity) {
		super(activity);
		this.activity = activity;
	}

	public PluginContextWrapper attatchPluginPackage(DLPluginPackage pluginPackage){
		this.pluginPackage = pluginPackage;
		try {
			prepareActivityTheme();
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return this;
	}

	private void prepareActivityTheme() throws PackageManager.NameNotFoundException {
		ComponentName componentName = new ComponentName(activity,activity.getClass());
		ActivityInfo activityInfo = activity.getPackageManager().getActivityInfo(componentName, 0);
		int activityTheme = activityInfo.theme;
		if (activityTheme == 0){
			int applicationTheme = activity.getApplicationInfo().theme;
			if (applicationTheme == 0){
				if (Build.VERSION.SDK_INT >= 14) {
					activityTheme = android.R.style.Theme_DeviceDefault;
				} else {
					activityTheme = android.R.style.Theme;
				}
			}else
				activityTheme = applicationTheme;
		}
		activity.setTheme(activityTheme);
		theme = pluginPackage.resources.newTheme();
		theme.setTo(activity.getTheme());
		// Finals适配三星以及部分加载XML出现异常BUG
		try {
			theme.applyStyle(activityTheme, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Resources.Theme getTheme() {
		if (theme == null) {
			return super.getTheme();
		}
		return theme;
	}

	@Override
	public Resources getResources() {
		if (pluginPackage == null) {
			return super.getResources();
		}
		return pluginPackage.resources;
	}

	@Override
	public AssetManager getAssets() {
		if (pluginPackage == null) {
			return super.getAssets();
		}
		return pluginPackage.assetManager;
	}

	@Override
	public ClassLoader getClassLoader() {
		if (pluginPackage == null) {
			return super.getClassLoader();
		}
		return pluginPackage.classLoader;
	}

	@Override
	public String getPackageName() {
		if (pluginPackage == null) {
			return super.getPackageName();
		}
		return pluginPackage.packageName;

	}
}
