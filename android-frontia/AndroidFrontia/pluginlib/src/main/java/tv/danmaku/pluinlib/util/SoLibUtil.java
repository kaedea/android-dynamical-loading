package tv.danmaku.pluinlib.util;

import android.os.Build;
import tv.danmaku.pluinlib.core.Constants;

import java.io.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/14.
 */
public class SoLibUtil {

	public static final String TAG = "SoLibUtil";

	public static Set<String> unZipSo(String apkFile, File tempDir) {

		HashSet<String> result = null;

		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}

		LogUtil.d(TAG,"[unZipSo] 开始解压so文件到 " + tempDir.getAbsolutePath());

		ZipFile zfile = null;
		boolean isSuccess = false;
		BufferedOutputStream fos = null;
		BufferedInputStream bis = null;
		try {
			zfile = new ZipFile(apkFile);
			ZipEntry ze = null;
			Enumeration zList = zfile.entries();
			while (zList.hasMoreElements()) {
				ze = (ZipEntry) zList.nextElement();
				String relativePath = ze.getName();

				if (!relativePath.startsWith("lib" + File.separator)) {
					LogUtil.d(TAG,"[unZipSo] 不是lib目录，跳过 "+relativePath);
					continue;
				}

				if (ze.isDirectory()) {
					File folder = new File(tempDir, relativePath);
					LogUtil.d(TAG,"[unZipSo] 正在创建目录 " + folder.getAbsolutePath());
					if (!folder.exists()) {
						folder.mkdirs();
					}

				} else {

					if (result == null) {
						result = new HashSet<String>(4);
					}

					File targetFile = new File(tempDir, relativePath);
					LogUtil.d(TAG,"[unZipSo] 正在解压so文件 " + targetFile.getAbsolutePath());
					if (!targetFile.getParentFile().exists()) {
						targetFile.getParentFile().mkdirs();
					}
					targetFile.createNewFile();

					fos = new BufferedOutputStream(new FileOutputStream(targetFile));
					bis = new BufferedInputStream(zfile.getInputStream(ze));
					byte[] buffer = new byte[2048];
					int count = -1;
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
			isSuccess = true;
		} catch (IOException e) {
			e.printStackTrace();
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
			if (zfile != null) {
				try {
					zfile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		LogUtil.d(TAG, "[unZipSo] 解压so文件结束, isSuccess = " + isSuccess);
		if (Constants.DEBUG) {
			LogUtil.d(TAG, "[unZipSo] Plugin so:");
			Iterator<String> iterator = result.iterator();
			while (iterator.hasNext()) {
				LogUtil.d(TAG, "[unZipSo] " + iterator.next());
			}
		}
		return result;
	}

	public static boolean copySo(File sourceDir, String so, String destDir) {

		try {

			LogUtil.d(TAG, "[copySo] 开始处理so文件");
			boolean isSuccess = false;

			if (Build.VERSION.SDK_INT >= 21) {
				String[] abis = Build.SUPPORTED_ABIS;
				if (abis != null) {
					for (String abi : abis) {
						LogUtil.d(TAG, "[copySo] try supported abi:"+abi);
						String name = "lib" + File.separator + abi + File.separator + so;
						File sourceFile = new File(sourceDir, name);
						if (sourceFile.exists()) {
							LogUtil.i(TAG,"[copySo] copy so: " + sourceFile.getAbsolutePath());
							isSuccess = FileUtil.copyFile(sourceFile.getAbsolutePath(), destDir + File.separator + Constants.DIR_NATIVE_LIB + File.separator + so);
							//api21 64位系统的目录可能有些不同
							//copyFile(sourceFile.getAbsolutePath(), destDir + File.separator +  name);
							break;
						}
					}
				} else {
					LogUtil.e(TAG, "[copySo] get abis == null");
				}
			} else {
				LogUtil.d(TAG, "[copySo] supported api:" + Build.CPU_ABI + " " + Build.CPU_ABI2);

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
					LogUtil.i(TAG,"[copySo] copy so: " + sourceFile.getAbsolutePath());
					isSuccess = FileUtil.copyFile(sourceFile.getAbsolutePath(), destDir + File.separator + Constants.DIR_NATIVE_LIB + File.separator + so);
				}
			}

			if (!isSuccess) {
				LogUtil.e(TAG, "[copySo] 安装 " + so + " 失败: NO_MATCHING_ABIS");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}
}
