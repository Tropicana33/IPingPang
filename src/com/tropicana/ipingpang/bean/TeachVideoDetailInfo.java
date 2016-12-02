package com.tropicana.ipingpang.bean;

public class TeachVideoDetailInfo {

	public String title;
	public String url;
	public String thumbnail;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public TeachVideoDetailInfo(String title, String url, String thumbnail) {
		super();
		this.title = title;
		this.url = url;
		this.thumbnail = thumbnail;
	}
	
	
}
