/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package com.kaedea.frontia.demo.blur;

import moe.studio.behavior.stackblur.StackBlurPlugin;
import moe.studio.frontia.ext.AssetsRequest;

/**
 * Created by Kaede on 16/6/28.
 */
public class StackBlurRequest extends AssetsRequest<StackBlurPlugin> {

    public StackBlurPlugin createPlugin(String path) {
        return new StackBlurPlugin(path);
    }

    @Override
    public String getAssetsPath() {
        return "stackblur.apk";
    }

    @Override
    public int getAssetsVersion() {
        return 1;
    }

    @Override
    public String requestPluginId() {
        return "moe.studio.plugin.stackblur";
    }
}
