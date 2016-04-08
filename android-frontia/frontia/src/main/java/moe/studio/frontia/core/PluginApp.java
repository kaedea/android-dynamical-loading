/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia.core;

import android.app.Application;
import android.content.Context;

/**
 * 插件的Application, 也是插件的入口类。
 *
 * 插件必须扩展改类作为自己的Application, 并且实现 {@link PluginApp#getBehavior()}方法。
 *
 * @author kaede
 * @version date 2016/12/2
 */

public abstract class PluginApp extends Application {
    protected Context mContext;

    /**
     * 给插件设置宿主的Context实例
     */
    public void setAppContext(Context context) {
        mContext = context;
    }

    public abstract PluginBehavior getBehavior();
}
