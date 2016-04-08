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

import moe.studio.frontia.Frontia;
import moe.studio.frontia.Internals;
import moe.studio.frontia.SyncPluginManager;
import moe.studio.frontia.core.PluginManager;
import moe.studio.frontia.core.PluginRequest;
import moe.studio.frontia.ext.PluginSetting;
import moe.studio.frontia.model.TestAssetsFailRequest;
import moe.studio.frontia.model.TestAssetsRequest;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class FrontiaTest {

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
    public void testPluginManager() throws Exception {
        PluginRequest request = Frontia.instance().add(new TestAssetsRequest(), SyncPluginManager.Mode.UPDATE);
        PluginManager manager = request.getManager();
        Assert.assertEquals(manager.getClass(), SyncPluginManager.class);

        Frontia.RequestState state = Frontia.instance().addAsync(new TestAssetsRequest(), SyncPluginManager.Mode.UPDATE);
        request = state.getRequest();
        manager = request.getManager();
        Assert.assertEquals(manager.getClass(), Frontia.class);
    }


    @Test
    public void testAddRequest() throws Exception {
        PluginRequest request = Frontia.instance().add(new TestAssetsRequest(), SyncPluginManager.Mode.UPDATE);
        Assert.assertTrue(request.getState() == PluginRequest.State.UPD_SUCCESS);
        Assert.assertTrue(Internals.FileUtils.exist(request.getPluginPath()));

        request = Frontia.instance().add(request, SyncPluginManager.Mode.LOAD);
        Assert.assertTrue(request.getState() == PluginRequest.State.LOA_SUCCESS);
    }

    @Test
    public void testFailRequest() throws Exception {
        PluginRequest request = Frontia.instance().add(new TestAssetsFailRequest(), SyncPluginManager.Mode.UPDATE);
        Assert.assertTrue(request.getState() == PluginRequest.State.UPD_NO_PLUGIN);
        Assert.assertTrue(!Internals.FileUtils.exist(request.getPluginPath()));

        request.switchState(PluginRequest.State.UPD_SUCCESS);
        request = Frontia.instance().add(request, SyncPluginManager.Mode.LOAD);
        Assert.assertTrue(request.getState() == PluginRequest.State.WTF);
    }


}
