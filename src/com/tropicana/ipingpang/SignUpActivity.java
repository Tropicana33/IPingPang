package com.tropicana.ipingpang;

import com.tropicana.ipangpang.view.ClearEditText;
import com.tropicana.ipingpang.base.BaseActivity;
import com.tropicana.ipingpang.bmob.bean.MyUser;
import com.tropicana.ipingpang.bmob.bean.User;
import com.tropicana.ipingpang.utils.CommonUtils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;

public class SignUpActivity extends BaseActivity {

	private View view;
	private ClearEditText et_sign_account;
	private ClearEditText et_sign_password;
	private ClearEditText et_sign_password_comfirm;
	private Button btn_sign_comfirm;
	private CheckBox cb_sign_agree;
	
	private String account="";
	private String password="";
	private String password_comfirm="";
	@Override
	public void initData() {
		view=View.inflate(getApplicationContext(), R.layout.activity_sign_up, null);
		et_sign_account=(ClearEditText) view.findViewById(R.id.et_sign_account);
		et_sign_password=(ClearEditText) view.findViewById(R.id.et_sign_password);
		et_sign_password_comfirm=(ClearEditText) view.findViewById(R.id.et_sign_password_comfirm);
		btn_sign_comfirm=(Button) view.findViewById(R.id.btn_sign_comfirm);
		cb_sign_agree=(CheckBox) view.findViewById(R.id.cb_sign_agree);
		
		tv_title.setText("用户注册");
		fl_base.addView(view); 
		btn_close.setVisibility(View.INVISIBLE);
		if (cb_sign_agree.isChecked()) {
			btn_sign_comfirm.setBackgroundResource(R.drawable.btn_start_normal);
			btn_sign_comfirm.setClickable(true);
		}else {
			btn_sign_comfirm.setBackgroundResource(R.drawable.btn_start_pressed);
			btn_sign_comfirm.setClickable(false);
		}
			
		cb_sign_agree.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					btn_sign_comfirm.setBackgroundResource(R.drawable.btn_start_normal);
					//btn_sign_comfirm.setClickable(true);
				}else {
					btn_sign_comfirm.setBackgroundResource(R.drawable.btn_start_pressed);
					//btn_sign_comfirm.setClickable(false);
				}
			}
		});
		btn_sign_comfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("TOUCH","click");
				account=et_sign_account.getText().toString().trim();  //获取输入账号密码
				password=et_sign_password.getText().toString().trim();
				password_comfirm=et_sign_password_comfirm.getText().toString().trim();
				
				if (!account.equals("") && !password.equals("")) {	
					if (password.equals(password_comfirm)) {
						if (password.length()>=6 && password.length()<=16) {
							register();  //注册账号
						}else {
							ShowToast("输入密码格式不对");
						}
						
					}else {
						Toast.makeText(getApplicationContext(), "两次输入密码不一致", 0).show();
					}
					
				}else {
					Toast.makeText(getApplicationContext(), "账号密码不能为空", 0).show();
				}
			}
		});
	}

	private void register(){

		
		boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
		if(!isNetConnected){
			ShowToast("网络无连接");
			return;
		}
		
		final ProgressDialog progress = new ProgressDialog(SignUpActivity.this);
		progress.setMessage("正在注册...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		//由于每个应用的注册所需的资料都不一样，故IM sdk未提供注册方法，用户可按照bmod SDK的注册方式进行注册。
		//注册的时候需要注意两点：1、User表中绑定设备id和type，2、设备表中绑定username字段
		final User bu = new User();
		bu.setUsername(account);
		bu.setPassword(password);
		//将user和设备id进行绑定aa
		bu.setSex(true);
		/*bu.setDeviceType("android");
		bu.setInstallId(BmobInstallation.getInstallationId(this));*/
		bu.signUp(SignUpActivity.this, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				progress.dismiss();
				ShowToast("注册成功");
				//启动登入页面
				Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
				startActivity(intent);
				finish();
				
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				BmobLog.i(arg1);
				ShowToast("注册失败:" + arg1);
				progress.dismiss();
			}
		});
	}

	
	@Override
	public void back() {
		finish();
	}
}
