/* HISTORY
 * CATEGORY			 :- PREFERENCES
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- SAVING SETTINGS | SETUP AND RELEVANT VALUES | GET CLEAR WITH CLEAR DATA INSIDE APPLICATION SETTINGS
 * DESCRITION 		 :- SAVING SHARED PREFERENCES FOR APPLICATION
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * 10001       VIKALP PATEL    16/05/2014       				 CREATED
 * --------------------------------------------------------------------------------------------------------------------
 */

package com.netdoers.zname.beans;

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
	
	public void setRefreshContact(boolean flag)
	{
		editor = sharedPreferences.edit();
		editor.putBoolean("isRefreshContact", flag);
		editor.commit();
	}
	public boolean getRefreshContact()
	{
		return sharedPreferences.getBoolean("isRefreshCallLogs", false);
	}
	public void setRefreshCallLogs(boolean flag)
	{
		editor = sharedPreferences.edit();
		editor.putBoolean("isRefreshContact", flag);
		editor.commit();
	}
	public boolean getRefreshCallLogs()
	{
		return sharedPreferences.getBoolean("isRefreshCallLogs", false);
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
	public void setLastSyncPhoneBook(String str)
	{
		editor = sharedPreferences.edit();
		editor.putString("syncPhoneBook", str);
		editor.commit();
	}
	
	public String getLastSyncPhoneBook()
	{
		String flag = sharedPreferences.getString("syncPhoneBook", null);
		return flag;
	}
	
	public void setUserName(String str){
		editor = sharedPreferences.edit();
		editor.putString("zName", str);
		editor.commit();
	}
	
	public String getUserName(){
		String flag = sharedPreferences.getString("zName", null);
		return flag;
	}
	public void setUserNumber(String str){
		editor = sharedPreferences.edit();
		editor.putString("zNumber", str);
		editor.commit();
	}
	
	public String getUserNumber(){
		String flag = sharedPreferences.getString("zNumber", null);
		return flag;
	}
	public void setFullName(String str){
		editor = sharedPreferences.edit();
		editor.putString("full_name", str);
		editor.commit();
	}
	
	public String getFullName(){
		String flag = sharedPreferences.getString("full_name", null);
		return flag;
	}
	public void setProfilePicPath(String str){
		editor = sharedPreferences.edit();
		editor.putString("profile_pic", str);
		editor.commit();
	}
	
	public String getProfilePicPath(){
		String flag = sharedPreferences.getString("profile_pic", null);
		return flag;
	}
	public void setApiKey(String str)
	{
		editor = sharedPreferences.edit();
		editor.putString("API_KEY", str);
		editor.commit();
	}
	
	public String getApiKey()
	{
		String flag = sharedPreferences.getString("API_KEY", null);
		return flag;
	}
	
	public void setTotalContacts(String str)
	{
		editor = sharedPreferences.edit();
		editor.putString("totalCount", str);
		editor.commit();
	}
	
	public String getTotalContacts()
	{
		String flag = sharedPreferences.getString("totalCount", null);
		return flag;
	}
	
	public void setLastSync(String str)
	{
		editor = sharedPreferences.edit();
		editor.putString("lastSync", str);
		editor.commit();
	}
	
	public String getLastSync()
	{
		String flag = sharedPreferences.getString("lastSync", null);
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
	
	public void setHash(String id)
	{
		editor = sharedPreferences.edit();
		editor.putString("hash", id);
		editor.commit();
	}
	public String getHash()
	{
		String deviceId = sharedPreferences.getString("hash",null);
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
