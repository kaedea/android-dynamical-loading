package com.yy.mobile.ylink.dynamicload.fragment;

import android.support.v4.app.Fragment;

/**
 * Created by kaede on 2015/12/11.
 */
public class DLBasePluginFragment extends Fragment {
	protected PluginContextWrapper pluginContext; //Plugin should use this context to get their res.

	public DLBasePluginFragment attachPluginContext(PluginContextWrapper context){
		this.pluginContext = context;
		return this;
	}


}
