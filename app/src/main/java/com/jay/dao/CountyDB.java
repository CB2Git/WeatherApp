package com.jay.dao;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.jay.bean.County;

public class CountyDB {
	private static String DB_NAME = "countys";

	// 获取所有县
	public static ArrayList<County> getAll(Context context) {
		ArrayList<County> countyList = new ArrayList<County>();
		SQLiteDatabase database = DBHelper.getInstance(context);
		Cursor cursor = database.query(DB_NAME, null, null, null, null, null,
				"county_pinyin ASC");
		getDataList(cursor, countyList);
		if (cursor != null)
			cursor.close();
		database.close();
		return countyList;
	}

	private static void getDataList(Cursor cursor, ArrayList<County> countyList) {
		while (cursor != null && cursor.moveToNext()) {
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String city_id = cursor.getString(cursor.getColumnIndex("city_id"));
			String county_name = cursor.getString(cursor
					.getColumnIndex("county_name"));
			String county_no = cursor.getString(cursor
					.getColumnIndex("county_no"));
			String county_pinyin = cursor.getString(cursor
					.getColumnIndex("county_pinyin"));
			County county = new County(id, city_id, county_name, county_no,
					county_pinyin);
			countyList.add(county);
		}
	}

	// 获取指定省下面的所有城市
	@SuppressLint("NewApi")
	public static ArrayList<County> getAllByCityId(Context context,
			String cityid) {
		ArrayList<County> countyList = new ArrayList<County>();
		SQLiteDatabase database = DBHelper.getInstance(context);
		Cursor cursor = database.query(true, DB_NAME, null, "city_id=?",
				new String[] { cityid }, null, null, "county_pinyin ASC", null,
				null);
		getDataList(cursor, countyList);
		database.close();
		return countyList;
	}

	@SuppressLint("NewApi")
	public static ArrayList<County> getAllByFifter(Context context,
			String fifter) {
		ArrayList<County> countyList = new ArrayList<County>();
		SQLiteDatabase database = DBHelper.getInstance(context);
		Cursor cursor = database.query(true, DB_NAME, null,
				"county_name like ?", new String[] { fifter + "%" }, null,
				null, "county_pinyin ASC", null, null);
		getDataList(cursor, countyList);
		database.close();
		return countyList;
	}

	public static County getCountyById(Context context, String Id) {
		County county = null;
		SQLiteDatabase database = DBHelper.getInstance(context);
		Cursor cursor = database.rawQuery(
				"SELECT * FROM countys WHERE id = ?",
				new String[] {Id});

		if (cursor != null && cursor.moveToNext()) {
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String city_id = cursor.getString(cursor.getColumnIndex("city_id"));
			String county_name = cursor.getString(cursor
					.getColumnIndex("county_name"));
			String county_no = cursor.getString(cursor
					.getColumnIndex("county_no"));
			String county_pinyin = cursor.getString(cursor
					.getColumnIndex("county_pinyin"));
			county = new County(id, city_id, county_name, county_no,
					county_pinyin);
		}
		return county;
	}
}
