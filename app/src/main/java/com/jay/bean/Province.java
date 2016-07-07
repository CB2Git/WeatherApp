package com.jay.bean;

public class Province {
	private String id;
	private String name;
	private String pinyin;

	public Province() {
	}

	public Province(String id, String name, String pinyin) {
		this.id = id;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
