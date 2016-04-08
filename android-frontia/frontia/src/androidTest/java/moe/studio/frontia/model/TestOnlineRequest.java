/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia.model;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import moe.studio.frontia.core.PluginRequest;
import moe.studio.frontia.ext.ShareLibrary.SoLibPackage;
import moe.studio.frontia.update.RemotePluginInfo;

/**
 * @author kaede
 * @version date 2016/12/9
 */

public class TestOnlineRequest extends PluginRequest<SoLibPackage> {

    @Override
    public List<? extends RemotePluginInfo> requestRemotePluginInfo(Context context)
            throws Exception {

        List<RemotePluginInfo> remotePluginInfos = new ArrayList<>();
        RemotePluginInfo remote1 = new RemotePluginInfo();
        remote1.downloadUrl = "";
        remote1.enable = true;
        remote1.fileSize = 10086;
        remote1.isForceUpdate = false;
        remote1.minAppBuild = 0;
        remote1.pluginId = "moe.studio.plugin.fresco";
        remote1.version = 1;

        RemotePluginInfo remote2 = new RemotePluginInfo();
        remote2.downloadUrl = "";
        remote2.enable = true;
        remote2.fileSize = 10086;
        remote2.isForceUpdate = false;
        remote2.minAppBuild = 0;
        remote2.pluginId = "moe.studio.plugin.fresco";
        remote2.version = 2;

        remotePluginInfos.add(remote1);
        // remotePluginInfos.add(remote2);

        return remotePluginInfos;
    }

    @Nullable
    @Override
    public String getId() {
        return "moe.studio.plugin.fresco";
    }

    @Override
    public String requestPluginId() {
        return "moe.studio.plugin.fresco";
    }

    @Override
    public boolean requestClearLocalPlugins(Context context) {
        return true;
    }

    @Override
    public SoLibPackage createPlugin(String path) {
        return new SoLibPackage(path);
    }
}
