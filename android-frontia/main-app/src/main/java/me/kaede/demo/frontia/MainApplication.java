package me.kaede.demo.frontia;

import android.app.Application;
import me.kaede.frontia.core.PluginManager;

/**
 * Created by Kaede on 16/6/28.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FrontiaApi.init();
        PluginManager.getInstance(this).init();
    }
}
