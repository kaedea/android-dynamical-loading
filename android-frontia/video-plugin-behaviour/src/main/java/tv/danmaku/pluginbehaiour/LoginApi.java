package tv.danmaku.pluginbehaiour;

import android.content.Context;
import tv.danmaku.frontia.bridge.host.hostapi.BaseApi;

/**
 * Created by kaede on 2015/12/7.
 */
public abstract class LoginApi extends BaseApi {

	abstract public boolean isLogined();

	abstract public void goToLogin(Context context);

}
