package com.netdoers.zname.beans;

public class NotificationDTO {
	private String zname;
	private String imagePath;
	private String fullName;

	public NotificationDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NotificationDTO(String zname, String imagePath) {
		super();
		this.zname = zname;
		this.imagePath = imagePath;
	}

	public NotificationDTO(String zname, String imagePath, String fullName) {
		super();
		this.zname = zname;
		this.imagePath = imagePath;
		this.fullName = fullName;
	}

	public String getZname() {
		return zname;
	}

	public void setZname(String zname) {
		this.zname = zname;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

}
