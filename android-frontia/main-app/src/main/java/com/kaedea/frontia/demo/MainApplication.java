package com.kaedea.frontia.demo;

import android.app.Application;
import me.kaede.mainapp.Frontia;
import tv.danmaku.frontia.core.PluginManager;

/**
 * Created by Kaede on 16/6/28.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Frontia.init();
        PluginManager.getInstance(this).init();
    }
}
