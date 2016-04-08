/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package com.kaedea.frontia.demo;

import android.app.Application;

/**
 * Created by Kaede on 16/6/28.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PluginHelper.init(this);
    }
}
