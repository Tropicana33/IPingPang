package com.tropicana.ipingpang.bean;

import java.util.ArrayList;

/**
 * ���������Ϣ�ķ�װ
 * ��������һ��Ҫ��jsonһ��
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
	 * children,�µ���Ƶ����url��player
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
