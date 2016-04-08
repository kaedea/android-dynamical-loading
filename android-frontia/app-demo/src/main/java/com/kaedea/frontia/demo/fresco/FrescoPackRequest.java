/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package com.kaedea.frontia.demo.fresco;

import moe.studio.frontia.ext.AssetsRequest;
import moe.studio.frontia.ext.ShareLibrary.SoLibPackage;

/**
 * @author kaede
 * @version date 2016/12/5
 */
public class FrescoPackRequest extends AssetsRequest<SoLibPackage> {

    @Override
    public String getAssetsPath() {
        return "fresco.apk";
    }

    @Override
    public String requestPluginId() {
        return "moe.studio.plugin.fresco";
    }

    @Override
    public int getAssetsVersion() {
        return 1;
    }

    @Override
    public SoLibPackage createPlugin(String s) {
        return new SoLibPackage(s);
    }
}
