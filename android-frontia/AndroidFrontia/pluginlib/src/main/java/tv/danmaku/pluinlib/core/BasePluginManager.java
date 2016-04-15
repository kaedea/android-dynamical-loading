package tv.danmaku.pluinlib.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Looper;
import android.text.TextUtils;
import tv.danmaku.pluinlib.SoLibPluginPackage;
import tv.danmaku.pluinlib.bridge.plugin.BaseBehaviour;
import tv.danmaku.pluinlib.util.ApkUtil;
import tv.danmaku.pluinlib.util.FileUtil;
import tv.danmaku.pluinlib.util.LogUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 */
public class BasePluginManager implements IPluginManager {

	public static final String TAG = "BasePluginHandler";
	Context context;
	Map<String, BasePluginPackage> packageHolder;

	private ExecutorService loadExecutor = Executors.newCachedThreadPool();

	public BasePluginManager(Context context) {
		this.context = context.getApplicationContext();
		packageHolder = new HashMap<>();
	}


	public BasePluginPackage loadPlugin(BasePluginPackage basePluginPackage){
		if (TextUtils.isEmpty(basePluginPackage.pluginPath) || !new File(basePluginPackage.pluginPath).exists()) {
			LogUtil.e(TAG, "pluginPath not exist!");
			return null;
		}
		String pluginPath = basePluginPackage.pluginPath;

		// 1.复制到内部临时路径
		if (!pluginPath.startsWith(context.getCacheDir().getAbsolutePath())) {
			String tempFilePath = context.getCacheDir().getAbsolutePath() + File.separator + System.currentTimeMillis() + ".apk";
			if (FileUtil.copyFile(pluginPath, tempFilePath)) {
				pluginPath = tempFilePath;
			} else {
				LogUtil.e(TAG, "复制插件文件失败:" + pluginPath + " to " + tempFilePath);
				return null;
			}
		}

		PackageInfo packageInfo = ApkUtil.getPackageInfo(context, pluginPath);
		if (packageInfo == null) {
			LogUtil.e(TAG, "packageInfo = null");
			return null;
		}

		// 2.签名校验
		if (!checkPluginValid(pluginPath)){
			LogUtil.e(TAG, "签名验证失败!");
			return null;
		}

		BasePluginPackage pluginPackage = getPluginPackage(packageInfo.packageName);
		if (pluginPackage != null) return basePluginPackage;

		basePluginPackage = basePluginPackage.loadPlugin(context, pluginPath);
		packageHolder.put(packageInfo.packageName, basePluginPackage);

		return basePluginPackage;
	}

	@Override
	public BasePluginPackage initPlugin(String pluginPath) {
		if (TextUtils.isEmpty(pluginPath) || !new File(pluginPath).exists()) {
			LogUtil.e(TAG, "pluginPath not exist!");
			return null;
		}

		// 1.复制到内部临时路径
		if (!pluginPath.startsWith(context.getCacheDir().getAbsolutePath())) {
			String tempFilePath = context.getCacheDir().getAbsolutePath() + File.separator + System.currentTimeMillis() + ".apk";
			if (FileUtil.copyFile(pluginPath, tempFilePath)) {
				pluginPath = tempFilePath;
			} else {
				LogUtil.e(TAG, "复制插件文件失败:" + pluginPath + " to " + tempFilePath);
				return null;
			}
		}

		PackageInfo packageInfo = ApkUtil.getPackageInfo(context, pluginPath);
		if (packageInfo == null) {
			LogUtil.e(TAG, "packageInfo = null");
			return null;
		}

		BasePluginPackage basePluginPackage = getPluginPackage(packageInfo.packageName);
		if (basePluginPackage != null) return basePluginPackage;

		// 2.签名校验
		if (!checkPluginValid(pluginPath)){
			LogUtil.e(TAG, "签名验证失败!");
			return null;
		}

		// 3.加载插件
		basePluginPackage = new SoLibPluginPackage(packageInfo.packageName);
		basePluginPackage.loadPlugin(context, pluginPath);
		packageHolder.put(packageInfo.packageName, basePluginPackage);

		return basePluginPackage;
	}

	public void aysncInitPlugin(final String pluginPath, final OnLoadPluginListener onLoadPluginListener){
		final android.os.Handler handler  = new android.os.Handler(Looper.myLooper());
		loadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				final BasePluginPackage basePluginPackage = initPlugin(pluginPath);
				handler.post(new Runnable() {
					@Override
					public void run() {
						onLoadPluginListener.onFinished(pluginPath, basePluginPackage);
					}
				});
			}
		});
	}

	@Override
	public BasePluginPackage getPluginPackage(String packageName) {
		return packageHolder.get(packageName);
	}

	@Override
	public Class loadPluginClass(BasePluginPackage basePluginPackage, String className) {
		return ApkUtil.loadPluginClass(basePluginPackage.classLoader, className);
	}

	public boolean checkPluginValid(String pluginPath){
		return true;
	}

	public BasePluginPackage createPluginPackage(String pluginPath){
		BasePluginPackage basePluginPackage = new SoLibPluginPackage();
		basePluginPackage.loadPlugin(context,pluginPath);
		return basePluginPackage;
	}

	public BaseBehaviour getPluginBehaviour(BasePluginPackage basePluginPackage){
		return basePluginPackage.getPluginBehaviour();
	}

	public interface OnLoadPluginListener{
		public void onFinished(String pluginPath,BasePluginPackage basePluginPackage);
	}




}
