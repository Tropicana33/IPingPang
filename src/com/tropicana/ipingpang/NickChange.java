package com.tropicana.ipingpang;

import com.tropicana.ipingpang.base.BaseActivity;
import com.tropicana.ipingpang.bmob.bean.User;

import android.view.View;
import android.widget.EditText;
import cn.bmob.v3.listener.UpdateListener;

public class NickChange extends BaseActivity {

	private View view;
	private EditText et_nick_change;
	private String nick;
	@Override
	public void initData() {
		view=View.inflate(getApplicationContext(), R.layout.activity_nick, null);
		et_nick_change=(EditText) view.findViewById(R.id.et_nick_change);
		fl_base.addView(view);
		tv_title.setText("修改昵称");
		btn_close.setImageResource(R.drawable.base_action_bar_true_bg_selector);
		btn_close.setVisibility(View.VISIBLE);
	}

	@Override
	public void back() {
		finish();
	}
	
	@Override
	public void close() {
		nick = et_nick_change.getText().toString().trim();
		if (nick.equals("")) {
			ShowToast("请填写昵称!");
			return;
		}
		updateInfo(nick);
	}
	
	/** 修改资料
	  * updateInfo
	  * @Title: updateInfo
	  * @return void
	  * @throws
	  */
	private void updateInfo(String nick) {
		final User user = userManager.getCurrentUser(User.class);
		User u = new User();
		u.setNick(nick);
		//u.setHight(110);
		u.setObjectId(user.getObjectId());
		u.update(this, new UpdateListener() {

			@Override
			public void onSuccess() {
				//final User c = userManager.getCurrentUser(User.class);
				ShowToast("修改成功");
				finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				ShowToast("onFailure:" + arg1);
			}
		});
	}
}
