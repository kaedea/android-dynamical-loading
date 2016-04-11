package me.kaede.ylinkplugindemo;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by estel on 2015/12/11.
 */
public class PluginManager {
	public static Fragment getComponent(){
		//MainFragment fragment= MainFragment.newInstance();
		return MainFragment.newInstance();
	}

	public static void toast(Context context,String msg){
		Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
	}
}
