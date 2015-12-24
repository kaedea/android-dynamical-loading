package me.kaede.mainapp;

import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import com.yy.mobile.ylink.dynamicload.core.DLIntent;
import com.yy.mobile.ylink.dynamicload.core.DLPluginManager;
import com.yy.mobile.ylink.dynamicload.core.DLPluginPackage;
import com.yy.mobile.ylink.utils.DLUtils;

import java.io.File;

public class LivePluginActivity2 extends FragmentActivity {

	private DLPluginPackage pluginPackage;

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

		LivePluginActivity.PluginItem pluginItem = new LivePluginActivity.PluginItem();
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
			pluginPackage = DLPluginManager.getInstance(this).getCurrentPluginPackage();
			getSupportFragmentManager().beginTransaction().add(R.id.container, pluginFragment).commitAllowingStateLoss();
		}

	}

	@Override
	public Resources getResources() {
		if (pluginPackage == null) {
			return super.getResources();
		}
		return pluginPackage.resources;
	}

	@Override
	public AssetManager getAssets() {
		if (pluginPackage == null) {
			return super.getAssets();
		}
		return pluginPackage.assetManager;
	}

	@Override
	public ClassLoader getClassLoader() {
		if (pluginPackage == null) {
			return super.getClassLoader();
		}
		return pluginPackage.classLoader;
	}


	LayoutInflater mInflater;
	@Override
	public Object getSystemService(String name) {
		if (LAYOUT_INFLATER_SERVICE.equals(name)) {
			if (mInflater == null) {
				mInflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
			}
			return mInflater;
		}
		return getBaseContext().getSystemService(name);
	}


}
