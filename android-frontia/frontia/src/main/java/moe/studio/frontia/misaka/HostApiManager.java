/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia.misaka;

import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import moe.studio.frontia.ext.ProxyHandler;

/**
 * 宿主API的管理类，宿主通过此管理类注册API的实现
 */
public final class HostApiManager {

    private static final String TAG = "plugin.HostApi";
    private static HostApiManager instance = new HostApiManager();

    private final Map<Class<? extends HostApi>, HostApi> hostApiMap;
    private final Map<Class<? extends HostApi>, Class<? extends HostApi>> hostApiClassMap;
    private final Map<Class<? extends HostApi>, WeakReference<? extends HostApi>> hostApiWeakMap;


    public static HostApiManager getInstance() {
        return instance;
    }

    private HostApiManager() {
        hostApiMap = new HashMap<>();
        hostApiClassMap = new HashMap<>();
        hostApiWeakMap = new HashMap<>();
    }

    public synchronized <H extends HostApi> boolean has(Class<H> key) {
        return hostApiMap.containsKey(key)
                || hostApiClassMap.containsKey(key);
    }

    public synchronized <H extends HostApi, I extends H> void register(Class<H> key, I value) {
        hostApiMap.put(key, value);
    }

    public synchronized <H extends HostApi, I extends H> void register(Class<H> key, Class<I> value) {
        try {
            value.getConstructor();
            hostApiClassMap.put(key, value);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(key.getName()
                    + "do not have constructor without params.", e);
        }
    }

    @Nullable
    public synchronized <H extends HostApi> H get(Class<H> key) {
        HostApi i = null;

        if (hostApiMap.containsKey(key)) {
            i = hostApiMap.get(key);
        }

        if (hostApiClassMap.containsKey(key)) {
            WeakReference<? extends HostApi> weak = hostApiWeakMap.get(key);
            if (weak != null) {
                i = weak.get();
                if (i == null) {
                    try {
                        Class<? extends HostApi> clazz = hostApiClassMap.get(key);
                        i = clazz.newInstance();
                        hostApiWeakMap.put(key, new WeakReference<>(i));
                    } catch (Throwable e) {
                        Log.w(TAG, e);
                    }
                }
            }
        }

        if (i == null) {
            return null;
        }

        try {
            return ProxyHandler.getProxy(key, (H) i);
        } catch (Exception e) {
            Log.w(TAG, e);
            return null;
        }
    }

    public synchronized <H extends HostApi> void remove(Class<H> key) {
        hostApiMap.remove(key);
        hostApiClassMap.remove(key);
        hostApiWeakMap.remove(key);
    }


}
