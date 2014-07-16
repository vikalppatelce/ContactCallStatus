/* HISTORY
 * CATEGORY			 :- SERVICE
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- IMPORT CONTACT SERVICE + CONTENT OBSERVER ON CONTACTS.CONTACTSCONTRACTS.DATA
 * 
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * ZM001      VIKALP PATEL     16/05/2014                       CREATED 
 * ZM002      VIKALP PATEL     04/06/2014                       ADDED ALL CONTACTS IN BEANS
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.netdoers.zname.AppConstants;
import com.netdoers.zname.BuildConfig;
import com.netdoers.zname.Zname;
import com.netdoers.zname.sqlite.DBConstant;

public class SyncCallStatusService extends Service {

	private static final String TAG = SyncCallStatusService.class.getSimpleName();
	public static final String BROADCAST_ACTION = "com.netdoers.zname.SyncCallStatusService";
	
	Intent broadCastIntent;
	
	StringBuilder strContactNumber  = new StringBuilder("");
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);
		return START_NOT_STICKY;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		onStartService();
		if (BuildConfig.DEBUG) {
			Log.i(TAG, String.valueOf(System.currentTimeMillis()));
		}
	}

	public void onStartService() {
		loadZnameData();
		uploadZnameData();
	}

	private void loadZnameData()
	{
		
		Cursor c = getContentResolver().query(DBConstant.All_Contacts_Columns.CONTENT_URI, null, DBConstant.All_Contacts_Columns.COLUMN_ZNAME + " IS NOT NULL", null, null);
		if(c!=null && c.getCount() > 0){
			int colZnumber = c.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_ZNAME);
			
			while(c.moveToNext()){
				if(!TextUtils.isEmpty(c.getString(colZnumber))){
					strContactNumber.append("\"").append(c.getString(colZnumber)).append("\",");
				}
			}
		}
		
	}
	
	public void uploadZnameData(){
		if(!TextUtils.isEmpty(strContactNumber)){
			try {
				new SyncCallStatusTask("{\"zname\":["+trimLastCommaString(strContactNumber.toString())+"]}").execute();
//				Log.i(TAG, "{\"contact_data\":["+trimLastCommaString(strContactNumber.toString())+"]}");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String trimLastCommaString(String str) {
		  if (str.length() > 0 && str.charAt(str.length()-1)==',') {
		    str = str.substring(0, str.length()-1);
		  }
		  return str;
		}
	
	private void DisplayLoggingInfo(String message) {
    	Log.d(TAG, "entered DisplayLoggingInfo");
    	broadCastIntent = new Intent(BROADCAST_ACTION);
    	broadCastIntent.putExtra("text", message);
    	sendBroadcast(broadCastIntent);
    	stopSelf();
    	if(BuildConfig.DEBUG)
    		Log.d(TAG, "stopSelf()");
    }

	private void parseSyncJSON(JSONObject jsonObject){
		try {
			JSONArray userArray = jsonObject.getJSONArray("users");
			ContentValues values;
			if(BuildConfig.DEBUG)
				Log.i(TAG, "Total Zname :" +String.valueOf(userArray.length()));
			
			for(int i=0; i< userArray.length();i++){
				JSONObject userObj = (JSONObject) userArray.get(i);
				values  = new ContentValues();
				values.put(DBConstant.All_Contacts_Columns.COLUMN_CALL_STATUS, userObj.getString("call_status"));
				int c = getContentResolver().update(DBConstant.All_Contacts_Columns.CONTENT_URI, values, DBConstant.All_Contacts_Columns.COLUMN_ZNAME+"=?", new String[]{userObj.getString("zname")});
				
				if(BuildConfig.DEBUG)
					Log.i(TAG, "Sync Call Status: " +String.valueOf(c));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public class SyncCallStatusTask extends AsyncTask<Void, Void, String>
	{
		  private static final String TAG ="SyncCallStatusTask";
		  String dataToSendString;

		  SyncCallStatusTask(String dataToSendString){
			  this.dataToSendString = dataToSendString;
//			  Log.i(TAG, dataToSend.toString());
		  }

		protected void onPreExecute() {
			super.onPreExecute();
			{
			}
		}
		  @Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub
			  String n = null;
			try {
				String url = AppConstants.URLS.CALL_SYNC_URL+Zname.getPreferences().getApiKey()+"/users/callstatus";
//				JSONObject dataToSend = RequestBuilder.getSyncCallData(dataToSendString);
//				String str = RestClient.postData(url, dataToSend);
				String str = RestClient.postStringData(url, dataToSendString);
				strContactNumber.setLength(0);
				strContactNumber.append("");
				if (BuildConfig.DEBUG) {
					Log.i(TAG, url);
					Log.i("Request-->", dataToSendString.toString());
					Log.i("Response-->", str);
				}
				JSONObject object = new JSONObject(str);
				parseSyncJSON(object);
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
			return n;
			}
		  
		  protected void onPostExecute(String result)
		  {
		    super.onPostExecute(result);
		    	DisplayLoggingInfo("Refreshed");	
		  }
		}
}
