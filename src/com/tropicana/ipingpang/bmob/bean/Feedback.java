package com.tropicana.ipingpang.bmob.bean;

import cn.bmob.v3.BmobObject;

public class Feedback extends BmobObject {

	private String content;  //��������
	private String contacts; //��ϵ��ʽ
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getContacts() {
		return contacts;
	}
	public void setContacts(String contacts) {
		this.contacts = contacts;
	}
	
	
}
