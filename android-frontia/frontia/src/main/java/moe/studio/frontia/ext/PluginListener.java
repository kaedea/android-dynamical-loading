/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia.ext;

import moe.studio.frontia.core.Plugin;
import moe.studio.frontia.core.PluginBehavior;
import moe.studio.frontia.core.PluginRequest;

/**
 * 请求任务监听器
 */
public interface PluginListener
        <B extends PluginBehavior, P extends Plugin<B>, R extends PluginRequest<P>> {

    /**
     * 开始更新插件回调
     *
     */
    void onPreUpdate(R request);

    /**
     * 插件更新完成
     */
    void onPostUpdate(R request);

    /**
     * 任务被取消
     */
    void onCanceled(R request);

    /**
     * 任务进度回调
     */
    void onProgress(R request, float progress);

    /**
     * 开始加载插件
     */
    void onPreLoad(R request);

    /**
     * 插件加载完成
     */
    void onPostLoad(R request, P plugin);

    /**
     * 创建 {@link PluginBehavior}
     */
    void onGetBehavior(R request, P plugin, B behavior);

    void onFail(R request, PluginError error);


    /**
     * 请求任务监听器的空实现
     */
    class ListenerImpl<B extends PluginBehavior, P extends Plugin<B>, R extends PluginRequest<P>>
            implements PluginListener<B, P, R> {

        @Override
        public void onPreUpdate(R request) {

        }

        @Override
        public void onPostUpdate(R request) {

        }

        @Override
        public void onCanceled(R request) {

        }

        @Override
        public void onProgress(R request, float progress) {

        }

        @Override
        public void onPreLoad(R request) {

        }

        @Override
        public void onPostLoad(R request, P plugin) {

        }

        @Override
        public void onGetBehavior(R request, P plugin, B behavior) {

        }

        @Override
        public void onFail(R request, PluginError error) {

        }
    }
}
