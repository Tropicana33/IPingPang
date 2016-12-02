package com.tropicana.ipingpang.bean;

import java.util.ArrayList;

/**
 * 网络分类信息的封装
 * 参数名字一定要和json一致
 * @author Administrator
 *
 */
public class NewsMenuData {

	public int retcode;
	public String extend;
	public ArrayList<NewsGroupInfo> data;
	

	public class NewsGroupInfo{
		public ArrayList<NewsChildInfo> news;
		public String date;
	}
	
	
	
	
	/**
	 * children,下的视频数据url，player
	 * @author Administrator
	 *
	 */
	public class NewsChildInfo{
		public String id;
		public String title;
		public String url;
		public String source;
		@Override
		public String toString() {
			return "NewsChildInfo [title=" + title + ", url=" + url + "]";
		}
		

		
	}
	
		

	@Override
	public String toString() {
		return "VideoMenuData [retcode=" + retcode + ", "
				+ "extend=" + extend + ", data=" + data + "]";
	}
	
	
	
	
}
