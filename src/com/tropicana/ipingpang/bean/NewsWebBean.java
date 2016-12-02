package com.tropicana.ipingpang.bean;

import java.util.ArrayList;

import android.R.integer;

public class NewsWebBean {

	public ArrayList<NewsWebInfo> menu;
	public int extend;
	public int retcode;
	
	public class NewsWebInfo{
		public String url;
		public String imageurl;
		public int id;
		public String title;
		
	}
}
