package com.tropicana.ipingpang.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtils {

	private static final String SHARE_PREFS_NAME="tropicana";
	private static SharedPreferences sp;
	
	public static void putBoolean(Context context,String key,boolean value){
		if (sp==null) {
			sp=context.getSharedPreferences(SHARE_PREFS_NAME, context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key, value).commit();
	}
	
	public static boolean getBoolean(Context context,String key,boolean defaultvalue){
		if (sp==null) {
			sp=context.getSharedPreferences(SHARE_PREFS_NAME, context.MODE_PRIVATE);
		}
		return sp.getBoolean(key, defaultvalue);
	}
	
	
	public static void  putString(Context context,String key,String value){
		if (sp==null) {
			sp=context.getSharedPreferences(SHARE_PREFS_NAME, context.MODE_PRIVATE);
		}
		sp.edit().putString(key, value).commit();
	}
	
	public static String getString(Context context,String key,String defaultvalue){
		if (sp==null) {
			sp=context.getSharedPreferences(SHARE_PREFS_NAME, context.MODE_PRIVATE);
		}
		return sp.getString(key, defaultvalue);
	}
	
	public static void  putInt(Context context,String key,Integer value){
		if (sp==null) {
			sp=context.getSharedPreferences(SHARE_PREFS_NAME, context.MODE_PRIVATE);
		}
		sp.edit().putInt(key, value).commit();
	}
	
	public static int getInt(Context context,String key,Integer defaultvalue){
		if (sp==null) {
			sp=context.getSharedPreferences(SHARE_PREFS_NAME, context.MODE_PRIVATE);
		}
		return sp.getInt(key, defaultvalue);
	}
}















