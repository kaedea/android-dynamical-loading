package tv.danmaku.pluinlib.util;

import android.os.Build;
import tv.danmaku.pluinlib.core.Constants;

import java.io.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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


	public static void readFileFromJar(String jarFilePath, String metaInfo) {
		LogUtil.d(TAG,"readFileFromJar:"+ jarFilePath + " "+metaInfo);
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(jarFilePath);
			JarEntry entry = jarFile.getJarEntry(metaInfo);
			if (entry != null) {
				InputStream input = jarFile.getInputStream(entry);

				return;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (jarFile != null) {
				try {
					jarFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return;

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
		LogUtil.d("delete", file.getAbsolutePath());
		return file.delete();
	}

	public static void printAll(File file) {
		if (Constants.DEBUG) {
			LogUtil.d("printAll", file.getAbsolutePath());
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

	public static String streamToString(InputStream input) throws IOException {

		InputStreamReader isr = new InputStreamReader(input);
		BufferedReader reader = new BufferedReader(isr);

		String line;
		StringBuffer sb = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		reader.close();
		isr.close();
		return sb.toString();
	}
}
