/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import moe.studio.frontia.BuildConfig;
import moe.studio.frontia.core.Plugin;
import moe.studio.frontia.core.PluginBehavior;
import moe.studio.frontia.core.PluginLoader;
import moe.studio.frontia.core.PluginManager;
import moe.studio.frontia.core.PluginRequest;
import moe.studio.frontia.ext.PluginApk;
import moe.studio.frontia.ext.PluginError;
import moe.studio.frontia.ext.ProxyHandler;
import moe.studio.frontia.core.PluginApp;

/**
 * 插件加载器。
 * <p>
 * 用于加载指定路径上的插件, 同时保存已经加载过的插件。
 */
@SuppressWarnings("unchecked")
class PluginLoaderImpl implements PluginLoader {

    public static final String TAG = "plugin.loader";

    private final Context mContext;
    private final Map<String, Plugin> mPackageHolder;

    PluginLoaderImpl(Context context) {
        mContext = context.getApplicationContext();
        mPackageHolder = new HashMap<>();
    }

    /**
     * "加载插件"
     *
     * @param request 更新状态
     * @return 更新状态
     */
    @Override
    public PluginRequest load(@NonNull final PluginRequest request) {
        Logger.i(TAG, "Loading plugin, id = " + request.getId());
        request.marker("Load");

        onPreLoad(request);

        if (request.isCanceled()) {
            onCanceled(request);
            return request;
        }

        if (request.getState() == PluginRequest.State.UPD_SUCCESS) {
            String path = request.getPluginPath();
            if (!TextUtils.isEmpty(path)) {
                // Plugin was updated, start to load plugin.
                Plugin plugin = request.createPlugin(path)
                        .attach(request.getManager());

                int retry = 0;
                request.setRetry(request.getManager().getSetting().getRetryCount());

                while (true) {
                    if (request.isCanceled()) {
                        onCanceled(request);
                        return request;
                    }

                    try {
                        request.setPlugin(load(request.getManager(), plugin));
                        Logger.v(TAG, "Load plugin success, path = " + path);
                        request.switchState(PluginRequest.State.LOA_SUCCESS);
                        onPostLoad(request);
                        return request;

                    } catch (PluginError.LoadError | PluginError.InstallError error) {
                        Logger.w(TAG, error);
                        try {
                            request.retry();
                            Logger.v(TAG, "Load fail, retry " + (retry++));
                            request.marker("Retry load " + retry);
                        } catch (PluginError.RetryError retryError) {
                            Logger.v(TAG, "Load plugin fail, error = "
                                    + error.toString());
                            onError(request, error);
                            return request;
                        }
                    }
                }

            } else {
                // Should not have this state.
                request.switchState(PluginRequest.State.WTF);
                onPostLoad(request);
                return request;
            }

        } else {
            onPostLoad(request);
            return request;
        }
    }

    @Override
    public Plugin load(PluginManager manager, Plugin plugin) throws PluginError.LoadError, PluginError.InstallError {
        String apkPath = plugin.getApkPath();
        File apk = new File(apkPath);
        Logger.d(TAG, "Loading plugin, path = " + apkPath);

        if (!apk.exists()) {
            throw new PluginError.LoadError("Apk file not exist.", PluginError.ERROR_INS_NOT_FOUND);
        }

        PluginApk pluginApk;

        try {
            pluginApk = ManifestUtils.parse(new File(apkPath));
            plugin.setPackage(pluginApk);

            CompatUtils.checkCompat(plugin.getPackage().dependencies, plugin.getPackage().ignores);
            Logger.d(TAG, "Check plugin dependency compat success.");

            if (TextUtils.isEmpty(pluginApk.packageName)) {
                throw new IOException("Can not get plugin's pkg name.");
            }
            if (TextUtils.isEmpty(pluginApk.versionCode)) {
                throw new IOException("Can not get plugin's version code.");
            }
        } catch (IOException e) {
            Logger.w(TAG, e);
            throw new PluginError.InstallError("Can not get target plugin's packageInfo.",
                    PluginError.ERROR_INS_PACKAGE_INFO);
        }

        // Check if the current version has been installed before.
        if (manager.getInstaller().isInstalled(pluginApk.packageName, pluginApk.versionCode)) {
            String installPath = manager.getInstaller().getInstallPath(pluginApk.packageName,
                    pluginApk.versionCode);

            if (Internals.FileUtils.exist(installPath)) {
                Logger.v(TAG, "The current version has been installed before.");
                plugin.setInstallPath(installPath);
                Plugin loaded = getPlugin(pluginApk.packageName);

                if (loaded != null) {
                    // The current plugin has been loaded.
                    Logger.v(TAG, "The current plugin has been loaded, id = "
                            + pluginApk.packageName);
                    return loaded;
                }

                // Load plugin from installed path.
                Logger.v(TAG, "Load plugin from installed path.");
                plugin = plugin.loadPlugin(mContext, installPath);
                putPlugin(pluginApk.packageName, plugin);
                return plugin;
            }
        }

        // The current plugin version is not yet installed.
        Logger.v(TAG, "Plugin not installed, load it from target path.");
        Plugin loaded = getPlugin(pluginApk.packageName);

        if (loaded != null) {
            Logger.v(TAG, "The current plugin has been loaded, id = "
                    + pluginApk.packageName);
            return loaded;
        }

        Logger.v(TAG, "Load plugin from dest path.");

        // Install the dest file into inner install dir.
        String install = manager.getInstaller().install(apkPath);
        plugin.setInstallPath(install);

        plugin = plugin.loadPlugin(mContext, install);
        putPlugin(pluginApk.packageName, plugin);

        // Delete temp file.
        if (apkPath.endsWith(manager.getSetting().getTempFileSuffix())) {
            Internals.FileUtils.delete(apkPath);
        }

        return plugin;
    }

    @Override
    public synchronized Plugin getPlugin(String packageName) {
        Plugin plugin = mPackageHolder.get(packageName);
        if (plugin != null && !plugin.isLoaded()) {
            return null;
        }
        return plugin;
    }

    @Override
    public synchronized void putPlugin(String id, Plugin plugin) {
        if (plugin != null && plugin.isLoaded()) {
            mPackageHolder.put(id, plugin);
        }
    }

    @Override
    public Class loadClass(@NonNull Plugin plugin, String className) throws PluginError.LoadError {
        if (!plugin.isLoaded()) {
            throw new PluginError.LoadError("Plug is not yet loaded.", PluginError.ERROR_LOA_NOT_LOADED);
        }

        try {
            return Internals.ApkUtils.loadClass(plugin.getPackage().classLoader, className);
        } catch (Exception e) {
            throw new PluginError.LoadError(e, PluginError.ERROR_LOA_CLASS);
        }

    }

    private void onPreLoad(PluginRequest request) {
        Logger.i(TAG, "onPreLoad state = " + request.getState());
        request.getManager().getCallback().preLoad(request);
    }

    private void onError(PluginRequest request, PluginError error) {
        Logger.i(TAG, "onError state = " + request.getState());
        request.switchState(PluginRequest.State.LOA_PLUGIN_FAIL);
        request.markException(error);
        onPostLoad(request);
    }

    private void onCanceled(PluginRequest request) {
        Logger.i(TAG, "onCanceled state = " + request.getState());
        request.switchState(PluginRequest.State.CANCELED);
        request.getManager().getCallback().onCancel(request);
    }

    private void onPostLoad(PluginRequest request) {
        Logger.i(TAG, "onPostLoad state = " + request.getState());
        PluginError error;

        if (request.getState() == PluginRequest.State.LOA_SUCCESS) {
            Plugin plugin = request.getPlugin();
            if (plugin != null) {
                request.getManager().getCallback().postLoad(request, plugin);
                onLoadSuccess(request, plugin);
                return;
            } else {
                request.switchState(PluginRequest.State.WTF);
            }
        }

        error = new PluginError.LoadError("Can not get plugin instance, " +
                "see request's state & exceptions", PluginError.ERROR_LOA_CREATE_PLUGIN);
        request.getManager().getCallback().loadFail(request, error);
    }

    private void onLoadSuccess(PluginRequest request, Plugin plugin) {
        Logger.i(TAG, "onLoadSuccess state = " + request.getState());

        if (BuildConfig.DEBUG) {
            Log.v(TAG, "Create behavior.");
        }

        try {
            PluginBehavior behavior = plugin.createBehavior(mContext);

            if (behavior == null) {
                behavior = createBehavior(plugin);
            }

            // Create invocation proxy for behavior.
            behavior = ProxyHandler.getProxy(PluginBehavior.class, behavior);
            plugin.setBehavior(behavior);
            request.getManager().getCallback().loadSuccess(request, plugin, behavior);

        } catch (Exception e) {
            Log.w(TAG, "Create behavior fail.");
            Log.w(TAG, e);
            PluginError.LoadError error = new PluginError.LoadError(e, PluginError.ERROR_LOA_BEHAVIOR);
            request.markException(error);
            request.getManager().getCallback().loadFail(request, error);
        }
    }

    @Override
    public PluginBehavior createBehavior(Plugin plugin) throws PluginError.LoadError {
        PluginError.LoadError error;
        try {
            PluginApk apk = plugin.getPackage();

            if (!TextUtils.isEmpty(apk.application)) {
                // Create plugin's behavior via Manifest entry (PluginApplication).
                Class entry = loadClass(plugin, apk.application);

                if (PluginApp.class.isAssignableFrom(entry)) {
                    PluginApp app = (PluginApp) entry.newInstance();
                    app.setAppContext(mContext);
                    return app.getBehavior();

                } else {
                    Logger.w(TAG, "Plugin's application can not assign to PluginApp.");
                    error = new PluginError.LoadError("Plugin's application can not assign to PluginApp.",
                            PluginError.ERROR_LOA_BEHAVIOR_ENTRY);
                }
            } else {
                Logger.w(TAG, "Cat not find plugin's app.");
                error = new PluginError.LoadError("Cat not find plugin's app.", PluginError.ERROR_LOA_BEHAVIOR_ENTRY);
            }
        } catch (Throwable e) {
            Logger.w(TAG, e);
            error = new PluginError.LoadError(e, PluginError.ERROR_LOA_BEHAVIOR_ENTRY);
        }
        throw error;
    }


}
