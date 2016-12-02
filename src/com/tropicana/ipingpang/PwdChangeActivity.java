package com.tropicana.ipingpang;

import com.tropicana.ipangpang.view.ClearEditText;
import com.tropicana.ipingpang.base.BaseActivity;
import com.tropicana.ipingpang.bmob.bean.User;

import android.app.ProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

public class PwdChangeActivity extends BaseActivity {

	private View view;
	private ClearEditText et_old_pwd,et_new_password,et_new_password_comfirm;
	private Button btn_change_pwd;
	private String old_pwd="",new_pwd="",new_pwd_comfirm="";
	
	
	@Override
	public void initData() {
		view=View.inflate(this, R.layout.activity_pwd_change, null);
		et_old_pwd=(ClearEditText) view.findViewById(R.id.et_old_pwd);
		et_new_password=(ClearEditText) view.findViewById(R.id.et_new_password);
		et_new_password_comfirm=(ClearEditText) view.findViewById(R.id.et_new_password_comfirm);
		btn_change_pwd=(Button) view.findViewById(R.id.btn_change_pwd);
		
		fl_base.addView(view);
		tv_title.setText("�����޸�");
		btn_close.setVisibility(View.GONE); 
		
		btn_change_pwd.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				old_pwd=et_old_pwd.getText().toString().trim();
				new_pwd=et_new_password.getText().toString().trim();
				new_pwd_comfirm=et_new_password_comfirm.getText().toString().trim();				
				if ("".equals(old_pwd)) {
					ShowToast("�����������");
					return;
				}
				if ("".equals(new_pwd)) {
					ShowToast("������������");
					return;
				}
				if ("".equals(new_pwd_comfirm)) {
					ShowToast("��ȷ��������");
					return;
				}
				if (new_pwd.equals(new_pwd_comfirm)) {		
					final ProgressDialog progress = new ProgressDialog(
							PwdChangeActivity.this);
					progress.setMessage("�����޸�����");
					progress.setCanceledOnTouchOutside(false);
					progress.show();
					BmobUser.updateCurrentUserPassword(getApplicationContext(),
							old_pwd, new_pwd, new UpdateListener() {

								@Override
								public void onSuccess() {									
									progress.dismiss();
									ShowToast("�����޸ĳɹ�");
									finish();
								}
								
								@Override
								public void onFailure(int code, String msg) {
									ShowToast("�����������");
									
								}
							});
				}else {
					ShowToast("�������벻һ��");
				}
		
			}
		});
		
	}

}
















