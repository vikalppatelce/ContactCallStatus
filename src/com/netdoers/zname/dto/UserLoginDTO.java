/* HISTORY
 * CATEGORY 		:- ACTIVITY
 * DEVELOPER		:- VIKALP PATEL
 * AIM			    :- ADD IPD ACTIVITY
 * DESCRIPTION 		:- SAVE IPD
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * 1000E      VIKALP PATEL    15/02/14          RELEASE         ADDED PASSHASH IN JSON
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.dto;

public class UserLoginDTO {

	boolean status;
	String userName;
	String sign_id;
	//ADDED 1000E
	String passHash;
	public String getPassHash() {
		return passHash;
	}

	public void setPassHash(String passHash) {
		this.passHash = passHash;
	}
	
	public UserLoginDTO(boolean status, String userName, String sign_id, String passHash) {
		this.status = status;
		this.userName = userName;
		this.sign_id = sign_id;
		this.passHash = passHash;
	}
	//ADDED 1000E
	public UserLoginDTO(boolean status, String userName, String signId) {
		this.status = status;
		this.userName = userName;
		sign_id = signId;
	}
	
	public UserLoginDTO() {
		// TODO Auto-generated constructor stub
	}

	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getSign_id() {
		return sign_id;
	}
	public void setSign_id(String signId) {
		sign_id = signId;
	}
}
