package tv.danmaku.pluginbehaiour;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import tv.danmaku.frontia.bridge.host.hostapi.BaseApi;

/**
 * Created by estel on 2015/12/9.
 */
public abstract class UserInfoApi extends BaseApi {
	abstract public Fragment getFragment(FragmentActivity activity);
}
