package com.jay.bean;

public class UseRecord {
	private String county_no;
	private String update_time;
	private String weather;

	public UseRecord() {
	}

	public UseRecord(String county_no, String update_time, String weather) {
		this.county_no = county_no;
		this.update_time = update_time;
		this.weather = weather;
	}

	public String getCounty_no() {
		return county_no;
	}

	public void setCounty_no(String county_no) {
		this.county_no = county_no;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

}
