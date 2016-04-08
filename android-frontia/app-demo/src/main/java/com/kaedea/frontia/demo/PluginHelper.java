/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package com.kaedea.frontia.demo;


import android.content.Context;

import moe.studio.frontia.BuildConfig;
import moe.studio.frontia.Frontia;
import moe.studio.frontia.ext.PluginSetting;

/**
 * Created by kaede on 2015/12/7.
 */
public class PluginHelper {


	/**
	 * 初始化，启动插件前，要确保已经执行该初始化方法
	 */
	public static void init(Context context) {
		PluginSetting setting = new PluginSetting.Builder()
				.setDebugMode(BuildConfig.DEBUG)
				.ignoreInstalledPlugin(BuildConfig.DEBUG)
				.build();
		Frontia.instance().init(context, setting);

		registerApis();
		registerLibraries();
	}

	private static void registerApis() {
		// HostApiManager.getInstance().register(LoginApi.class, LoginApiImpl.class);
	}

	private static void registerLibraries() {
		Frontia.registerLibrary("support_v4", 24);
	}

}
