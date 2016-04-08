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
import moe.studio.frontia.Internals.FileUtils;
import moe.studio.frontia.SyncPluginManager;
import moe.studio.frontia.core.PluginRequest;
import moe.studio.frontia.ext.PluginSetting;
import moe.studio.frontia.model.TestAssetsRequest;

/**
 * @author kaede
 * @version date 2016/12/9
 */
@RunWith(AndroidJUnit4.class)
public class PluginLoaderTest {
    static final String TAG = "plugin.test";
    private Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
        PluginSetting setting = new PluginSetting.Builder()
                .setDebugMode(BuildConfig.DEBUG)
                .ignoreInstalledPlugin(BuildConfig.DEBUG)
                .build();
        Frontia.instance().init(mContext, setting);
    }

    @After
    public void shutDown() {
        Frontia.release();
    }

    @Test
    public void testLoadPlugin() throws IOException {
        File testApk = File.createTempFile("test_load", "fresco.apk");

        PluginRequest request = new TestAssetsRequest();
        request.switchState(PluginRequest.State.UPD_SUCCESS);
        Frontia.instance().add(request, SyncPluginManager.Mode.LOAD);
        Assert.assertTrue(request.getState() == PluginRequest.State.WTF);

        request.switchState(PluginRequest.State.UPD_SUCCESS);
        request.setPluginPath(testApk.getAbsolutePath());
        Frontia.instance().add(request, SyncPluginManager.Mode.LOAD);
        Assert.assertTrue(request.getState() == PluginRequest.State.LOA_PLUGIN_FAIL);


        FileUtils.copyFileFromAsset(mContext, "fresco.apk", testApk);
        request.switchState(PluginRequest.State.UPD_SUCCESS);
        Frontia.instance().add(request, SyncPluginManager.Mode.LOAD);
        Assert.assertTrue(request.getState() == PluginRequest.State.LOA_SUCCESS);
    }
}
