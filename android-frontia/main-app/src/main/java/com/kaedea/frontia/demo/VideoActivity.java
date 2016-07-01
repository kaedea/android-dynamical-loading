package com.kaedea.frontia.demo;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import edu.gemini.tinyplayer.R;
import tv.danmaku.frontia.core.BasePluginRequest;
import tv.danmaku.frontia.core.PluginManager;
import tv.danmaku.frontia.core.error.IllegalPluginException;
import tv.danmaku.frontia.core.update.PluginUpdateHandler;
import tv.danmaku.pluginbehaiour.ITencentVideo;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener {
	public static final String TAG = "VideoActivity";

	int index = 0;
	private String[] mVideoId = {
			"t001469z2ma", "y0015vw2o7f",
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
	private ITencentVideo mTencentVideo;
	private LinearLayout mVideoContainer;
	private Button mBtnInitSdk;
	private Button mBtnInitSdkOnline;
	private Button mBtnPlayNext;
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		mVideoContainer = (LinearLayout) this.findViewById(R.id.player);
		mBtnInitSdk = (Button) this.findViewById(R.id.btn_init_sdk);
		mBtnInitSdkOnline = (Button) this.findViewById(R.id.btn_init_sdk_online);
		mBtnPlayNext = (Button) this.findViewById(R.id.btn_play_next);
		setListener();
		mHandler = new Handler(Looper.myLooper());
	}

	protected void setListener() {
		mBtnInitSdk.setOnClickListener(this);
		mBtnInitSdkOnline.setOnClickListener(this);
		mBtnPlayNext.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_init_sdk: // 加载Assets内部插件
				// 静态调试插件
				/*mTencentVideo = Entry.getTencentVideo(this);
				if (mTencentVideo != null) {
					mVideoContainer.addView(mTencentVideo.getVideoView(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
					mBtnPlayNext.setEnabled(true);
				}*/
				mBtnInitSdk.setText("释放插件中…");
				mBtnInitSdk.setEnabled(false);
				mBtnInitSdkOnline.setEnabled(false);
				AssetsVideoRequest assetsVideoRequest = new AssetsVideoRequest();
				assetsVideoRequest.setOnFinishedListener(new BasePluginRequest.OnFinishedListener() {
					@Override
					public void onFinished(Context context, BasePluginRequest pluginRequest) {
						if (pluginRequest.getState() == BasePluginRequest.REQUEST_LOAD_PLUGIN_SUCCESS) {
							try {
								mTencentVideo = (ITencentVideo) pluginRequest.pluginPackage.getPluginBehaviour(VideoActivity.this);
								mVideoContainer.addView(mTencentVideo.getVideoView(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
								mBtnPlayNext.setEnabled(true);
								mBtnInitSdk.setText("加载成功");
							} catch (IllegalPluginException e) {
								e.printStackTrace();
								pluginRequest.switchState(BasePluginRequest.REQUEST_GET_BEHAVIOUR_FAIL);
								pluginRequest.markException(e);
								Toast.makeText(VideoActivity.this, "插件加载失败", Toast.LENGTH_LONG).show();
								mBtnInitSdk.setEnabled(true);
								mBtnInitSdkOnline.setEnabled(true);
							}
						}
					}
				});
				PluginManager.getInstance(this).startRequestTask(assetsVideoRequest);
				break;
			case R.id.btn_init_sdk_online: // 加载在线插件
				mBtnInitSdkOnline.setText("下载插件中…");
				mBtnInitSdk.setEnabled(false);
				mBtnInitSdkOnline.setEnabled(false);
				OnlineVideoRequest request = new OnlineVideoRequest();
				request.updateHandler.setUpdateListener(new PluginUpdateHandler.UpdateListener() {
					@Override
					public void onUpdateProgress(final float progress) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mBtnInitSdkOnline.setText("下载插件中… " + progress * 100 +"%");
							}
						});
					}
				});
				request.setOnFinishedListener(new BasePluginRequest.OnFinishedListener() {
					@Override
					public void onFinished(Context context, BasePluginRequest pluginRequest) {
						if (pluginRequest.getState() == BasePluginRequest.REQUEST_LOAD_PLUGIN_SUCCESS) {
							try {
								mTencentVideo = (ITencentVideo) pluginRequest.pluginPackage.getPluginBehaviour(VideoActivity.this);
								mVideoContainer.addView(mTencentVideo.getVideoView(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
								mBtnPlayNext.setEnabled(true);
								mBtnInitSdkOnline.setText("加载成功");
							} catch (IllegalPluginException e) {
								e.printStackTrace();
								pluginRequest.switchState(BasePluginRequest.REQUEST_GET_BEHAVIOUR_FAIL);
								pluginRequest.markException(e);
								Toast.makeText(VideoActivity.this, "插件加载失败", Toast.LENGTH_LONG).show();
								mBtnInitSdk.setEnabled(true);
								mBtnInitSdkOnline.setEnabled(true);
							}
						}
					}
				});
				PluginManager.getInstance(this).startRequestTask(request);
				break;
			case R.id.btn_play_next:
				if (mTencentVideo != null) {
					mTencentVideo.play(mVideoId[index % mVideoId.length], 2);
					index++;
				}
				break;
		}
	}
}
