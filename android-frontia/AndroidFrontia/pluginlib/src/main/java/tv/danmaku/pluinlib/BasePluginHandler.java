package tv.danmaku.pluinlib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import java.io.File;
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
		if (TextUtils.isEmpty(pluginPath) || !new File(pluginPath).exists()) {
			LogUtil.e(TAG, "pluginPath not exist!");
			return null;
		}

		if (!pluginPath.startsWith(context.getCacheDir().getAbsolutePath())) {
			String tempFilePath = context.getCacheDir().getAbsolutePath() + File.separator + System.currentTimeMillis() + ".apk";
			if (FileUtil.copyFile(pluginPath, tempFilePath)) {
				pluginPath = tempFilePath;
			} else {
				LogUtil.e(TAG, "复制插件文件失败:" + pluginPath + " " + tempFilePath);
				return null;
			}
		}

		PackageInfo packageInfo = ApkHelper.getPackageInfo(context, pluginPath);
		/*PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(pluginPath,
				PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);*/
		if (packageInfo == null) {
			LogUtil.e(TAG, "packageInfo = null");
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
