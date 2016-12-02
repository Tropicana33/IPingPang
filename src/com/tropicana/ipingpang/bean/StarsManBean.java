package com.tropicana.ipingpang.bean;

import java.util.ArrayList;


public class StarsManBean {

	public int retcode;
	public int extend;
	public ArrayList<StarsInfo> stars;
	
	public class StarsInfo{
		public int id;
		public String url;
		public String player;
		public String image;
		@Override
		public String toString() {
			return "StarsInfo [url=" + url + ", player=" + player + "]";
		}

	}

	@Override
	public String toString() {
		return "StarsManBean [stars=" + stars + "]";
	}
	
	
}
