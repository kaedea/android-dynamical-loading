package me.kaede.mainapp.component;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import me.kaede.mainapp.R;

/**
 * Created by kaede on 2015/12/9.
 */
public class YLUserInfoFragment extends Fragment implements View.OnClickListener {


	public static YLUserInfoFragment newInstance(){
		YLUserInfoFragment fragment = new YLUserInfoFragment();
		return fragment;
	}

	public YLUserInfoFragment(){

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		try {
			view = inflater.inflate(R.layout.fragment_userinfo,null);
			view.findViewById(R.id.btn).setOnClickListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

	}



	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.btn:
				Toast.makeText(getActivity(), getResources().getString(R.string.app_name), Toast.LENGTH_LONG).show();
				break;
		}
	}


}
