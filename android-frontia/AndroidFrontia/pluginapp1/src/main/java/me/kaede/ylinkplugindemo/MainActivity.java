package me.kaede.ylinkplugindemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yy.mobile.ylink.bridge.CoreApiManager;
import com.yy.mobile.ylink.bridge.coreapi.LoginApi;
import com.yy.mobile.ylink.bridge.coreapi.UserInfoApi;
import com.yy.mobile.ylink.dynamicload.DLBasePluginFragmentActivity;
import me.kaede.pluginapp1.R;

public class MainActivity extends DLBasePluginFragmentActivity implements View.OnClickListener {

	private Button btnIsLogin;
	private Button btnGoToLogin;
	private LinearLayout layoutUserInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findView();
		setListener();
		init();
	}

	private void findView() {
		setContentView(R.layout.activity_plugin_main);
		btnIsLogin = (Button) findViewById(R.id.btn_islogin);
		btnGoToLogin = (Button) findViewById(R.id.btn_goToLogin);
		layoutUserInfo = (LinearLayout) findViewById(R.id.layout_userinfo);
	}

	private void setListener() {
		btnIsLogin.setOnClickListener(this);
		btnGoToLogin.setOnClickListener(this);
	}

	private void init() {
		UserInfoApi userInfoApi = CoreApiManager.getInstance().getApi(UserInfoApi.class);
		if (userInfoApi!=null){
			Fragment fragment = userInfoApi.getFragment(this);
			if (fragment !=null){
				getSupportFragmentManager().beginTransaction().add(R.id.layout_userinfo,fragment,"FRAGMENT_USERINFO").commit();
			}
		}
	}

	@Override
	public void onClick(View v) {
		LoginApi loginApi;
		switch (v.getId()) {
			case R.id.btn_islogin:
				loginApi = CoreApiManager.getInstance().getApi(LoginApi.class);
				if (loginApi != null) {
					boolean isLogin = loginApi.isLogined();
					Toast.makeText(that, "isLogin = " + isLogin, Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(that, "LoginApi is null", Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.btn_goToLogin:
				loginApi = CoreApiManager.getInstance().getApi(LoginApi.class);
				if (loginApi != null) {
					loginApi.goToLogin(this);
				} else {
					Toast.makeText(that, "LoginApi is null", Toast.LENGTH_LONG).show();
				}
				break;
			default:
				break;
		}
	}
}
