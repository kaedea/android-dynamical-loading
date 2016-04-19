package tv.danmaku.pluinlib.util;

import tv.danmaku.pluinlib.core.Constants;

import java.io.*;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/12.
 */
public class FileUtil {
	public static final String TAG = "FileUtil";

	public static boolean copyFile(String source, String dest) {
		try {
			return copyFile(new FileInputStream(new File(source)), dest);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean copyFile(final InputStream inputStream, String dest) {
		LogUtil.d(TAG, "copyFile to " + dest);
		FileOutputStream oputStream = null;
		try {
			File destFile = new File(dest);
			destFile.getParentFile().mkdirs();
			destFile.createNewFile();

			oputStream = new FileOutputStream(destFile);
			byte[] bb = new byte[48 * 1024];
			int len = 0;
			while ((len = inputStream.read(bb)) != -1) {
				oputStream.write(bb, 0, len);
			}
			oputStream.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (oputStream != null) {
				try {
					oputStream.close();
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
		return false;
	}




	/**
	 * 递归删除文件及文件夹
	 * @param file
	 */
	public static boolean deleteAll(File file) {
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles != null && childFiles.length > 0) {
				for (int i = 0; i < childFiles.length; i++) {
					deleteAll(childFiles[i]);
				}
			}
		}
		LogUtil.d(TAG,"delete: " + file.getAbsolutePath());
		return file.delete();
	}

	public static void printAll(File file) {
		if (Constants.DEBUG) {
			LogUtil.d(TAG,"[printAll] " + file.getAbsolutePath());
			if (file.isDirectory()) {
				File[] childFiles = file.listFiles();
				if (childFiles != null && childFiles.length > 0) {
					for (int i = 0; i < childFiles.length; i++) {
						printAll(childFiles[i]);
					}
				}
			}
		}
	}

}
