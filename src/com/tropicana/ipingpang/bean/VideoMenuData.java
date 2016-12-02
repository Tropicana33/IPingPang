package com.tropicana.ipingpang.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ���������Ϣ�ķ�װ
 * ��������һ��Ҫ��jsonһ��
 * @author Administrator
 *
 */
public class VideoMenuData implements Serializable{

	public int retcode;
	public int extend;
	public String more;
	public ArrayList<VideoInfo> videos;
	public ArrayList<TopVideoInfo> topvideos;
	
	public class TopVideoInfo implements Serializable{
		public String id;
		public String title;
		public int type;
		public String url;
		public String thumbnail;
		public String date;
		public String source;
		@Override
		public String toString() {
			return "TopVideoInfo [title=" + title + ", url=" + url + ", thumbnail=" + thumbnail + "]";
		}
		
		
	}
	
	/**
	 * children,�µ���Ƶ����url��player
	 * @author Administrator
	 *
	 */
	public class VideoInfo implements Serializable{
		public String id;
		public String title;
		public int type;
		public String url;
		public String thumbnail;
		public String date;
		public String source;
		@Override
		public String toString() {
			return "ViewInfo [title=" + title 
					+ " source=" + source + "]";
		}

		
	}
	
	/**
	 * �˶�Ա����
	 * @author Administrator
	 *
	 */
	/*public class Player implements Serializable{
		public String player1;
		public String player2;
		@Override
		public String toString() {
			return "Player [player1=" + player1 + ", player2=" + player2 + "]";
		}
		
		
	}
*/
	@Override
	public String toString() {
		return "VideoMenuData [retcode=" + retcode + ", extend=" + extend + ", more=" + more + ", videos=" + videos
				+ ", topvideos=" + topvideos + "]";
	}

	

	
	
	
	
}
