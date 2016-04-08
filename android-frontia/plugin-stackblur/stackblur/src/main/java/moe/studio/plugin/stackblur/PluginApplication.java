/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.plugin.stackblur;

import moe.studio.frontia.core.PluginApp;

/**
 * @author kaede
 * @version date 2016/12/5
 */

public class PluginApplication extends PluginApp {
    @Override
    public StackBlurImpl getBehavior() {
        return new StackBlurImpl();
    }
}
