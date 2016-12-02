package com.tropicana.ipingpang;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.tropicana.ipangpang.view.ClearEditText;
import com.tropicana.ipingpang.base.BaseActivity;
import com.tropicana.ipingpang.bmob.bean.User;
import com.tropicana.ipingpang.utils.LogUtils;
import com.tropicana.ipingpang.utils.SharePreferenceUtils;
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
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class LoginActivity extends BaseActivity{

	private View view;
	private Button btn_login;
	private TextView btn_sign_up;
	private ClearEditText et_account;
	private ClearEditText et_password;
	private CheckBox cb_remmember_pwd;
	private String account;
	private String password;
	private boolean is_remember_pwd=false;

	@Override
	public void initData() {
		view=View.inflate(getApplicationContext(), R.layout.login_activity, null);
		btn_login=(Button) view.findViewById(R.id.btn_login);
		btn_sign_up=(TextView) view.findViewById(R.id.btn_sign_up);
		et_account=(ClearEditText) view.findViewById(R.id.et_account);
		et_password=(ClearEditText) view.findViewById(R.id.et_password);
		cb_remmember_pwd=(CheckBox) view.findViewById(R.id.cb_remember_pwd);
		tv_title.setText("�˻�����");
		fl_base.addView(view); 		
        btn_close.setVisibility(View.GONE); 
        //getLocationInfo();
        account=SharePreferenceUtils.getString(getApplicationContext(), "user_account", "");
        et_account.setText(account);
        et_account.setSelection(account.length());//���λ��
		btn_login.setOnClickListener(new OnClickListener() {//�˺ŵ���			
			@Override
			public void onClick(View v) {
				account=et_account.getText().toString().trim();
				password=et_password.getText().toString().trim();
				if (!account.equals("")&& !password.equals("")) {
					System.out.println("�˺ŵ���");
					checkIsRemberPwd();//����Ƿ��ס����
					login();  //�����˺�
				}else {
					Toast.makeText(getApplicationContext(), "�˺����벻��Ϊ��", 0).show();
				}
				
			}
		});
		btn_sign_up.setOnClickListener(new OnClickListener() { //�˺�ע��			
			@Override
			public void onClick(View v) {
				Intent intentSign=new Intent(LoginActivity.this,SignUpActivity.class);
				startActivity(intentSign);
			}
		});
		
		//��ס����
		is_remember_pwd=SharePreferenceUtils.getBoolean(getApplicationContext(), 
				"is_remmember_pwd", false);
		cb_remmember_pwd.setChecked(is_remember_pwd);
		Log.d("TOUCH","�Ƿ��ס���룺"+is_remember_pwd);
		if (is_remember_pwd) {
			password=SharePreferenceUtils.getString(getApplicationContext(), 
					"user_pwd", "");
			Log.d("TOUCH","���룺"+password);
			et_password.setText(password);
		}
		cb_remmember_pwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isCheck) {
				is_remember_pwd=isCheck;			
				SharePreferenceUtils.putBoolean(getApplicationContext(), 
							"is_remmember_pwd", is_remember_pwd);
				
			}
		});
	
	}

	private void login(){
		if (TextUtils.isEmpty(account)) {
			ShowToast("�˺Ų���Ϊ��");
			return;
		}

		if (TextUtils.isEmpty(password)) {
			ShowToast("���벻��Ϊ��");
			return;
		}

		final ProgressDialog progress = new ProgressDialog(
				LoginActivity.this);
		progress.setMessage("���ڵ�½...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		User user = new User();
		user.setUsername(account);
		user.setPassword(password);
		userManager.login(user,new SaveListener() {
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progress.setMessage("���ڵ�����...");
					}
				});
				progress.dismiss();
				Intent intent = new Intent(LoginActivity.this,MyInfoActivity.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onFailure(int errorcode, String arg0) {
				progress.dismiss();
				BmobLog.i(arg0);
				ShowToast(arg0);
			}
		});
		
	}
	
	private void checkIsRemberPwd(){
		if (is_remember_pwd) {  //����Ƿ��ס����
			SharePreferenceUtils.putString(getApplicationContext(), 
					"user_pwd", et_password.getText().toString().trim());
			Log.d("TOUCH","check���룺"+et_password.getText().toString().trim());
		}else {
			SharePreferenceUtils.putString(getApplicationContext(), "user_pwd", "");
			Log.d("TOUCH","���룺"+password);
		}				
	}
	
	@Override
	public void back() {
		finish();  //����activity
	}
	
	
	
	
}
