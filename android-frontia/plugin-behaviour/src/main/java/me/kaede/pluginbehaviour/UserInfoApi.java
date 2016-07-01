package me.kaede.pluginbehaviour;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import me.kaede.frontia.bridge.host.hostapi.BaseApi;

/**
 * Created by kaede on 2015/12/9.
 */
public abstract class UserInfoApi extends BaseApi {
	abstract public Fragment getFragment(FragmentActivity activity);
}
