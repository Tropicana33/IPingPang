package com.tropicana.ipingpang.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class MyStoreDbUtils {

private StoreDbOpenHelper helper;
	
	public MyStoreDbUtils(Context context) {
		helper=new StoreDbOpenHelper(context);
	}
	
	/**
	 * ����һ����ʷ��¼���䷵��ֵ�����ж����ݿ��Ƿ���ڸ���ʷ��¼��
	 */
	public boolean find(String title){
		
		boolean result=false;
		
		SQLiteDatabase db=helper.getReadableDatabase();
		if(db.isOpen()){
			Cursor cursor=db.rawQuery("select * from store where title =?", new String[]{title});
			if(cursor.moveToFirst()){
				result=true;
			}
			cursor.close();
			db.close();
		}
		return result;
	}
	
	/**
	 * ���һ��������
	 */
	public boolean add(String title,String url){
		if(find(title)){
			return false;
		}
		SQLiteDatabase db=helper.getWritableDatabase();
		if(db.isOpen()){
			db.execSQL("insert into store (title,url) values (?,?)",new Object[]{title,url});
		}
		return find(title);
	}
	
	/**
	 * ���һ����Ϣ
	 */
	public void InsertData(String title,String url,String time,String thumbnail){
		
		SQLiteDatabase db=helper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("title", title);
		values.put("url", url);
		values.put("time", time);
		values.put("thumbnail", thumbnail);
		if(db.isOpen()){
			Log.d("TOUCH", "DB is open");
			db.insert("store", null, values);
		}
		values.clear();
	}
	
	/**
	 * ����ȫ������ʷ��¼
	 */
	public ArrayList<StoreInfo> findAll(){
		ArrayList<StoreInfo> storeInfos=new ArrayList<StoreInfo>();
		SQLiteDatabase db=helper.getReadableDatabase();
		if(db.isOpen()){
			Cursor cursor=db.rawQuery("select title,url,time,thumbnail from store",null);
			int i=0;   //cursor�������
			for(i=cursor.getCount()-1;i>=0;i--)
			{
				cursor.moveToPosition(i);
			  	StoreInfo info=new StoreInfo();
			   	info.setTitle(cursor.getString(0));
			   	info.setUrl(cursor.getString(1));
			   	info.setTime(cursor.getString(2));
			   	info.setThumbnail(cursor.getString(3));
			   	storeInfos.add(info);
			   	info=null;		
			}
			
		}
		return storeInfos;
	}
	
	/**
	 * ɾ����������
	 */
	public void deleteAll(){
		SQLiteDatabase db=helper.getWritableDatabase();
		db.delete("store", null, null);
	}
	
	
	/**
	 * ɾ��һ����ʷ��¼
	 */
	public void delete(String title){
		SQLiteDatabase db=helper.getWritableDatabase();
		if(db.isOpen()){
			db.execSQL("delete from store where title=?",new Object[]{title});
			db.close();
		}
	}
	
	/**
	 * ������ʷ��¼
	 */
	public void update(String oldtitle,String newtitle,String url){
		SQLiteDatabase db=helper.getWritableDatabase();
		if(db.isOpen()){
			if(TextUtils.isEmpty(newtitle)){
				newtitle=oldtitle;
			}
			db.execSQL("update store set title=? where title=?",new Object[]{newtitle,url,oldtitle});
			db.close();
		}
		
	}
	
	
	
	public String findHistoryUrl(String title){
		String result="";
		SQLiteDatabase db=helper.getReadableDatabase();
		if(db.isOpen()){
			Cursor cursor=db.rawQuery("select url from store where title=?", new String[]{title});
			while (cursor.moveToFirst()) {
				result=cursor.getString(0);
				
			}
			cursor.close();
			db.close();
		}
		return result;
	}
	
	/**
	 * �õ���ǰʱ��
	 */
	public String getCurrentTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}
	
	
}
