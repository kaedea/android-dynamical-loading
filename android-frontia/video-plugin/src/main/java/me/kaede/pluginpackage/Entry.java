package me.kaede.pluginpackage;

import android.app.Activity;
import tv.danmaku.pluginbehaiour.ITencentVideo;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/11.
 */
public class Entry {

	public static ITencentVideo getTencentVideo(Activity activity){
		ITencentVideo iTencentVideo = TencentVideoImpl.getInstance(activity);
		iTencentVideo.onCreate();
		return iTencentVideo;
	}

}
