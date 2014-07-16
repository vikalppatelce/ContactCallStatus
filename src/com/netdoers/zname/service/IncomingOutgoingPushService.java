package com.netdoers.zname.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.netdoers.zname.AppConstants;
import com.netdoers.zname.BuildConfig;
import com.netdoers.zname.Zname;
import com.netdoers.zname.receiver.ReceiverInComingCall;

public class IncomingOutgoingPushService extends IntentService{

	public static final String TAG = "IncomingOutgoingPushService";
	private String incomingNumber;
	private boolean isBusy;
	
//	@Override
//	public IBinder onBind(Intent intent) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public void onCreate() {
//		// TODO Auto-generated method stub
//		super.onCreate();
//	}
	
	public IncomingOutgoingPushService() {
		super("IncomingOutgoingPushService");
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		incomingNumber = intent.getStringExtra(ReceiverInComingCall.INTENT_EXTRA);
		isBusy = intent.getBooleanExtra(ReceiverInComingCall.INTENT_IS_BUSY, false);
		onStartService();
		if(BuildConfig.DEBUG){
			Log.i(TAG, "CallReceiver:" +incomingNumber);
			Log.i(TAG, "Busy:" +isBusy);
		}
	}

	/*@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		incomingNumber = intent.getStringExtra(ReceiverInComingCall.INTENT_EXTRA);
		isBusy = intent.getBooleanExtra(ReceiverInComingCall.INTENT_IS_BUSY, false);
		onStartService();
		if(BuildConfig.DEBUG){
			Log.i(TAG, "CallReceiver:" +incomingNumber);
			Log.i(TAG, "Busy:" +isBusy);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);
		return START_NOT_STICKY;
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}*/
	
	public void onStartService(){
		if(isNetworkAvailable()){
			new IncomingOutgoingAsyncTask(isBusy).execute();
		}
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	
	public class IncomingOutgoingAsyncTask extends AsyncTask<Void, Void, Void>{
         private boolean isBusyorNot;
         private int intIsBusy;
         private boolean successvalue;
         private String errorvalue;
         
         public IncomingOutgoingAsyncTask(boolean isBusy){
        	 isBusyorNot = isBusy;
        	 if(isBusyorNot){
        		 intIsBusy = 1;
        	 }else{
        		 intIsBusy = 0;
        	 }
         }
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				String str = RestClient.getData(AppConstants.URLS.CALL_URL+Zname.getPreferences().getApiKey()+"/callstatus/"+intIsBusy);
				Log.i(TAG,AppConstants.URLS.CALL_URL+Zname.getPreferences().getApiKey()+"/callstatus/"+intIsBusy);
				Log.i(TAG, str);
				JSONObject object = new JSONObject(str);
				if(!(successvalue = object.getBoolean("status"))){
					try{
						errorvalue = object.getString("errors");
					}
					catch(JSONException e){
						Log.e(TAG, e.toString());
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e(TAG, e.toString());
			}
			return null;
		}
		
	}
}
