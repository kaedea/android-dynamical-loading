/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia.ext;

import android.content.Context;

import java.util.List;

import moe.studio.frontia.core.Plugin;
import moe.studio.frontia.core.PluginRequest;
import moe.studio.frontia.update.RemotePluginInfo;

/**
 * @author kaede
 * @version date 2016/12/6
 */

public abstract class AssetsRequest<P extends Plugin> extends PluginRequest<P> {

    @Override
    public List<? extends RemotePluginInfo> requestRemotePluginInfo(Context context) throws Exception {
        return null;
    }

    @Override
    public boolean requestClearLocalPlugins(Context context) {
        return false;
    }

    @Override
    public boolean isFromAssets() {
        return true;
    }

    @Override
    public abstract String getAssetsPath();

    @Override
    public abstract int getAssetsVersion();

}
