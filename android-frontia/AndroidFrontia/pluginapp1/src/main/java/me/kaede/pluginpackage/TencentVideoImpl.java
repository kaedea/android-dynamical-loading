package me.kaede.pluginpackage;

import android.content.Context;
import android.view.View;
import com.tencent.qqlive.mediaplayer.api.TVK_DlnaFactory;
import com.tencent.qqlive.mediaplayer.api.TVK_IDlnaMgr;
import com.tencent.qqlive.mediaplayer.api.TVK_SDKMgr;
import tv.danmaku.pluginbehaiour.ITencentVideo;
import tv.danmaku.pluinlib.LogUtil;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/13.
 */
public class TencentVideoImpl implements ITencentVideo {
	public static final String TAG = "TencentVideoImpl";
	boolean isInit = false;
	private static final String APPKEY_TINYPLAYER = "DbWzjYdqH9TI2tkBHjuIIfrjJE4mphw+ckykKyP/zofrkJ7omBH3B2csn+ujIl2rL4fQfgy5vS+0P06XE/HghzAwUgq8Lld3lkxQ7qYTtutBLIbqF2xIAyaGnYYGFlg3R3+7d/SBVFLTS9hD5n4vkyQHUQGY9KF7lK5wbL6O0xyqTQXPBwl42dtE8KfXpER30kLycCgEOE/TUxdX99jC0VMjmVWz+d9zkhcEnMop89tE27R8rygXW1t897+JExAmD6zULM1lCG45JYUcmTaW0+nZYrPvpUz8IeQ1Z0XyWblUGo59TSw6Pj0eWxoNK4FN6Ea7yU9c9Bxv7Aba/Rzg4Q==";

	@Override
	public void onCreate(Context context) {
		LogUtil.i(
				TAG, "onCreate");
		if (!isInit) {
			TVK_SDKMgr.setDebugEnable(true);
			TVK_SDKMgr.initSdk(context, APPKEY_TINYPLAYER, ""); //测试用AppKey：1~100
			TVK_IDlnaMgr dlnaMgr = TVK_DlnaFactory.getDlnaInstance();
			if (null != dlnaMgr) {
				dlnaMgr.search(false);
			}
			isInit = true;
		}
	}

	@Override
	public View getVideoView() {
		return null;
	}

	@Override
	public void play(String mVid, int mPlayType) {

	}
}
