/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia.ext;

import android.support.annotation.Nullable;

import moe.studio.frontia.core.Plugin;
import moe.studio.frontia.core.PluginBehavior;
import moe.studio.frontia.core.PluginRequest;

/**
 * 回调监听器，参考 {@linkplain PluginListener};
 */
@SuppressWarnings("unchecked")
public class PluginCallback {

    protected static final String TAG = "plugin.callback";

    @SuppressWarnings("WeakerAccess")
    public PluginCallback() {
    }


    @Nullable
    protected PluginListener getListener(PluginRequest request) {
        return request.getListener();
    }

    public void onCancel(PluginRequest request) {
        PluginListener listener = getListener(request);
        if (listener != null) {
            listener.onCanceled(request);
        }
    }

    public void notifyProgress(PluginRequest request, float progress) {
        PluginListener listener = getListener(request);
        if (listener != null) {
            listener.onProgress(request, progress);
        }
    }

    public void preUpdate(PluginRequest request) {
        PluginListener listener = getListener(request);
        if (listener != null) {
            listener.onPreUpdate(request);
        }
    }

    public void postUpdate(PluginRequest request) {
        PluginListener listener = getListener(request);
        if (listener != null) {
            listener.onPostUpdate(request);
        }
    }

    public void preLoad(PluginRequest request) {
        PluginListener listener = getListener(request);

        if (listener != null) {
            listener.onPreLoad(request);
        }
    }

    public void postLoad(PluginRequest request, Plugin plugin) {
        PluginListener listener = getListener(request);

        if (listener != null) {
            listener.onPostLoad(request, plugin);
        }
    }

    public void loadSuccess(PluginRequest request, Plugin plugin, PluginBehavior behavior) {
        PluginListener listener = getListener(request);

        if (listener != null) {
            listener.onGetBehavior(request, plugin, behavior);
        }


    }

    public void loadFail(PluginRequest request, PluginError error) {
        PluginListener listener = getListener(request);

        if (listener != null) {
            listener.onFail(request, error);
        }

    }

}
