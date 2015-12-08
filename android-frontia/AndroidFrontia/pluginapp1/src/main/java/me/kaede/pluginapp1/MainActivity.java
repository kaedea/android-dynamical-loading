package me.kaede.pluginapp1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.yy.mobile.ylink.dynamicload.DLBasePluginActivity;

public class MainActivity extends DLBasePluginActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
}
