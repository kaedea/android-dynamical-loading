package me.kaede.pluginbehaviour.videosdk;

import android.app.Activity;
import me.kaede.frontia.SoLibPluginPackage;
import me.kaede.frontia.bridge.plugin.BaseBehaviour;
import me.kaede.frontia.core.error.IllegalPluginException;

import java.lang.reflect.Method;

/**
 * Created by kaede on 2016/4/14.
 */
public class TencentVideoPackage extends SoLibPluginPackage {

	public static final String PLUGIN_ENTRY = "me.kaede.plugin.videosdk.Entry";

	public TencentVideoPackage(String pluginPath) {
		super(pluginPath);
	}

	@Override
	public BaseBehaviour getPluginBehaviour(Object... args) throws IllegalPluginException {
		Class clazz = loadPluginClass(PLUGIN_ENTRY);
		try {
			Method method = clazz.getMethod("getTencentVideo", Activity.class);
			return (ITencentVideo) method.invoke(null, args[0]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}