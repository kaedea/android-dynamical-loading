package me.kaede.mainapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import edu.gemini.tinyplayer.R;
import tv.danmaku.pluginbehaiour.ITencentVideo;
import tv.danmaku.pluginbehaiour.IToast;
import tv.danmaku.pluinlib.core.BasePluginManager;
import tv.danmaku.pluinlib.core.BasePluginPackage;
import tv.danmaku.pluinlib.util.LogUtil;

import java.io.File;
import java.lang.reflect.Method;

public class MainActivity extends Activity {

	public static final String TAG = "MainActivity";
	private BasePluginPackage basePluginPackage;
	private BasePluginManager basePluginHandler;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		YLink.init();
	}

	public void onClickLivePlugin(View view) {
		Intent intent = new Intent(this, LivePluginActivity.class);
		startActivity(intent);
	}

	public void onLoadSimplePlugin(View view) {
		// 测试 SimplePluginPackage
		File tempFile = new File(Environment.getExternalStorageDirectory() + File.separator + "pluginapp1-debug.apk");

		if (!tempFile.exists()) {
			LogUtil.w(TAG, "插件不存在");
			Toast.makeText(this, "插件不存在", Toast.LENGTH_LONG).show();
			return;
		}
		basePluginHandler = new BasePluginManager(this);
		/*basePluginPackage = basePluginHandler.initPlugin(tempFile.getAbsolutePath());
		if (basePluginPackage == null) {
			Toast.makeText(this, "加载插件失败", Toast.LENGTH_LONG).show();
		}*/

		basePluginHandler.aysncInitPlugin(tempFile.getAbsolutePath(), new BasePluginManager.OnLoadPluginListener() {
			@Override
			public void onFinished(String pluginPath, BasePluginPackage basePluginPackage) {
				MainActivity.this.basePluginPackage = basePluginPackage;
				Toast.makeText(MainActivity.this, "加载成功", Toast.LENGTH_LONG).show();
			}
		});

	}

	public void onCallMethod(View view) {
		Class clazz = basePluginHandler.loadPluginClass(basePluginPackage, "me.kaede.pluginpackage.Entry");
		try {
			Method method = clazz.getMethod("getToast");
			IToast iToast = (IToast) method.invoke(null);
			iToast.toast(this, "dude!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onPlay(View view) {
		Class clazz = basePluginHandler.loadPluginClass(basePluginPackage, "me.kaede.pluginpackage.Entry");
		try {
			Method method = clazz.getMethod("getTencentVideo", Activity.class);
			ITencentVideo iTencentVideo = (ITencentVideo) method.invoke(null, this);
			LinearLayout linearLayout = (LinearLayout) this.findViewById(R.id.player);
			linearLayout.addView(iTencentVideo.getVideoView(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			iTencentVideo.play("a0012p8g8cr", 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
