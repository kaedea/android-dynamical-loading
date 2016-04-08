/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import moe.studio.frontia.ext.PluginError;
import moe.studio.frontia.core.PluginBehavior;

import static moe.studio.frontia.Internals.FileUtils;
import static moe.studio.frontia.Internals.SoLibUtils;

/**
 * 带有SO库的APK插件，用于满足带有SO库的SDK插件化。
 */
@SuppressWarnings("WeakerAccess")
public abstract class SoLibPlugin<B extends PluginBehavior> extends SimplePlugin<B> {

    public static final String TAG = "plugin.simple.SoLib";

    protected Set<File> mSoLibs;

    public SoLibPlugin(String apkPath) {
        super(apkPath);
        mSoLibs = new HashSet<>();
    }

    @Override
    public SoLibPlugin loadPlugin(Context context, String installPath) throws PluginError.LoadError {
        Logger.d(TAG, "Install plugin so libs.");

        File apkFile = new File(installPath);
        checkApkFile(apkFile);

        try {
            mSoLibDir = createSoLibDir(apkFile);
        } catch (IOException e) {
            throw new PluginError.LoadError(e, PluginError.ERROR_LOA_SO_DIR);
        }

        try {
            installSoLib(context, apkFile, mSoLibDir);
        } catch (IOException e) {
            throw new PluginError.LoadError(e, PluginError.ERROR_LOA_SO_INSTALL);
        }

        super.loadPlugin(context, installPath);
        return this;
    }

    protected File createSoLibDir(File apkFile) throws IOException {
        File file = new File(apkFile.getParentFile(), mSetting.getSoLibDir());
        FileUtils.checkCreateDir(file);
        return file;
    }

    protected void installSoLib(Context context, File apkFile, File soLibDir) throws IOException {
        Logger.d(TAG, "Install plugin so libs, destDir = " + soLibDir);

        // TODO: 2016/11/30 Optimize so libs installation.
        File tempDir = new File(soLibDir.getParentFile(), mSetting.getTempSoLibDir());
        FileUtils.checkCreateDir(tempDir);
        Set<String> soList = SoLibUtils.extractSoLib(apkFile, tempDir);

        if (soList != null) {
            for (String soName : soList) {
                File soLib = SoLibUtils.copySoLib(context, tempDir, soName, soLibDir);
                if (soLib != null) {
                    mSoLibs.add(soLib);
                }
            }
            FileUtils.delete(tempDir);
        }
    }

    public Set<File> getSoLibs() {
        return mSoLibs;
    }
}
