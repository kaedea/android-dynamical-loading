package me.kaede.anroidclassloadersample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class GetAllClassLoaderActivity extends AppCompatActivity {

	public static final String TAG = "GetAllClassLoader";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_all_class_loader);

		StringBuilder sb = new StringBuilder();
		ClassLoader classLoader = getClassLoader();
		int i = 1;
		if (classLoader != null){
			Log.i(TAG, "[onCreate] classLoader " + i + " : " + classLoader.toString());
			sb.append("classLoader " + i + " : " + classLoader.toString()).append("\n").append("\n");
			i++;
			while (classLoader.getParent()!=null){
				classLoader = classLoader.getParent();
				Log.i(TAG,"[onCreate] classLoader " + i + " : " + classLoader.toString());
				sb.append("classLoader " + i + " : " + classLoader.toString()).append("\n").append("\n");
				i++;
			}
		}
		TextView textView = (TextView) findViewById(R.id.tv);
		textView.setText(sb);
	}
}
