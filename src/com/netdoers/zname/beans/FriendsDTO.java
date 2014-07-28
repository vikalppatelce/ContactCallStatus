package com.netdoers.zname.beans;

public class FriendsDTO {
	private String contactID;
	private String zName;
	private String contactNumber;
	private String profilePath;
	private String fullName;

	public FriendsDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FriendsDTO(String contactID, String zName, String contactNumber,
			String profilePath, String fullName) {
		super();
		this.contactID = contactID;
		this.zName = zName;
		this.contactNumber = contactNumber;
		this.profilePath = profilePath;
		this.fullName = fullName;
	}

	public FriendsDTO(String zName, String contactNumber, String profilePath,
			String fullName) {
		super();
		this.zName = zName;
		this.contactNumber = contactNumber;
		this.profilePath = profilePath;
		this.fullName = fullName;
	}

	public String getContactID() {
		return contactID;
	}

	public void setContactID(String contactID) {
		this.contactID = contactID;
	}

	public String getzName() {
		return zName;
	}

	public void setzName(String zName) {
		this.zName = zName;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getProfilePath() {
		return profilePath;
	}

	public void setProfilePath(String profilePath) {
		this.profilePath = profilePath;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
