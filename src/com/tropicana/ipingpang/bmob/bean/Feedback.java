package com.tropicana.ipingpang.bmob.bean;

import cn.bmob.v3.BmobObject;

public class Feedback extends BmobObject {

	private String content;  //反馈内容
	private String contacts; //联系方式
	
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
