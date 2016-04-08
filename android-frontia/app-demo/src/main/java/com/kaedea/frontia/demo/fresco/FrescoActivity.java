/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package com.kaedea.frontia.demo.fresco;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.view.SimpleDraweeView;

import edu.gemini.tinyplayer.R;
import moe.studio.frontia.Frontia;
import moe.studio.frontia.ext.PluginListener.ListenerImpl;
import moe.studio.frontia.ext.ShareLibrary.SoLibBehavior;
import moe.studio.frontia.ext.ShareLibrary.SoLibPackage;

import static moe.studio.frontia.SyncPluginManager.Mode.LOAD;
import static moe.studio.frontia.SyncPluginManager.Mode.UPDATE;

public class FrescoActivity extends AppCompatActivity {

    public static final String IMAGE_URL = "http://dn-assets-gitcafe-com.qbox.me/" +
            "Kaedea/Kaede-Assets/raw/gitcafe-pages/image/girly/jk-01.jpg";

    private boolean mIsLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fresco);
    }

    public void onLoadSoLibs(View view) {
        FrescoPackRequest request = new FrescoPackRequest();
        request.setListener(new PluginListner());
        Frontia.instance().addAsync(request, UPDATE | LOAD);
    }

    public void onLoadImage(View view) {
        if (!mIsLoaded) {
            // Sync load Fresco's SoLibs pack.
            FrescoPackRequest request = new FrescoPackRequest();
            request.setListener(new PluginListner());
            Frontia.instance().add(request, UPDATE | LOAD);
        }

        SimpleDraweeView drawee = new SimpleDraweeView(this);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.iv_gif);
        viewGroup.addView(drawee, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        drawee.setImageURI(Uri.parse(IMAGE_URL));
    }

    private class PluginListner extends ListenerImpl<SoLibBehavior, SoLibPackage, FrescoPackRequest> {
        @Override
        public void onGetBehavior(FrescoPackRequest request, SoLibPackage plugin, SoLibBehavior behavior) {
            // Load Success
            behavior.loadLibrary();
            mIsLoaded = true;
            FLog.setMinimumLoggingLevel(FLog.VERBOSE);
            FrescoHelper.init(getApplicationContext());
        }
    }

}
