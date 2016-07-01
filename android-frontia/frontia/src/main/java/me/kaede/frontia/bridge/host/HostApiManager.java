package me.kaede.frontia.bridge.host;

import me.kaede.frontia.bridge.host.hostapi.BaseApi;

import java.util.HashMap;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/14.
 * <p/>
 * 宿主API的管理类，宿主通过此管理类注册API的实现
 */
public class HostApiManager {

    private static HostApiManager instance = new HostApiManager();

    HashMap<Class<? extends BaseApi>, BaseApi> apiMap;

    public static HostApiManager getInstance() {
        return instance;
    }

    private HostApiManager() {
        apiMap = new HashMap<>();
    }

    public boolean containsApi(Class<? extends BaseApi> clazz) {
        return apiMap.containsKey(clazz);
    }

    public <T extends BaseApi> T getApi(Class<T> clazz) {
        return (T) apiMap.get(clazz);
    }

    public void putApi(Class<? extends BaseApi> key, BaseApi value) {
        apiMap.put(key, value);
    }

}
