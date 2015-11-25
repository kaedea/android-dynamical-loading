package me.kaede.androidjnisample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		File dir = this.getDir("jniLibs", Activity.MODE_PRIVATE);
		File distFile = new File(dir.getAbsolutePath() + File.separator + "libstackblur.so");

		if (copyFileFromAssets(this, "libstackblur.so", distFile.getAbsolutePath())){
			System.load(distFile.getAbsolutePath());
			NativeBlurProcess.isLoadLibraryOk.set(true);
		}
	}

	public void onDoBlur(View view){
		ImageView imageView = (ImageView) findViewById(R.id.iv_app);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.sym_def_app_icon);
		Bitmap blur = NativeBlurProcess.blur(bitmap,20,false);
		imageView.setImageBitmap(blur);
	}


	public static boolean copyFileFromAssets(Context context, String fileName, String path) {
		boolean copyIsFinish = false;
		try {
			InputStream is = context.getAssets().open(fileName);
			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}
			fos.close();
			is.close();
			copyIsFinish = true;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("MainActivity", "[copyFileFromAssets] IOException "+e.toString());
		}
		return copyIsFinish;
	}
}
