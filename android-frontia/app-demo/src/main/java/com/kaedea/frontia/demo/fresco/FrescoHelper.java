/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package com.kaedea.frontia.demo.fresco;

import android.content.Context;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.logging.FLog;
import com.facebook.common.soloader.SoLoaderShim;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

/**
 * Created by Kaede on 16/12/5.
 */

public class FrescoHelper {
    public static void init(Context context) {
        FLog.setMinimumLoggingLevel(FLog.VERBOSE);

        SoLoaderShim.setHandler(new SoLoaderShim.Handler() {
            @Override
            public void loadLibrary(String libraryName) {
//                Frontia.RequestState state = Frontia.instance().getRequestState(FrescoPackRequest.class);
//                if (state == null) {
//                    // Fresco's SoLibs pack has not been loaded. We can not use Fresco without SoLibs.
//                    throw new RuntimeException("Fresco's SoLibs pack is not loaded!");
//                }
            }
        });

        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(context.getExternalCacheDir())
                .setBaseDirectoryName("fresco_sample")
                .setMaxCacheSize(200*1024*1024)//200MB
                .build();
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig.newBuilder(context)
                .setMainDiskCacheConfig(diskCacheConfig)
                .build();
        Fresco.initialize(context, imagePipelineConfig);
    }
}
