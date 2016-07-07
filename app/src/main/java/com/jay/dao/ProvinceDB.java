package com.jay.dao;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jay.bean.Province;

public class ProvinceDB {
	private static String DB_NAME = "provinces";

	/**
	 * 获取全部Province
	 */
	public static ArrayList<Province> getAll(Context context) {
		ArrayList<Province> provinceList = new ArrayList<Province>();
		SQLiteDatabase database = DBHelper.getInstance(context);
		Cursor cursor = database.query(DB_NAME, null, null, null, null, null,
				"province_pinyin ASC");

		while (cursor != null && cursor.moveToNext()) {
			String id = cursor.getString(cursor.getColumnIndex("id"));

			String province_name = cursor.getString(cursor
					.getColumnIndex("province_name"));
			
			String pinyin = cursor.getString(cursor
					.getColumnIndex("province_pinyin"));

			Province province = new Province(id, province_name,pinyin);
			provinceList.add(province);
		}
		if (cursor != null)
			cursor.close();
		database.close();
		return provinceList;
	}
}
