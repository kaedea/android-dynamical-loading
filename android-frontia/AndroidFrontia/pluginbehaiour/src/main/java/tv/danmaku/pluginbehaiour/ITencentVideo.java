package tv.danmaku.pluginbehaiour;

import android.content.Context;
import android.view.View;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 */
public interface ITencentVideo {
	public void onCreate(Context context);
	public View getVideoView();
	public void play(String mVid, int mPlayType);
 }
