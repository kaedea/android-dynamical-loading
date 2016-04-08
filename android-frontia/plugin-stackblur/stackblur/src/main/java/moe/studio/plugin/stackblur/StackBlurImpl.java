/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.plugin.stackblur;

import android.graphics.Bitmap;
import android.util.Log;

import moe.studio.behavior.stackblur.IStackBlur;

/**
 * @author kaede
 * @version date 2016/12/5
 */

public class StackBlurImpl implements IStackBlur {

    @Override
    public void initSoLibs() {
        // load so file from internal directory
        try {
            System.loadLibrary("stackblur");
            Log.i("MainActivity", "loadLibrary success!");
            NativeBlurProcess.isLoadLibraryOk.set(true);
        } catch (Throwable throwable) {
            Log.i("MainActivity", "loadLibrary error!" + throwable);
        }
    }

    @Override
    public Bitmap blur(Bitmap original, float radius) {
        return NativeBlurProcess.blur(original, radius);
    }
}
