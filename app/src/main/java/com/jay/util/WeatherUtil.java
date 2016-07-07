package com.jay.util;

import com.baidu.apistore.sdk.ApiCallBack;
import com.baidu.apistore.sdk.ApiStoreSDK;
import com.baidu.apistore.sdk.network.Parameters;
import com.jay.bean.County;

/**
 * 异步回调天气信息
 * 
 * @author Jay
 * 
 */
public class WeatherUtil {
	private static String REQUEST_WEATHER_ADDR = "http://apis.baidu.com/heweather/weather/free";

	@SuppressWarnings("unchecked")
	public static void getWeather(County county, ApiCallBack callBack) {
		Parameters para = new Parameters();
		para.put("city", county.getPinyin());
		ApiStoreSDK.execute(REQUEST_WEATHER_ADDR, ApiStoreSDK.GET, para,
				callBack);

	}
}
