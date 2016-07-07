package com.jay.bean;

import java.io.Serializable;

public class County implements Serializable {
	private String id;
	private String city_id;

	private String name;
	private String no;
	private String pinyin;

	public County() {
	}

	public County(String id, String city_id, String name, String no,
			String pinyin) {
		this.id = id;
		this.city_id = city_id;
		this.name = name;
		this.no = no;
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

	public String getCity_id() {
		return city_id;
	}

	public void setCity_id(String city_id) {
		this.city_id = city_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	@Override
	public String toString() {
		return "County \nid=" + id + ",\ncity_id=" + city_id + "\n name=" + name
				+ "\nno=" + no + ",\npinyin=" + pinyin;
	}
}
