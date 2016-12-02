package com.tropicana.ipingpang.utils;

import com.tropicana.ipingpang.application.MyApplication;

import android.content.Context;
import android.content.SharedPreferences;

public class CacheUtils {
	
	/**
	 * 设置缓存(缓存json内容)key为标识，value为内容
	 */
	public static void setCache(Context context,String key,String value){
		SharePreferenceUtils.putString(context, key, value);
	}
	
	/**
	 * 读取缓存
	 */
	public static String getCache(String key,Context context) {
		return SharePreferenceUtils.getString(context, key, null);
	}
	
	
}
