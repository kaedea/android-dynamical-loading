package tv.danmaku.pluinlib;

import android.util.Log;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/11.
 */
public class LogUtil {
	public static void v(String TAG, String msg) {
		Log.v(TAG, msg);
	}

	public static void d(String TAG, String msg) {
		Log.d(TAG, msg);
	}

	public static void i(String TAG, String msg) {
		Log.i(TAG, msg);
	}

	public static void w(String TAG, String msg) {
		Log.w(TAG, msg);
	}

	public static void e(String TAG, String msg) {
		Log.e(TAG, msg);
	}
}
