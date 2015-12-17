package me.kaede.ylinkplugindemo;

import android.support.v4.app.Fragment;

/**
 * Created by estel on 2015/12/11.
 */
public class PluginManager {
	public static Fragment getComponent(){
		//MainFragment fragment= MainFragment.newInstance();
		return MainFragment2.newInstance();
	}
}
