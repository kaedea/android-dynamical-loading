package me.kaede.androidjnisample;

import android.graphics.Bitmap;
import android.util.Log;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Blur using the NDK and native code.
 * Created by ping on 15/3/16.
 * code from https://github.com/kikoso/android-stackblur
 */
public class NativeBlurProcess {

    public static AtomicBoolean isLoadLibraryOk = new AtomicBoolean(false);

	private static native void functionToBlur(Bitmap bitmapOut, int radius, int threadCount, int threadIndex, int round);

	public static Bitmap blur(Bitmap original, float radius, boolean useMultiThread) {
        if (!isLoadLibraryOk.get()) {
	        Log.i("NativeBlurProcess", "isLoadLibraryOk false!");
            return original;
        }

        long begin = System.currentTimeMillis();
		Log.i("NativeBlurProcess", "blur begin, radius = " + radius);

        Bitmap bitmapOut = original.copy(Bitmap.Config.ARGB_8888, true);

        //去掉多线程，用不用耗时差距很小
//        if (useMultiThread) {
//            int coreCount = Runtime.getRuntime().availableProcessors();
//            ExecutorService executorService = Executors.newFixedThreadPool(coreCount);
//            ArrayList<NativeTask> horizontal = new ArrayList<NativeTask>(coreCount);
//            ArrayList<NativeTask> vertical = new ArrayList<NativeTask>(coreCount);
//            for (int i = 0; i < coreCount; i++) {
//                horizontal.add(new NativeTask(bitmapOut, (int) radius, coreCount, i, 1));
//                vertical.add(new NativeTask(bitmapOut, (int) radius, coreCount, i, 2));
//            }
//            try {
//                executorService.invokeAll(horizontal);
//                executorService.invokeAll(vertical);
//            } catch (Throwable throwable) {
//                MLog.error("NativeBlurProcess", "blur useMultiThread error! " + throwable);
//            }
//            executorService.shutdown();
//        } else {
            functionToBlur(bitmapOut, (int) radius, 1, 0, 1);
            functionToBlur(bitmapOut, (int) radius, 1, 0, 2);
//        }

		Log.i("NativeBlurProcess", "blur radius:" + radius + " end, cast time  = " + (System.currentTimeMillis() - begin));
        return bitmapOut;
	}

	private static class NativeTask implements Callable<Void> {
		private final Bitmap _bitmapOut;
		private final int _radius;
		private final int _totalCores;
		private final int _coreIndex;
		private final int _round;

		public NativeTask(Bitmap bitmapOut, int radius, int totalCores, int coreIndex, int round) {
			_bitmapOut = bitmapOut;
			_radius = radius;
			_totalCores = totalCores;
			_coreIndex = coreIndex;
			_round = round;
		}

		@Override public Void call() throws Exception {
			functionToBlur(_bitmapOut, _radius, _totalCores, _coreIndex, _round);
			return null;
		}

	}
}
