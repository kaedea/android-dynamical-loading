package tv.danmaku.frontia.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.*;

import tv.danmaku.frontia.core.error.IllegalPluginException;
import tv.danmaku.frontia.core.error.LoadPluginException;
import tv.danmaku.frontia.core.error.PluginException;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/12.
 */
public class PluginFileUtil {
    public static final String TAG = "FileUtil";

    public static void copyFile(String source, String dest) throws IOException {
        copyFile(new FileInputStream(new File(source)), dest);
    }

    public static void copyFile(final InputStream inputStream, String dest) throws IOException {
        PluginLogUtil.v(TAG, "[copyFile]copy to " + dest);
        FileOutputStream outputStream = null;
        try {
            File destFile = new File(dest);
            destFile.getParentFile().mkdirs();
            destFile.createNewFile();
            outputStream = new FileOutputStream(destFile);
            byte[] buffer = new byte[4096];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void copyFileFromAsset(Context context, String pathAssets, String pathDest) throws IOException {
        PluginLogUtil.v(TAG, "copyFile from assets " + pathAssets + ", to " + pathDest);
        if (TextUtils.isEmpty(pathAssets)) {
            PluginLogUtil.w(TAG, "asset path invalid!");
            throw new IOException("asset path is empty");
        }
        if (TextUtils.isEmpty(pathDest)) {
            PluginLogUtil.w(TAG, "dest path invalid!");
            throw new IOException("dest path is empty");
        }
        File fileDest = new File(pathDest);
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            fileDest.getParentFile().mkdirs();
            fileDest.createNewFile();
            is = context.getAssets().open(pathAssets);
            fos = new FileOutputStream(fileDest);
            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    PluginLogUtil.w(TAG, "close fos error!");
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    PluginLogUtil.w(TAG, "close is error!");
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean delete(@NonNull String filePath) {
        return new File(filePath).delete();
    }

    /**
     * 递归删除文件及文件夹
     * @param file 需要删除的文件
     */
    public static void deleteAll(File file) {
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles != null && childFiles.length > 0) {
                for (File childFile : childFiles) {
                    deleteAll(childFile);
                }
            }
        }
        PluginLogUtil.v(TAG, "delete: " + file.getAbsolutePath());
        file.delete();
    }

    public static void printAll(File file) {
        boolean isDirectory = file.isDirectory();
        PluginLogUtil.v(TAG, "[printAll]" + file.getAbsolutePath() + ", isDirectory = " + isDirectory);
        if (isDirectory) {
            File[] childFiles = file.listFiles();
            if (childFiles != null && childFiles.length > 0) {
                for (File childFile : childFiles) {
                    printAll(childFile);
                }
            }
        }
    }
}
