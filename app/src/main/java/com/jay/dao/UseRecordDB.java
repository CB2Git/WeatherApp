package com.jay.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jay.bean.UseRecord;

import java.util.ArrayList;

public class UseRecordDB {
	private static String DB_NAME = "user_location";

	public static ArrayList<UseRecord> getAll(Context context) {
		ArrayList<UseRecord> userList = new ArrayList<UseRecord>();
		SQLiteDatabase database = DBHelper.getInstance(context);
		Cursor cursor = database.query(DB_NAME, null, null, null, null, null,
				null);

		while (cursor != null && cursor.moveToNext()) {
			String county_no = cursor.getString(cursor
					.getColumnIndex("county_no"));
			String last_update_time = cursor.getString(cursor
					.getColumnIndex("last_update_time"));
			String weather = cursor.getString(cursor.getColumnIndex("weather"));

			UseRecord useRecord = new UseRecord(county_no, last_update_time,
					weather);
			userList.add(useRecord);
		}
		if (cursor != null)
			cursor.close();
		database.close();
		return userList;
	}

	/**
	 * 存储用户添加的位置信息
	 * 
	 * @param UseRecord
	 */
	public static void saveUseRecord(Context context, UseRecord UseRecord) {
		SQLiteDatabase database = DBHelper.getInstance(context);
		database.beginTransaction();
		try {
			database.delete(DB_NAME, "county_no = ?", new String[]{UseRecord.getCounty_no()});
			ContentValues cValues = new ContentValues();
			cValues.put("county_no", UseRecord.getCounty_no());
			cValues.put("last_update_time", UseRecord.getUpdate_time());
			cValues.put("weather", UseRecord.getWeather());
			database.insert(DB_NAME, null, cValues);
			database.setTransactionSuccessful();
			
		}finally{
			database.endTransaction();
			database.close();
		}
	}
}
