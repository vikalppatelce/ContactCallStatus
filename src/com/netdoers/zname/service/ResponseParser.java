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
 * 1000A       VIKALP PATEL    04/02/14         RELEASE          ADD SX,IPD & OPD IMAGES
 * 1000B-3     VIKALP PATEL    11/02/14         RELEASE          ADD PAYMENT API
 * 1000E       VIKALP PATEL    15/02/14         RELEASE          ADD PASSWORD HASHING IN JSON
 * 1000F       VIKALP PATEL    05/03/14         RELEASE          ADD DEPOSITED BANK LOV
 * P3A03       VIKALP PATEL    10/05/14         PHASEIII         ADD SERVICE CONTACT SHARING
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.netdoers.zname.AppConstants;
import com.netdoers.zname.beans.MediaUploadResponse;
import com.netdoers.zname.beans.RegistrationDTO;



public class ResponseParser 
{
	public static RegistrationDTO getRegistrationResponse(String params)
	{
		RegistrationDTO loginDTO = null;
		JSONObject childObject;
		String username = null;
		boolean status;
		String userid = null;
		String passhash=null;//ADDED 1000E
		try 
		{
			childObject = new JSONObject(params);
			status = childObject.getBoolean(AppConstants.RESPONSES.RegistrationResponse.STATUS);
			
			if(childObject.has(AppConstants.RESPONSES.RegistrationResponse.ZNAME))
			{
				username = childObject.getString(AppConstants.RESPONSES.RegistrationResponse.ZNAME);
			}
			if(childObject.has(AppConstants.RESPONSES.RegistrationResponse.ERROR))
			{
				userid = childObject.getString(AppConstants.RESPONSES.RegistrationResponse.ERROR);
			}
			if(childObject.has(AppConstants.RESPONSES.RegistrationResponse.API_KEY))
			{
				passhash = childObject.getString(AppConstants.RESPONSES.RegistrationResponse.API_KEY);
			}
			loginDTO = new RegistrationDTO(status, userid, username, passhash);
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return loginDTO;
	}
	
	public static MediaUploadResponse getMediaUploadResponse(String s)
	{
		MediaUploadResponse uploadResponse = new MediaUploadResponse();
		try {
			JSONObject jsonObject = new JSONObject(new String(s));
			
			if(jsonObject.has("success"))
			{
				uploadResponse.setSuccess(jsonObject.getBoolean("success"));
			}
			if(jsonObject.has("user_id"))
			{
				uploadResponse.setUser_id(jsonObject.getString("user_id"));
			}
			if(jsonObject.has("file_name"))
			{
				uploadResponse.setFile_name(jsonObject.getString("file_name"));
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uploadResponse;
	}
}
