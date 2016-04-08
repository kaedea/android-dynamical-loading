/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia.ext;

import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import moe.studio.frontia.BuildConfig;

import static java.lang.reflect.Proxy.newProxyInstance;

/**
 * @author kaede
 * @version date 2016/12/8
 */
public class ProxyHandler<T> implements InvocationHandler {

    private static final String TAG = "plugin.proxy";
    private final T mTarget;

    ProxyHandler(T t) {
        mTarget = t;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Proxy invoke, class = " + proxy.getClass().getName()
                    + ", method = " + method.getName());
        }

        try {
            return method.invoke(mTarget, args);

        } catch (Throwable e) {
            Log.w(TAG, "Invoke plugin method fail.");
            Log.w(TAG, e);
            if (BuildConfig.DEBUG) {
                throw new RuntimeException(e);
            }
        }

        // The plugin behavior interface is not capable with the Impl of the plugin.
        // Return the default value of the given type as to abort crash here.
        return getDefaultValue(method.getReturnType());
    }

    private static Object getDefaultValue(Class<?> type) {

        // Check primitive type.
        if (type == Boolean.class || type == boolean.class) {
            return false;
        } else if (type == Byte.class || type == byte.class) {
            return 0;
        } else if (type == Character.class || type == char.class) {
            return '\u0000';
        } else if (type == Short.class || type == short.class) {
            return 0;
        } else if (type == Integer.class || type == int.class) {
            return 0;
        } else if (type == Long.class || type == long.class) {
            return 0L;
        } else if (type == Float.class || type == float.class) {
            return 0.0F;
        } else if (type == Double.class || type == double.class) {
            return 0.0D;
        } else if (type == Void.class || type == void.class) {
            return null;
        }

        return null;
    }

    @Nullable
    public static <T> T getProxy(Class<T> itf, T t) throws Exception {
        try {
            Class<?> clazz = t.getClass();
            Class<?>[] interfaces = clazz.getInterfaces();

            if (interfaces != null && interfaces.length > 0) {
                for (Class item : interfaces) {
                    if (itf.isAssignableFrom(item)) {
                        Object instance = newProxyInstance(clazz.getClassLoader(),
                                new Class[]{item},
                                new ProxyHandler<>(t));
                        return (T) instance;
                    }
                }
            }
            Log.w(TAG, "Can not find proxy interface.");
            throw new Exception("Can not find proxy interface.");

        } catch (Throwable e) {
            Log.w(TAG, "Create interface proxy ail.");
            Log.w(TAG, e);
            throw new Exception("Create interface proxy ail.", e);
        }
    }
}
