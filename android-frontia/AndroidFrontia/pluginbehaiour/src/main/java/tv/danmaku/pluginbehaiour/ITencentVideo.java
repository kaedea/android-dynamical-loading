package tv.danmaku.pluginbehaiour;

import android.view.View;
import tv.danmaku.pluinlib.bridge.plugin.BaseBehaviour;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 */
public interface ITencentVideo extends BaseBehaviour {
	public void onCreate();
	public View getVideoView();
	public void play(String mVid, int mPlayType);
 }
