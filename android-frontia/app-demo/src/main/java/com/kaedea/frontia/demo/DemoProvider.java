/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package com.kaedea.frontia.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import com.kaedea.frontia.demo.blur.BlurActivity;
import com.kaedea.frontia.demo.fresco.FrescoActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kaede on 16/8/10.
 */
public class DemoProvider {

    public static ArrayMap<String, ActivityHolder> demos;

    static {
        demos = new ArrayMap<>();
        ActivityHolder camera = new ActivityHolder();

        // default demos
        camera.addActivity("Fresco SoLibs Pack", "Plugin package for Fresco so libs.", FrescoActivity.class);
        camera.addActivity("StackBlur Plugin", "A plugin that implements stack blur.", BlurActivity.class);
//        camera.addActivity("Video Plugin", "A plugin that implements Video sdk.", VideoActivity.class);
        demos.put("Default", camera);
    }

    /**
     * list and navigate demo activities.
     * Created by kaede on 2015/10/13.
     */
    public static class ActivityHolder {

        List<ActivityEntry> entries;

        ActivityHolder() {
            entries = new ArrayList<>();
        }

        public int getCount() {
            return entries.size();
        }

        void addActivity(@NonNull String name, String description, Class<? extends Activity> activity, Intent intent) {
            ActivityEntry activityHolder = new ActivityEntry();
            activityHolder.name = name;
            activityHolder.description = description;
            activityHolder.activity = activity;
            activityHolder.intent = intent;
            entries.add(activityHolder);
        }

        void addActivity(String name, String description, Class<? extends Activity> activity) {
            addActivity(name, description, activity, null);
        }

        public String getActivityName(int position) {
            return entries.get(position).name;
        }

        public String getActivityDesc(int position) {
            return entries.get(position).description;
        }

        public Class<? extends Activity> getActivity(int position) {
            return entries.get(position).activity;
        }

        public Intent getIntent(int position) {
            return entries.get(position).intent;
        }

        public void startActivity(Context context, int position) {
            Intent intent = getIntent(position);
            if (intent == null) {
                context.startActivity(new Intent(context, getActivity(position)));
                return;
            }
            intent.setClass(context, getActivity(position));
            context.startActivity(intent);
        }

        public static class ActivityEntry {
            String name;
            String description;
            Class<? extends Activity> activity;
            Intent intent;
        }
    }
}
