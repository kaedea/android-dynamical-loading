package com.yy.mobile.core.ylink.bridge.coreapi;

import android.content.Context;

/**
 * Created by kaede on 2015/12/7.
 */
public abstract class LoginApi extends BaseApi{

	abstract public boolean isLogined();

	abstract public void goToLogin(Context context);

}
