package com.tropicana.ipingpang.bean;

import java.util.ArrayList;

import android.R.integer;

public class PingpangWebBean {

	public ArrayList<PingpangWebMenu> menu;
	public int extend;
	public int retcode;
	
	public class PingpangWebMenu{
		public String url;
		public String imageurl;
		public int id;
		public String title;
		
	}
}
