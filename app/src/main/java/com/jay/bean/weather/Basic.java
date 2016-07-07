package com.jay.bean.weather;

public class Basic {
	/*
	 * city 城市名称
	 */
	private String city;
	/*
	 * cnty 国家名称
	 */
	private String cnty;
	/*
	 * 城市id
	 */
	private String id;
	/*
	 * lat 纬度
	 */
	private String lat;
	/*
	 * lon 经度
	 */
	private String lon;
	/*
	 * update 数据更新时间,24小时制
	 */
	private Update update;

	/*
	 * city 城市名称
	 */

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity() {
		return city;
	}

	public void setCnty(String cnty) {
		this.cnty = cnty;
	}

	public String getCnty() {
		return cnty;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLat() {
		return lat;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getLon() {
		return lon;
	}

	public void setUpdate(Update update) {
		this.update = update;
	}

	public Update getUpdate() {
		return update;
	}

}