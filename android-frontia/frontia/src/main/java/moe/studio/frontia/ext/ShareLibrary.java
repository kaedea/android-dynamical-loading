/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia.ext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import moe.studio.frontia.BuildConfig;
import moe.studio.frontia.SoLibPlugin;
import moe.studio.frontia.core.PluginBehavior;

/**
 * @author kaede
 * @version date 2016/12/5
 */

public class ShareLibrary {

    public static final String TAG = "plugin.ShareLibrary";

    /**
     * @author kaede
     * @version date 2016/12/5
     */
    public interface SoLibBehavior extends PluginBehavior {
        void loadLibrary();

        Set<File> getLibrary();
    }

    /**
     * @author kaede
     * @version date 2016/12/5
     */
    public static class SoLibPackage extends SoLibPlugin<SoLibBehavior> {

        public SoLibPackage(String apkPath) {
            super(apkPath);
        }

        @Override
        public SoLibBehavior createBehavior(Context context) {
            return new SoLibBehavior() {
                private final byte[] mLock = new byte[0];
                private boolean mIsLoaded;

                @Override
                @SuppressLint("UnsafeDynamicallyLoadedCode")
                public void loadLibrary() {
                    if (!mIsLoaded) {
                        synchronized (mLock) {
                            if (!mIsLoaded) {
                                for (File item : mSoLibs) {
                                    if (BuildConfig.DEBUG) {
                                        Log.i(ShareLibrary.TAG, "Load share library, path = "
                                                + item.getAbsolutePath());
                                    }
                                    System.load(item.getAbsolutePath());
                                }
                                mIsLoaded = true;
                                return;
                            }
                        }
                    }
                    Log.w(TAG, "Libraries have already been loaded once.");
                }

                @Override
                public Set<File> getLibrary() {
                    return mSoLibs;
                }
            };
        }

        @Override
        protected void installSoLib(Context context, File apkFile, File soLibDir) throws IOException {
            super.installSoLib(context, apkFile, soLibDir);

            // As for SoLibs package plugin, we do not create plugin's ClassLoader with SoLibs.
            mSoLibDir = null;
        }
    }
}
