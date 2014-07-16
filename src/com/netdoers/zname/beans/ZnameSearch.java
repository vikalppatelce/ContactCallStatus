package com.netdoers.zname.beans;

public class ZnameSearch {
	private String fullName;
	private String zName;
	private String imagePath;

	public ZnameSearch() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ZnameSearch(String fullName, String zName, String imagePath) {
		super();
		this.fullName = fullName;
		this.zName = zName;
		this.imagePath = imagePath;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getzName() {
		return zName;
	}

	public void setzName(String zName) {
		this.zName = zName;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
}
