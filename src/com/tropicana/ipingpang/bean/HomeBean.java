package com.tropicana.ipingpang.bean;

import java.util.ArrayList;

import com.tropicana.ipingpang.bean.StarsManBean.StarsInfo;
import com.tropicana.ipingpang.bean.TeachVideoBean.TeachVideoName;
import com.tropicana.ipingpang.bean.VideoMenuData.VideoInfo;

/**
 * Ê×Ò³Êý¾Ý
 * @author Administrator
 *
 */

public class HomeBean {

	public int retcode;
	public int extend;
	public String more;
	
	public ArrayList<MyTopInfo> mytop;
	public ArrayList<VideoInfo> hotspots;
	public ArrayList<StarsInfo> homestars;
	public ArrayList<TeachVideoName> hometeachs;
	public ArrayList<VideoInfo> amateurs;
	public ShareInfo shareinfo;
	
	public class MyTopInfo{
		public String id;
		public String title;
		public String url;
		public String thumbnail;
		public String date;
		public int type;

		@Override
		public String toString() {
			return "TopVideoInfo [title=" + title + ", url=" + url + ", thumbnail=" + thumbnail + "]";
		}
				
	}
	

	@Override
	public String toString() {
		return "HomeBean [extend=" + extend + ", mytop=" + mytop + ", homestars=" + homestars + ", hometeachs="
				+ hometeachs + ", amateurs=" + amateurs + ", shareinfo=" + shareinfo + "]";
	}
	
	
 

	
}
