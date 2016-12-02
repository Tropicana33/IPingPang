package com.tropicana.ipingpang.global;

import android.os.Environment;

public class GlobalContants {

	//public static final String SERVER_URL="http://192.168.1.119:8080/ipp"; //336
	//public static final String SERVER_URL="http://192.168.1.106:8080/ipp";  //宿舍
	public static final String SERVER_URL="http://1.ipingpang.sinaapp.com/ipp";//新浪云

	public static final String HOME_URL=SERVER_URL+"/homeData.json";  //首页数据
	public static final String VIDEO_URL=SERVER_URL+"/video.json";  //视频数据
	public static final String STARS_MAN_URL=SERVER_URL+"/starsMan.json";//男明星数据
	public static final String STARS_WOMEN_URL=SERVER_URL+"/starsWomen.json";//女明星数据
	public static final String WEIXIN_URL=SERVER_URL+"/MediaCenter/WeiXin/weixin.json";   //微信咨讯数据
	public static final String MEDIACENTER_URL=SERVER_URL+"/mediacenter.json";  //媒体中心数据
	public static final String TEACH_URL=SERVER_URL+"/MediaCenter/TeachVideo/teach.json";//教学中心数据                                    
	
	public static final int PUBLISH_COMMENT = 1;
	public static final int NUMBERS_PER_PAGE = 15;// 每次请求返回评论条数
	public static final int SAVE_FAVOURITE = 2;
	public static final int GET_FAVOURITE = 3;
	public static final int GO_SETTINGS = 4;

	public static final String SEX_MALE = "male";
	public static final String SEX_FEMALE = "female";
	
	
	/**
	 * 存放发送图片的目录
	 */
	public static String BMOB_PICTURE_PATH = Environment.getExternalStorageDirectory()	+ "/bmobimdemo/image/";
	
	/**
	 * 我的头像保存目录
	 */
	public static String MyAvatarDir = "/sdcard/ipingpang/avatar/";
	/**
	 * 拍照回调
	 */
	public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 1;//拍照修改头像
	public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;//本地相册修改头像
	public static final int REQUESTCODE_UPLOADAVATAR_CROP = 3;//系统裁剪头像
	
	public static final int REQUESTCODE_TAKE_CAMERA = 0x000001;//拍照
	public static final int REQUESTCODE_TAKE_LOCAL = 0x000002;//本地图片
	public static final int REQUESTCODE_TAKE_LOCATION = 0x000003;//位置
	public static final String EXTRA_STRING = "extra_string";
	
	
	public static final String ACTION_REGISTER_SUCCESS_FINISH ="register.success.finish";//注册成功之后登陆页面退出
}
