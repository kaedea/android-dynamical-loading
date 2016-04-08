/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia.ext;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import moe.studio.frontia.BuildConfig;

/**
 * 插件配置类
 */
@SuppressWarnings("WeakerAccess")
public final class PluginSetting {

    private final int mMaxRetry;
    private final String mRootDir;
    private final String mOptDexDir;
    private final String mSoLibDir;
    private final String mTempSoLibDir;
    private final String mTempFileSuf;
    private final String mPluginName;
    private final String mCustomSignature;
    private final boolean mIsDebugMode;
    private final boolean mIgnoreInstalledPlugin;

    private PluginSetting(boolean isDebugMode,
                          boolean ignoreInstalledPlugin,
                          String customSignature, String rootDir,
                          String optDexDir,
                          String soLibDir,
                          String tempSoLibDir,
                          String tempFileSuf,
                          String pluginName,
                          int maxRetry) {
        mIsDebugMode = isDebugMode;
        mIgnoreInstalledPlugin = ignoreInstalledPlugin;
        mCustomSignature = customSignature;
        mRootDir = rootDir;
        mOptDexDir = optDexDir;
        mSoLibDir = soLibDir;
        mTempSoLibDir = tempSoLibDir;
        mTempFileSuf = tempFileSuf;
        mPluginName = pluginName;
        mMaxRetry = maxRetry;
    }

    /**
     * 失败重试次数
     */
    public int getRetryCount() {
        return mMaxRetry;
    }

    /**
     * 获取自定义插件签名
     */
    @Nullable
    public String getCustomSignature() {
        return mCustomSignature;
    }

    /**
     * 获取插件根目录
     */
    public String getRootDir() {
        return mRootDir;
    }

    /**
     * 获取插件OptDex目录
     */
    public String getOptimizedDexDir() {
        return mOptDexDir;
    }

    /**
     * 获取插件so库目录
     */
    public String getSoLibDir() {
        return mSoLibDir;
    }

    /**
     * 获取插件so库临时目录
     */
    public String getTempSoLibDir() {
        return mTempSoLibDir;
    }

    /**
     * 临时文件文件名后缀
     */
    public String getTempFileSuffix() {
        return mTempFileSuf;
    }

    /**
     * 获取插件安装文件名
     */
    public String getPluginName() {
        return mPluginName;
    }

    /**
     * 是否是调试模式
     */
    public boolean isDebugMode() {
        return mIsDebugMode;
    }

    /**
     * 是否无视已安装插件, 强制使用外部插件
     */
    public boolean ignoreInstalledPlugin() {
        return mIgnoreInstalledPlugin;
    }

    /**
     * 是否使用自定义插件签名
     * 如果是, 使用自定义的插件签名; 如果否, 使用App的签名作为插件的签名。
     */
    public boolean useCustomSignature() {
        return !TextUtils.isEmpty(mCustomSignature);
    }


    public static class Builder {

        private static final int MAX_RETRY = 3;
        private static final String DIR_PLUGIN = "frontia";
        private static final String DIR_OPT_DEX = "code-cache";
        private static final String DIR_SO_LIB = "lib";
        private static final String DIR_TEMP_SO = "temp";
        private static final String TEMP_FILE_SUFFIX = ".tmp";
        private static final String PLUGIN_NAME = "base-1.apk";

        private int mMaxRetry = MAX_RETRY;
        private String mCustomSignature;
        private String mRootDir = DIR_PLUGIN;
        private String mOptDexDir = DIR_OPT_DEX;
        private String mSoLibDir = DIR_SO_LIB;
        private String mTempSoLibDir = DIR_TEMP_SO;
        private String mPluginName = PLUGIN_NAME;
        private String mTempFileSuf = TEMP_FILE_SUFFIX;
        private boolean mIgnoreInstalledPlugin = false;
        private boolean mIsDebugMode = BuildConfig.DEBUG;

        public Builder() {
        }

        /**
         * 设置调制模式
         */
        public Builder setDebugMode(boolean debugMode) {
            mIsDebugMode = debugMode;
            return this;
        }

        /**
         * 设置强制使用外部插件
         */
        public Builder ignoreInstalledPlugin(boolean ignore) {
            mIgnoreInstalledPlugin = ignore;
            return this;
        }

        /**
         * 设置自定义插件签名
         */
        public Builder useCustomSignature(String signature) {
            mCustomSignature = signature;
            return this;
        }

        /**
         * 设置插件根目录
         */
        public Builder setRootDir(@NonNull String rootDir) {
            mRootDir = rootDir;
            return this;
        }

        /**
         * 设置插件OptDex目录
         */
        public Builder setOptimizedDexDir(@NonNull String optDexDir) {
            mOptDexDir = optDexDir;
            return this;
        }

        /**
         * 设置插件so库目录
         */
        public Builder setSoLibDir(@NonNull String soLibDir) {
            mSoLibDir = soLibDir;
            return this;
        }

        /**
         * 设置插件so库临时目录
         */
        public Builder setTempSoLibDir(@NonNull String tempSoLibDir) {
            mTempSoLibDir = tempSoLibDir;
            return this;
        }

        /**
         * 设置临时文件文件名后缀
         */
        public Builder setTempFileSuffix(String tempFileSuf) {
            mTempFileSuf = tempFileSuf;
            return this;
        }

        /**
         * 设置插件安装文件名
         */
        public Builder setPluginName(@NonNull String pluginName) {
            mPluginName = pluginName;
            return this;
        }

        /**
         * 设置失败重试次数
         */
        public Builder setMaxRetry(int count) {
            if (count > 0) {
                mMaxRetry = count;
            }
            return this;
        }

        public PluginSetting build() {
            return new PluginSetting(
                    mIsDebugMode,
                    mIgnoreInstalledPlugin,
                    mCustomSignature,
                    mRootDir,
                    mOptDexDir,
                    mSoLibDir,
                    mTempSoLibDir,
                    mTempFileSuf,
                    mPluginName,
                    mMaxRetry);
        }
    }
}
