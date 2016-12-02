package com.tropicana.ipangpang.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * frament基类
 * @author Administrator
 *
 */
public abstract class BaseFragment extends Fragment {

	public Activity mActivity;
	
	//fragment创建
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		mActivity= getActivity();
	}
	
	
	//处理fragment的布局
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return initViews();
		
		
	}
	
	
	//fragment依附activity创建完成
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
	}
	
	@Override
	public void onAttach(Activity activity) {
		
		super.onAttach(activity);
	}
	
	//初始化布局
	public abstract View initViews();
	
	public void initData(){
		
	}
	
	
	
	
}
