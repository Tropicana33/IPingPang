package com.tropicana.ipingpang;

import com.tropicana.ipingpang.base.BaseActivity;
import com.tropicana.ipingpang.bmob.bean.Feedback;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.bmob.v3.listener.SaveListener;

public class FeedBackActivity extends BaseActivity {


	private View view;
	private EditText et_feedback_content,et_feedback_contacts;
	private Button btn_feedback_commit;
	private String content,contacts;
	@Override
	public void initData() {
		view=View.inflate(getApplicationContext(), R.layout.feedback_activity, null);
		et_feedback_content=(EditText) view.findViewById(R.id.et_feedback_content);
		et_feedback_contacts=(EditText) view.findViewById(R.id.et_feedback_contacts);
		btn_feedback_commit=(Button) view.findViewById(R.id.btn_feedback_commit);
		fl_base.addView(view);
		tv_title.setText("���鷴��");
		btn_close.setVisibility(View.INVISIBLE);
		
		btn_feedback_commit.setOnClickListener(new OnClickListener() {			
			@Override//�ύ����
			public void onClick(View v) {
				content=et_feedback_content.getText().toString().trim();
				contacts=et_feedback_contacts.getText().toString().trim();
				if("".equals(content)){
					ShowToast("���������ݣ��Ա����ǸĽ���");
					return;
				}
				if ("".equals(contacts)) {
					ShowToast("������������ϵ��ʽ���Ա�������ϵ�㣡");
					return;
				}
				
				saveFeedbackMsg(contacts, content); //��д�������ݺ���ϵ��ʽ
				
			}
		});
		
		
	}

	@Override
	public void back() {
		finish();
	}
	
	/**
	 * ���淴����Ϣ�������
	 * @param contacts
	 * @param msg
	 */
	private void saveFeedbackMsg(String contacts,String msg){
		Feedback feedback=new Feedback();
		feedback.setContent(msg);
		feedback.setContacts(contacts);
		feedback.save(this,new SaveListener() {
			
			@Override
			public void onSuccess() {
				ShowToast("������Ϣ�Ѿ��ύ��лл���Ľ��飡");
				
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				Log.d("TOUCH", "������Ϣ�ύʧ��");
			}
		});
		
	}
	
}
