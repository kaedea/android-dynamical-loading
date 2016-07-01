package me.kaede.frontia.bridge.host.hostapi;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/14.
 * <p/>
 * 宿主API的基类，插件通过这些接口调用宿主的API；
 * 插件只能访问API接口，具体实现是透明的，由宿主通过ApiManager注册；
 */
public abstract class BaseApi {
    int version;

    /**
     * 具体Api版本查询，方便插件项目做适配
     *
     * @return 当前版本号
     */
    public int getVersion() {
        return version;
    }

    /**
     * 是否存在目标方法，建议插件项目调用钱查询
     *
     * @param methodname 方法名
     * @return 是否存在
     */
    public boolean isMethodExist(String methodname) {
        return true;
    }
}
