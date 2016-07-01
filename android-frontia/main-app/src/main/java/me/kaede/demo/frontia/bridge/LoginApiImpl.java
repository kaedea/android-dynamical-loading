package me.kaede.demo.frontia.bridge;

import android.content.Context;
import me.kaede.pluginbehaviour.LoginApi;


/**
 * Created by kaede on 2015/12/7.
 */
public class LoginApiImpl extends LoginApi {
	@Override
	public boolean isLogined() {
		return false;
	}

	@Override
	public void goToLogin(Context context) {

	}
}
