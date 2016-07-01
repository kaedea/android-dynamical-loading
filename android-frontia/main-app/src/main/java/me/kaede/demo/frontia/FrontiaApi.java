package me.kaede.demo.frontia;


import me.kaede.demo.frontia.bridge.LoginApiImpl;
import me.kaede.demo.frontia.bridge.UserInfoApiImpl;
import me.kaede.frontia.core.PluginConstants;
import me.kaede.pluginbehaviour.LoginApi;
import me.kaede.pluginbehaviour.UserInfoApi;
import me.kaede.frontia.bridge.host.HostApiManager;
import me.kaede.frontia.bridge.host.hostapi.BaseApi;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by kaede on 2015/12/7.
 */
public class FrontiaApi {
	static Map<Class<? extends BaseApi>, Class<? extends BaseApi>> apisMap;

	static {
		apisMap = new HashMap<>();
		apisMap.put(LoginApi.class, LoginApiImpl.class);
		apisMap.put(UserInfoApi.class, UserInfoApiImpl.class);
	}

	/**
	 * 初始化，启动插件前，要确保已经执行该初始化方法
	 */
	public static void init() {
		PluginConstants.setDebugMode(true, true);
		Set<Class<? extends BaseApi>> keySet = apisMap.keySet();
		for (Class<? extends BaseApi> key :
				keySet) {
			Class<? extends BaseApi> value = apisMap.get(key);
			try {
				HostApiManager.getInstance().putApi(key, value.newInstance());
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
}
