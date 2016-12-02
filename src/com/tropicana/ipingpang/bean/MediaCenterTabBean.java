package com.tropicana.ipingpang.bean;

import java.util.ArrayList;


public class MediaCenterTabBean {

	public int retcode;
	public int extend;
	public ArrayList<TabDetailInfo> menu;
	
	public class TabDetailInfo{
		public int id;
		public String title;
		public String url;
		
		@Override
		public String toString() {
			return "TabDetailInfo [title=" + title + "]";
		}
		
		
	}

	@Override
	public String toString() {
		return "MediaCenterTabBean [retcode=" + retcode + ", extend=" + extend + ", menu=" + menu + "]";
	}
	
	
}
