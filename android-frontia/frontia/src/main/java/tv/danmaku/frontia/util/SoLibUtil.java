package tv.danmaku.frontia.util;

import android.os.Build;
import tv.danmaku.frontia.core.PluginConstants;

import java.io.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/14.
 */
public class SoLibUtil {
    public static final String TAG = "SoLibUtil";

    /**
     * 将SO库解压到指定路径并返回所有解压好的SO库文件名字的集合
     * @param apkFile APK包路径
     * @param destDir 目标文件夹
     * @return SO库文件名集合
     */
    public static Set<String> extractSoLib(String apkFile, File destDir) throws IOException {
        HashSet<String> result = new HashSet<String>(4);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        PluginLogUtil.v(TAG, "[extractSoLib]开始解压so文件到 " + destDir.getAbsolutePath());
        ZipFile zipFile = null;
        BufferedOutputStream fos = null;
        BufferedInputStream bis = null;
        try {
            zipFile = new ZipFile(apkFile);
            ZipEntry ze;
            Enumeration zList = zipFile.entries();
            while (zList.hasMoreElements()) {
                ze = (ZipEntry) zList.nextElement();
                String relativePath = ze.getName();
                if (!relativePath.startsWith("lib" + File.separator)) {
                    PluginLogUtil.v(TAG, "[unZipSo]不是lib目录，跳过 " + relativePath);
                    continue;
                }
                if (ze.isDirectory()) {
                    File folder = new File(destDir, relativePath);
                    PluginLogUtil.v(TAG, "[unZipSo]正在创建目录 " + folder.getAbsolutePath());
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                } else {
                    File targetFile = new File(destDir, relativePath);
                    PluginLogUtil.v(TAG, "[extractSoLib]正在解压so文件 " + targetFile.getAbsolutePath());
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();
                    fos = new BufferedOutputStream(new FileOutputStream(targetFile));
                    bis = new BufferedInputStream(zipFile.getInputStream(ze));
                    byte[] buffer = new byte[4096];
                    int count;
                    while ((count = bis.read(buffer)) != -1) {
                        fos.write(buffer, 0, count);
                        fos.flush();
                    }
                    fos.close();
                    fos = null;
                    bis.close();
                    bis = null;
                    result.add(relativePath.substring(relativePath.lastIndexOf(File.separator) + 1));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("unzip solibs fail", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // print all so libs
        PluginLogUtil.v(TAG, "[extractSoLib]解压so文件结束");
        if (PluginConstants.DEBUG) {
            PluginLogUtil.d(TAG, "---------------- plugin so ----------------");
            for (String item : result) {
                PluginLogUtil.v(TAG, item);
            }
            PluginLogUtil.d(TAG, "---------------- plugin so ----------------");
        }
        return result;
    }

    /**
     * 将一个SO库复制到指定路径，会先检查改SO库是否与当前CPU兼容
     * @param sourceDir     SO库所在目录
     * @param so            SO库名字
     * @param destDir       目标根目录
     * @param nativeLibName 目标SO库目录名
     * @return 是否成功
     */
    public static boolean copySoLib(File sourceDir, String so, String destDir, String nativeLibName) throws IOException {
        boolean hasMatch = false;
        if (Build.VERSION.SDK_INT >= 21) {
            String[] abis = Build.SUPPORTED_ABIS;
            if (abis != null) {
                for (String abi : abis) {
                    PluginLogUtil.v(TAG, "[copySoLib]try supported abi:" + abi);
                    String name = "lib" + File.separator + abi + File.separator + so;
                    File sourceFile = new File(sourceDir, name);
                    if (sourceFile.exists()) {
                        hasMatch = true;
                        PluginLogUtil.v(TAG, "[copySoLib]copy so: " + sourceFile.getAbsolutePath());
                        PluginFileUtil.copyFile(sourceFile.getAbsolutePath(), destDir + File.separator + nativeLibName + File.separator + so);
                        //api21 64位系统的目录可能有些不同
                        //copyFile(sourceFile.getAbsolutePath(), destDir + File.separator +  name);
                        break;
                    }
                }
            } else {
                PluginLogUtil.w(TAG, "[copySoLib]get abis = null");
            }
        } else {
            PluginLogUtil.v(TAG, "[copySoLib]supported api:" + Build.CPU_ABI + " " + Build.CPU_ABI2);
            String name = "lib" + File.separator + Build.CPU_ABI + File.separator + so;
            File sourceFile = new File(sourceDir, name);
            if (!sourceFile.exists() && Build.CPU_ABI2 != null) {
                name = "lib" + File.separator + Build.CPU_ABI2 + File.separator + so;
                sourceFile = new File(sourceDir, name);
                if (!sourceFile.exists()) {
                    name = "lib" + File.separator + "armeabi" + File.separator + so;
                    sourceFile = new File(sourceDir, name);
                }
            }
            if (sourceFile.exists()) {
                hasMatch = true;
                PluginLogUtil.v(TAG, "[copySoLib]copy so: " + sourceFile.getAbsolutePath());
                PluginFileUtil.copyFile(sourceFile.getAbsolutePath(), destDir + File.separator + nativeLibName + File.separator + so);
            }
        }
        if (!hasMatch) {
            PluginLogUtil.d(TAG, "[copySoLib]无法安装 " + so + ", NO_MATCHING_ABIS, 设备CPU不支持");
            return false;
        }
        return true;
    }
}
