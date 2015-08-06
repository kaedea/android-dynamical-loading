package me.kaede.android_loading_dex;

import akatuki.kaede.utils.KaedeUtil;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import dalvik.system.DexClassLoader;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

	final public static String TAG = "MainActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void onLoadDexCLick(View v){
		if (KaedeUtil.copyFileFromAssetsToSd(this, "test_dexloader.jar", Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test_dexloader.jar")) {
			Log.e(TAG, "成功复制jar到SD卡");

			final File optimizedDexOutputPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test_dexloader.jar");

			// 无法直接从SD卡加载.dex文件，需要指定APP缓存目录（.dex文件会被解压到此目录）
			File dexOutputDir = this.getDir("dex", 0);
			Log.e(TAG, "dexOutputDir:" + dexOutputDir.getAbsolutePath());
			DexClassLoader cl = new DexClassLoader(optimizedDexOutputPath.getAbsolutePath(), dexOutputDir.getAbsolutePath(), null, getClassLoader());
			Class libProviderClazz = null;

			try {
				libProviderClazz = cl.loadClass("me.kaede.dexclassloader.MyLoader");

				// 遍历类里所有方法
				Method[] methods = libProviderClazz.getDeclaredMethods();
				for (int i = 0; i < methods.length; i++) {
					Log.e(TAG, methods[i].toString());
				}

				Method start = libProviderClazz.getDeclaredMethod("func");// 获取方法
				start.setAccessible(true);// 未加这句之前报了一个错误：access to method
				// denied 加上之后可以了。
				String string = (String) start.invoke(libProviderClazz.newInstance());// 调用方法
				Log.e(TAG, string);

				Toast.makeText(this, string, Toast.LENGTH_LONG).show();

			} catch (Exception exception) {
				// Handle exception gracefully here.
				exception.printStackTrace();
			}

		}

	}

	public void onLoadApkCLick(View v){
		if (KaedeUtil.copyFileFromAssetsToSd(this, "test_DexClassLoader.apk", Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test_DexClassLoader.apk")) {
			Log.e(TAG, "成功复制apk到SD卡");
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test_DexClassLoader.apk";
			try {

				// 4.1以后不能够将optimizedDirectory设置到sd卡目录， 否则抛出异常.
				File optimizedDirectoryFile = getDir("dex", 0);
				DexClassLoader classLoader = new DexClassLoader(path, optimizedDirectoryFile.getAbsolutePath(), null, getClassLoader());

				// 通过反射机制调用
				Class mLoadClass = classLoader.loadClass("me.kaede.dexclassloader.MainActivity");
				Constructor constructor = mLoadClass.getConstructor(new Class[] {});
				Object testActivity = constructor.newInstance(new Object[] {});

				// 遍历类里所有方法
				Method[] methods = mLoadClass.getDeclaredMethods();
				for (int i = 0; i < methods.length; i++) {
					Log.e(TAG, methods[i].toString());
				}

				// 获取sayHello方法
				Method method = mLoadClass.getMethod("func");
				method.setAccessible(true);
				Object content = method.invoke(testActivity);
				Toast.makeText(this, content.toString(), Toast.LENGTH_LONG).show();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
