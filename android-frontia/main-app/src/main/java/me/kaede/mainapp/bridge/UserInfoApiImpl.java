package me.kaede.mainapp.bridge;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import me.kaede.mainapp.YLUserInfoFragment;
import tv.danmaku.pluginbehaiour.UserInfoApi;

/**
 * Created by kaede on 2015/12/9.
 */
public class UserInfoApiImpl extends UserInfoApi {

	@Override
	public Fragment getFragment(FragmentActivity activity) {
		return YLUserInfoFragment.newInstance();
	}
}
