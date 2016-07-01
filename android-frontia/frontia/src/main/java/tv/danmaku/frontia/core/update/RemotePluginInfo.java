/*
 * Copyright (c) 2015-2016 BiliBili Inc.
 */

package tv.danmaku.frontia.core.update;

import android.support.annotation.NonNull;

/**
 * 查询服务器插件信息时候用的实体类
 * Created by Kaede on 16/5/3.
 */
public class RemotePluginInfo implements Comparable<RemotePluginInfo> {
    public String pluginId;
    public int version;
    public String downloadLink;
    public long fileSize;
    public boolean enable;
    public boolean isForceUpdate;
    public int minAppBuild;

    @Override
    public int compareTo(@NonNull RemotePluginInfo another) {
        // 从大到小排序
        int compare = this.version - another.version;
        return -compare;

    }
}
