/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import moe.studio.frontia.ext.PluginError;
import moe.studio.frontia.Internals.FileUtils;
import moe.studio.frontia.core.Plugin;
import moe.studio.frontia.core.PluginBehavior;
import moe.studio.frontia.core.PluginInstaller;
import moe.studio.frontia.core.PluginLoader;
import moe.studio.frontia.core.PluginManager;
import moe.studio.frontia.core.PluginRequest;
import moe.studio.frontia.core.PluginUpdater;
import moe.studio.frontia.ext.PluginCallback;
import moe.studio.frontia.ext.PluginSetting;

import static moe.studio.frontia.Logger.DEBUG;

/**
 * Frontia
 * 1. 启动插件更新、安装、加载任务;
 * 2. 提供对任务的控制;
 * 3. 提供获取插件的方法;
 * <p>
 * 乗り越えれ、Frontier！我が目立ては「Star Oceans」！
 */
@SuppressWarnings({"WeakerAccess", "SuspiciousMethodCalls", "unchecked"})
public final class Frontia extends SyncPluginManager {

    private static final String TAG = "plugin.frontia";
    private static Frontia sInstance;


    public static Frontia instance() {
        if (sInstance == null) {
            synchronized (Frontia.class) {
                if (sInstance == null) {
                    sInstance = new Frontia();
                }
            }
        }
        return sInstance;
    }

    public static void release() {
        if (sInstance == null) {
            return;
        }
        synchronized (Frontia.class) {
            sInstance = null;
        }
    }

    private boolean mHasInit = false;
    private final byte[] mLock = new byte[0];

    private SyncPluginManager mManager;
    private PluginCallback mCallback;
    private ExecutorService mExecutorService;
    private Map<Class<? extends PluginRequest>, RequestState> mRequestStates;

    private Frontia() {
        super(null, null, null, null, null);

    }

    /**
     * 初始化
     */
    public void init(Context context) {
        if (!mHasInit) {
            synchronized (mLock) {
                if (!mHasInit) {
                    mHasInit = true;
                    PluginSetting setting = new PluginSetting.Builder()
                            .setDebugMode(DEBUG)
                            .ignoreInstalledPlugin(DEBUG)
                            .build();
                    PluginLoader loader = new PluginLoaderImpl(context);
                    PluginUpdater updater = new PluginUpdaterImpl(context);
                    PluginInstaller installer = new PluginInstallerImpl(context, setting);
                    mCallback = new CallbackDelivery(new Handler(Looper.getMainLooper()));
                    mExecutorService = Executors.newSingleThreadExecutor();
                    mManager = new SyncPluginManager(loader, updater, installer, setting, new PluginCallback());
                    printDebugInfo();
                    return;
                }
            }
        }

        throw new RuntimeException("Frontia has already been initialized.");
    }

    /**
     * 初始化
     *
     * @param context Context
     * @param setting 插件设置
     */
    public void init(Context context, @NonNull PluginSetting setting) {
        if (!mHasInit) {
            synchronized (mLock) {
                if (!mHasInit) {
                    mHasInit = true;
                    PluginLoader loader = new PluginLoaderImpl(context);
                    PluginUpdater updater = new PluginUpdaterImpl(context);
                    PluginInstaller installer = new PluginInstallerImpl(context, setting);
                    mCallback = new CallbackDelivery(new Handler(Looper.getMainLooper()));
                    mExecutorService = Executors.newSingleThreadExecutor();
                    mManager = new SyncPluginManager(loader, updater, installer, setting, new PluginCallback());
                    printDebugInfo();
                    return;
                }
            }
        }

        throw new RuntimeException("Frontia has already been initialized.");
    }

    /**
     * 初始化
     *
     * @param context         Context
     * @param setting         插件设置
     * @param callbackHandler 监听器回调用Handler
     * @param executorService 异步加载插件任务用的线程池
     */
    public void init(Context context, @NonNull PluginSetting setting,
                     @NonNull Handler callbackHandler, @NonNull ExecutorService executorService) {
        if (!mHasInit) {
            synchronized (mLock) {
                if (!mHasInit) {
                    mHasInit = true;
                    PluginLoader loader = new PluginLoaderImpl(context);
                    PluginUpdater updater = new PluginUpdaterImpl(context);
                    PluginInstaller installer = new PluginInstallerImpl(context, setting);
                    mCallback = new CallbackDelivery(callbackHandler);
                    mExecutorService = executorService;
                    mManager = new SyncPluginManager(loader, updater, installer, setting, new PluginCallback());
                    printDebugInfo();
                    return;
                }
            }
        }

        throw new RuntimeException("Frontia has already been initialized.");
    }

    /**
     * 同步加载一个插件
     *
     * @param request 插件请求
     * @param mode    加载模式, 参考{@link Mode}
     * @return 当前插件请求
     */
    public PluginRequest add(@NonNull PluginRequest request, int mode) {
        return add(request, JobToDo.doing(mManager, mode));
    }

    /**
     * 同步加载一个插件
     *
     * @param request 插件请求
     * @param job     做神马, 参考{@link Mode}
     * @return 当前插件请求
     */
    public PluginRequest add(@NonNull PluginRequest request, @NonNull JobToDo job) {
        if (!mHasInit) {
            throw new RuntimeException("Frontia has not yet been init.");
        }

        PluginManager manager = request.getManager();
        return mManager.add(
                request.attach(manager == null ? mManager : manager),
                job);
    }

    public PluginManager getSyncManager() {
        if (!mHasInit) {
            throw new RuntimeException("Frontia has not yet been init.");
        }

        return mManager;
    }

    /**
     * 异步加载一个插件
     * <p>
     * 如果需要获取一个已经正在运行的插件加载任务的状态, 可以通过{@link #getRequestState(Class)}获得。
     *
     * @param request 插件请求
     * @param mode    加载模式, 参考{@linkplain Frontia.Mode}
     * @return 当前插件请求
     */
    public RequestState addAsync(@NonNull PluginRequest request, int mode) {
        return addAsync(request, JobToDo.doing(this, mode));
    }

    /**
     * 异步加载一个插件
     * <p>
     * 如果需要获取一个已经正在运行的插件加载任务的状态, 可以通过{@link #getRequestState(Class)}获得。
     *
     * @param request 插件请求
     * @param job     做神马, 参考{@link Mode}
     * @return 当前插件请求
     */
    public RequestState addAsync(@NonNull final PluginRequest request, @NonNull final JobToDo job) {
        if (!mHasInit) {
            throw new RuntimeException("Frontia has not yet been init.");
        }

        mRequestStates = ensureHashMap(mRequestStates);
        RequestState requestState = mRequestStates.get(request.getClass());

        // Cancel if exist.
        if (requestState != null) {
            requestState.cancel();
        }

        request.attach(this);
        Future<PluginRequest> future = mExecutorService.submit(new Callable<PluginRequest>() {
            @Override
            public PluginRequest call() throws Exception {
                return add(request, job);
            }
        });

        requestState = new RequestState(request, future);
        mRequestStates.put(request.getClass(), requestState);
        return requestState;
    }

    /**
     * 获取插件加载任务的状态
     */
    @Nullable
    public RequestState getRequestState(Class<? extends PluginRequest> clazz) {
        if (!mHasInit) {
            throw new RuntimeException("Frontia has not yet been init.");
        }

        return mRequestStates == null || mRequestStates == Collections.EMPTY_MAP ?
                null : mRequestStates.get(clazz);
    }

    @Override
    public Class getClass(Class<? extends Plugin> clazz, String className) throws PluginError.LoadError {
        if (!mHasInit) {
            throw new RuntimeException("Frontia has not yet been init.");
        }
        return mManager.getClass(clazz, className);
    }

    @Override
    public <B extends PluginBehavior, P extends Plugin<B>> B getBehavior(P clazz) throws PluginError.LoadError {
        if (!mHasInit) {
            throw new RuntimeException("Frontia has not yet been init.");
        }

        return mManager.getBehavior(clazz);
    }

    @Override
    public <B extends PluginBehavior, P extends Plugin<B>> P getPlugin(P clazz) {
        if (!mHasInit) {
            throw new RuntimeException("Frontia has not yet been init.");
        }
        return mManager.getPlugin(clazz);
    }

    @Override
    public void addLoadedPlugin(Class<? extends PluginBehavior> clazz, Plugin plugin) {
        if (!mHasInit) {
            throw new RuntimeException("Frontia has not yet been init.");
        }

        mManager.addLoadedPlugin(clazz, plugin);
    }

    @Override
    public PluginSetting getSetting() {
        if (!mHasInit) {
            throw new RuntimeException("Frontia has not yet been init.");
        }

        return mManager.getSetting();
    }

    @Override
    public PluginLoader getLoader() {
        if (!mHasInit) {
            throw new RuntimeException("Frontia has not yet been init.");
        }

        return mManager.getLoader();
    }

    @Override
    public PluginUpdater getUpdater() {
        if (!mHasInit) {
            throw new RuntimeException("Frontia has not yet been init.");
        }

        return mManager.getUpdater();
    }

    @Override
    public PluginInstaller getInstaller() {
        if (!mHasInit) {
            throw new RuntimeException("Frontia has not yet been init.");
        }

        return mManager.getInstaller();
    }

    @Override
    public PluginCallback getCallback() {
        if (!mHasInit) {
            throw new RuntimeException("Frontia has not yet been init.");
        }

        return mCallback;
    }

    public ExecutorService getExecutor() {
        if (!mHasInit) {
            throw new RuntimeException("Frontia has not yet been init.");
        }

        return mExecutorService;
    }

    private void printDebugInfo() {
        if (!mHasInit) {
            throw new RuntimeException("Frontia has not yet been init.");
        }

        if (DEBUG) {
            Logger.v(TAG, "-");
            Logger.v(TAG, "Frontia init");
            Logger.v(TAG, "Debug Mode = " + mManager.getSetting().isDebugMode());
            Logger.v(TAG, "Ignore Installed Plugin = " + mManager.getSetting().ignoreInstalledPlugin());
            Logger.v(TAG, "Use custom signature = " + mManager.getSetting().useCustomSignature());
            Logger.v(TAG, "--");
            FileUtils.dumpFiles(new File(mManager.getInstaller().getRootPath()));
            Logger.v(TAG, "--");
            Logger.v(TAG, "-");
        }
    }

    public static void registerLibrary(String name, int version) {
        CompatUtils.registerLibrary(name, version);
    }

    /**
     * 插件加载任务的状态类
     */
    public static class RequestState {

        private final PluginRequest mRequest;
        private final Future<PluginRequest> mFuture;

        public RequestState(PluginRequest request, Future<PluginRequest> future) {
            mRequest = request;
            mFuture = future;
        }

        /**
         * 取消插件请求任务
         */
        public void cancel() {
            mRequest.cancel();
            mFuture.cancel(true);
        }

        /**
         * 获取插件请求任务
         */
        public PluginRequest getRequest() {
            return mRequest;
        }

        /**
         * 同步等待插件任务执行结束并返回插件请求任务
         *
         * @param timeout 超时时间
         * @return 插件请求任务
         */
        public PluginRequest getFutureRequest(long timeout) {
            PluginRequest pluginRequest;
            try {
                pluginRequest = mFuture.get(timeout, TimeUnit.MILLISECONDS);

            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                Logger.i(TAG, "Get future request fail, error = " + e.getMessage());
                Logger.w(TAG, e);
                pluginRequest = mRequest.markException(e);
            }
            return pluginRequest;
        }

    }

}
