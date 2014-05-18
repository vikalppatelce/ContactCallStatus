/* HISTORY
 * CATEGORY			 :- PREFERENCES
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- SAVING SETTINGS | SETUP
 * DESCRITION 		 :-  SAVING SHARED PREFERENCES FOR APPLICATION
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * 10001       VIKALP PATEL    06/01/2014       				GETTER - SETTER FOR FULLSCREEN MODE OF APPLICATION
 * 10002       VIKALP PATEL    08/01/2014       				GETTER - SETTER FOR DEVICE ID & IMEI NO
 * 1000E       VIKALP PATEL    15/02/2014       RELEASE         ADDED PASSHASH IN JSON
 * --------------------------------------------------------------------------------------------------------------------
 */

package com.netdoers.zname.dto;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Preferences {

	SharedPreferences sharedPreferences;
	Editor editor;
	
	public Preferences(Context context) {
		// TODO Auto-generated constructor stub
		sharedPreferences = context.getSharedPreferences("Cache", Context.MODE_PRIVATE);
	}
	
	public void setUserLoginDTO(UserLoginDTO userLoginDTO)
	{
		editor = sharedPreferences.edit();
		editor.putString("userId", userLoginDTO.getSign_id());
		editor.putString("name", userLoginDTO.getUserName());
		editor.putString("passHash", userLoginDTO.getPassHash());//ADDED 1000E
		editor.commit();
	}
	
	public UserLoginDTO getUserLoginDTO()
	{
		UserLoginDTO userLoginDTO = new UserLoginDTO();
		userLoginDTO.setSign_id(sharedPreferences.getString("userId", null));
		userLoginDTO.setUserName(sharedPreferences.getString("name", null));
		userLoginDTO.setPassHash(sharedPreferences.getString("passHash", null));//ADDED 1000E
		return userLoginDTO;
	}
	public void setFirstTime(boolean flag)
	{
		editor = sharedPreferences.edit();
		editor.putBoolean("isFirstTime", flag);
		editor.commit();
	}
	public boolean getFirstTime()
	{
		return sharedPreferences.getBoolean("isFirstTime", false);
	}
	public void setSponsorImage(String str)
	{
		editor = sharedPreferences.edit();
		editor.putString("sponsorImage", str);
		editor.commit();
	}
	
	public String getSponsorImage()
	{
		String flag = sharedPreferences.getString("sponsorImage", null);
		return flag;
	}
	
	public void setScreenWidth(String str)
	{
		editor = sharedPreferences.edit();
		editor.putString("screenWidth", str);
		editor.commit();
	}
	
	public String getScreenWidth()
	{
		String flag = sharedPreferences.getString("screenWidth", null);
		return flag;
	}
	
	public String getSpeciality()
	{
		String flag = sharedPreferences.getString("isSet", null);
		return flag;
	}
	
	public void setUpgrade(boolean flag)
	{
		editor = sharedPreferences.edit();
		editor.putBoolean("isUpgrade", flag);
		editor.commit();
	}
	
	public boolean getUpgrade()
	{
		return sharedPreferences.getBoolean("isUpgrade", false);
	}
	public void setSpeciality(String flag)
	{
		editor = sharedPreferences.edit();
		editor.putString("isSet", flag);
		editor.commit();
	}
	
	public void setIsLOVInserted(boolean bool)
	{
		editor = sharedPreferences.edit();
		editor.putBoolean("isLOVInserted", bool);
		editor.commit();
	}
	
	public boolean getIsLOVInserted()
	{
		boolean bool = sharedPreferences.getBoolean("isLOVInserted", false);
		return bool;
	}
	
	//SA10001
	public void setFullScreenMode(boolean bol)
	{
		editor = sharedPreferences.edit();
		editor.putBoolean("isFullScreenMode", bol);
		editor.commit();
	}
	public boolean getFullScreenMode()
	{
		boolean bool = sharedPreferences.getBoolean("isFullScreenMode", false);
		return bool;
	}
	//EA10001
	
	//SA10002
	public void setDeviceId(String id)
	{
		editor = sharedPreferences.edit();
		editor.putString("deviceId", id);
		editor.commit();
	}
	public String getDeviceId()
	{
		String deviceId = sharedPreferences.getString("deviceId", "Device Id Not Found");
				return deviceId;
	}
	public void setDeviceSize(String id)
	{
		editor = sharedPreferences.edit();
		editor.putString("deviceId", id);
		editor.commit();
	}
	public String getDeviceSize()
	{
		String deviceId = sharedPreferences.getString("deviceSize", "Device Size Not Found");
				return deviceId;
	}
	
	public void setSyncFrequency(String id)
	{
		editor = sharedPreferences.edit();
		editor.putString("syncFrequency", id);
		editor.commit();
	}
	public String getSyncFrequency()
	{
		String syncFrequency = sharedPreferences.getString("syncFrequency", "0");
				return syncFrequency;
	}
	public void setResizeImage(String id)
	{
		editor = sharedPreferences.edit();
		editor.putString("resizeImage", id);
		editor.commit();
	}
	public String getResizeImage()
	{
		String resizeImage = sharedPreferences.getString("resizeImage", "0");
				return resizeImage;
	}
	public void setIMEINo(String id)
	{
		editor = sharedPreferences.edit();
		editor.putString("iMEINo", id);
		editor.commit();
	}
	public String getIMEINo()
	{
		String phoneId = sharedPreferences.getString("iMEINo", "IMEI Not Found");
		return phoneId;
	}
	//EA10002
}
