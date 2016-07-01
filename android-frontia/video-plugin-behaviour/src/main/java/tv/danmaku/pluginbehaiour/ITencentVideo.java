package tv.danmaku.pluginbehaiour;

import android.content.Context;
import android.view.View;
import tv.danmaku.frontia.bridge.plugin.BaseBehaviour;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 */
public interface ITencentVideo extends BaseBehaviour {
	public void toast(Context context,String msg);
	public void onCreate();
	public View getVideoView();
	public void play(String mVid, int mPlayType);
 }
