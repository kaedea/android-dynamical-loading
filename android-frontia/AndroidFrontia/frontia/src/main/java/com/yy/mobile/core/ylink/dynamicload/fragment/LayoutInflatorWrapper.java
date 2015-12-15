package com.yy.mobile.core.ylink.dynamicload.fragment;

import android.content.Context;
import android.view.LayoutInflater;

/**
 * Created by kaede on 2015/12/11.
 */
public class LayoutInflatorWrapper extends LayoutInflater {

	public LayoutInflatorWrapper(Context context) {
		super(context);
	}

	public LayoutInflatorWrapper(LayoutInflater original, Context newContext) {
		super(original, newContext);
	}

	@Override
	public LayoutInflater cloneInContext(Context newContext) {
		return this;
	}
}
