package me.kaede.pluginpackage;

import android.app.Activity;
import tv.danmaku.pluginbehaiour.ITencentVideo;
import tv.danmaku.pluginbehaiour.IToast;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/11.
 */
public class Entry {
	public static void init(){

	}

	public static IToast getToast(){
		return new ToastImpl();
	}

	public static ITencentVideo getTencentVideo(Activity activity){
		ITencentVideo iTencentVideo = new TencentVideoImpl(activity);
		iTencentVideo.onCreate();
		return iTencentVideo;
	}

}
