/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import moe.studio.frontia.Frontia;
import moe.studio.frontia.Internals;
import moe.studio.frontia.Logger;
import moe.studio.frontia.PluginInstallerImpl;
import moe.studio.frontia.ext.PluginError;
import moe.studio.frontia.ext.PluginSetting;

/**
 * @author kaede
 * @version date 2016/12/9
 */
@RunWith(AndroidJUnit4.class)
public class PluginInstallerTest {
    static final String TAG = "plugin.test";
    private Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
        PluginSetting setting = new PluginSetting.Builder()
                .setDebugMode(BuildConfig.DEBUG)
                .ignoreInstalledPlugin(false)
                .build();
        Frontia.instance().init(mContext, setting);
    }

    @After
    public void shutDown() {
        Frontia.release();
    }

    @Test
    public void testInstall() throws IOException, PluginError.InstallError {
        String tmpDirPath = System.getProperty("java.io.tmpdir", ".");
        File tmpDir = new File(tmpDirPath);
        Logger.d(TAG, "Get system temp dir, path = " + tmpDirPath);

        File testApk = File.createTempFile("frontia_test_", "test.apk", tmpDir);
        Internals.FileUtils.copyFileFromAsset(mContext, "test.apk", testApk);
        Assert.assertTrue(testApk.exists());

        PluginInstallerImpl installer = new PluginInstallerImpl(mContext,
                Frontia.instance().getSetting());
        String install = installer.install(testApk.getAbsolutePath());
        Assert.assertEquals(install, new File(installer.getRootPath(),
                "moe.studio.plugin.fresco/1/base-1.apk").getAbsolutePath());

    }
}
