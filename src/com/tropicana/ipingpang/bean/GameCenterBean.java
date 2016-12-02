package com.tropicana.ipingpang.bean;

import java.util.ArrayList;


public class GameCenterBean {

	public int retcode;
	public int extend;
	public String more;
	public ArrayList<GameInfo> games;
	
	
	public class GameInfo{
		public String id;
		public String url;
		public String title;
		public String thumbnail;
		@Override
		public String toString() {
			return "GameInfo [url=" + url + ", title=" + title + ", thumbnail=" + thumbnail + "]";
		}
		
		
		
		
	}


	@Override
	public String toString() {
		return "GameCenterBean [retcode=" + retcode + ", extend=" + extend + ", more=" + more + ", games=" + games
				+ "]";
	}


	

	
	
	
}
