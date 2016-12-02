package com.tropicana.ipingpang.global;

import android.os.Environment;

public class GlobalContants {

	//public static final String SERVER_URL="http://192.168.1.119:8080/ipp"; //336
	//public static final String SERVER_URL="http://192.168.1.106:8080/ipp";  //����
	public static final String SERVER_URL="http://1.ipingpang.sinaapp.com/ipp";//������

	public static final String HOME_URL=SERVER_URL+"/homeData.json";  //��ҳ����
	public static final String VIDEO_URL=SERVER_URL+"/video.json";  //��Ƶ����
	public static final String STARS_MAN_URL=SERVER_URL+"/starsMan.json";//����������
	public static final String STARS_WOMEN_URL=SERVER_URL+"/starsWomen.json";//Ů��������
	public static final String WEIXIN_URL=SERVER_URL+"/MediaCenter/WeiXin/weixin.json";   //΢����Ѷ����
	public static final String MEDIACENTER_URL=SERVER_URL+"/mediacenter.json";  //ý����������
	public static final String TEACH_URL=SERVER_URL+"/MediaCenter/TeachVideo/teach.json";//��ѧ��������                                    
	
	public static final int PUBLISH_COMMENT = 1;
	public static final int NUMBERS_PER_PAGE = 15;// ÿ�����󷵻���������
	public static final int SAVE_FAVOURITE = 2;
	public static final int GET_FAVOURITE = 3;
	public static final int GO_SETTINGS = 4;

	public static final String SEX_MALE = "male";
	public static final String SEX_FEMALE = "female";
	
	
	/**
	 * ��ŷ���ͼƬ��Ŀ¼
	 */
	public static String BMOB_PICTURE_PATH = Environment.getExternalStorageDirectory()	+ "/bmobimdemo/image/";
	
	/**
	 * �ҵ�ͷ�񱣴�Ŀ¼
	 */
	public static String MyAvatarDir = "/sdcard/ipingpang/avatar/";
	/**
	 * ���ջص�
	 */
	public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 1;//�����޸�ͷ��
	public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;//��������޸�ͷ��
	public static final int REQUESTCODE_UPLOADAVATAR_CROP = 3;//ϵͳ�ü�ͷ��
	
	public static final int REQUESTCODE_TAKE_CAMERA = 0x000001;//����
	public static final int REQUESTCODE_TAKE_LOCAL = 0x000002;//����ͼƬ
	public static final int REQUESTCODE_TAKE_LOCATION = 0x000003;//λ��
	public static final String EXTRA_STRING = "extra_string";
	
	
	public static final String ACTION_REGISTER_SUCCESS_FINISH ="register.success.finish";//ע��ɹ�֮���½ҳ���˳�
}
