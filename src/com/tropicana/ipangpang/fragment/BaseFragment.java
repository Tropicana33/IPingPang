package com.tropicana.ipangpang.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * frament����
 * @author Administrator
 *
 */
public abstract class BaseFragment extends Fragment {

	public Activity mActivity;
	
	//fragment����
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		mActivity= getActivity();
	}
	
	
	//����fragment�Ĳ���
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return initViews();
		
		
	}
	
	
	//fragment����activity�������
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
	}
	
	@Override
	public void onAttach(Activity activity) {
		
		super.onAttach(activity);
	}
	
	//��ʼ������
	public abstract View initViews();
	
	public void initData(){
		
	}
	
	
	
	
}
