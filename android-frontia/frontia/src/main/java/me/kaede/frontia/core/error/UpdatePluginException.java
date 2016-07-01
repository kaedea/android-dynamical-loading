/*
 * Copyright (c) 2015-2016 BiliBili Inc.
 */

package me.kaede.frontia.core.error;

/**
 * Created by Kaede on 16/6/6.
 * 更新插件过程中出现的异常
 */
public class UpdatePluginException extends PluginException {
    public UpdatePluginException() {
    }

    public UpdatePluginException(String detailMessage) {
        super(detailMessage);
    }

    public UpdatePluginException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public UpdatePluginException(Throwable throwable) {
        super(throwable);
    }
}
