package tv.danmaku.pluinlib.bridge.host;

import android.app.Activity;
import tv.danmaku.pluinlib.bridge.host.hostapi.BaseApi;

import java.util.HashMap;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/14.
 *
 * 宿主API的管理类，宿主通过此管理类注册API的实现
 */
public class HostApiManager {

	static HostApiManager instance = new HostApiManager();
	Activity proxyActivity; // 代理Activity的实例，优先使用这个实例

	HashMap<Class<?extends BaseApi>,BaseApi> apiMap;

	public static HostApiManager getInstance(){
		return instance;
	}

	private HostApiManager(){
		apiMap = new HashMap<>();
	}

	public boolean containsApi(Class<?extends BaseApi> clazz){
		return apiMap.containsKey(clazz);
	}

	public <T extends BaseApi> T getApi(Class<T> clazz){
		return (T) apiMap.get(clazz);
	}

	public void putApi(Class<?extends BaseApi> key,BaseApi value){
		apiMap.put(key, value);
	}

	public Activity getActivity() {
		return proxyActivity;
	}

	public void setActivity(Activity proxyActivity) {
		this.proxyActivity = proxyActivity;
	}
}
