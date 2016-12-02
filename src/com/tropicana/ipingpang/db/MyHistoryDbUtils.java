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

public class MyHistoryDbUtils {

private MyHistoryDbOpenHelper helper;
	
	public MyHistoryDbUtils(Context context) {
		helper=new MyHistoryDbOpenHelper(context);
	}
	
	/**
	 * ����һ����ʷ��¼���䷵��ֵ�����ж����ݿ��Ƿ���ڸ���ʷ��¼��
	 */
	public boolean find(String title){
		
		boolean result=false;
		
		SQLiteDatabase db=helper.getReadableDatabase();
		if(db.isOpen()){
			Cursor cursor=db.rawQuery("select * from history where title =?", new String[]{title});
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
			db.execSQL("insert into history (title,url) values (?,?)",new Object[]{title,url});
		}
		return find(title);
	}
	
	/**
	 * ���һ��������
	 */
	public void InsertData(String title,String url,String time){
		
		SQLiteDatabase db=helper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("title", title);
		values.put("url", url);
		values.put("time", time);
		if(db.isOpen()){
			Log.d("TOUCH", "DB is open");
			db.insert("history", null, values);
		}
		values.clear();
	}
	
	
	/**
	 * ɾ����������
	 */
	public void deleteAll(){
		SQLiteDatabase db=helper.getWritableDatabase();
		db.delete("history", null, null);
	}
	
	
	/**
	 * ɾ��һ����ʷ��¼
	 */
	public void delete(String title){
		SQLiteDatabase db=helper.getWritableDatabase();
		if(db.isOpen()){
			db.execSQL("delete from history where title=?",new Object[]{title});
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
			db.execSQL("update history set title=? where title=?",new Object[]{newtitle,url,oldtitle});
			db.close();
		}
		
	}
	
	/**
	 * ����ȫ������ʷ��¼
	 */
	public ArrayList<HistoryInfo> findAll(){
		ArrayList<HistoryInfo> historyInfos=new ArrayList<HistoryInfo>();
		SQLiteDatabase db=helper.getReadableDatabase();
		if(db.isOpen()){
			Cursor cursor=db.rawQuery("select title,url,time from history",null);
			/*while (cursor.moveToNext()) {
			   	HistoryInfo info=new HistoryInfo();
			   	info.setTitle(cursor.getString(0));
			   	info.setUrl(cursor.getString(1));
			   	info.setTime(cursor.getString(2));
			   	historyInfos.add(info);
			   	info=null;		
			}*/
			int i=0;   //cursor�������
			for(i=cursor.getCount()-1;i>=0;i--)
			{
				cursor.moveToPosition(i);
			  	HistoryInfo info=new HistoryInfo();
			   	info.setTitle(cursor.getString(0));
			   	info.setUrl(cursor.getString(1));
			   	info.setTime(cursor.getString(2));
			   	historyInfos.add(info);
			   	info=null;		
			}
		}
		return historyInfos;
	}
	
	public String findHistoryUrl(String title){
		String result="";
		SQLiteDatabase db=helper.getReadableDatabase();
		if(db.isOpen()){
			Cursor cursor=db.rawQuery("select url from history where title=?", new String[]{title});
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
