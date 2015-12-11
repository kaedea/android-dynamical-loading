package com.yy.mobile.core.ylink.bridge;

import android.app.Activity;
import com.yy.mobile.core.ylink.bridge.coreapi.BaseApi;

import java.util.HashMap;

/**
 * Created by kaede on 2015/12/7.
 */
public class CoreApiManager {

	static CoreApiManager instance = new CoreApiManager();
	Activity proxyActivity; // 代理Activity的实例，优先使用这个实例

	HashMap<Class<?extends BaseApi>,BaseApi> apiMap;

	public static CoreApiManager getInstance(){
		return instance;
	}

	private CoreApiManager(){
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
