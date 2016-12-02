package com.tropicana.ipingpang.bmob.bean;

import java.util.List;

import org.json.JSONObject;

import cn.bmob.v3.BmobUser;

public class MyUser extends BmobUser {

	private static final long serialVersionUID = 1L;
	private String name;
	private Integer age;
	
	private List<String> goodAt;
	private List<JSONObject> notice;
	
	private String address;
	private Float latitude;
	private Float longitude;
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	public List<String> getGoodAt() {
		return goodAt;
	}

	public void setGoodAt(List<String> goodAt) {
		this.goodAt = goodAt;
	}

	public List<JSONObject> getNotice() {
		return notice;
	}

	public void setNotice(List<JSONObject> notice) {
		this.notice = notice;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
}
