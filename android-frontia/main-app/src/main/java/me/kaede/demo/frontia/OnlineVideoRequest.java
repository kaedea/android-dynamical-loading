package me.kaede.demo.frontia;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import tv.danmaku.frontia.core.BasePluginPackage;
import tv.danmaku.frontia.core.BasePluginRequest;
import tv.danmaku.frontia.core.error.UpdatePluginException;
import tv.danmaku.frontia.core.update.RemotePluginInfo;
import tv.danmaku.pluginbehaiour.TencentVideoPackage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Kaede on 16/6/28.
 */
public class OnlineVideoRequest extends BasePluginRequest {

    public static final String PLUGIN_ID = "me.kaede.videoplugin";

    @Override
    public void getRemotePluginInfo(Context context, @NonNull BasePluginRequest pluginRequest) throws UpdatePluginException {
        pluginRequest.isAssetsPlugin = false;
        try {
            JSONObject jsonObject = loadJSONFromAsset(context);
            if (jsonObject.optInt("code") == 0){
                JSONObject data = jsonObject.optJSONObject("data");
                if (data != null) {
                    String id = data.optString("id");
                    pluginRequest.pluginId = id;
                    pluginRequest.needClearLocalPlugin = data.optInt("clear") == 1;
                    JSONArray versions = data.optJSONArray("versions");
                    if (versions != null && versions.length() > 0) {
                        pluginRequest.remotePluginInfoList = new ArrayList<>();
                        for (int i = 0; i < versions.length(); i++) {
                            JSONObject item = versions.optJSONObject(i);
                            RemotePluginInfo remotePluginInfo = new RemotePluginInfo();
                            remotePluginInfo.fileSize = item.optLong("size");
                            remotePluginInfo.minAppBuild = item.optInt("nub_build");
                            remotePluginInfo.enable = item.optInt("enable") == 1;
                            remotePluginInfo.isForceUpdate = item.optInt("force") == 1;
                            remotePluginInfo.downloadLink = item.optString("url");
                            remotePluginInfo.pluginId = id;
                            remotePluginInfo.version = item.optInt("ver_code");
                            pluginRequest.remotePluginInfoList.add(remotePluginInfo);
                        }
                    }
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            throw new UpdatePluginException("get online plugin info error", e);
        }
    }

    // 模拟获取服务器插件信息
    private JSONObject loadJSONFromAsset(Context context) throws IOException, JSONException {
        JSONObject json = null;
        InputStream is = context.getAssets().open("onlineplugininfo.json");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        String string = new String(buffer, "UTF-8");
        json = new JSONObject(string);
        return json;
    }

    @NonNull
    @Override
    public String getLocalPluginId() {
        return TextUtils.isEmpty(pluginId) ? PLUGIN_ID : pluginId;
    }

    @Override
    public BasePluginPackage createPluginPackage(String pluginPath) {
        return new TencentVideoPackage(pluginPath);
    }

    @Override
    public void postLoadPlugin(Context context, BasePluginRequest pluginRequest) {

    }
}
