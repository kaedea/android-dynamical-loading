package tv.danmaku.pluinlib.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import dalvik.system.DexClassLoader;
import tv.danmaku.pluinlib.bridge.plugin.BaseBehaviour;
import tv.danmaku.pluinlib.util.ApkUtil;

/**
 * Copyright (c) 2015 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 */
public abstract class BasePluginPackage {

	public String pluginPath;
	public String packageName;
	public String defaultActivity;
	public DexClassLoader classLoader;
	public AssetManager assetManager;
	public Resources resources;
	public boolean hasSoLib;
	public String internalSoLibDir;
	public boolean hasLoadedSoLib;
	public String md5;

	public PackageInfo packageInfo;

	public BasePluginPackage(String pluginPath){
		this.pluginPath = pluginPath;
	}

	@Override
	public String toString() {
		if (packageInfo != null) {
			return packageInfo.toString();
		}
		return "BasePluginPackage{" +
				"pluginPath='" + pluginPath + '\'' +
				'}';
	}

	/**
	 * 加载插件的接口，具体实现根据需求的不同由不同的继承类完成
	 * @param context Context实例
	 * @return 完成加载工作的实体类
	 */
	public BasePluginPackage loadPlugin(Context context) {
		return loadPlugin(context, pluginPath);
	}

	public abstract BasePluginPackage loadPlugin(Context context, String packagePath);

	/**
	 * 检查插件的合法性
	 * @return
	 */
	public boolean checkPluginValid() {
		return false;
	}

	/**
	 * 获取插件的行为接口，用于控制插件
	 * @param args 初始化插件行为接口需要用到的参数（这里用到了不定数参数，是否能优化？）
	 * @return
	 */
	public abstract BaseBehaviour getPluginBehaviour(Object... args);

	/**
	 * 加载插件的一个类
	 * @param className 类名
	 * @return
	 */
	public Class loadPluginClass(String className) {
		return ApkUtil.loadPluginClass(classLoader, className);
	}

}
