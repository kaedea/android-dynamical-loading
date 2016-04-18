package tv.danmaku.pluinlib;

import android.content.Context;
import dalvik.system.DexClassLoader;
import tv.danmaku.pluinlib.bridge.plugin.BaseBehaviour;
import tv.danmaku.pluinlib.core.BasePluginPackage;
import tv.danmaku.pluinlib.core.Constants;
import tv.danmaku.pluinlib.util.ApkUtil;
import tv.danmaku.pluinlib.util.FileUtil;
import tv.danmaku.pluinlib.util.LogUtil;
import tv.danmaku.pluinlib.util.SoLibUtil;

import java.io.File;
import java.util.Set;

/**
 * Copyright (c) 2016 BiliBili Inc.
 * Created by kaede on 2016/4/8.
 *
 * 带有SO库的APK插件，用于满足带有SO库的SDK插件化
 */
public abstract class SoLibPluginPackage extends BasePluginPackage {

	public static final String TAG = "SoLibPluginPackage";

	public SoLibPluginPackage() {

	}

	@Override
	public BasePluginPackage loadPlugin(Context context) {
		return loadPlugin(context, pluginPath);
	}

	@Override
	public BasePluginPackage loadPlugin(Context context, String packagePath) {

		// 获取PackageInfo
		if (this.packageInfo == null) {
			this.packageInfo = ApkUtil.getPackageInfo(context, packagePath);
		}

		// 获取插件在DATA目录的安装路径（包名+版本+base-1.apk）
		String destApkPath = genInstallPath(context, packageInfo.packageName, String.valueOf(packageInfo.versionCode));
		LogUtil.i(TAG,"[loadPlugin] destApkPath = " + destApkPath);

		// 安装插件（拷贝插件到安装路径）
		boolean isCopySuccess = false;
		File destApk = new File(destApkPath);

		if (destApk.exists() && !Constants.DEBUG) {
			LogUtil.i(TAG,"[loadPlugin] 目标插件已存在，检验插件合法性");
			// TODO: 检验插件的合法性
			isCopySuccess = true;
		} else {
			LogUtil.i(TAG,"[loadPlugin] 安装目标插件（拷贝插件到安装路径）");
			isCopySuccess = FileUtil.copyFile(packagePath, destApkPath);
		}

		if (isCopySuccess) {
			// 解压SO库，并根据当前CPU的类型选择正确的SO库
			// TODO: 有必要每次都重新解压一边SO库吗？

			LogUtil.i(TAG,"[loadPlugin] 解压SO库");
			File apkParent = destApk.getParentFile();
			File tempSoDir = new File(apkParent, Constants.DIR_TEMP_SO);
			Set<String> soList = SoLibUtil.unZipSo(packagePath, tempSoDir);
			if (soList != null) {
				for (String soName : soList) {
					SoLibUtil.copySo(tempSoDir, soName, apkParent.getAbsolutePath());
				}
				// 删掉临时文件
				FileUtil.deleteAll(tempSoDir);
			}
			// 删除DEX优化的缓存文件
			File fileDexCache = new File(apkParent, Constants.DIR_DALVIK_CACHE);
			if (fileDexCache.exists()) {
				FileUtil.deleteAll(fileDexCache);
			}

			// 创建插件的ClassLoader
			LogUtil.i(TAG,"[loadPlugin] 创建ClassLoader");
			this.classLoader = createClassLoader(destApkPath, context.getClassLoader());
			this.assetManager = ApkUtil.createAssetManager(destApkPath);
			this.resources = ApkUtil.createResources(context, this.assetManager);
		}
		return this;
	}

	@Override
	public abstract BaseBehaviour getPluginBehaviour(Object... args);

	/**
	 * 插件的安装目录, 插件apk将来会被放在这个目录下面
	 */
	private String genInstallPath(Context context, String pluginId, String pluginVersion) {
		return getPluginRootDir(context) + "/" + pluginId + "/" + pluginVersion + "/base-1.apk";
	}

	private String getPluginRootDir(Context context) {
		return context.getDir(Constants.DIR_PLUGIN, Context.MODE_PRIVATE).getAbsolutePath();
	}

	private DexClassLoader createClassLoader(String absolutePluginApkPath, ClassLoader parentClassLoader) {
		String apkParentDir = new File(absolutePluginApkPath).getParent();

		File optDir = new File(apkParentDir, Constants.DIR_DALVIK_CACHE);
		optDir.mkdirs();

		File libDir = new File(apkParentDir, Constants.DIR_NATIVE_LIB);
		libDir.mkdirs();
		DexClassLoader dexClassLoader = new DexClassLoader(absolutePluginApkPath, optDir.getAbsolutePath(), libDir.getAbsolutePath(), parentClassLoader);
		return dexClassLoader;
	}
}
