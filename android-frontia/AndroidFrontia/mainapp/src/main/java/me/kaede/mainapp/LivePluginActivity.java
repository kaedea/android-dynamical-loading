package me.kaede.mainapp;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.yy.mobile.core.ylink.dynamicload.core.DLIntent;
import com.yy.mobile.core.ylink.dynamicload.core.DLPluginManager;
import com.yy.mobile.core.ylink.utils.DLUtils;

import java.io.File;

public class LivePluginActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_live_plugin);
		init();
	}

	private void init() {

		File tempFile = new File( Environment.getExternalStorageDirectory() + File.separator +"pluginapp1-debug.apk");

		if (!tempFile.exists()) {
			return;
		}

		PluginItem pluginItem = new PluginItem();
		pluginItem.pluginPath = tempFile.getAbsolutePath();
		pluginItem.packageInfo = DLUtils.getPackageInfo(this, pluginItem.pluginPath);
		if (pluginItem.packageInfo.activities != null && pluginItem.packageInfo.activities.length > 0) {
			pluginItem.launcherActivityName = pluginItem.packageInfo.activities[0].name;
		}
		if (pluginItem.packageInfo.services != null && pluginItem.packageInfo.services.length > 0) {
			pluginItem.launcherServiceName = pluginItem.packageInfo.services[0].name;
		}
		DLPluginManager.getInstance(this).loadApk(pluginItem.pluginPath);

		//ChannelAppID.getInstance().updateCurrentChannelAppIdInfo(itemData.sid, itemData.ssid);
		DLIntent intent = new DLIntent(pluginItem.packageInfo.packageName, "me.kaede.ylinkplugindemo.PluginManager");
        /*intent.putExtra("sid",(int)itemData.sid);
        intent.putExtra("subsid",(int)itemData.ssid);*/
		Fragment pluginFragment = DLPluginManager.getInstance(this).getPluginFragment(this,intent);
		if (pluginFragment!=null){
			getSupportFragmentManager().beginTransaction().add(R.id.container, pluginFragment).commitAllowingStateLoss();
		}

	}

	public static class PluginItem {
		public PackageInfo packageInfo;
		public String pluginPath;
		public String launcherActivityName;
		public String launcherServiceName;

		public PluginItem() {
		}
	}

}
