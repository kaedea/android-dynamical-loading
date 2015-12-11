/*
 * Copyright (C) 2014 singwhatiwanna(任玉刚) <singwhatiwanna@gmail.com>
 *
 * collaborator:田啸,宋思宇,Mr.Simple
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yy.mobile.core.ylink.dynamicload;

import android.annotation.TargetApi;
import android.app.*;
import android.content.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.*;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toolbar;
import com.yy.mobile.core.ylink.bridge.CoreApiManager;
import com.yy.mobile.core.ylink.dynamicload.core.DLIntent;
import com.yy.mobile.core.ylink.dynamicload.core.DLPluginManager;
import com.yy.mobile.core.ylink.dynamicload.core.DLPluginPackage;
import com.yy.mobile.core.ylink.utils.DLConstants;

import java.io.*;

/**
 * note: can use that like this.
 * 
 * @see {@link DLBasePluginActivity.that}
 * @author renyugang
 */
public class DLBasePluginActivity extends Activity implements DLPlugin {

    private static final String TAG = "DLBasePluginActivity";

    /**
     * 代理activity，可以当作Context来使用，会根据需要来决定是否指向this
     */
    protected Activity mProxyActivity;

    @Override
    public AssetManager getAssets() {
        return super.getAssets();
    }

    /**
     * 等同于mProxyActivity，可以当作Context来使用，会根据需要来决定是否指向this<br/>
     * 可以当作this来使用
     */
    protected Activity that;
    protected DLPluginManager mPluginManager;
    protected DLPluginPackage mPluginPackage;

    protected int mFrom = DLConstants.FROM_INTERNAL;

    @Override
    public void setIntent(Intent newIntent) {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            super.setIntent(newIntent);
        } else {
            mProxyActivity.setIntent(newIntent);
        }
    }

    @Override
    public void attach(Activity proxyActivity, DLPluginPackage pluginPackage) {
        Log.d(TAG, "attach: proxyActivity= " + proxyActivity);
        mProxyActivity = (Activity) proxyActivity;
        that = mProxyActivity;
        mPluginPackage = pluginPackage;
        CoreApiManager.getInstance().setActivity(that);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mFrom = savedInstanceState.getInt(DLConstants.FROM, DLConstants.FROM_INTERNAL);
        }

        if (mFrom == DLConstants.FROM_INTERNAL) {
            super.onCreate(savedInstanceState);
            mProxyActivity = this;
            that = mProxyActivity;
        }

        mPluginManager = DLPluginManager.getInstance(that);
        Log.d(TAG, "onCreate: from= "
                + (mFrom == DLConstants.FROM_INTERNAL ? "DLConstants.FROM_INTERNAL" : "FROM_EXTERNAL"));
    }

    @Override
    public void setContentView(View view) {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            super.setContentView(view);
        } else {
            mProxyActivity.setContentView(view);
        }
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            super.setContentView(view, params);
        } else {
            mProxyActivity.setContentView(view, params);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            super.setContentView(layoutResID);
        } else {
            mProxyActivity.setContentView(layoutResID);
        }
    }

    @Override
    public void addContentView(View view, LayoutParams params) {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            super.addContentView(view, params);
        } else {
            mProxyActivity.addContentView(view, params);
        }
    }

    @Override
    public View findViewById(int id) {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            return super.findViewById(id);
        } else {
            return mProxyActivity.findViewById(id);
        }
    }

    @Override
    public Intent getIntent() {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            return super.getIntent();
        } else {
            return mProxyActivity.getIntent();
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            return super.getClassLoader();
        } else {
            return mProxyActivity.getClassLoader();
        }
    }

    @Override
    public Resources getResources() {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            return super.getResources();
        } else {
            return mProxyActivity.getResources();
        }
    }

    @Override
    public String getPackageName() {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            return super.getPackageName();
        } else {
            return mPluginPackage.packageName;
        }
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            return super.getLayoutInflater();
        } else {
            return mProxyActivity.getLayoutInflater();
        }
    }

    @Override
    public MenuInflater getMenuInflater() {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            return super.getMenuInflater();
        } else {
            return mProxyActivity.getMenuInflater();
        }
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            return super.getSharedPreferences(name, mode);
        } else {
            return mProxyActivity.getSharedPreferences(name, mode);
        }
    }

    @Override
    public Context getApplicationContext() {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            return super.getApplicationContext();
        } else {
            return mProxyActivity.getApplicationContext();
        }
    }

    @Override
    public WindowManager getWindowManager() {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            return super.getWindowManager();
        } else {
            return mProxyActivity.getWindowManager();
        }
    }

    @Override
    public Window getWindow() {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            return super.getWindow();
        } else {
            return mProxyActivity.getWindow();
        }
    }

    @Override
    public Object getSystemService(String name) {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            return super.getSystemService(name);
        } else {
            return mProxyActivity.getSystemService(name);
        }
    }

    @Override
    public void finish() {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            super.finish();
        } else {
            mProxyActivity.finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStart() {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            super.onStart();
        }
    }

    @Override
    public void onRestart() {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            super.onRestart();
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            super.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            super.onSaveInstanceState(outState);
        }
    }

    public void onNewIntent(Intent intent) {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            super.onNewIntent(intent);
        }
    }

    @Override
    public void onResume() {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            super.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            super.onPause();
        }
    }

    @Override
    public void onStop() {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            super.onStop();
        }
    }

    @Override
    public void onDestroy() {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            super.onDestroy();
        }else {
            CoreApiManager.getInstance().setActivity(null);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            return super.onKeyUp(keyCode, event);
        }
        return false;
    }

    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            super.onWindowAttributesChanged(params);
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            super.onWindowFocusChanged(hasFocus);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            return super.onCreateOptionsMenu(menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mFrom == DLConstants.FROM_INTERNAL) {
            return onOptionsItemSelected(item);
        }
        return false;
    }

    /**
     * @param dlIntent
     * @return may be {@link #START_RESULT_SUCCESS},
     *         {@link #START_RESULT_NO_PKG}, {@link #START_RESULT_NO_CLASS},
     *         {@link #START_RESULT_TYPE_ERROR}
     */
    public int startPluginActivity(DLIntent dlIntent) {
        return startPluginActivityForResult(dlIntent, -1);
    }

    /**
     * @param dlIntent
     * @return may be {@link #START_RESULT_SUCCESS},
     *         {@link #START_RESULT_NO_PKG}, {@link #START_RESULT_NO_CLASS},
     *         {@link #START_RESULT_TYPE_ERROR}
     */
    public int startPluginActivityForResult(DLIntent dlIntent, int requestCode) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            if (dlIntent.getPluginPackage() == null) {
                dlIntent.setPluginPackage(mPluginPackage.packageName);
            }
        }
        return mPluginManager.startPluginActivityForResult(that, dlIntent, requestCode);
    }
    
    public int startPluginService(DLIntent dlIntent) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            if (dlIntent.getPluginPackage() == null) {
                dlIntent.setPluginPackage(mPluginPackage.packageName);
            }
        }
        return mPluginManager.startPluginService(that, dlIntent);
    }
    public int stopPluginService(DLIntent dlIntent) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            if (dlIntent.getPluginPackage() == null) {
                dlIntent.setPluginPackage(mPluginPackage.packageName);
            }
        }
        return mPluginManager.stopPluginService(that, dlIntent);
    }
    
    public int bindPluginService(DLIntent dlIntent, ServiceConnection conn, int flags) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            if (dlIntent.getPluginPackage() == null) {
                dlIntent.setPluginPackage(mPluginPackage.packageName);
            }
        }
        return mPluginManager.bindPluginService(that, dlIntent, conn, flags);
    }
    
    public int unBindPluginService(DLIntent dlIntent, ServiceConnection conn) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            if (dlIntent.getPluginPackage() == null)
            dlIntent.setPluginPackage(mPluginPackage.packageName);
        }
        return mPluginManager.unBindPluginService(that, dlIntent, conn);
    }

//    /**
//     * 直接调用that.startService
//     * that 可能有两种情况
//     * 1.指向this 
//     * 2.指向DLProxyActivity 
//     */
//    public ComponentName startService(Intent service) {
//        return that.startService(service);
//    }
//
//    @Override
//    public boolean stopService(Intent name) {
//        // TODO Auto-generated method stub
//        return super.stopService(name);
//    }
//
//    @Override
//    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
//        // TODO Auto-generated method stub
//        return super.bindService(service, conn, flags);
//    }
//
//    @Override
//    public void unbindService(ServiceConnection conn) {
//        // TODO Auto-generated method stub
//        super.unbindService(conn);
//    }


    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
    }

    @Override
    protected void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        super.onApplyThemeResource(theme, resid, first);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override
    protected void onChildTitleChanged(Activity childActivity, CharSequence title) {
        super.onChildTitleChanged(childActivity, title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public CharSequence onCreateDescription() {
        return super.onCreateDescription();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return super.onCreateDialog(id);
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        return super.onCreateDialog(id, args);
    }

    @Override
    public void onCreateNavigateUpTaskStack(TaskStackBuilder builder) {
        super.onCreateNavigateUpTaskStack(builder);
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public View onCreatePanelView(int featureId) {
        return super.onCreatePanelView(featureId);
    }

    @Override
    public boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas) {
        return super.onCreateThumbnail(outBitmap, canvas);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return super.onGenericMotionEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        return super.onKeyShortcut(keyCode, event);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onNavigateUp() {
        return super.onNavigateUp();
    }

    @Override
    public boolean onNavigateUpFromChild(Activity child) {
        return super.onNavigateUpFromChild(child);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        super.onPanelClosed(featureId, menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        super.onPrepareDialog(id, dialog, args);
    }

    @Override
    public void onPrepareNavigateUpTaskStack(TaskStackBuilder builder) {
        super.onPrepareNavigateUpTaskStack(builder);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        return super.onPreparePanel(featureId, view, menu);
    }

    @Override
    public void onProvideAssistData(Bundle data) {
        super.onProvideAssistData(data);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return super.onRetainNonConfigurationInstance();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public boolean onSearchRequested() {
        return super.onSearchRequested();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        return super.onTrackballEvent(event);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    @Override
    public void onVisibleBehindCanceled() {
        super.onVisibleBehindCanceled();
    }

    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        return super.onWindowStartingActionMode(callback);
    }


    /**
     * public methods
     */

    @Override
    public LoaderManager getLoaderManager() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getLoaderManager();
        } else {
            return super.getLoaderManager();
        }
    }

    @Override
    public View getCurrentFocus() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getCurrentFocus();
        } else {
            return super.getCurrentFocus();
        }
    }

    @Override
    public void closeContextMenu() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.closeContextMenu();
        } else {
            super.closeContextMenu();
        }
    }

    @Override
    public void closeOptionsMenu() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.closeOptionsMenu();
        } else {
            super.closeOptionsMenu();
        }
    }

    @Override
    public PendingIntent createPendingResult(int requestCode, Intent data, int flags) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.createPendingResult(requestCode, data, flags);
        } else {
            return super.createPendingResult(requestCode, data, flags);
        }
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.dispatchGenericMotionEvent(ev);
        } else {
            return super.dispatchGenericMotionEvent(ev);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.dispatchKeyEvent(event);
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.dispatchKeyShortcutEvent(event);
        } else {
            return super.dispatchKeyShortcutEvent(event);
        }
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.dispatchPopulateAccessibilityEvent(event);
        } else {
            return super.dispatchPopulateAccessibilityEvent(event);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.dispatchTouchEvent(ev);
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public boolean dispatchTrackballEvent(MotionEvent ev) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.dispatchTrackballEvent(ev);
        } else {
            return super.dispatchTrackballEvent(ev);
        }
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.dump(prefix, fd, writer, args);
        } else {
            super.dump(prefix, fd, writer, args);
        }
    }

    @Override
    public void finishActivity(int requestCode) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.finishActivity(requestCode);
        } else {
            super.finishActivity(requestCode);
        }
    }

    @Override
    public void finishActivityFromChild(Activity child, int requestCode) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.finishActivityFromChild(child, requestCode);
        } else {
            super.finishActivityFromChild(child, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void finishAffinity() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.finishAffinity();
        } else {
            super.finishAffinity();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void finishAfterTransition() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.finishAfterTransition();
        } else {
            super.finishAfterTransition();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void finishAndRemoveTask() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.finishAndRemoveTask();
        } else {
            super.finishAndRemoveTask();
        }
    }

    @Override
    public void finishFromChild(Activity child) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.finishFromChild(child);
        } else {
            super.finishFromChild(child);
        }
    }

    @Override
    public ActionBar getActionBar() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getActionBar();
        } else {
            return super.getActionBar();
        }
    }

    @Override
    public ComponentName getCallingActivity() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getCallingActivity();
        } else {
            return super.getCallingActivity();
        }
    }

    @Override
    public String getCallingPackage() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getCallingPackage();
        } else {
            return super.getCallingPackage();
        }
    }

    @Override
    public int getChangingConfigurations() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getChangingConfigurations();
        } else {
            return super.getChangingConfigurations();
        }
    }

    @Override
    public ComponentName getComponentName() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getComponentName();
        } else {
            return super.getComponentName();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Scene getContentScene() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getContentScene();
        } else {
            return super.getContentScene();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public TransitionManager getContentTransitionManager() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getContentTransitionManager();
        } else {
            return super.getContentTransitionManager();
        }
    }

    @Override
    public FragmentManager getFragmentManager() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getFragmentManager();
        } else {
            return super.getFragmentManager();
        }
    }

    @Override
    public Object getLastNonConfigurationInstance() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getLastNonConfigurationInstance();
        } else {
            return super.getLastNonConfigurationInstance();
        }
    }

    @Override
    public String getLocalClassName() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getLocalClassName();
        } else {
            return super.getLocalClassName();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getParentActivityIntent();
        } else {
            return super.getParentActivityIntent();
        }
    }

    @Override
    public SharedPreferences getPreferences(int mode) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getPreferences(mode);
        } else {
            return super.getPreferences(mode);
        }
    }

    @Override
    public int getRequestedOrientation() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getRequestedOrientation();
        } else {
            return super.getRequestedOrientation();
        }
    }

    @Override
    public int getTaskId() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getTaskId();
        } else {
            return super.getTaskId();
        }
    }

    @Override
    public boolean hasWindowFocus() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.hasWindowFocus();
        } else {
            return super.hasWindowFocus();
        }
    }

    @Override
    public void invalidateOptionsMenu() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.invalidateOptionsMenu();
        } else {
            super.invalidateOptionsMenu();
        }
    }

    @Override
    public boolean isChangingConfigurations() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.isChangingConfigurations();
        } else {
            return super.isChangingConfigurations();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public boolean isDestroyed() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.isDestroyed();
        } else {
            return super.isDestroyed();
        }
    }

    @Override
    public boolean isFinishing() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.isFinishing();
        } else {
            return super.isFinishing();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public boolean isImmersive() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.isImmersive();
        } else {
            return super.isImmersive();
        }
    }

    @Override
    public boolean isTaskRoot() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.isTaskRoot();
        } else {
            return super.isTaskRoot();
        }
    }

    @Override
    public boolean moveTaskToBack(boolean nonRoot) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.moveTaskToBack(nonRoot);
        } else {
            return super.moveTaskToBack(nonRoot);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean navigateUpTo(Intent upIntent) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.navigateUpTo(upIntent);
        } else {
            return super.navigateUpTo(upIntent);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean navigateUpToFromChild(Activity child, Intent upIntent) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.navigateUpToFromChild(child, upIntent);
        } else {
            return super.navigateUpToFromChild(child, upIntent);
        }
    }

    @Override
    public void openContextMenu(View view) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.openContextMenu(view);
        } else {
            super.openContextMenu(view);
        }
    }

    @Override
    public void openOptionsMenu() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.openOptionsMenu();
        } else {
            super.openOptionsMenu();
        }
    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.overridePendingTransition(enterAnim, exitAnim);
        } else {
            super.overridePendingTransition(enterAnim, exitAnim);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void postponeEnterTransition() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.postponeEnterTransition();
        } else {
            super.postponeEnterTransition();
        }
    }

    @Override
    public void recreate() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.recreate();
        } else {
            super.recreate();
        }
    }

    @Override
    public void registerForContextMenu(View view) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.registerForContextMenu(view);
        } else {
            super.registerForContextMenu(view);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean releaseInstance() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.releaseInstance();
        } else {
            return super.releaseInstance();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void reportFullyDrawn() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.reportFullyDrawn();
        } else {
            super.reportFullyDrawn();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean requestVisibleBehind(boolean visible) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.requestVisibleBehind(visible);
        } else {
            return super.requestVisibleBehind(visible);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setActionBar(Toolbar toolbar) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.setActionBar(toolbar);
        } else {
            super.setActionBar(toolbar);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setContentTransitionManager(TransitionManager tm) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.setContentTransitionManager(tm);
        } else {
            super.setContentTransitionManager(tm);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setEnterSharedElementCallback(SharedElementCallback callback) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.setEnterSharedElementCallback(callback);
        } else {
            super.setEnterSharedElementCallback(callback);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setExitSharedElementCallback(SharedElementCallback callback) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.setExitSharedElementCallback(callback);
        } else {
            super.setExitSharedElementCallback(callback);
        }
    }

    @Override
    public void setFinishOnTouchOutside(boolean finish) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.setFinishOnTouchOutside(finish);
        } else {
            super.setFinishOnTouchOutside(finish);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void setImmersive(boolean i) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.setImmersive(i);
        } else {
            super.setImmersive(i);
        }
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.setRequestedOrientation(requestedOrientation);
        } else {
            super.setRequestedOrientation(requestedOrientation);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setTaskDescription(ActivityManager.TaskDescription taskDescription) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.setTaskDescription(taskDescription);
        } else {
            super.setTaskDescription(taskDescription);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.setTitle(title);
        } else {
            super.setTitle(title);
        }
    }

    @Override
    public void setTitle(int titleId) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.setTitle(titleId);
        } else {
            super.setTitle(titleId);
        }
    }

    @Override
    public void setTitleColor(int textColor) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.setTitleColor(textColor);
        } else {
            super.setTitleColor(textColor);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.setVisible(visible);
        } else {
            super.setVisible(visible);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean shouldUpRecreateTask(Intent targetIntent) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.shouldUpRecreateTask(targetIntent);
        } else {
            return super.shouldUpRecreateTask(targetIntent);
        }
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.startActionMode(callback);
        } else {
            return super.startActionMode(callback);
        }
    }

    @Override
    public void startActivities(Intent[] intents) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.startActivities(intents);
        } else {
            super.startActivities(intents);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void startActivities(Intent[] intents, Bundle options) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.startActivities(intents, options);
        } else {
            super.startActivities(intents, options);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.startActivity(intent);
        } else {
            super.startActivity(intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void startActivity(Intent intent, Bundle options) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.startActivity(intent, options);
        } else {
            super.startActivity(intent, options);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.startActivityForResult(intent, requestCode);
        } else {
            super.startActivityForResult(intent, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.startActivityForResult(intent, requestCode, options);
        } else {
            super.startActivityForResult(intent, requestCode, options);
        }
    }

    @Override
    public void startActivityFromChild(Activity child, Intent intent, int requestCode) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.startActivityFromChild(child, intent, requestCode);
        } else {
            super.startActivityFromChild(child, intent, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void startActivityFromChild(Activity child, Intent intent, int requestCode, Bundle options) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.startActivityFromChild(child, intent, requestCode, options);
        } else {
            super.startActivityFromChild(child, intent, requestCode, options);
        }
    }

    @Override
    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.startActivityFromFragment(fragment, intent, requestCode);
        } else {
            super.startActivityFromFragment(fragment, intent, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode, Bundle options) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.startActivityFromFragment(fragment, intent, requestCode, options);
        } else {
            super.startActivityFromFragment(fragment, intent, requestCode, options);
        }
    }

    @Override
    public boolean startActivityIfNeeded(Intent intent, int requestCode) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.startActivityIfNeeded(intent, requestCode);
        } else {
            return super.startActivityIfNeeded(intent, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean startActivityIfNeeded(Intent intent, int requestCode, Bundle options) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.startActivityIfNeeded(intent, requestCode, options);
        } else {
            return super.startActivityIfNeeded(intent, requestCode, options);
        }
    }

    @Override
    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags);
        } else {
            super.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags, options);
        } else {
            super.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags, options);
        }
    }

    @Override
    public void startIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags);
        } else {
            super.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void startIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
        } else {
            super.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
        }
    }

    @Override
    public void startIntentSenderFromChild(Activity child, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.startIntentSenderFromChild(child, intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags);
        } else {
            super.startIntentSenderFromChild(child, intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void startIntentSenderFromChild(Activity child, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.startIntentSenderFromChild(child, intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
        } else {
            super.startIntentSenderFromChild(child, intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void startLockTask() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.startLockTask();
        } else {
            super.startLockTask();
        }
    }

    @Override
    public void startManagingCursor(Cursor c) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.startManagingCursor(c);
        } else {
            super.startManagingCursor(c);
        }
    }

    @Override
    public boolean startNextMatchingActivity(Intent intent) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.startNextMatchingActivity(intent);
        } else {
            return super.startNextMatchingActivity(intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean startNextMatchingActivity(Intent intent, Bundle options) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.startNextMatchingActivity(intent, options);
        } else {
            return super.startNextMatchingActivity(intent, options);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void startPostponedEnterTransition() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.startPostponedEnterTransition();
        } else {
            super.startPostponedEnterTransition();
        }
    }

    @Override
    public void startSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData, boolean globalSearch) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.startSearch(initialQuery, selectInitialQuery, appSearchData, globalSearch);
        } else {
            super.startSearch(initialQuery, selectInitialQuery, appSearchData, globalSearch);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void stopLockTask() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.stopLockTask();
        } else {
            super.stopLockTask();
        }
    }

    @Override
    public void stopManagingCursor(Cursor c) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.stopManagingCursor(c);
        } else {
            super.stopManagingCursor(c);
        }
    }

    @Override
    public void takeKeyEvents(boolean get) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.takeKeyEvents(get);
        } else {
            super.takeKeyEvents(get);
        }
    }

    @Override
    public void triggerSearch(String query, Bundle appSearchData) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.triggerSearch(query, appSearchData);
        } else {
            super.triggerSearch(query, appSearchData);
        }
    }

    @Override
    public void unregisterForContextMenu(View view) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.unregisterForContextMenu(view);
        } else {
            super.unregisterForContextMenu(view);
        }
    }


    /**
     * ContextThemeWrapper
     * */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.applyOverrideConfiguration(overrideConfiguration);
        } else {
            super.applyOverrideConfiguration(overrideConfiguration);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        /*if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.attachBaseContext(newBase);
        } else {
            super.attachBaseContext(newBase);
        }*/
        super.attachBaseContext(newBase);
    }

    @Override
    public Resources.Theme getTheme() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getTheme();
        } else {
            return super.getTheme();
        }
    }

    @Override
    public void setTheme(int resid) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.setTheme(resid);
        } else {
            super.setTheme(resid);
        }
    }

    /**
     * ContextWrapper
     * */

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.bindService(service, conn, flags);
        } else {
            return super.bindService(service, conn, flags);
        }
    }

    @Override
    public int checkCallingOrSelfPermission(String permission) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.checkCallingOrSelfPermission(permission);
        } else {
            return super.checkCallingOrSelfPermission(permission);
        }
    }

    @Override
    public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.checkCallingOrSelfUriPermission(uri, modeFlags);
        } else {
            return super.checkCallingOrSelfUriPermission(uri, modeFlags);
        }
    }

    @Override
    public int checkCallingPermission(String permission) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.checkCallingPermission(permission);
        } else {
            return super.checkCallingPermission(permission);
        }
    }

    @Override
    public int checkCallingUriPermission(Uri uri, int modeFlags) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.checkCallingUriPermission(uri, modeFlags);
        } else {
            return super.checkCallingUriPermission(uri, modeFlags);
        }
    }

    @Override
    public int checkPermission(String permission, int pid, int uid) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.checkPermission(permission, pid, uid);
        } else {
            return super.checkPermission(permission, pid, uid);
        }
    }

    @Override
    public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.checkUriPermission(uri, pid, uid, modeFlags);
        } else {
            return super.checkUriPermission(uri, pid, uid, modeFlags);
        }
    }

    @Override
    public int checkUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.checkUriPermission(uri, readPermission, writePermission, pid, uid, modeFlags);
        } else {
            return super.checkUriPermission(uri, readPermission, writePermission, pid, uid, modeFlags);
        }
    }

    @Override
    public void clearWallpaper() throws IOException {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.clearWallpaper();
        } else {
            super.clearWallpaper();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public Context createConfigurationContext(Configuration overrideConfiguration) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.createConfigurationContext(overrideConfiguration);
        } else {
            return super.createConfigurationContext(overrideConfiguration);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public Context createDisplayContext(Display display) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.createDisplayContext(display);
        } else {
            return super.createDisplayContext(display);
        }
    }

    @Override
    public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.createPackageContext(packageName, flags);
        } else {
            return super.createPackageContext(packageName, flags);
        }
    }

    @Override
    public String[] databaseList() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.databaseList();
        } else {
            return super.databaseList();
        }
    }

    @Override
    public boolean deleteDatabase(String name) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.deleteDatabase(name);
        } else {
            return super.deleteDatabase(name);
        }
    }

    @Override
    public boolean deleteFile(String name) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.deleteFile(name);
        } else {
            return super.deleteFile(name);
        }
    }

    @Override
    public void enforceCallingOrSelfPermission(String permission, String message) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.enforceCallingOrSelfPermission(permission, message);
        } else {
            super.enforceCallingOrSelfPermission(permission, message);
        }
    }

    @Override
    public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags, String message) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.enforceCallingOrSelfUriPermission(uri, modeFlags, message);
        } else {
            super.enforceCallingOrSelfUriPermission(uri, modeFlags, message);
        }
    }

    @Override
    public void enforceCallingPermission(String permission, String message) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.enforceCallingPermission(permission, message);
        } else {
            super.enforceCallingPermission(permission, message);
        }
    }

    @Override
    public void enforceCallingUriPermission(Uri uri, int modeFlags, String message) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.enforceCallingUriPermission(uri, modeFlags, message);
        } else {
            super.enforceCallingUriPermission(uri, modeFlags, message);
        }
    }

    @Override
    public void enforcePermission(String permission, int pid, int uid, String message) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.enforcePermission(permission, pid, uid, message);
        } else {
            super.enforcePermission(permission, pid, uid, message);
        }
    }

    @Override
    public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags, String message) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.enforceUriPermission(uri, pid, uid, modeFlags, message);
        } else {
            super.enforceUriPermission(uri, pid, uid, modeFlags, message);
        }
    }

    @Override
    public void enforceUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags, String message) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.enforceUriPermission(uri, readPermission, writePermission, pid, uid, modeFlags, message);
        } else {
            super.enforceUriPermission(uri, readPermission, writePermission, pid, uid, modeFlags, message);
        }
    }

    @Override
    public String[] fileList() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.fileList();
        } else {
            return super.fileList();
        }
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getApplicationInfo();
        } else {
            return super.getApplicationInfo();
        }
    }

    @Override
    public Context getBaseContext() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getBaseContext();
        } else {
            return super.getBaseContext();
        }
    }

    @Override
    public File getCacheDir() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getCacheDir();
        } else {
            return super.getCacheDir();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public File getCodeCacheDir() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getCodeCacheDir();
        } else {
            return super.getCodeCacheDir();
        }
    }

    @Override
    public ContentResolver getContentResolver() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getContentResolver();
        } else {
            return super.getContentResolver();
        }
    }

    @Override
    public File getDatabasePath(String name) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getDatabasePath(name);
        } else {
            return super.getDatabasePath(name);
        }
    }

    @Override
    public File getDir(String name, int mode) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getDir(name, mode);
        } else {
            return super.getDir(name, mode);
        }
    }

    @Override
    public File getExternalCacheDir() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getExternalCacheDir();
        } else {
            return super.getExternalCacheDir();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public File[] getExternalCacheDirs() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getExternalCacheDirs();
        } else {
            return super.getExternalCacheDirs();
        }
    }

    @Override
    public File getExternalFilesDir(String type) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getExternalFilesDir(type);
        } else {
            return super.getExternalFilesDir(type);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public File[] getExternalFilesDirs(String type) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getExternalFilesDirs(type);
        } else {
            return super.getExternalFilesDirs(type);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public File[] getExternalMediaDirs() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getExternalMediaDirs();
        } else {
            return super.getExternalMediaDirs();
        }
    }

    @Override
    public File getFilesDir() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getFilesDir();
        } else {
            return super.getFilesDir();
        }
    }

    @Override
    public File getFileStreamPath(String name) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getFileStreamPath(name);
        } else {
            return super.getFileStreamPath(name);
        }
    }

    @Override
    public Looper getMainLooper() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getMainLooper();
        } else {
            return super.getMainLooper();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public File getNoBackupFilesDir() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getNoBackupFilesDir();
        } else {
            return super.getNoBackupFilesDir();
        }
    }

    @Override
    public File getObbDir() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getObbDir();
        } else {
            return super.getObbDir();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public File[] getObbDirs() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getObbDirs();
        } else {
            return super.getObbDirs();
        }
    }

    @Override
    public String getPackageCodePath() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getPackageCodePath();
        } else {
            return super.getPackageCodePath();
        }
    }

    @Override
    public PackageManager getPackageManager() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getPackageManager();
        } else {
            return super.getPackageManager();
        }
    }

    @Override
    public String getPackageResourcePath() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getPackageResourcePath();
        } else {
            return super.getPackageResourcePath();
        }
    }

    @Override
    public Drawable getWallpaper() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getWallpaper();
        } else {
            return super.getWallpaper();
        }
    }

    @Override
    public int getWallpaperDesiredMinimumHeight() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getWallpaperDesiredMinimumHeight();
        } else {
            return super.getWallpaperDesiredMinimumHeight();
        }
    }

    @Override
    public int getWallpaperDesiredMinimumWidth() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.getWallpaperDesiredMinimumWidth();
        } else {
            return super.getWallpaperDesiredMinimumWidth();
        }
    }

    @Override
    public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.grantUriPermission(toPackage, uri, modeFlags);
        } else {
            super.grantUriPermission(toPackage, uri, modeFlags);
        }
    }

    @Override
    public boolean isRestricted() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.isRestricted();
        } else {
            return super.isRestricted();
        }
    }

    @Override
    public FileInputStream openFileInput(String name) throws FileNotFoundException {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.openFileInput(name);
        } else {
            return super.openFileInput(name);
        }
    }

    @Override
    public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.openFileOutput(name, mode);
        } else {
            return super.openFileOutput(name, mode);
        }
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.openOrCreateDatabase(name, mode, factory);
        } else {
            return super.openOrCreateDatabase(name, mode, factory);
        }
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.openOrCreateDatabase(name, mode, factory, errorHandler);
        } else {
            return super.openOrCreateDatabase(name, mode, factory, errorHandler);
        }
    }

    @Override
    public Drawable peekWallpaper() {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.peekWallpaper();
        } else {
            return super.peekWallpaper();
        }
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.registerReceiver(receiver, filter);
        } else {
            return super.registerReceiver(receiver, filter);
        }
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.registerReceiver(receiver, filter, broadcastPermission, scheduler);
        } else {
            return super.registerReceiver(receiver, filter, broadcastPermission, scheduler);
        }
    }

    @Override
    public void removeStickyBroadcast(Intent intent) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.removeStickyBroadcast(intent);
        } else {
            super.removeStickyBroadcast(intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void removeStickyBroadcastAsUser(Intent intent, UserHandle user) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.removeStickyBroadcastAsUser(intent, user);
        } else {
            super.removeStickyBroadcastAsUser(intent, user);
        }
    }

    @Override
    public void revokeUriPermission(Uri uri, int modeFlags) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.revokeUriPermission(uri, modeFlags);
        } else {
            super.revokeUriPermission(uri, modeFlags);
        }
    }

    @Override
    public void sendBroadcast(Intent intent) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.sendBroadcast(intent);
        } else {
            super.sendBroadcast(intent);
        }
    }

    @Override
    public void sendBroadcast(Intent intent, String receiverPermission) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.sendBroadcast(intent, receiverPermission);
        } else {
            super.sendBroadcast(intent, receiverPermission);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.sendBroadcastAsUser(intent, user);
        } else {
            super.sendBroadcastAsUser(intent, user);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.sendBroadcastAsUser(intent, user, receiverPermission);
        } else {
            super.sendBroadcastAsUser(intent, user, receiverPermission);
        }
    }

    @Override
    public void sendOrderedBroadcast(Intent intent, String receiverPermission) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.sendOrderedBroadcast(intent, receiverPermission);
        } else {
            super.sendOrderedBroadcast(intent, receiverPermission);
        }
    }

    @Override
    public void sendOrderedBroadcast(Intent intent, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.sendOrderedBroadcast(intent, receiverPermission, resultReceiver, scheduler, initialCode, initialData, initialExtras);
        } else {
            super.sendOrderedBroadcast(intent, receiverPermission, resultReceiver, scheduler, initialCode, initialData, initialExtras);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.sendOrderedBroadcastAsUser(intent, user, receiverPermission, resultReceiver, scheduler, initialCode, initialData, initialExtras);
        } else {
            super.sendOrderedBroadcastAsUser(intent, user, receiverPermission, resultReceiver, scheduler, initialCode, initialData, initialExtras);
        }
    }

    @Override
    public void sendStickyBroadcast(Intent intent) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.sendStickyBroadcast(intent);
        } else {
            super.sendStickyBroadcast(intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void sendStickyBroadcastAsUser(Intent intent, UserHandle user) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.sendStickyBroadcastAsUser(intent, user);
        } else {
            super.sendStickyBroadcastAsUser(intent, user);
        }
    }

    @Override
    public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.sendStickyOrderedBroadcast(intent, resultReceiver, scheduler, initialCode, initialData, initialExtras);
        } else {
            super.sendStickyOrderedBroadcast(intent, resultReceiver, scheduler, initialCode, initialData, initialExtras);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle user, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.sendStickyOrderedBroadcastAsUser(intent, user, resultReceiver, scheduler, initialCode, initialData, initialExtras);
        } else {
            super.sendStickyOrderedBroadcastAsUser(intent, user, resultReceiver, scheduler, initialCode, initialData, initialExtras);
        }
    }

    @Override
    public void setWallpaper(Bitmap bitmap) throws IOException {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.setWallpaper(bitmap);
        } else {
            super.setWallpaper(bitmap);
        }
    }

    @Override
    public void setWallpaper(InputStream data) throws IOException {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.setWallpaper(data);
        } else {
            super.setWallpaper(data);
        }
    }

    @Override
    public boolean startInstrumentation(ComponentName className, String profileFile, Bundle arguments) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.startInstrumentation(className, profileFile, arguments);
        } else {
            return super.startInstrumentation(className, profileFile, arguments);
        }
    }

    @Override
    public ComponentName startService(Intent service) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.startService(service);
        } else {
            return super.startService(service);
        }
    }

    @Override
    public boolean stopService(Intent name) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            return mProxyActivity.stopService(name);
        } else {
            return super.stopService(name);
        }
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.unbindService(conn);
        } else {
            super.unbindService(conn);
        }
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.unregisterReceiver(receiver);
        } else {
            super.unregisterReceiver(receiver);
        }
    }

    /**
     * Context
     * */

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.registerComponentCallbacks(callback);
        } else {
            super.registerComponentCallbacks(callback);
        }
    }

    @Override
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        if (mFrom == DLConstants.FROM_EXTERNAL) {
            mProxyActivity.unregisterComponentCallbacks(callback);
        } else {
            super.unregisterComponentCallbacks(callback);
        }
    }
}
