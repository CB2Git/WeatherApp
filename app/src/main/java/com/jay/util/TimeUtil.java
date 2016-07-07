package com.jay.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
	public static String getCurrentTime() {
		SimpleDateFormat format = (SimpleDateFormat) SimpleDateFormat
				.getInstance();
		format.applyPattern("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}

	public static Date praseTime(String str) {
		SimpleDateFormat format = (SimpleDateFormat) SimpleDateFormat
				.getInstance();
		format.applyPattern("yyyy-MM-dd HH:mm:ss");
		Date data = null;
		try {
			data = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return data;
	}
}
