/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia.ext;

/**
 * 插件异常
 */

public abstract class PluginError extends Exception {

    /**
     * 更新错误
     */
    public static final int ERROR_UPD_CANCELED = 2001;       // 客户取消
    public static final int ERROR_UPD_DOWNLOAD = 2002;       // 下载失败
    public static final int ERROR_UPD_NO_TEMP = 2003;        // 创建临时文件失败
    public static final int ERROR_UPD_EXTRACT = 2004;        // 解压 Assets 插件失败
    public static final int ERROR_UPD_CAPACITY = 2005;       // 文件空间不够
    public static final int ERROR_UPD_REQUEST = 2006;        // 请求网络插件信息失败

    /**
     * 安装错误
     */
    public static final int ERROR_INS_NOT_FOUND = 3001;      // 文件丢失
    public static final int ERROR_INS_PACKAGE_INFO = 3002;   // 无法获取 PackageInfo
    public static final int ERROR_INS_SIGNATURE = 3003;      // 签名不对
    public static final int ERROR_INS_INSTALL = 3004;        // 安装失败
    public static final int ERROR_INS_CAPACITY = 3005;       // 文件空间不够
    public static final int ERROR_INS_INSTALL_PATH = 3006;   // 获取安装路径失败

    /**
     * 加载错误
     */
    public static final int ERROR_LOA_NOT_FOUND = 4001;      // 文件丢失
    public static final int ERROR_LOA_OPT_DIR = 4002;        // 创建 Optimized Dex 目录失败
    public static final int ERROR_LOA_SO_DIR = 4003;         // 创建 so库 目录失败
    public static final int ERROR_LOA_SO_INSTALL = 4004;     // 安装 so库 失败
    public static final int ERROR_LOA_CLASSLOADER = 4005;    // 创建 ClassLoader 失败
    public static final int ERROR_LOA_ASSET_MANAGER = 4006;  // 创建 AssetManager 失败
    public static final int ERROR_LOA_CLASS = 4007;          // 获取插件的 Class 失败
    public static final int ERROR_LOA_BEHAVIOR = 4008;       // 获取插件的 Behavior 失败
    public static final int ERROR_LOA_BEHAVIOR_ENTRY = 4009; // 通过Manifest自动获取插件 Behavior 失败
    public static final int ERROR_LOA_NOT_LOADED = 4010;     // 插件还没有被加载
    public static final int ERROR_LOA_CREATE_PLUGIN = 4011;  // 创建 Plugin 对象失败
    public static final int ERROR_LOA_DEPENDENCY = 4012;     // 无法满足插件依赖

    private static final int ERROR_OVER_RETRY = 1001;

    /**
     * 失败重试超标
     */
    public final static class RetryError extends PluginError {
        public RetryError() {
            super("Reach max retry.", ERROR_OVER_RETRY);
        }
    }

    /**
     * 用户取消
     */
    public final static class CancelError extends PluginError {

        public CancelError(int code) {
            super("Operation was canceled.", code);
        }
    }

    /**
     * 更新插件异常
     */
    public final static class UpdateError extends PluginError {

        public UpdateError(String detailMessage, int code) {
            super(detailMessage, code);
        }

        public UpdateError(Throwable throwable, int code) {
            super(throwable, code);
        }
    }

    /**
     * 安装插件异常
     */
    public final static class InstallError extends PluginError {

        public InstallError(String detailMessage, int code) {
            super(detailMessage, code);
        }

        public InstallError(Throwable throwable, int code) {
            super(throwable, code);
        }
    }

    /**
     * 加载插件异常
     */
    public final static class LoadError extends PluginError {

        public LoadError(String detailMessage, int code) {
            super(detailMessage, code);
        }

        public LoadError(Throwable throwable, int code) {
            super(throwable, code);
        }
    }

    private final int mCode;

    public PluginError(String detailMessage, int code) {
        super(detailMessage);
        mCode = code;
    }

    public PluginError(Throwable throwable, int code) {
        super(throwable);
        mCode = code;
    }

    public PluginError(String detailMessage, Throwable throwable, int code) {
        super(detailMessage, throwable);
        mCode = code;
    }

    public int getCode() {
        return mCode;
    }

    @Override
    public String toString() {
        return "PluginError{" +
                "code=" + mCode +
                ", msg = " + super.toString() +
                '}';
    }
}
