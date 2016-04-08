/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia.model;

import android.content.Context;

import moe.studio.frontia.core.Plugin;
import moe.studio.frontia.ext.AssetsRequest;
import moe.studio.frontia.ext.ShareLibrary;

/**
 * @author kaede
 * @version date 2016/12/9
 */
public class TestAssetsRequest extends AssetsRequest<ShareLibrary.SoLibPackage> {

    @Override
    public String getAssetsPath() {
        return "fresco.apk";
    }

    @Override
    public int getAssetsVersion() {
        return 1;
    }

    @Override
    public String requestPluginId() {
        return "moe.studio.plugin.fresco";
    }

    @Override
    public Plugin createPlugin(String path) {
        return new ShareLibrary.SoLibPackage(path);
    }

    @Override
    public boolean requestClearLocalPlugins(Context context) {
        return true;
    }
}
