package me.kaede.mainapp;

import com.yy.mobile.ylink.bridge.CoreApiManager;
import com.yy.mobile.ylink.bridge.coreapi.BaseApi;
import com.yy.mobile.ylink.bridge.coreapi.LoginApi;
import com.yy.mobile.ylink.bridge.coreapi.UserInfoApi;
import me.kaede.mainapp.bridge.LoginApiImpl;
import me.kaede.mainapp.bridge.UserInfoApiImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 主项目做好CoreApi的实现，并注册到YLink里面去，插件通过CoreApiManager获得CoreApi的实现
 * Created by kaede on 2015/12/7.
 */
public class YLink {
	static Map<Class< ?extends BaseApi>,Class< ?extends BaseApi>> apisMap;
	static {
		apisMap = new HashMap<>();
		apisMap.put(LoginApi.class,LoginApiImpl.class);
		apisMap.put(UserInfoApi.class,UserInfoApiImpl.class);
	}

	/**
	 * 初始化，启动插件前，要确保已经执行该初始化方法
	 */
	public static void init(){
		Set<Class<? extends BaseApi>> keySet = apisMap.keySet();
		for (Class<? extends BaseApi> key :
				keySet) {
			Class< ? extends BaseApi> value = apisMap.get(key);
			try {
				CoreApiManager.getInstance().putApi(key,value.newInstance());
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
}
