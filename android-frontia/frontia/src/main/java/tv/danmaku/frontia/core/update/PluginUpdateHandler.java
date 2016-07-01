/*
 * Copyright (c) 2015-2016 BiliBili Inc.
 */

package tv.danmaku.frontia.core.update;

import java.lang.ref.WeakReference;

/**
 * 插件更新控制器：
 * 1. 控制更新过程，如取消更新；
 * 2. 更新过程回调，如通知更新进度；
 * Created by Kaede on 16/6/3.
 */
public class PluginUpdateHandler {
    public static final String TAG = "PluginUpdateHandler";

    private boolean mIsCanceled;
    private float progress;
    WeakReference<UpdateListener> mUpdateListenerWeak;

    public void cancel() {
        mIsCanceled = true;
    }

    public boolean isCanceled() {
        // 调用了PluginUpdateHandler#cancel，或者调用了点前线程的Thread#interrupt；
        return mIsCanceled || Thread.currentThread().isInterrupted();
    }

    public void notifyProgress(float progress) {
        this.progress = progress;
        onNotifyProgress(progress);
    }

    public void onNotifyProgress(float progress) {
        if (mUpdateListenerWeak != null) {
            UpdateListener updateListener = mUpdateListenerWeak.get();
            if (updateListener != null) {
                updateListener.onUpdateProgress(progress);
            }
        }
    }

    public void setUpdateListener(UpdateListener updateListener) {
        mUpdateListenerWeak = new WeakReference<>(updateListener);
    }

    public float getProgress() {
        return progress;
    }

    public interface UpdateListener {
        void onUpdateProgress(float progress);
    }
}
