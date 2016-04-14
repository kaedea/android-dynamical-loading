package tv.danmaku.pluinlib.util;

import android.os.Build;
import tv.danmaku.pluinlib.core.Constants;

import java.io.File;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/14.
 */
public class SoLibUtil {
	public static boolean copySo(File sourceDir, String so, String destDir) {

		try {

			boolean isSuccess = false;

			if (Build.VERSION.SDK_INT >= 21) {
				String[] abis = Build.SUPPORTED_ABIS;
				if (abis != null) {
					for (String abi: abis) {
						LogUtil.d("try supported abi:", abi);
						String name = "lib" + File.separator + abi + File.separator + so;
						File sourceFile = new File(sourceDir, name);
						if (sourceFile.exists()) {
							isSuccess = FileUtil.copyFile(sourceFile.getAbsolutePath(), destDir + File.separator + Constants.DIR_NATIVE_LIB + File.separator + so);
							//api21 64位系统的目录可能有些不同
							//copyFile(sourceFile.getAbsolutePath(), destDir + File.separator +  name);
							break;
						}
					}
				}
			} else {
				LogUtil.d(FileUtil.TAG,"supported api:"+ Build.CPU_ABI+" "+ Build.CPU_ABI2);

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
					isSuccess = FileUtil.copyFile(sourceFile.getAbsolutePath(), destDir + File.separator + Constants.DIR_NATIVE_LIB + File.separator + so);
				}
			}

			if (!isSuccess) {
				LogUtil.w(FileUtil.TAG, "安装 " + so + " 失败: NO_MATCHING_ABIS");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		return true;
	}
}
