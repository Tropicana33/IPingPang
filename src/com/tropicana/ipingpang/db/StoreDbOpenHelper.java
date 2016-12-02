package com.tropicana.ipingpang.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StoreDbOpenHelper extends SQLiteOpenHelper{
	public static final String CREATE_STORE="create table store ("
			+ "_id integer primary key autoincrement,"
			+ "title text,"
			+ "url text,"
			+ "time text,"
			+ "thumbnail text)";
	
	
	public StoreDbOpenHelper(Context context) {
		super(context,"store.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_STORE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
