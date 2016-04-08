/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia;

import android.os.Handler;

import moe.studio.frontia.ext.PluginError;
import moe.studio.frontia.core.Plugin;
import moe.studio.frontia.core.PluginBehavior;
import moe.studio.frontia.core.PluginRequest;
import moe.studio.frontia.ext.PluginCallback;

/**
 * @author kaede
 * @version date 2016/12/1
 */

final class CallbackDelivery extends PluginCallback {

    private final Handler mDelivery;

    @SuppressWarnings("WeakerAccess")
    public CallbackDelivery(Handler delivery) {
        mDelivery = delivery;
    }

    @Override
    public void onCancel(final PluginRequest request) {
        if (getListener(request) == null) {
            return;
        }

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                CallbackDelivery.super.onCancel(request);
            }
        });
    }

    @Override
    public void notifyProgress(final PluginRequest request, final float progress) {
        if (getListener(request) == null) {
            return;
        }

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                CallbackDelivery.super.notifyProgress(request, progress);
            }
        });
    }

    @Override
    public void preUpdate(final PluginRequest request) {
        if (getListener(request) == null) {
            return;
        }

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                CallbackDelivery.super.preUpdate(request);
            }
        });
    }

    @Override
    public void postUpdate(final PluginRequest request) {
        if (getListener(request) == null) {
            return;
        }

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                CallbackDelivery.super.postUpdate(request);
            }
        });
    }

    @Override
    public void preLoad(final PluginRequest request) {
        if (getListener(request) == null) {
            return;
        }

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                CallbackDelivery.super.preLoad(request);
            }
        });
    }

    @Override
    public void postLoad(final PluginRequest request, final Plugin plugin) {
        if (getListener(request) == null) {
            return;
        }

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                CallbackDelivery.super.postLoad(request, plugin);
            }
        });
    }

    @Override
    public void loadSuccess(final PluginRequest request, final Plugin plugin, final PluginBehavior behavior) {
        if (getListener(request) == null) {
            return;
        }

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                CallbackDelivery.super.loadSuccess(request, plugin, behavior);
            }
        });
    }

    @Override
    public void loadFail(final PluginRequest request, final PluginError error) {
        if (getListener(request) == null) {
            return;
        }

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                CallbackDelivery.super.loadFail(request, error);
            }
        });
    }
}
