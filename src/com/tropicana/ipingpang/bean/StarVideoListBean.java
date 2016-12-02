package com.tropicana.ipingpang.bean;

import java.util.ArrayList;


public class StarVideoListBean {

	public int retcode;
	public int extend;
	public String player;
	public String des;
	public ArrayList<StarVideoInfo> video;
	
	
	public class StarVideoInfo{
		public String id;
		public String url;
		public String title;
		public String date;
		public String source;
		public String thumbnail;
		@Override
		public String toString() {
			return "StarVideoInfo [title=" + title + ", source=" + source + "]";
		}
		
		
		
	}


	@Override
	public String toString() {
		return "StarVideoListBean [retcode=" + retcode + ", extend=" + extend + ", player=" + player + ", more=" + des
				+ ", video=" + video + "]";
	}


	
	
	
}
