package me.kaede.pluginpackage;

import android.content.Context;
import android.widget.Toast;
import tv.danmaku.pluginbehaiour.IToast;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/11.
 */
public class ToastImpl implements IToast {
	@Override
	public void toast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
}
