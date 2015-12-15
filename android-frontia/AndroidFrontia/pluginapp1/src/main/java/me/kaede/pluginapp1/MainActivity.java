package me.kaede.pluginapp1;

import android.os.Bundle;
import com.yy.mobile.core.ylink.dynamicload.DLBasePluginActivity;

public class MainActivity extends DLBasePluginActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plugin_main);
	}
}
