package tv.danmaku.pluginbehaiour;

import android.app.Activity;
import tv.danmaku.pluinlib.SoLibPluginPackage;
import tv.danmaku.pluinlib.bridge.plugin.BaseBehaviour;

import java.lang.reflect.Method;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/14.
 */
public class TecentVideoPackage extends SoLibPluginPackage {

	@Override
	public BaseBehaviour getPluginBehaviour(Object... args) {
		Class clazz = loadPluginClass("me.kaede.pluginpackage.Entry");
		try {
			Method method = clazz.getMethod("getTencentVideo", Activity.class);
			return (ITencentVideo) method.invoke(null, args[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}