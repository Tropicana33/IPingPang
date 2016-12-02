package com.tropicana.ipingpang.bean;

import java.util.ArrayList;

import android.R.integer;

public class WeiboBean {

	public ArrayList<WeiboInfo> menu;
	public int extend;
	public int retcode;
	
	public class WeiboInfo{
		public String url;
		public String imageurl;
		public int id;
		public String title;
		
	}
}
