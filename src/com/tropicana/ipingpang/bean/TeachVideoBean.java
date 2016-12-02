package com.tropicana.ipingpang.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.tropicana.ipingpang.bean.VideoMenuData.VideoInfo;

public class TeachVideoBean {

	public ArrayList<TeachVideoName> menu;
	public int extend;
	public int recode;
	
	public class TeachVideoName{
		public int id;
		public String url;
		public String title;
		public String imageurl;
		@Override
		public String toString() {
			return "TeachVideoName [title=" + title + "]";
		}
		
	}
	
	public class TeachVideoInfo implements Serializable{
		
		public String title;
		public String url;
		public String thumbnail;
		@Override
		public String toString() {
			return "TeachVideoInfo [title=" + title + ", url=" + url + "]";
		}
		
	}

	@Override
	public String toString() {
		return "TeachVideoBean [menu=" + menu + ", extend=" + extend + ", recode=" + recode + "]";
	}
	
	
}
