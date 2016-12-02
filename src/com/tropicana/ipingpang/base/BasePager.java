package com.tropicana.ipingpang.base;

import java.util.List;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tropicana.ipingpang.MainActivity;
import com.tropicana.ipingpang.R;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;

/**
 * ��ҳ�������ҳ�����
 * @author Administrator
 *
 */
public class BasePager {

	public static final int STATE_UNKOWN = 0;
	public static final int STATE_LOADING = 1;
	public static final int STATE_ERROR = 2;
	public static final int STATE_EMPTY = 3;
	public static final int STATE_SUCCESS = 4;
	public int state = STATE_UNKOWN;
	
	public Activity mActivity;
	public View mRootView;  //���ֶ���
	
	public TextView tv_pager_title;
	public ImageButton btn_menu;
	public FrameLayout fl_content;
	
	public RelativeLayout rl_loading_empty_pager;
	public RelativeLayout rl_loading_error_pager;
	public Button btn_reload;
	public RelativeLayout rl_loading_pager;
	
	public BasePager(Activity activity){
		mActivity=activity;
		initViews();
		//changeView(STATE_LOADING);
	}
	
	/**
	 * ��ʼ������
	 */
	public void initViews(){
		mRootView=View.inflate(mActivity, R.layout.basepager, null);
		tv_pager_title=(TextView) mRootView.findViewById(R.id.tv_pager_titie);
		btn_menu=(ImageButton) mRootView.findViewById(R.id.btn_menu);
		
		rl_loading_pager=(RelativeLayout) mRootView.findViewById(R.id.rl_loading_pager);
		rl_loading_empty_pager=(RelativeLayout) mRootView.findViewById(R.id.rl_loading_empty_pager);
		rl_loading_error_pager=(RelativeLayout) mRootView.findViewById(R.id.rl_loading_error_pager);
		btn_reload=(Button) mRootView.findViewById(R.id.btn_reload_pager);
		fl_content=(FrameLayout) mRootView.findViewById(R.id.fl_pager_content);
		
		btn_menu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				toggleSlidingMenu();
				
			}
		});
		
		btn_reload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				reload();
			}
		});
		
	}
	
	protected void reload() {
		
	}

	protected void toggleSlidingMenu() {
		MainActivity mainUI=(MainActivity) mActivity;
		SlidingMenu slidingMenu=mainUI.getSlidingMenu();
		slidingMenu.toggle();//�л�״̬�����أ���ʾ
	}
	
	public void initData(){
		
	}
	
	/**
	 * �ı���ʾ״̬
	 */
	public void changeView(int state){
		if (state==STATE_LOADING || state==STATE_UNKOWN) {
			rl_loading_pager.setVisibility(View.VISIBLE);
			rl_loading_empty_pager.setVisibility(View.GONE);
			rl_loading_error_pager.setVisibility(View.GONE);			
		}else if (state==STATE_ERROR) {
			rl_loading_pager.setVisibility(View.GONE);
			rl_loading_empty_pager.setVisibility(View.GONE);
			rl_loading_error_pager.setVisibility(View.VISIBLE);	
		}else if (state==STATE_EMPTY) {
			rl_loading_pager.setVisibility(View.GONE);
			rl_loading_empty_pager.setVisibility(View.VISIBLE);
			rl_loading_error_pager.setVisibility(View.GONE);	
		}else if (state==STATE_SUCCESS) {
			rl_loading_pager.setVisibility(View.GONE);
			rl_loading_empty_pager.setVisibility(View.GONE);
			rl_loading_error_pager.setVisibility(View.GONE);	
		}
	}
	
	/**У������ */
	public int checkData(List datas) {
		if(datas==null){
			return STATE_ERROR;//  ���������ʧ��
		}else{
			if(datas.size()==0){
				return STATE_EMPTY;
			}else{
				return STATE_SUCCESS;
			}
		}
		
	}
	
	
}
