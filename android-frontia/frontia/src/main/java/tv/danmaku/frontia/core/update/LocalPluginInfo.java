/*
 * Copyright (c) 2015-2016 BiliBili Inc.
 */

package tv.danmaku.frontia.core.update;

import android.support.annotation.NonNull;

/**
 * Created by Kaede on 16/6/3.
 */
public class LocalPluginInfo implements Comparable<LocalPluginInfo> {
    public String pluginId;
    public int version;
    public boolean isValid;

    @Override
    public int compareTo(@NonNull LocalPluginInfo another) {
        // 从大到小排序
        return another.version - this.version;
    }
}
