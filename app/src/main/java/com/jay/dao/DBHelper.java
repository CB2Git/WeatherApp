package com.jay.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "CityDB.db";
	private static final int DB_VERSION = 1;
	private File mDBPath;
	private AssetManager mAssetManager;
	private static DBHelper helper = null;

	public static synchronized SQLiteDatabase getInstance(Context context) {
		if (helper == null)
			helper = new DBHelper(context);
		return helper.getWritableDatabase();
	}

	public static synchronized void closeDB() {
		if (helper != null)
			helper.close();
		helper = null;
	}

	private DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		mDBPath = context.getDatabasePath(DB_NAME);
		Log.v("数据库目录", mDBPath.getAbsolutePath());
		mAssetManager = context.getAssets();
		copyDB();
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	/**
	 * 拷贝Assets目录下的数据库文件到DataBases文件夹下
	 */
	public void copyDB() {
		if (mDBPath.exists())
			return;
		try {
			InputStream inputStream = mAssetManager.open(DB_NAME);
			// 必须先创建文件夹才行，不然没有文件夹会让创建文件失败
			mDBPath.getParentFile().mkdirs();
			if (mDBPath.createNewFile()) {
				FileOutputStream out = new FileOutputStream(mDBPath);
				byte[] buffer = new byte[1024];
				int len = -1;
				while ((len = inputStream.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				out.close();
				inputStream.close();
				Log.e("DBHelper", "复制ASSERT数据库文件成功");
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("DBHelper", "读取ASSERT数据库文件失败");
		}
	}
}