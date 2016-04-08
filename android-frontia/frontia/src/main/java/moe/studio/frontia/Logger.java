/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia;

import android.util.Log;

import moe.studio.frontia.BuildConfig;

class Logger {

    static final boolean DEBUG = BuildConfig.DEBUG;

    static void v(String TAG, String msg) {
        if (!DEBUG) return;
        Log.v(TAG, msg);
    }

    static void d(String TAG, String msg) {
        if (!DEBUG) return;
        Log.d(TAG, msg);
    }

    static void i(String TAG, String msg) {
        if (!DEBUG) return;
        Log.i(TAG, msg);
    }

    static void w(String TAG, String msg) {
        Log.w(TAG, msg);
    }

    static void w(String TAG, Throwable throwable) {
        Log.w(TAG, throwable);
    }
}
