/* HISTORY
 * CATEGORY 		:- BEAN
 * DEVELOPER		:- VIKALP PATEL
 * AIM			    :- USE AS BEAN IN CONTACTS FRAGMENT
 * DESCRIPTION 		:- TO STORE CONTACT AS BEAN WHICH IS SEND TO WEB-SERVICE INSIDE CONTACTS FRAGMENT
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * 10001      VIKALP PATEL    16/05/14           	            CREATED
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.beans;


public class ZnameDTO {
	String contactId;
	String fullName;
	String contactNumber;
	String zNumber;
	String zNameApiKey;

	public ZnameDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ZnameDTO(String contactId, String fullName, String contactNumber,
			String zNumber, String zNameApiKey) {
		super();
		this.contactId = contactId;
		this.fullName = fullName;
		this.contactNumber = contactNumber;
		this.zNumber = zNumber;
		this.zNameApiKey = zNameApiKey;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getzNumber() {
		return zNumber;
	}

	public void setzNumber(String zNumber) {
		this.zNumber = zNumber;
	}

	public String getzNameApiKey() {
		return zNameApiKey;
	}

	public void setzNameApiKey(String zNameApiKey) {
		this.zNameApiKey = zNameApiKey;
	}
}
