package me.kaede.mainapp.bridge;

import android.content.Context;
import com.yy.mobile.ylink.bridge.CoreApiManager;
import com.yy.mobile.ylink.bridge.coreapi.LoginApi;

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
		Context activity = context;
		if(CoreApiManager.getInstance().getActivity()!=null) activity = CoreApiManager.getInstance().getActivity();
	}
}
