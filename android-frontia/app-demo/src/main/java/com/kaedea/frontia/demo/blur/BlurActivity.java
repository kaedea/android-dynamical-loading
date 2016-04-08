/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package com.kaedea.frontia.demo.blur;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import edu.gemini.tinyplayer.R;
import moe.studio.behavior.stackblur.IStackBlur;
import moe.studio.behavior.stackblur.StackBlurPlugin;
import moe.studio.frontia.Frontia;
import moe.studio.frontia.ext.PluginListener;
import moe.studio.frontia.ext.PluginListener.ListenerImpl;

import static moe.studio.frontia.SyncPluginManager.Mode.LOAD;
import static moe.studio.frontia.SyncPluginManager.Mode.UPDATE;

public class BlurActivity extends AppCompatActivity {

    private IStackBlur mStackBlur;
    private ListenerImpl<IStackBlur, StackBlurPlugin, StackBlurRequest> mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur);

        mListener = new PluginListener.ListenerImpl<IStackBlur, StackBlurPlugin,
                StackBlurRequest>() {
            @Override
            public void onGetBehavior(StackBlurRequest request, StackBlurPlugin plugin,
                                      IStackBlur behavior) {
                mStackBlur = behavior;
                mStackBlur.initSoLibs();
            }

        };
    }

    public void onLoadSoLibs(View view) {
        StackBlurRequest request = new StackBlurRequest();
        request.setListener(mListener);
        Frontia.instance().addAsync(request, LOAD | UPDATE);
    }

    public void onDoBlur(View view) {
        if (mStackBlur == null) {
            StackBlurRequest request = new StackBlurRequest();
            request.setListener(mListener);
            Frontia.instance().add(request, LOAD | UPDATE);
        }

        ImageView imageView = (ImageView) findViewById(R.id.iv_app);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.sym_def_app_icon);
        Bitmap blur = mStackBlur.blur(bitmap, 20);
        imageView.setImageBitmap(blur);
    }
}
