package com.jay.bean;

public class City {
	private String id;
	private String province_id;
	private String name;
	private String pinyin;

	public City() {
	}

	public City(String id, String province_id, String name, String pinyin) {
		super();
		this.id = id;
		this.province_id = province_id;
		this.name = name;
		this.pinyin = pinyin;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProvince_id() {
		return province_id;
	}

	public void setProvince_id(String province_id) {
		this.province_id = province_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
