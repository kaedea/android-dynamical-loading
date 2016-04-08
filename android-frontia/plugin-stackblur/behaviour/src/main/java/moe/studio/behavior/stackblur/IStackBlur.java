/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.behavior.stackblur;

import android.graphics.Bitmap;

import moe.studio.frontia.core.PluginBehavior;

/**
 * @author kaede
 * @version date 2016/12/5
 */

public interface IStackBlur extends PluginBehavior {

    void initSoLibs();

    Bitmap blur(Bitmap original, float radius);
}
