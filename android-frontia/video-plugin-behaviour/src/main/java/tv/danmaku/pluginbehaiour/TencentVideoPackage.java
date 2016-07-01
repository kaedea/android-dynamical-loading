package tv.danmaku.pluginbehaiour;

import android.app.Activity;
import tv.danmaku.frontia.SoLibPluginPackage;
import tv.danmaku.frontia.bridge.plugin.BaseBehaviour;
import tv.danmaku.frontia.core.error.IllegalPluginException;

import java.lang.reflect.Method;

/**
 * Created by kaede on 2016/4/14.
 */
public class TencentVideoPackage extends SoLibPluginPackage {

	public TencentVideoPackage(String pluginPath) {
		super(pluginPath);
	}

	@Override
	public BaseBehaviour getPluginBehaviour(Object... args) throws IllegalPluginException {
		Class clazz = loadPluginClass("me.kaede.pluginpackage.Entry");
		try {
			Method method = clazz.getMethod("getTencentVideo", Activity.class);
			return (ITencentVideo) method.invoke(null, args[0]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}