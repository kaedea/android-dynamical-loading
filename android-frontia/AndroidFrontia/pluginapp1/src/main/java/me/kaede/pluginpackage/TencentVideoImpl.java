package me.kaede.pluginpackage;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;
import com.tencent.qqlive.mediaplayer.api.*;
import com.tencent.qqlive.mediaplayer.view.TVK_PlayerVideoView;
import tv.danmaku.pluginbehaiour.ITencentVideo;
import tv.danmaku.pluinlib.util.LogUtil;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/13.
 */
public class TencentVideoImpl implements ITencentVideo {
	public static final String TAG = "TencentVideoImpl";
	static boolean isInit = false;
	private static final String APPKEY_TINYPLAYER = "DbWzjYdqH9TI2tkBHjuIIfrjJE4mphw+ckykKyP/zofrkJ7omBH3B2csn+ujIl2rL4fQfgy5vS+0P06XE/HghzAwUgq8Lld3lkxQ7qYTtutBLIbqF2xIAyaGnYYGFlg3R3+7d/SBVFLTS9hD5n4vkyQHUQGY9KF7lK5wbL6O0xyqTQXPBwl42dtE8KfXpER30kLycCgEOE/TUxdX99jC0VMjmVWz+d9zkhcEnMop89tE27R8rygXW1t897+JExAmD6zULM1lCG45JYUcmTaW0+nZYrPvpUz8IeQ1Z0XyWblUGo59TSw6Pj0eWxoNK4FN6Ea7yU9c9Bxv7Aba/Rzg4Q==";


	Activity activity;
	//播放器相关
	private TVK_PlayerVideoView mDrawImgSurface = null;
	private TVK_IMediaPlayer mVideoPlayer = null;
	private TVK_UserInfo mUserinfo = null;
	private TVK_PlayerVideoInfo mPlayerinfo = null;

	//播放vid
	private String[] mVideoId = {"t001469z2ma", "y0015vw2o7f",
			"y0015vw2o7f", "j0015lqcpcu",
			"e0015jga0wp", "j0137p2txbs",
			"y0016j6llrg",//付费id,使用这个vid 的时候需要设置清晰度不能为mp4和msd
			"a0012p8g8cr",// 动漫
			"9Wzab3vNJ8b", // 试看
			"q0013te787c",
			"t0016jjsrri",   //横有黑边
			"y0012j6s11e",  // 竖有黑边，西游降魔
			"100003600", // 直播 深圳卫视
			"100002500",
			"r0016w5wxcw"
	};
	//UI 相关
	protected LayoutInflater mInflater;

	private int mLastWidth = 0;
	private int mLastHeigth = 0;

	public TencentVideoImpl(Activity activity) {
		this.activity = activity;
		if (!isInit) {
			LogUtil.i(TAG, "initSdk");
			TVK_SDKMgr.setDebugEnable(true);
			TVK_SDKMgr.initSdk(activity, APPKEY_TINYPLAYER, ""); //测试用AppKey：1~100
			TVK_IDlnaMgr dlnaMgr = TVK_DlnaFactory.getDlnaInstance();
			if (null != dlnaMgr) {
				dlnaMgr.search(false);
			}
			isInit = true;
		}
	}

	@Override
	public void onCreate() {
		LogUtil.i(TAG, "onCreate");
		// 创建播放器
		mDrawImgSurface = new TVK_PlayerVideoView(activity);
		mDrawImgSurface.setBackgroundColor(Color.BLACK);
		mVideoPlayer = TVK_MediaPlayerFactory.createMediaPlayer(activity, mDrawImgSurface);

		// 回调例子
		initListenersDemo();
	}

	@Override
	public View getVideoView() {
		return mDrawImgSurface;
	}

	@Override
	public void play(String mVid, int mPlayType) {
		//用qq号登陆的请务必写入qq号码和cookie
		mUserinfo = new TVK_UserInfo("", "");
		mPlayerinfo = new TVK_PlayerVideoInfo(mPlayType, mVid, "");
		mVideoPlayer.openMediaPlayer(activity, mUserinfo, mPlayerinfo, "", 0, 0);
	}


	//Listeners
	private void initListenersDemo() {

		mVideoPlayer.setOnAdClickedListener(new TVK_IMediaPlayer.OnAdClickedListener() {

			@Override
			public void onLandingViewClosed(TVK_IMediaPlayer mpImpl) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAdWarnerTipClick(TVK_IMediaPlayer mpImpl) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAdSkipClick(TVK_IMediaPlayer mpImpl,
			                          boolean isCopyRightForWarner) {
				Toast.makeText(activity.getApplicationContext(), "播放广告时点击跳过\nOnAdSkipClickListener", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onAdReturnClick(TVK_IMediaPlayer mpImpl) {
				Toast.makeText(activity.getApplicationContext(), "播放广告时点击返回按钮\nOnAdReturnClickListener", Toast.LENGTH_SHORT).show();
				activity.finish();
			}

			@Override
			public void onAdFullScreenClick(TVK_IMediaPlayer mpImpl) {
				Toast.makeText(activity.getApplicationContext(), "播放广告时点击全屏按钮，进入全屏播放\nOnAdFullScreenClickListener", Toast.LENGTH_SHORT).show();

				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);

				ViewGroup.LayoutParams params = mDrawImgSurface.getLayoutParams();
//				mLastPlayWindowParams = params;
				mLastHeigth = params.height;
				mLastWidth = params.width;
				params.width = ViewGroup.LayoutParams.MATCH_PARENT;
				params.height = ViewGroup.LayoutParams.MATCH_PARENT;
				mDrawImgSurface.setLayoutParams(params);
			}

			@Override
			public void onAdExitFullScreenClick(TVK_IMediaPlayer mpImpl) {
				// TODO Auto-generated method stub
				final WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

				attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
				activity.getWindow().setAttributes(attrs);
				activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
				ViewGroup.LayoutParams params = mDrawImgSurface.getLayoutParams();
				params.height = mLastHeigth;
				params.width = mLastWidth;
				mDrawImgSurface.setLayoutParams(params);
			}
		});

		//使用自带的UI时必须设置，否则无法返回
		mVideoPlayer.setOnControllerClickListener(new TVK_IMediaPlayer.OnControllerClickListener() {
			@Override
			public void onCacheClick(TVK_PlayerVideoInfo mInfo) {
			}

			@Override
			public void onBackClick(TVK_PlayerVideoInfo mInfo) {
				activity.finish();
			}

			@Override
			public void onAttationClick(TVK_PlayerVideoInfo mInfo) {
			}

			@Override
			public void onBackOnFullScreenClick(TVK_PlayerVideoInfo mInfo) {
			}

			@Override
			public void onReopenClick(TVK_NetVideoInfo.RecommadInfo mInfo) {
				// TODO Auto-generated method stub

			}
		});

		mVideoPlayer.setOnVideoPreparedListener(new TVK_IMediaPlayer.OnVideoPreparedListener() {

			@Override
			public void onVideoPrepared(TVK_IMediaPlayer mpImpl) {
				Toast.makeText(activity.getApplicationContext(), "视频加载完成的通知，调用Start即可开始播放\nOnVideoPreparedListener", Toast.LENGTH_SHORT).show();
				mVideoPlayer.start();
				mVideoPlayer.seekTo((int) (mVideoPlayer.getDuration() - 10000));
			}
		});
		mVideoPlayer.setOnCompletionListener(new TVK_IMediaPlayer.OnCompletionListener() {

			@Override
			public void onCompletion(TVK_IMediaPlayer mpImpl) {
				Toast.makeText(activity.getApplicationContext(), "视频接受播放的回调，收到这个消息后不需要调用Stop来关闭视频\nOnCompletionListener", Toast.LENGTH_SHORT).show();
			}
		});

		mVideoPlayer.setOnPreAdListener(new TVK_IMediaPlayer.OnPreAdListener() {

			@Override
			public void onPreAdPreparing(TVK_IMediaPlayer mpImpl) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPreAdPrepared(TVK_IMediaPlayer mpImpl, long adDuration) {
				// TODO Auto-generated method stub
				mVideoPlayer.start();
			}
		});
		mVideoPlayer.setOnErrorListener(new TVK_IMediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(TVK_IMediaPlayer mpImpl, int model, int what,
			                       int extra, String detailInfo, Object Info) {
				Toast.makeText(activity.getApplicationContext(), "视频播放失败(" + model + ", " + what + ")", Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		mVideoPlayer.setOnInfoListener(new TVK_IMediaPlayer.OnInfoListener() {

			@Override
			public boolean onInfo(TVK_IMediaPlayer mpImpl, int what, Object extra) {

				switch (what) {
					case TVK_PlayerMsg.PLAYER_INFO_START_BUFFERING:
						Toast.makeText(activity.getApplicationContext(), "视频开始缓冲", Toast.LENGTH_SHORT).show();
						break;
					case TVK_PlayerMsg.PLAYER_INFO_ENDOF_BUFFERING:
						Toast.makeText(activity.getApplicationContext(), "视频缓冲结束", Toast.LENGTH_SHORT).show();
						break;
					default:
						break;
				}
				return false;
			}
		});
	}
}
