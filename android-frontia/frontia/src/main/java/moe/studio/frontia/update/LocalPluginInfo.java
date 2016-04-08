/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia.update;

import android.support.annotation.NonNull;

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
