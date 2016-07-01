/*
 * Copyright (c) 2015-2016 BiliBili Inc.
 */

package me.kaede.frontia.core.error;

/**
 * Created by Kaede on 2016/4/27.
 * 加载插件过程出现异常
 */
public class LoadPluginException extends PluginException {
    public LoadPluginException() {
    }

    public LoadPluginException(String detailMessage) {
        super(detailMessage);
    }

    public LoadPluginException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public LoadPluginException(Throwable throwable) {
        super(throwable);
    }
}
