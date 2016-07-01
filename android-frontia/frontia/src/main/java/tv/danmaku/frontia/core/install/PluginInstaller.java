/*
 * Copyright (c) 2015-2016 BiliBili Inc.
 */

package tv.danmaku.frontia.core.install;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.support.annotation.NonNull;

import tv.danmaku.frontia.core.PluginConstants;
import tv.danmaku.frontia.util.ApkUtil;
import tv.danmaku.frontia.util.PluginFileUtil;
import tv.danmaku.frontia.util.PluginLogUtil;
import tv.danmaku.frontia.util.SignatureUtil;

import java.io.File;

/**
 * Created by Kaede on 2016/4/25.
 * 插件安装器，提供插件的安装和卸载策略。
 */
public class PluginInstaller {
    public static final String TAG = "PluginInstaller";
    volatile static PluginInstaller instance;

    public static PluginInstaller getInstance(Context context) {
        if (instance == null) {
            instance = new PluginInstaller(context);
        }
        return instance;
    }

    Context context;

    private PluginInstaller(Context context) {
        this.context = context.getApplicationContext();
    }

    public String getTempPluginPath() {
        return context.getCacheDir().getAbsolutePath() + File.separator + System.currentTimeMillis() + PluginConstants.EXT_TEMP_PLUGIN;
    }

    public boolean checkPluginValid(String pluginPath) {
        PluginLogUtil.d(TAG, "[checkPluginValid]检验插件合法性");
        File pluginFile = new File(pluginPath);
        if (!pluginFile.exists()) return false;
        if (PluginConstants.DEBUG) {
            PluginLogUtil.d(TAG, "[checkPluginValid]调试模式，不校验插件签名: " + pluginPath);
            return true;
        }
        // 验证插件APK签名，如果被篡改过，将获取不到正确的证书
        Signature[] pluginSignatures = SignatureUtil.collectCertificates(pluginPath, false);
        if (pluginSignatures == null) {
            pluginSignatures = SignatureUtil.collectCertificates(context, pluginPath);
        }
        if (pluginSignatures == null) {
            PluginLogUtil.w(TAG, "[checkPluginValid]获取插件签名失败: " + pluginPath);
            return false;
        }
        SignatureUtil.printSignature(pluginSignatures);
        // 1. 检验插件的签名和宿主的签名是否一致
        // 可选步骤，验证插件APK证书是否和宿主程序证书相同
        // 证书中存放的是公钥和算法信息，而公钥和私钥是1对1的
        // 公钥相同意味着是同一个作者发布的程序
        /*Signature[] mainSignatures = null;
		try {
			PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
			mainSignatures = pkgInfo.signatures;
			LogUtil.e(TAG, "宿主签名:");
			SignatureUtil.printSignature(mainSignatures);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			LogUtil.e(TAG, "获取宿主签名失败");
		}
		if (!SignatureUtil.isSignaturesSame(mainSignatures, pluginSignatures)) {
			LogUtil.e(TAG, "插件签名和宿主签名不一致: " + pluginPath);
			return false;
		}*/
        // 2. 检验插件的签名是不是指定签名
        String signature = PluginConstants.SIGNATURE_PLUGIN;
        if (!SignatureUtil.isSignaturesSame(signature, pluginSignatures)) {
            PluginLogUtil.w(TAG, "[checkPluginValid]插件签名和指定签名不一致: " + pluginPath);
            return false;
        }
        PluginLogUtil.v(TAG, "[checkPluginValid]插件签名校验成功: " + pluginPath);
        return true;
    }

    public boolean checkPluginValid(String pluginPath, boolean deleteIfInvalid) {
        if (checkPluginValid(pluginPath)) return true;
        if (deleteIfInvalid) deletePlugin(pluginPath);
        return false;
    }

    public boolean checkPluginValid(String pluginId, String pluginVersion, boolean deleteIfInvalid) {
        String pluginPath = getPluginInstallPath(pluginId, pluginVersion);
        if (checkPluginValid(pluginPath)) return true;
        if (deleteIfInvalid) deletePlugin(pluginId, pluginVersion);
        return false;
    }

    public boolean deletePlugin(String pluginPath) {
        return new File(pluginPath).delete();
    }

    public void deletePlugin(String pluginId, String pluginVersion) {
        PluginFileUtil.deleteAll(new File(getPluginInstallPath(pluginId, pluginVersion)));
    }

    public void deletePlugins(String pluginId) {
        PluginFileUtil.deleteAll(new File(getPluginDir(pluginId)));
    }

    public String getPluginInstallPath(String pluginId, String pluginVersion) {
        return getPluginRootDir() + File.separator + pluginId + File.separator + pluginVersion + File.separator + PluginConstants.FILE_PLUGIN_NAME;
    }

    public String getPluginInstallPath(String pluginPath) {
        PackageInfo packageInfo = getPluginInfo(pluginPath);
        if (packageInfo == null) {
            return null;
        }
        return getPluginInstallPath(packageInfo.packageName, String.valueOf(packageInfo.versionCode));
    }

    /**
     * @return 所有插件存放的根目录
     */
    public String getPluginRootDir() {
        return context.getDir(PluginConstants.DIR_PLUGIN, Context.MODE_PRIVATE).getAbsolutePath();
    }

    /**
     * @param pluginId 插件包名
     * @return 特定插件的根目录
     */
    public String getPluginDir(@NonNull String pluginId) {
        return getPluginRootDir() + File.separator + pluginId;
    }

    public boolean isPluginInstalled(String pluginId, String pluginVersion) {
        if (PluginConstants.ignoreInstalledPlugin) {
            // 强制使用外部插件
            return false;
        }
        return checkPluginValid(pluginId, pluginVersion, true);
    }

    public boolean isPluginInstalled(String pluginPath) {
        if (PluginConstants.ignoreInstalledPlugin) {
            // 强制使用外部插件
            return false;
        }
        PackageInfo packageInfo = getPluginInfo(pluginPath);
        return packageInfo != null && checkPluginValid(packageInfo.packageName, String.valueOf(packageInfo.versionCode), true);
    }

    public PackageInfo getPluginInfo(String pluginPath) {
        return ApkUtil.getPackageInfo(context, pluginPath);
    }

}
