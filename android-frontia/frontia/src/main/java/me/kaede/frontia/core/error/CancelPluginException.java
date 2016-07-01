/*
 * Copyright (c) 2015-2016 BiliBili Inc.
 */

package me.kaede.frontia.core.error;

/**
 * Created by Kaede on 16/6/6.
 */
public class CancelPluginException extends PluginException {
    public CancelPluginException() {
    }

    public CancelPluginException(String detailMessage) {
        super(detailMessage);
    }

    public CancelPluginException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public CancelPluginException(Throwable throwable) {
        super(throwable);
    }
}
