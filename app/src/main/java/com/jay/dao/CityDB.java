package com.jay.dao;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jay.bean.City;
import com.jay.bean.County;

public class CityDB {

	private static String DB_NAME = "citys";

	// 获取所有市
	public static ArrayList<City> getAll(Context context) {
		ArrayList<City> cityList = new ArrayList<City>();
		SQLiteDatabase database = DBHelper.getInstance(context);
		Cursor cursor = database.query(DB_NAME, null, null, null, null, null,
				"city_pinyin ASC");

		getDataList(cursor, cityList);
		if (cursor != null)
			cursor.close();
		database.close();
		return cityList;
	}

	private static void getDataList(Cursor cursor, ArrayList<City> cityList) {
		while (cursor != null && cursor.moveToNext()) {
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String province_id = cursor.getString(cursor
					.getColumnIndex("province_id"));
			String city_name = cursor.getString(cursor
					.getColumnIndex("city_name"));

			String city_pinyin = cursor.getString(cursor
					.getColumnIndex("city_pinyin"));
			City city = new City(id, province_id, city_name, city_pinyin);
			cityList.add(city);
		}
	}

	// 获取指定省份下的市
	@SuppressLint("NewApi")
	public static ArrayList<City> getAllByProvinceId(Context context,
			String ProvinceId) {
		ArrayList<City> cityList = new ArrayList<City>();
		SQLiteDatabase database = DBHelper.getInstance(context);
		Cursor cursor = database.query(true, DB_NAME, null, "province_id =?",
				new String[] { ProvinceId }, null, null, "city_pinyin ASC",
				null, null);

		getDataList(cursor, cityList);
		if (cursor != null)
			cursor.close();
		database.close();
		return cityList;
	}

	// 武汉市江夏区
	public static County getCountyByName(Context context, String str) {
		int pos = str.indexOf("市");
		String cityName = str.substring(0, pos);
		String countyName = str.substring(pos + 1, str.length() - 1);
		SQLiteDatabase database = DBHelper.getInstance(context);
		String sql = "SELECT DISTINCT " + "countys.id," + "countys.city_id,"
				+ "countys.county_name," + "countys.county_no,"
				+ "countys.county_pinyin " + " FROM countys, citys WHERE "

				+ "countys.county_name LIKE ? "
				+ "AND countys.city_id = citys.id "
				+ "AND citys.city_name LIKE ? ";
		County county = null;
		Cursor cursor = database.rawQuery(sql, new String[] {
				"%" + countyName + "%", "%" + cityName + "%" });
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
		if (cursor != null)
			cursor.close();
		database.close();
		return county;
	}
}
