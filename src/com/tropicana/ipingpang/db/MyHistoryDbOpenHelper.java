package com.tropicana.ipingpang.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyHistoryDbOpenHelper extends SQLiteOpenHelper {

	public static final String CREATE_HISTORY="create table history ("
			+ "_id integer primary key autoincrement,"
			+ "title text,"
			+ "url text,"
			+ "time text)";
	
	
	public MyHistoryDbOpenHelper(Context context) {
		super(context,"history.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_HISTORY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
