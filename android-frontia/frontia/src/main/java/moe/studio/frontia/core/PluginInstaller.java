/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia.core;

import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;

import moe.studio.frontia.ext.PluginError;

/**
 * 插件安装器
 */
public interface PluginInstaller {
    /**
     * 插件插件是否安全
     */
    boolean checkSafety(String apkPath);

    /**
     * 插件插件是否安全
     *
     * @param apkPath         插件路径
     * @param deleteIfInvalid 插件不安全时是否删除插件
     * @return 安全与否
     */
    boolean checkSafety(String apkPath, boolean deleteIfInvalid);

    /**
     * 插件插件是否安全
     *
     * @param pluginId        插件ID
     * @param version         插件版本
     * @param deleteIfInvalid 插件不安全时是否删除插件
     * @return 安全与否
     */
    boolean checkSafety(String pluginId, String version, boolean deleteIfInvalid);

    /**
     * 删除插件
     */
    void delete(String apkPath);

    /**
     * 删除插件
     *
     * @param pluginId 插件ID
     * @param version  插件版本
     */
    void delete(String pluginId, String version);

    /**
     * 删除指定ID的插件的所有版本
     */
    void deletePlugins(String pluginId);

    /**
     * 文件空间是否足够
     *
     * @throws IOException 不够
     */
    void checkCapacity() throws IOException;

    /**
     * 创建一个临时文件
     *
     * @param prefix 前缀
     * @return 临时文件
     * @throws IOException 创建失败
     */
    File createTempFile(String prefix) throws IOException;

    /**
     * 获取所有插件的根目录
     */
    String getRootPath();

    /**
     * 删除指定ID的插件的根目录
     */
    String getPluginPath(@NonNull String pluginId);

    /**
     * 获取指定插件版本对应的安装路径
     *
     * @param pluginId 插件ID
     * @param version  插件版本
     * @return 安装路径
     */
    String getInstallPath(String pluginId, String version);

    /**
     * 获取插件安装路径
     *
     * @param apkPath 插件包路径
     * @return 安装路径
     */
    @Nullable
    String getInstallPath(String apkPath);

    /**
     * 判断指定版本的插件是否已经安装
     *
     * @param pluginId 插件ID
     * @param version  插件版本
     * @return 安装与否
     */
    boolean isInstalled(String pluginId, String version);

    /**
     * 判断插件是否已经安装
     */
    boolean isInstalled(String apkPath);

    /**
     * 安装指定路径上的插件
     */
    String install(String apkPath) throws PluginError.InstallError;

    /**
     * 获取插件的PackageInfo
     */
    PackageInfo getPackageInfo(String apkPath);
}
