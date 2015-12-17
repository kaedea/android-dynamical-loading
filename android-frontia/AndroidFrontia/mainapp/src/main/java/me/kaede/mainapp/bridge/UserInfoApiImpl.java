package me.kaede.mainapp.bridge;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.yy.mobile.ylink.bridge.coreapi.UserInfoApi;
import me.kaede.mainapp.component.YLUserInfoFragment;

/**
 * Created by kaede on 2015/12/9.
 */
public class UserInfoApiImpl extends UserInfoApi {

	public static final String FRAGMENT_USERINFO = "FRAGMENT_USERINFO";

	@Override
	public Fragment getFragment(FragmentActivity activity) {
		return YLUserInfoFragment.newInstance();
	}
}
