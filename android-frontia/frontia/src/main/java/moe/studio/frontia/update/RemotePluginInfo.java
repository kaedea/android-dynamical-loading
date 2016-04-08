/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia.update;

import android.support.annotation.NonNull;

/**
 * 查询服务器插件信息时候用的实体类
 */
public class RemotePluginInfo implements Comparable<RemotePluginInfo> {

    public String pluginId;
    public int version;
    public String downloadUrl;
    public long fileSize;
    public boolean enable;
    public boolean isForceUpdate;
    public int minAppBuild;

    @Override
    public int compareTo(@NonNull RemotePluginInfo another) {
        // MAX TO MIN.
        int compare = this.version - another.version;
        return -compare;

    }
}
