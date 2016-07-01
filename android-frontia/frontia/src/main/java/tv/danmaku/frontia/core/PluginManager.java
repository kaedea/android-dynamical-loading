/*
 * Copyright (c) 2015-2016 BiliBili Inc.
 */

package tv.danmaku.frontia.core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import tv.danmaku.frontia.core.install.PluginInstaller;
import tv.danmaku.frontia.core.load.PluginLoader;
import tv.danmaku.frontia.core.update.PluginUpdater;
import tv.danmaku.frontia.util.PluginFileUtil;
import tv.danmaku.frontia.util.PluginLogUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 插件管理器：
 * 1. 启动插件更新、安装、加载任务；
 * 2. 提供对任务的控制；
 * Created by kaede on 2016/4/8.
 */
public class PluginManager {
    public static final String TAG = "PluginManager";
    private volatile static PluginManager instance;

    public static PluginManager getInstance(Context context) {
        if (instance == null) {
            instance = new PluginManager(context);
        }
        return instance;
    }

    private Context mContext;
    private Handler mMainHandler;
    private ExecutorService mExecutorService;
    private PluginInstaller mInstaller;
    private PluginUpdater mUpdater;
    private PluginLoader mLoader;
    private Map<Class<? extends BasePluginRequest>, RequestState> mRequestHolder;

    protected PluginManager(Context context) {
        mContext = context.getApplicationContext();
        mMainHandler = new Handler(Looper.getMainLooper());
        mRequestHolder = new HashMap<>();
        mExecutorService = Executors.newCachedThreadPool();
        mUpdater = PluginUpdater.getInstance(context);
        mInstaller = PluginInstaller.getInstance(context);
        mLoader = PluginLoader.getInstance(context);
        // Debug Log
        PluginLogUtil.w(TAG, "记得在放送版本中关闭调试信息！");
        PluginLogUtil.d(TAG, "---------------- pluginmanager init ----------------");
        PluginLogUtil.v(TAG, "Debug Mode = " + PluginConstants.DEBUG);
        PluginLogUtil.v(TAG, "Ignore Installed Plugin = " + PluginConstants.ignoreInstalledPlugin);
        if (PluginConstants.DEBUG) {
            PluginLogUtil.v(TAG, "-------- plugins installed --------");
            PluginFileUtil.printAll(new File(mInstaller.getPluginRootDir()));
        }
        PluginLogUtil.d(TAG, "---------------- pluginmanager init ----------------");
    }

    public void init() {
        mUpdater.attach(this);
        mLoader.attach(this);
    }

    public RequestState startRequestTask(@NonNull BasePluginRequest pluginRequest) {
        RequestTask requestTask = new RequestTask(pluginRequest) {
            @Override
            public void doRequest() {
                // 默认的异步任务是，查询、更新、并加载插件；
                // 如果需要执行不同的逻辑，RequestTask#doRequest；
                mUpdater.requestPlugin(mPluginRequest);
                mUpdater.updatePlugin(mPluginRequest);
                mLoader.loadPlugin(mPluginRequest);
            }
        };
        return startRequestTask(requestTask);
    }

    public RequestState startRequestTask(RequestTask requestTask) {
        PluginLogUtil.d(TAG, "[startRequestTask]");
        if (requestTask == null) {
            return null;
        }
        RequestState requestState = mRequestHolder.get(requestTask.mPluginRequest.getClass());
        if (requestState != null) {
            requestState.cancel();
        }
        Future<BasePluginRequest> future = mExecutorService.submit(requestTask);
        requestState = new RequestState(requestTask.mPluginRequest, future);
        mRequestHolder.put(requestTask.mPluginRequest.getClass(), requestState);
        return requestState;
    }

    public RequestState getRequestState(Class<? extends BasePluginRequest> clazz) {
        return  mRequestHolder.get(clazz);
    }

    public ExecutorService getExecutor() {
        return mExecutorService;
    }

    public Handler getMainHandler() {
        return mMainHandler;
    }

    public PluginUpdater getUpdater() {
        return mUpdater;
    }

    public PluginInstaller getInstaller() {
        return mInstaller;
    }

    public PluginLoader getLoader() {
        return mLoader;
    }

    public class RequestState {
        public BasePluginRequest mPluginRequest;
        public Future<BasePluginRequest> mUpdateFuture;

        public RequestState(BasePluginRequest pluginRequest, Future<BasePluginRequest> updateFuture) {
            mPluginRequest = pluginRequest;
            mUpdateFuture = updateFuture;
        }

        public boolean isFail() {
            return mPluginRequest.isUpdateFail();
        }

        public void cancel() {
            mPluginRequest.updateHandler.cancel();
            mUpdateFuture.cancel(true);
        }

        public BasePluginRequest getPluginRequest() {
            return mPluginRequest;
        }

        public BasePluginRequest getFutureUpdateState() {
            BasePluginRequest pluginRequest;
            try {
                pluginRequest = mUpdateFuture.get(30 * 1000, TimeUnit.MILLISECONDS); // 等待异步任务15秒，超时认为失败；
            } catch (InterruptedException e) {
                e.printStackTrace();
                pluginRequest =  mPluginRequest;
                PluginLogUtil.i(TAG,"[RequestState#getFutureUpdateState]interrupt exception = " + e.getMessage());
            } catch (ExecutionException | TimeoutException e) {
                e.printStackTrace();
                pluginRequest =  mPluginRequest.markException(e);
            }
            return pluginRequest;
        }
    }

    public abstract static class RequestTask implements Callable<BasePluginRequest> {
        public BasePluginRequest mPluginRequest;

        public RequestTask(@NonNull BasePluginRequest pluginRequest) {
            mPluginRequest = pluginRequest;
        }

        @Override
        public BasePluginRequest call() throws Exception {
            doRequest();
            PluginLogUtil.i(TAG,"[RequestState#call]print request state log = " + mPluginRequest.getStateLog());
            return mPluginRequest;
        }

        public abstract void doRequest();
    }
}
