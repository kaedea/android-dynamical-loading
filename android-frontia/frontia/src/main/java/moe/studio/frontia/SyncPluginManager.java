/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import moe.studio.frontia.core.Plugin;
import moe.studio.frontia.core.PluginBehavior;
import moe.studio.frontia.core.PluginInstaller;
import moe.studio.frontia.core.PluginLoader;
import moe.studio.frontia.core.PluginManager;
import moe.studio.frontia.core.PluginRequest;
import moe.studio.frontia.core.PluginUpdater;
import moe.studio.frontia.ext.PluginCallback;
import moe.studio.frontia.ext.PluginError.LoadError;
import moe.studio.frontia.ext.PluginSetting;

import static moe.studio.frontia.SyncPluginManager.Mode.LOAD;
import static moe.studio.frontia.SyncPluginManager.Mode.UPDATE;
import static moe.studio.frontia.ext.PluginError.ERROR_LOA_NOT_LOADED;

/**
 * Frontia
 * 1. 启动插件更新、安装、加载任务;
 * 2. 提供对任务的控制;
 * 3. 提供获取插件的方法;
 * <p>
 * 乗り越えれ、Frontier！我が目立ては「Star Oceans」！
 */
@SuppressWarnings({"WeakerAccess", "SuspiciousMethodCalls", "unchecked"})
public class SyncPluginManager implements PluginManager {

    private static final String TAG = "plugin.manager";

    private final PluginLoader mLoader;
    private final PluginUpdater mUpdater;
    private final PluginInstaller mInstaller;
    private final PluginSetting mSetting;
    private final PluginCallback mCallback;

    private Map<Class<? extends PluginBehavior>, Plugin> mLoadedPlugins;

    public SyncPluginManager(PluginLoader mLoader, PluginUpdater mUpdater, PluginInstaller mInstaller,
                             PluginSetting mSetting, PluginCallback mCallback) {
        this.mLoader = mLoader;
        this.mUpdater = mUpdater;
        this.mInstaller = mInstaller;
        this.mSetting = mSetting;
        this.mCallback = mCallback;
    }

    /**
     * 加载一个插件
     *
     * @param request 插件请求
     * @param mode    加载模式, 参考{@link Mode}
     * @return 当前插件请求
     */
    public PluginRequest add(@NonNull PluginRequest request, int mode) {
        if (request.getManager() == null) {
            request.attach(this);
        }

        return add(request, JobToDo.doing(this, mode));
    }

    /**
     * 加载一个插件
     *
     * @param request 插件请求
     * @param job     做神马, 参考{@link Mode}
     * @return 当前插件请求
     */
    public PluginRequest add(@NonNull PluginRequest request, @NonNull JobToDo job) {
        if (request.getManager() == null) {
            request.attach(this);
        }

        Logger.i(TAG, "request id = " + request.getId() +
                ", state log = " + request.getLog());
        job.doing(request);
        return request;
    }


    @Override
    public Class getClass(Class<? extends Plugin> clazz, String className) throws LoadError {
        if (mLoadedPlugins == null || mLoadedPlugins == Collections.EMPTY_MAP) {
            return null;
        }

        Plugin plugin = mLoadedPlugins.get(clazz);

        if (plugin == null) {
            throw new LoadError("Plugin has not yet been loaded.", ERROR_LOA_NOT_LOADED);
        }

        return mLoader.loadClass(plugin, className);
    }

    @Override
    public <B extends PluginBehavior, P extends Plugin<B>> B getBehavior(P clazz) throws LoadError {
        if (mLoadedPlugins == null || mLoadedPlugins == Collections.EMPTY_MAP) {
            return null;
        }

        Plugin plugin = mLoadedPlugins.get(clazz);

        if (plugin != null) {
            PluginBehavior behavior = plugin.getBehavior();
            if (behavior != null) {
                return (B) behavior;
            }
        }

        throw new LoadError("Plugin has not yet been loaded.", ERROR_LOA_NOT_LOADED);
    }

    @Override
    public <B extends PluginBehavior, P extends Plugin<B>> P getPlugin(P clazz) {
        return mLoadedPlugins == null || mLoadedPlugins == Collections.EMPTY_MAP ?
                null : (P) mLoadedPlugins.get(clazz);
    }

    @Override
    public void addLoadedPlugin(Class<? extends PluginBehavior> clazz, Plugin plugin) {
        mLoadedPlugins = ensureHashMap(mLoadedPlugins);
        mLoadedPlugins.put(clazz, plugin);
    }

    protected Map ensureHashMap(Map map) {
        if (map == null || map == Collections.EMPTY_MAP) {
            map = new HashMap();
        }

        return map;
    }

    @Override
    public PluginSetting getSetting() {
        return mSetting;
    }

    @Override
    public PluginLoader getLoader() {
        return mLoader;
    }

    @Override
    public PluginUpdater getUpdater() {
        return mUpdater;
    }

    @Override
    public PluginInstaller getInstaller() {
        return mInstaller;
    }

    @Override
    public PluginCallback getCallback() {
        return mCallback;
    }

    /**
     * 插件加载的模式
     */
    public static class Mode {
        /**
         * 更新插件, 从远程下载最新版本的插件并拷贝到对应的安装路径
         */
        public static final int UPDATE = 0x0001;
        /**
         * 从对应的安装路径加载插件
         */
        public static final int LOAD = 0x0010;
    }

    /**
     * 告诉Frontia当前任务要做什么
     */
    public abstract static class JobToDo {

        public static JobToDo doing(PluginManager manager, int mode) {
            JobToDo task;
            switch (mode) {
                case UPDATE:               // Only update plugin.
                    task = new Update(manager);
                    break;
                case LOAD:                 // Only load plugin.
                    task = new Load(manager);
                    break;
                case UPDATE | LOAD:        // Update and load plugin.
                default:
                    task = new UpdateAndLoad(manager);
                    break;
            }
            return task;
        }

        final PluginManager mManager;

        public JobToDo(PluginManager manager) {
            mManager = manager;
        }

        public abstract void doing(PluginRequest request);

        /* Impl */
        private static class Update extends JobToDo {

            Update(PluginManager manager) {
                super(manager);
            }

            @Override
            public void doing(PluginRequest request) {
                mManager.getUpdater().updatePlugin(request);
            }

        }

        private static class Load extends JobToDo {

            Load(PluginManager manager) {
                super(manager);
            }

            @Override
            public void doing(PluginRequest request) {
                mManager.getLoader().load(request);
            }

        }

        private static class UpdateAndLoad extends JobToDo {

            UpdateAndLoad(PluginManager manager) {
                super(manager);
            }

            @Override
            public void doing(PluginRequest request) {
                new Update(mManager).doing(request);
                new Load(mManager).doing(request);
            }
        }
    }
}
