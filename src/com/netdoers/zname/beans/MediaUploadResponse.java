/* HISTORY
 * CATEGORY 		:- BEAN
 * DEVELOPER		:- VIKALP PATEL
 * AIM			    :- USE AS BEAN FOR MEDIA UPLOAD RESPONSE FROM WEB-SERVICE IN SIGN UP & EDIT PROFILE
 * DESCRIPTION 		:- TO STORE MEDIA UPLOAD RESPONSE FROM WEB-SERVICE 
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * 10001      VIKALP PATEL    16/05/2014                        CREATED
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.beans;

public class MediaUploadResponse {

	boolean success;
	String user_id;
	String file_name;
	public MediaUploadResponse(boolean success, String userId, String fileName) {
		this.success = success;
		user_id = userId;
		file_name = fileName;
	}
	public MediaUploadResponse() {
		// TODO Auto-generated constructor stub
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String userId) {
		user_id = userId;
	}
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String fileName) {
		file_name = fileName;
	}	
}
