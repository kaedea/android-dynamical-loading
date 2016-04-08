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

import moe.studio.frontia.model.TestOnlineRequest;
import moe.studio.frontia.Frontia;
import moe.studio.frontia.PluginUpdaterImpl;
import moe.studio.frontia.SyncPluginManager;
import moe.studio.frontia.core.PluginRequest;
import moe.studio.frontia.ext.PluginSetting;
import moe.studio.frontia.model.TestAssetsRequest;

import static moe.studio.frontia.core.PluginRequest.State.LOA_SUCCESS;
import static moe.studio.frontia.core.PluginRequest.State.UPD_NEED_DOWNLOAD;
import static moe.studio.frontia.core.PluginRequest.State.UPD_NEED_EXTRACT;
import static moe.studio.frontia.core.PluginRequest.State.UPD_SUCCESS;

/**
 * @author kaede
 * @version date 2016/12/9
 */
@RunWith(AndroidJUnit4.class)
public class PluginUpdaterTest {
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
    public void testRequestInfo() {
        PluginUpdaterImpl updater = new PluginUpdaterImpl(mContext);

        PluginRequest request = new TestOnlineRequest();
        request.attach(Frontia.instance());
        request.setClearLocalPlugins(true);
        updater.requestPlugin(request);
        Assert.assertEquals(request.getState(), UPD_NEED_DOWNLOAD);

        request = new TestAssetsRequest();
        request.attach(Frontia.instance());
        request.setId(request.requestPluginId());
        request.setClearLocalPlugins(true);
        updater.requestPlugin(request);
        Assert.assertEquals(request.getState(), UPD_NEED_EXTRACT);

        request = new TestAssetsRequest();
        Frontia.instance().add(request, SyncPluginManager.Mode.UPDATE | SyncPluginManager.Mode.LOAD);
        Assert.assertEquals(request.getState(), LOA_SUCCESS);

        request = new TestOnlineRequest();
        request.attach(Frontia.instance());
        request.setClearLocalPlugins(false);
        updater.requestPlugin(request);
        Assert.assertEquals(request.getState(), UPD_SUCCESS);

        request = new TestAssetsRequest();
        request.attach(Frontia.instance());
        request.setClearLocalPlugins(false);
        updater.requestPlugin(request);
        Assert.assertEquals(request.getState(), UPD_SUCCESS);
    }
}
