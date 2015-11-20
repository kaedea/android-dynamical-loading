package me.kaede.androidjnisample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			System.loadLibrary("stackblur");
			NativeBlurProcess.isLoadLibraryOk.set(true);
			Log.i("MainActivity", "loadLibrary success!");
		} catch (Throwable throwable) {
			Log.i("MainActivity", "loadLibrary error!" + throwable);
		}
	}

	public void onDoBlur(View view){
		ImageView imageView = (ImageView) findViewById(R.id.iv_app);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.sym_def_app_icon);
		Bitmap blur = NativeBlurProcess.blur(bitmap,20,false);
		imageView.setImageBitmap(blur);
	}
}
