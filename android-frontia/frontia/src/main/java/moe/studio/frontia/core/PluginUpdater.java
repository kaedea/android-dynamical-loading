/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia.core;

import android.support.annotation.NonNull;

/**
 * 插件更新器
 */
public interface PluginUpdater {
    /**
     * 更新插件
     *
     * @param request 插件请求
     * @return 插件请求
     */
    PluginRequest updatePlugin(@NonNull PluginRequest request);
}
