package com.jay.activity;

import android.app.Application;

import com.baidu.apistore.sdk.ApiStoreSDK;

public class MyApplication extends Application {

	private static String API_KEY = "44fcfc982204fe9ac10f950695b346e3";

	@Override
	public void onCreate() {
		// 初始化ApiStore
		ApiStoreSDK.init(this, API_KEY);
		super.onCreate();
	}
}
