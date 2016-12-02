package com.tropicana.ipingpang.utils;

import com.tropicana.ipingpang.application.MyApplication;

import android.content.Context;
import android.content.SharedPreferences;

public class CacheUtils {
	
	/**
	 * ���û���(����json����)keyΪ��ʶ��valueΪ����
	 */
	public static void setCache(Context context,String key,String value){
		SharePreferenceUtils.putString(context, key, value);
	}
	
	/**
	 * ��ȡ����
	 */
	public static String getCache(String key,Context context) {
		return SharePreferenceUtils.getString(context, key, null);
	}
	
	
}
