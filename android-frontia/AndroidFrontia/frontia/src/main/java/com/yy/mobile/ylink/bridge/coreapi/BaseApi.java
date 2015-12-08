package com.yy.mobile.ylink.bridge.coreapi;

/**
 * Created by kaede on 2015/12/7.
 */
public abstract class BaseApi {
	int version;

	/**
	 * 具体CoreApi版本查询，方便插件项目做适配
	 * @return 当前版本号
	 */
	public int getVersion(){
		return version;
	}

	/**
	 * 是否存在目标方法，建议插件项目调用钱查询
	 * @param methodname 方法名
	 * @return 是否存在
	 */
	public boolean isMethodExist(String methodname){
		return  true;
	}
}
