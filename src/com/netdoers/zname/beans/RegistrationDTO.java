/* HISTORY
 * CATEGORY 		:- BEAN
 * DEVELOPER		:- VIKALP PATEL
 * AIM			    :- USE AS BEAN FOR SIGN UP
 * DESCRIPTION 		:- TO STORE SIGN UP RESPONSE FROM WEB-SERVICE
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * 10001      VIKALP PATEL    16/05/14           	            CREATED
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.beans;

public class RegistrationDTO {

	boolean status;
	String error;
	String zname;
	String apikey;

	public RegistrationDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public RegistrationDTO(boolean status, String error, String zname,
			String apikey) {
		super();
		this.status = status;
		this.error = error;
		this.zname = zname;
		this.apikey = apikey;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getZname() {
		return zname;
	}
	public void setZname(String zname) {
		this.zname = zname;
	}
	public String getApikey() {
		return apikey;
	}
	public void setApikey(String apikey) {
		this.apikey = apikey;
	}
}
