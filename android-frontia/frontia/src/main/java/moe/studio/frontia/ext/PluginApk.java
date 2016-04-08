/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia.ext;

import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import java.util.Map;
import java.util.Set;

import dalvik.system.DexClassLoader;

/**
 * @author kaede
 * @version date 2016/12/2
 */

public class PluginApk {

    public String application;
    public String packageName;
    public String versionCode;
    public String versionName;

    public PackageInfo packageInfo;
    public Resources resources;
    public AssetManager assetManager;
    public DexClassLoader classLoader;
    public Map<String, Integer> dependencies;
    public Set<String> ignores;

    @Override
    public String toString() {
        return "PluginApk{" +
                "application='" + application + '\'' +
                ", packageName='" + packageName + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", versionName='" + versionName + '\'' +
                ", packageInfo=" + packageInfo +
                ", resources=" + resources +
                ", assetManager=" + assetManager +
                ", classLoader=" + classLoader +
                '}';
    }

    public void set(@NonNull PluginApk apk) {
        this.application  = this.application  == null ? apk.application  : this.application ;
        this.packageName  = this.packageName  == null ? apk.packageName  : this.packageName ;
        this.versionCode  = this.versionCode  == null ? apk.versionCode  : this.versionCode ;
        this.versionName  = this.versionName  == null ? apk.versionName  : this.versionName ;
        this.packageInfo  = this.packageInfo  == null ? apk.packageInfo  : this.packageInfo ;
        this.resources    = this.resources    == null ? apk.resources    : this.resources   ;
        this.assetManager = this.assetManager == null ? apk.assetManager : this.assetManager;
        this.classLoader  = this.classLoader  == null ? apk.classLoader  : this.classLoader ;
        this.dependencies = this.dependencies == null ? apk.dependencies : this.dependencies;
        this.ignores      = this.ignores      == null ? apk.ignores      : this.ignores     ;
    }
}
