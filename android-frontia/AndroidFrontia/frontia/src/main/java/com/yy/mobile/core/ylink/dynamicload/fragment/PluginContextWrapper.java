package com.yy.mobile.core.ylink.dynamicload.fragment;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;
import com.yy.mobile.core.ylink.dynamicload.core.DLPluginPackage;

/**
 * Created by kaede on 2015/12/11.
 */
public class PluginContextWrapper extends ContextWrapper {
	DLPluginPackage pluginPackage;

	public PluginContextWrapper(Context base) {
		super(base);
	}

	public PluginContextWrapper attatchPluginPackage(DLPluginPackage pluginPackage){
		this.pluginPackage = pluginPackage;
		return this;
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
}
