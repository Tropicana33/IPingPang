package com.tropicana.ipingpang.utils;

import com.tropicana.ipingpang.R;

import android.app.Activity;
import android.content.Context;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class ShareSDKUtils {

	public static Context mContext;
	private String Title;
	private String TitleUrl;
	private String Text;
	private String ImageUrl;
	private String Url;
	private String Comment;	  
	private String VenueName;
	private String SiteUrl; 
	
	public ShareSDKUtils(Context context){
		this.mContext=context;
		this.Title=SharePreferenceUtils.getString(context, "Title", "");
		this.TitleUrl=SharePreferenceUtils.getString(context, "TitleUrl", "");
		this.Text=SharePreferenceUtils.getString(context, "Text", "");
		this.ImageUrl=SharePreferenceUtils.getString(context, "ImageUrl", "");
		this.Url=SharePreferenceUtils.getString(context, "Url", "");
		this.Comment=SharePreferenceUtils.getString(context, "Comment", "");
		this.VenueName=SharePreferenceUtils.getString(context, "VenueName", "");
		this.SiteUrl=SharePreferenceUtils.getString(context, "SiteUrl", "");
		
	} 

	/**
	 * ShareSDK代码
	 */
	public void showShare(){
		
		ShareSDK.initSDK(mContext);
		 OnekeyShare oks = new OnekeyShare();
		 //关闭sso授权
		 oks.disableSSOWhenAuthorize(); 
		// oks.setTheme(OnekeyShareTheme.SKYBLUE); //主题天然色
		// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		 // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		 oks.setTitle(Title);
		 // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		 oks.setTitleUrl(TitleUrl);
		 // text是分享文本，所有平台都需要这个字段
		 oks.setText(Text);
		 // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		 //oks.setImagePath("/sdcard/logo.png");//确保SDcard下面存在此张图片
		 oks.setImageUrl(ImageUrl);
		 //oks.setImageUrl("http://www.ipingpang.com");
		 // url仅在微信（包括好友和朋友圈）中使用
		 oks.setUrl(Url);
		 // comment是我对这条分享的评论，仅在人人网和QQ空间使用
		 oks.setComment(Comment);
		 // site是分享此内容的网站名称，仅在QQ空间使用
		 oks.setSite(mContext.getString(R.string.app_name));
		 // siteUrl是分享此内容的网站地址，仅在QQ空间使用
		 oks.setSiteUrl(SiteUrl); 
		 oks.setVenueName(VenueName);  //分享名字
		// 启动分享GUI
		 oks.show(mContext);
		}
	
	
}
