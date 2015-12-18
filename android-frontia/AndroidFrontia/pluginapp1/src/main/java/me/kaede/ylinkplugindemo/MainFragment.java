package me.kaede.ylinkplugindemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.yy.mobile.ylink.bridge.CoreApiManager;
import com.yy.mobile.ylink.bridge.coreapi.UserInfoApi;
import com.yy.mobile.ylink.dynamicload.fragment.DLBasePluginFragment;
import com.yy.mobile.ylink.dynamicload.fragment.LayoutInflatorWrapper;
import me.kaede.pluginapp1.R;

/**
 * Created by kaede on 2015/12/11.
 */
public class MainFragment extends DLBasePluginFragment implements View.OnClickListener {
	public static MainFragment newInstance(){
		return  new MainFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		try {
			LayoutInflater layoutInflater = inflater.cloneInContext(pluginContext);
			view = layoutInflater.inflate(R.layout.activity_plugin_main, null);
			view.findViewById(R.id.btn_islogin).setOnClickListener(this);
			view.findViewById(R.id.btn_goToLogin).setOnClickListener(this);
			//view.findViewById(R.id.layout_userinfo).setOnClickListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn_islogin:
			Toast.makeText(getActivity(),pluginContext.getResources().getString(R.string.main_btn2),Toast.LENGTH_LONG).show();
		    break;
		case R.id.btn_goToLogin:
			//Toast.makeText(getActivity(),"btn_goToLogin",Toast.LENGTH_LONG).show();
		case R.id.layout_userinfo:
			UserInfoApi userInfoApi = CoreApiManager.getInstance().getApi(UserInfoApi.class);
			if (userInfoApi == null) {
				Toast.makeText(getActivity(),"userInfoApi = null",Toast.LENGTH_LONG).show();;
				return;
			}
			try {
				getChildFragmentManager().beginTransaction().replace(R.id.layout_userinfo, userInfoApi.getFragment(getActivity())).commitAllowingStateLoss();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
		    break;
		}
	}
}
