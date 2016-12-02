package com.tropicana.ipingpang.base;

import com.tropicana.ipingpang.R;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public  class BaseDetailPager{
	
	public static final int STATE_UNKOWN = 0;
	public static final int STATE_LOADING = 1;
	public static final int STATE_ERROR = 2;
	public static final int STATE_EMPTY = 3;
	public static final int STATE_SUCCESS = 4;
	public int state = STATE_UNKOWN;
	
	public Activity mActivity;
	public View mRootView;
	public FrameLayout fl_stars_pager_content;
	
	public RelativeLayout rl_loading_empty;
	public RelativeLayout rl_loading_error;
	public Button btn_reload;
	public RelativeLayout rl_loading;
	
	
	public BaseDetailPager(Activity activity){
		mActivity=activity;
		mRootView=initView();
		//changeView(STATE_LOADING);
	}

	public View initView(){
		View view=View.inflate(mActivity, R.layout.stars_base_pager, null);

		rl_loading=(RelativeLayout) view.findViewById(R.id.rl_loading_pager1);
		rl_loading_empty=(RelativeLayout) view.findViewById(R.id.rl_loading_empty_pager1);
		rl_loading_error=(RelativeLayout) view.findViewById(R.id.rl_loading_error_pager1);
		btn_reload=(Button) view.findViewById(R.id.btn_reload_pager1);
		fl_stars_pager_content=(FrameLayout) view.findViewById(R.id.fl_stars_pager_content);

		btn_reload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				reload();
			}
		});

		return view;
		
	}
	
	protected void reload() {
		changeView(STATE_LOADING);
		new Handler().postDelayed(new Runnable(){   
		    public void run() {   //延时1000再取数据
		    	getDataFromServer();
		    }   
		 }, 1000);   

	}

	/**
	 * 从服务器获取数据
	 */
	protected void getDataFromServer() {
		
	}

	public void initData() {
		
	}

	/**
	 * 改变显示状态
	 */
	public void changeView(int state){
		if (state==STATE_LOADING || state==STATE_UNKOWN) {
			rl_loading.setVisibility(View.VISIBLE);
			rl_loading_empty.setVisibility(View.GONE);
			rl_loading_error.setVisibility(View.GONE);			
		}else if (state==STATE_ERROR) {
			rl_loading.setVisibility(View.GONE);
			rl_loading_empty.setVisibility(View.GONE);
			rl_loading_error.setVisibility(View.VISIBLE);	
			//btn_reload.setClickable(true);
		}else if (state==STATE_EMPTY) {
			rl_loading.setVisibility(View.GONE);
			rl_loading_empty.setVisibility(View.VISIBLE);
			rl_loading_error.setVisibility(View.GONE);	
		}else if (state==STATE_SUCCESS) {
			rl_loading.setVisibility(View.GONE);
			rl_loading_empty.setVisibility(View.GONE);
			rl_loading_error.setVisibility(View.GONE);	
		}
	}
	
}
