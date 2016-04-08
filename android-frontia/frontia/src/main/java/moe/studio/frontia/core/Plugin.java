/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.io.File;
import java.util.Set;

import moe.studio.frontia.ext.PluginApk;
import moe.studio.frontia.ext.PluginError;
import moe.studio.frontia.ext.PluginSetting;

/**
 * Plugin entity class.
 * 君の望んでいたすべてはここにいる。
 */
@SuppressWarnings("WeakerAccess")
public abstract class Plugin<B extends PluginBehavior> {

    public static final String TAG = "plugin.abs.package";

    protected File mOptDexDir;
    protected File mSoLibDir;
    protected String mInstallPath;
    protected PluginManager mManager;
    protected PluginSetting mSetting;
    protected B mBehavior;

    private boolean mIsLoaded;
    private final byte[] mLock;
    private final String mApkPath;
    protected final PluginApk mPackage;

    public Plugin(String apkPath) {
        mLock = new byte[]{};
        mIsLoaded = false;
        mApkPath = apkPath;
        mInstallPath = apkPath;
        mPackage = new PluginApk();
        mSetting = new PluginSetting.Builder().build();
    }

    @Override
    public String toString() {
        return "Plugin{" +
                "Apk = " + (mPackage == null ? "null" : mPackage) +
                ", ApkPath = '" + mApkPath + '\'' +
                '}';
    }

    public Plugin attach(@NonNull PluginManager manager) {
        mManager = manager;
        mSetting = manager.getSetting();
        return this;
    }

    /**
     * 插件是否已经加载
     */
    public final boolean isLoaded() {
        if (mIsLoaded) {
            return true;
        }
        synchronized (mLock) {
            return mIsLoaded;
        }
    }

    /**
     * 设置插件已经加载
     */
    public final void setLoaded() {
        if (mIsLoaded) {
            return;
        }
        synchronized (mLock) {
            mIsLoaded = true;
        }
    }

    @Nullable
    public final B getBehavior() {
        return mBehavior;
    }

    public final void setBehavior(B behavior) {
        mBehavior = behavior;
    }

    /**
     * 获取插件外部路径, 如果没有, 返回Null
     */
    public String getApkPath() {
        return mApkPath;
    }

    /**
     * 获取插件安装路径(内部), 如果没有, 返回Null
     */
    public String getInstallPath() {
        return mInstallPath;
    }

    /**
     * 设置插件安装路径
     */
    public void setInstallPath(String installPath) {
        mInstallPath = installPath;
    }

    @NonNull
    public PluginApk getPackage() {
        return mPackage;
    }

    /**
     * 设置插件的{@link PluginApk}
     */
    public void setPackage(PluginApk packageInfo) {
        mPackage.set(packageInfo);
    }

    public void setIgnoreDepencies(Set<String> ignores) {
        mPackage.ignores = ignores;
    }

    /**
     * 获取插件so库安装目录
     */
    @Nullable
    public File getSoLibDir() {
        return mSoLibDir;
    }

    /**
     * 获取插件OptDex存放路径
     */
    @Nullable
    public File getOptimizedDexDir() {
        return mOptDexDir;
    }

    /**
     * 加载指定路径上的插件，具体实现根据需求的不同由不同的继承类完成
     */
    public abstract Plugin loadPlugin(Context context, String packagePath) throws PluginError.LoadError;

    /**
     * 获取插件的行为接口，用于控制插件
     * <p>
     * 默认情况下, Frontia通过{@link PluginLoader#createBehavior(Plugin)}自动创建插件的行为接口。用户
     * 也可以通过覆盖此方法来由自己创建行为接口。
     */
    @WorkerThread
    public B createBehavior(Context context) throws Exception {
        return null;
    }
}
