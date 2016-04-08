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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import moe.studio.frontia.CompatUtils;
import moe.studio.frontia.Frontia;
import moe.studio.frontia.ext.PluginError;
import moe.studio.frontia.ext.PluginSetting;

/**
 * @author kaede
 * @version date 2016/12/9
 */
@RunWith(AndroidJUnit4.class)
public class DependencyCompatTest {

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
    public void testFrontiaCompat() {
        Map<String, Integer> dependencies = new HashMap<>();
        dependencies.put("frontia", BuildConfig.VERSION_CODE);
        Exception exception = null;
        try {
            CompatUtils.checkCompat(dependencies, null);
        } catch (PluginError.LoadError error) {
            exception = error;
        }
        Assert.assertEquals(exception, null);

        dependencies.put("frontia", BuildConfig.VERSION_CODE - 1);
        try {
            CompatUtils.checkCompat(dependencies, null);
        } catch (PluginError.LoadError error) {
            exception = error;
        }
        Assert.assertEquals(exception, null);

        dependencies.put("frontia", BuildConfig.VERSION_CODE + 1);
        try {
            CompatUtils.checkCompat(dependencies, null);
        } catch (PluginError.LoadError error) {
            exception = error;
        }
        Assert.assertNotEquals(exception, null);
    }


    @Test
    public void testDependencyCompat() {
        Map<String, Integer> dependencies = new HashMap<>();
        dependencies.put("frontia", BuildConfig.VERSION_CODE);
        Exception exception = null;
        try {
            CompatUtils.checkCompat(dependencies, null);
        } catch (PluginError.LoadError error) {
            exception = error;
        }
        Assert.assertEquals(exception, null);

        dependencies.put("support_v4", 10);
        try {
            CompatUtils.checkCompat(dependencies, null);
        } catch (PluginError.LoadError error) {
            exception = error;
        }
        Assert.assertNotEquals(exception, null);

        exception = null;
        Set<String> ignores = new HashSet<>();
        ignores.add("support_v4");
        try {
            CompatUtils.checkCompat(dependencies, ignores);
        } catch (PluginError.LoadError error) {
            exception = error;
        }
        Assert.assertEquals(exception, null);

        Frontia.registerLibrary("support_v4", 9);
        try {
            CompatUtils.checkCompat(dependencies, null);
        } catch (PluginError.LoadError error) {
            exception = error;
        }
        Assert.assertNotEquals(exception, null);

        exception = null;
        try {
            Frontia.registerLibrary("support_v4", 10);
        } catch (Exception e) {
            exception = e;
        }
        Assert.assertNotEquals(exception, null);

        exception = null;
        dependencies.put("support_v7", 23);
        Frontia.registerLibrary("support_v7", 124);
        try {
            CompatUtils.checkCompat(dependencies, ignores);
        } catch (PluginError.LoadError error) {
            exception = error;
        }
        Assert.assertEquals(exception, null);
    }
}
