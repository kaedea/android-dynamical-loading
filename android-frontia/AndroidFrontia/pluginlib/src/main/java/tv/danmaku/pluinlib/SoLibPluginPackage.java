package tv.danmaku.pluinlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import dalvik.system.DexClassLoader;
import tv.danmaku.pluinlib.bridge.plugin.BaseBehaviour;
import tv.danmaku.pluinlib.core.Constants;
import tv.danmaku.pluinlib.util.ApkUtil;
import tv.danmaku.pluinlib.core.BasePluginPackage;
import tv.danmaku.pluinlib.util.FileUtil;
import tv.danmaku.pluinlib.util.SoLibUtil;

import java.io.File;
import java.util.Set;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 */
public class SoLibPluginPackage extends BasePluginPackage {

	public SoLibPluginPackage(String packageName) {
		super(packageName);
	}

	public SoLibPluginPackage(PackageInfo packageInfo) {
		super(packageInfo);
	}

	public SoLibPluginPackage(){

	}

	@Override
	public BasePluginPackage loadPlugin(Context context, String packagePath) {

		if (this.packageInfo == null) {
			this.packageInfo = ApkUtil.getPackageInfo(context, packagePath);
		}

		String destApkPath = genInstallPath(context, packageInfo.packageName, String.valueOf(packageInfo.versionCode));
		FileUtil.deleteAll(new File(destApkPath));
		boolean isCopySuccess = FileUtil.copyFile(packagePath, destApkPath);
		if (isCopySuccess) {
			File apkParent = new File(destApkPath).getParentFile();
			File tempSoDir = new File(apkParent, Constants.DIR_TEMP_SO);
			Set<String> soList = FileUtil.unZipSo(packagePath, tempSoDir);
			if (soList != null) {
				for (String soName : soList) {
					SoLibUtil.copySo(tempSoDir, soName, apkParent.getAbsolutePath());
				}
				//删掉临时文件
				FileUtil.deleteAll(tempSoDir);
			}
			FileUtil.deleteAll(new File(apkParent, Constants.DIR_DALVIK_CACHE));

			this.classLoader = createClassLoader(destApkPath, context.getClassLoader());
			this.assetManager = ApkUtil.createAssetManager(destApkPath);
			this.resources = ApkUtil.createResources(context, this.assetManager);
		}

		return this;
	}

	@Override
	public BaseBehaviour getPluginBehaviour(Object... args) {
		return null;
	}

	@SuppressLint("SdCardPath")
	public static File getLibrayAbsDirectory(Context context) {
		String dataDir = context.getApplicationInfo().dataDir;

		File libDir = new File(dataDir, "lib");
		return libDir;
	}


	/**
	 * 插件的安装目录, 插件apk将来会被放在这个目录下面
	 */
	private String genInstallPath(Context context, String pluginId, String pluginVersion) {
		return getPluginRootDir(context) + "/" + pluginId + "/" + pluginVersion + "/base-1.apk";
	}

	private String getPluginRootDir(Context context) {
		return context.getDir(Constants.DIR_PLUGIN, Context.MODE_PRIVATE).getAbsolutePath();
	}

	public DexClassLoader createClassLoader(String absolutePluginApkPath, ClassLoader parentClassLoader) {
		String apkParentDir = new File(absolutePluginApkPath).getParent();

		File optDir = new File(apkParentDir, Constants.DIR_DALVIK_CACHE);
		optDir.mkdirs();

		File libDir = new File(apkParentDir, Constants.DIR_NATIVE_LIB);
		libDir.mkdirs();
		DexClassLoader dexClassLoader = new DexClassLoader(absolutePluginApkPath, optDir.getAbsolutePath(), libDir.getAbsolutePath(), parentClassLoader);
		return dexClassLoader;
	}


}
