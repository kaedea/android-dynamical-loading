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
import java.util.Set;

import moe.studio.frontia.Frontia;
import moe.studio.frontia.Internals;
import moe.studio.frontia.Logger;
import moe.studio.frontia.ManifestUtils;
import moe.studio.frontia.ext.PluginApk;
import moe.studio.frontia.ext.PluginSetting;

/**
 * @author kaede
 * @version date 2016/12/9
 */
@RunWith(AndroidJUnit4.class)
public class UtilsTest {
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
    public void testFileOperations() throws IOException {
        File cacheDir = mContext.getExternalCacheDir();

        for (int i = 0; i < 50; i++) {
            Internals.FileUtils.copyFileFromAsset(mContext, "test.apk",
                    new File(cacheDir, "extract_asset" + File.separator + "test_" + i));
        }

        for (int i = 0; i < 50; i++) {
            Internals.FileUtils.copyFile(new File(cacheDir, "extract_asset" + File.separator + "test_" + i),
                    new File(cacheDir, "copy_file" + File.separator + "test_" + i));
        }
    }

    @Test
    public void testSoLibOperations() throws IOException {
        String tmpDirPath = System.getProperty("java.io.tmpdir", ".");
        File tmpDir = new File(tmpDirPath);
        File testApk = File.createTempFile("frontia_test_", "test.apk", tmpDir);
        Internals.FileUtils.copyFileFromAsset(mContext, "fresco.apk", testApk);
        Logger.d(TAG, "Get test apk, path = " + testApk);

        Set<String> soLibs = Internals.SoLibUtils.extractSoLib(testApk, new File(tmpDir, "extract_solib"));
        Assert.assertTrue(soLibs.contains("libimagepipeline.so"));
    }

    @Test
    public void testManifestUtils() throws IOException {
        String tmpDirPath = System.getProperty("java.io.tmpdir", ".");
        File tmpDir = new File(tmpDirPath);
        Logger.d(TAG, "Get system temp dir, path = " + tmpDirPath);

        File testApk = File.createTempFile("frontia_test_", "test.apk", tmpDir);
        Internals.FileUtils.copyFileFromAsset(mContext, "test.apk", testApk);
        Assert.assertTrue(testApk.exists());

        PluginApk apk = ManifestUtils.parse(testApk);
        Assert.assertEquals(apk.application, "moe.studio.plugin.fresco.TestApplication");
        Assert.assertEquals(apk.packageName, "moe.studio.plugin.fresco");
        Assert.assertEquals(apk.versionName, "1.0");
        Assert.assertEquals(apk.versionCode, "1");
        Assert.assertEquals(apk.application, "moe.studio.plugin.fresco.TestApplication");
        Assert.assertTrue(apk.dependencies.get("frontia") == 400);
        Assert.assertTrue(apk.dependencies.get("support_v4") == 23);
        Assert.assertTrue(apk.dependencies.get("frontia_v7") == 23);
        Assert.assertTrue(apk.dependencies.get("test_dependency") == 300);
    }
}
