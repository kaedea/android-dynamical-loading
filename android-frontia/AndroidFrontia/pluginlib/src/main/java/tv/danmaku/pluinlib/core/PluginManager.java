package tv.danmaku.pluinlib.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
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
 *
 * 插件管理器，提供同步和异步获取插件实体类的接口
 */
public class PluginManager implements IPluginManager {

	public static final String TAG = "BasePluginHandler";
	static PluginManager instance;
	Context context;
	Handler handler;
	Map<String, BasePluginPackage> packageHolder;

	private ExecutorService loadExecutor = Executors.newCachedThreadPool();

	public static PluginManager getInstance(Context context) {
		if (instance != null) return instance;
		synchronized (PluginManager.class) {
			instance = new PluginManager(context);
			return instance;
		}
	}

	public PluginManager(Context context) {
		this.context = context.getApplicationContext();
		packageHolder = new HashMap<>();
		handler = new Handler(Looper.myLooper());
	}


	public BasePluginPackage loadPlugin(BasePluginPackage basePluginPackage){

		String pluginPath = basePluginPackage.pluginPath;

		if (TextUtils.isEmpty(pluginPath) || !new File(pluginPath).exists()) {
			LogUtil.e(TAG, "pluginPath not exist!");
			return null;
		}

		// 1.复制到内部临时路径
		if (!pluginPath.startsWith(context.getCacheDir().getAbsolutePath())) {
			String tempFilePath = context.getCacheDir().getAbsolutePath() + File.separator + System.currentTimeMillis() + ".apk";
			LogUtil.i(TAG, "复制到内部临时路径:" + tempFilePath);
			if (FileUtil.copyFile(pluginPath, tempFilePath)) {
				basePluginPackage.pluginPath = tempFilePath;
				pluginPath = tempFilePath;
			} else {
				new File(tempFilePath).delete();
				LogUtil.e(TAG, "复制插件文件失败:" + pluginPath + " to " + tempFilePath);
				return null;
			}
		}

		LogUtil.i(TAG, "获取插件的PackageInfo");
		PackageInfo packageInfo = ApkUtil.getPackageInfo(context, pluginPath);
		if (packageInfo == null) {
			new File(pluginPath).delete();
			LogUtil.e(TAG, "packageInfo = null");
			return null;
		}

		// 2.签名校验
		LogUtil.i(TAG, "校验插件的签名");
		if (!checkPluginValid(pluginPath)){
			LogUtil.e(TAG, "签名验证失败!");
			new File(pluginPath).delete();
			return null;
		}

		// 3.检查是否已经加载到缓存，有则直接使用缓存
		LogUtil.i(TAG, "get PluginPackage from holder : "+packageInfo.packageName);
		BasePluginPackage pluginPackage = getPluginPackage(packageInfo.packageName);
		if (pluginPackage != null) {
			LogUtil.i(TAG, "hit");
			return pluginPackage;
		}
		LogUtil.i(TAG, "no hit");

		// 4.加载指定路径上的插件
		LogUtil.i(TAG, "load plugin");
		basePluginPackage = basePluginPackage.loadPlugin(context);
		packageHolder.put(packageInfo.packageName, basePluginPackage);

		new File(pluginPath).delete();
		return basePluginPackage;
	}


	public void loadPluginAysnc(BasePluginPackage basePluginPackage, OnLoadPluginListener onLoadPluginListener){
		loadExecutor.execute(new LoadPluginTask(basePluginPackage,onLoadPluginListener));
	}

	@Override
	public BasePluginPackage initPlugin(String pluginPath) {
		return null;
	}



	@Override
	public BasePluginPackage getPluginPackage(String packageName) {
		return packageHolder.get(packageName);
	}

	@Override
	public Class loadPluginClass(BasePluginPackage basePluginPackage, String className) {
		return basePluginPackage.loadPluginClass(className);
	}

	public boolean checkPluginValid(String pluginPath){
		return true;
	}

	public BaseBehaviour getPluginBehaviour(BasePluginPackage basePluginPackage){
		return basePluginPackage.getPluginBehaviour();
	}

	public interface OnLoadPluginListener{
		public void onFinished(String pluginPath,BasePluginPackage basePluginPackage);
	}

	public class LoadPluginTask implements Runnable {
		BasePluginPackage basePluginPackage;
		OnLoadPluginListener onLoadPluginListener;

		public LoadPluginTask(BasePluginPackage basePluginPackage, OnLoadPluginListener onLoadPluginListener) {
			this.basePluginPackage = basePluginPackage;
			this.onLoadPluginListener = onLoadPluginListener;
		}

		@Override
		public void run() {
			final BasePluginPackage basePluginPackage = loadPlugin(this.basePluginPackage);
			handler.post(new Runnable() {
				@Override
				public void run() {
					onLoadPluginListener.onFinished(basePluginPackage.pluginPath,basePluginPackage);
				}
			});
		}
	}




}
