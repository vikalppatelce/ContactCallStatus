/* HISTORY
 * CATEGORY 		:- NETWORK
 * DEVELOPER		:- VIKALP PATEL
 * AIM			    :- JSON REQUEST BUILDER
 * DESCRIPTION 		:- JSON REQUEST BUILDER
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * ZMRQ01     VIKALP PATEL     28/05/14         JSON	        CREATED
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.netdoers.zname.Zname;
import com.netdoers.zname.beans.ZnameDTO;

public class RequestBuilder {

	public static JSONObject getRegistraionData(String name, String username,
			String number, String picture, String device_id,
			String device_imsi, String device_name, String myVersion)// EDITED
																		// X0001
	{
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("full_name", name);
			stringBuffer.put("zname", username);
			stringBuffer.put("contact_number", number);
			stringBuffer.put("profile_pic", picture);
			stringBuffer.put("device_name", device_name);
			stringBuffer.put("os_version", myVersion);
			stringBuffer.put("device_id", device_id);
			stringBuffer.put("device_imsi", device_imsi);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	public static JSONObject getSyncCallData(String username)// EDITED
	{
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("zname", username);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	public static JSONObject getVerificationData(String username, String code)// EDITED
	// X0001
	{
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("code", code);
			stringBuffer.put("zname", username);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	public static JSONObject getSignInData(String device_id,
			String device_imsi, String device_name, String myVersion,
			String username) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("zname", username);
			stringBuffer.put("device_name", device_name);
			stringBuffer.put("os_version", myVersion);
			stringBuffer.put("device_id", device_id);
			stringBuffer.put("device_imsi", device_imsi);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

//	D: REQUEST BUILDER  SENT TO SERVER ONCE GCM IS REGISTERED[GCM] [PUSH NOTIFICATION] [REQUEST BUILDER]
	public static JSONObject getPushNotificationData(String imei)
	{
		JSONObject stringBuffer = new JSONObject();
		
		//JSONObject ParentBuffer = new JSONObject();
		try
		{
//			stringBuffer.put("device_id", imei);
			stringBuffer.put("registraion_id", Zname.getSharedPreferences().getString("registration_id", "Not yet Registered"));
			stringBuffer.put("app_version", Zname.getSharedPreferences().getInt("appVersion", 0));
//			stringBuffer.put("zname", Zname.getPreferences().getUserName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return stringBuffer;//ParentBuffer;
	}
	
	public static JSONObject getAddRequestData(String username) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("zname", username);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	public static JSONObject getProfileUpdateData(String device_id,
			String device_imsi, String device_name, String myVersion,
			String name, String number)// EDITED
	// X0001
	{
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("device_name", device_name);
			stringBuffer.put("os_version", myVersion);
			stringBuffer.put("device_id", device_id);
			stringBuffer.put("device_imsi", device_imsi);
			stringBuffer.put("contact_number", number);
			stringBuffer.put("full_name", name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	public static JSONObject getBusyData(String zname, String zname_api_key,
			String busy_number1, String busy_number2, String busy_time,
			boolean busy)// EDITED
							// X0001
	{
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("zname", zname);
			stringBuffer.put("api_key", zname_api_key);
			stringBuffer.put("busy_number_1", busy_number1);
			stringBuffer.put("busy_number_2", busy_number2);
			stringBuffer.put("busy_time", busy_time);
			stringBuffer.put("isBusy", busy);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	public static JSONObject getZnameAvaliabeData(String name) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("zname", name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	public static JSONArray getZnameJSON(ArrayList<ZnameDTO> sxPatient)
			throws JSONException {
		JSONArray services = new JSONArray();
		if (sxPatient != null && sxPatient.size() > 0) {
			for (int i = 0; i < sxPatient.size(); i++) {
				services.put(getZnameData(sxPatient.get(i)));
			}
		}
		return services;
	}

	public static JSONObject getZnameData(ZnameDTO expenseDTOs) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("contact_id", expenseDTOs.getContactId());
			jsonObject.put("full_name", expenseDTOs.getFullName());
			jsonObject.put("contact_number", expenseDTOs.getContactNumber());
			jsonObject.put("znumber", expenseDTOs.getzNumber());
			jsonObject.put("zname_api_key", expenseDTOs.getzNameApiKey());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return jsonObject;
	}

	public static JSONObject getZnameUploadData(JSONObject jsonObject) {
		JSONObject stringBuffer = new JSONObject();

		// JSONObject ParentBuffer = new JSONObject();
		try {
			stringBuffer
					.put("user_api_key", Zname.getPreferences().getApiKey());
			stringBuffer.put("contact_info", jsonObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;// ParentBuffer;
	}

	public static JSONObject getUploadData() {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("zname", Zname.getPreferences().getUserName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
}
