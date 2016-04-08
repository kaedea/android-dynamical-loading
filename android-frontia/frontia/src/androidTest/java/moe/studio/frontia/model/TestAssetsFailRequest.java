/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia.model;

import moe.studio.frontia.core.Plugin;
import moe.studio.frontia.ext.AssetsRequest;
import moe.studio.frontia.ext.ShareLibrary;

/**
 * @author kaede
 * @version date 2016/12/9
 */
public class TestAssetsFailRequest extends AssetsRequest<ShareLibrary.SoLibPackage> {

    @Override
    public String getAssetsPath() {
        return null;
    }

    @Override
    public int getAssetsVersion() {
        return 0;
    }

    @Override
    public String requestPluginId() {
        return null;
    }

    @Override
    public Plugin createPlugin(String path) {
        return null;
    }
}
