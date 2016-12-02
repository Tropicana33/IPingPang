package com.tropicana.ipingpang.bmob.bean;

import java.io.Serializable;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

public class VideoBombBean extends BmobObject implements Serializable{

	/**
	 * qiang yu entity,每个列表item内容
	 * 2014/4/27
	 */
private static final long serialVersionUID = -6280656428527540320L;
	
	private String title;
	private String content;
	private String url;
	private String thumbnail;
	
	private BmobFile Contentfigureurl;
	private int love;
	private int hate;
	private int share;
	private int comment;
	private boolean isPass;
	private boolean myFav;//收藏
	private boolean myLove;//赞
	private BmobRelation relation;
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public BmobFile getContentfigureurl() {
		return Contentfigureurl;
	}
	public void setContentfigureurl(BmobFile contentfigureurl) {
		Contentfigureurl = contentfigureurl;
	}
	public int getLove() {
		return love;
	}
	public void setLove(int love) {
		this.love = love;
	}
	public int getHate() {
		return hate;
	}
	public void setHate(int hate) {
		this.hate = hate;
	}
	public int getShare() {
		return share;
	}
	public void setShare(int share) {
		this.share = share;
	}
	public int getComment() {
		return comment;
	}
	public void setComment(int comment) {
		this.comment = comment;
	}
	public boolean isPass() {
		return isPass;
	}
	public void setPass(boolean isPass) {
		this.isPass = isPass;
	}
	public boolean isMyFav() {
		return myFav;
	}
	public void setMyFav(boolean myFav) {
		this.myFav = myFav;
	}
	public boolean isMyLove() {
		return myLove;
	}
	public void setMyLove(boolean myLove) {
		this.myLove = myLove;
	}
	public BmobRelation getRelation() {
		return relation;
	}
	public void setRelation(BmobRelation relation) {
		this.relation = relation;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public String toString() {
		return "VideoBombBean [title=" + title + ", url=" + url + ", love=" + love + ", hate=" + hate + ", share="
				+ share + ", relation=" + relation + "]";
	}
	
	
	
}
