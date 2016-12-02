package com.tropicana.ipingpang;

import com.tropicana.ipingpang.base.BaseActivity;

import android.view.View;

public class AboutActivity extends BaseActivity {

	private View view;
	
	@Override
	public void initData() {
		view=View.inflate(getApplicationContext(), R.layout.about_activity, null);
		fl_base.addView(view);
		tv_title.setText("πÿ”⁄∞Æ∆π≈“");
		btn_close.setVisibility(View.INVISIBLE);
	}

	@Override
	public void back() {
		finish();
	}
	
}
