/*
 * Copyright (c) 2015-2016 BiliBili Inc.
 */

package me.kaede.frontia.core.error;

/**
 * Created by Kaede on 2016/4/27.
 * 非法参数异常
 */
public class IllegalPluginException extends PluginException {
    public IllegalPluginException() {
    }

    public IllegalPluginException(String detailMessage) {
        super(detailMessage);
    }

    public IllegalPluginException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public IllegalPluginException(Throwable throwable) {
        super(throwable);
    }
}
