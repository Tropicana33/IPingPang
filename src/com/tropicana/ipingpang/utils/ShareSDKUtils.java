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
	 * ShareSDK����
	 */
	public void showShare(){
		
		ShareSDK.initSDK(mContext);
		 OnekeyShare oks = new OnekeyShare();
		 //�ر�sso��Ȩ
		 oks.disableSSOWhenAuthorize(); 
		// oks.setTheme(OnekeyShareTheme.SKYBLUE); //������Ȼɫ
		// ����ʱNotification��ͼ�������  2.5.9�Ժ�İ汾�����ô˷���
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		 // title���⣬ӡ��ʼǡ����䡢��Ϣ��΢�š���������QQ�ռ�ʹ��
		 oks.setTitle(Title);
		 // titleUrl�Ǳ�����������ӣ�������������QQ�ռ�ʹ��
		 oks.setTitleUrl(TitleUrl);
		 // text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
		 oks.setText(Text);
		 // imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
		 //oks.setImagePath("/sdcard/logo.png");//ȷ��SDcard������ڴ���ͼƬ
		 oks.setImageUrl(ImageUrl);
		 //oks.setImageUrl("http://www.ipingpang.com");
		 // url����΢�ţ��������Ѻ�����Ȧ����ʹ��
		 oks.setUrl(Url);
		 // comment���Ҷ�������������ۣ�������������QQ�ռ�ʹ��
		 oks.setComment(Comment);
		 // site�Ƿ�������ݵ���վ���ƣ�����QQ�ռ�ʹ��
		 oks.setSite(mContext.getString(R.string.app_name));
		 // siteUrl�Ƿ�������ݵ���վ��ַ������QQ�ռ�ʹ��
		 oks.setSiteUrl(SiteUrl); 
		 oks.setVenueName(VenueName);  //��������
		// ��������GUI
		 oks.show(mContext);
		}
	
	
}
