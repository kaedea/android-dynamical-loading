package me.kaede.demo.frontia;

import android.content.Context;
import android.support.annotation.NonNull;
import me.kaede.frontia.core.BasePluginPackage;
import me.kaede.frontia.core.BasePluginRequest;
import me.kaede.frontia.core.error.UpdatePluginException;
import me.kaede.pluginbehaviour.videosdk.TencentVideoPackage;

/**
 * Created by Kaede on 16/6/28.
 */
public class AssetsVideoRequest extends BasePluginRequest {
    @Override
    public void getRemotePluginInfo(Context context, @NonNull BasePluginRequest pluginRequest) throws UpdatePluginException {
        pluginRequest.pluginId = "me.kaede.videoplugin";
        pluginRequest.isAssetsPlugin = true;
        pluginRequest.assetsPath = "videoplugin.apk";
        pluginRequest.assetsPluginVersion = 1;
    }

    @Override
    public BasePluginPackage createPluginPackage(String pluginPath) {
        return new TencentVideoPackage(pluginPath);
    }

    @Override
    public void postLoadPlugin(Context context, BasePluginRequest pluginRequest) {

    }
}
