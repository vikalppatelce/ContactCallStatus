/*HISTORY
* CATEGORY 				:- APPLICATION CONTEXT
* DEVELOPER				:- VIKALP PATEL
* AIM 					:- GETTING APPLICATION CONTEXT.
* DESCRIPTION 			:- GETTING FUNCTION WHICH ARE REQUIRED IN OVERALL APPLICATION
* S - START E- END C- COMMENTED U -EDITED A -ADDED
* --------------------------------------------------------------------------------------------------------------------
* INDEX 	DEVELOPER 		DATE 		FUNCTION		DESCRIPTION
* --------------------------------------------------------------------------------------------------------------------
* ZM001    VIKALP PATEL    16/05/2014                   CREATED
* ZM002    VIKALP PATEL    24/06/2014                   ADDED VOLLEY FOR NETWORK OPTIMIZATION
* --------------------------------------------------------------------------------------------------------------------	
*/
package com.netdoers.zname;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.netdoers.zname.beans.Preferences;
import com.netdoers.zname.errorreporting.ExceptionHandler;
import com.netdoers.zname.service.DataController;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class Zname extends Application{

	public static Zname zname;
	public static DataController dataController;
	public static SharedPreferences sharedPreferences;
	public static Preferences preferences;
	public static ImageLoaderConfiguration imageLoaderConfiguration;
	public static final String TAG = Zname.class.getSimpleName();
	 
    private RequestQueue mRequestQueue;
	

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		zname = this;
		dataController = new DataController();
		preferences = new Preferences(this);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		ExceptionHandler.register(zname);
		imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(zname).build();
	}
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}
	public static Zname getApplication()
	{
		return zname;
	}
	public static synchronized Zname getInstance() {
        return zname;
    }
	public static DataController getDataController() {
		return dataController;
	}
	
	public static Preferences getPreferences() {
		return preferences;
	}
	public static ImageLoaderConfiguration getImageLoaderConfiguration() {
		return imageLoaderConfiguration;
	}
	public static SharedPreferences getSharedPreferences()
	{
		return sharedPreferences;
	}
	public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
 
        return mRequestQueue;
    }
	 public <T> void addToRequestQueue(Request<T> req, String tag) {
	        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
	        getRequestQueue().add(req);
	    }
	 
	    public <T> void addToRequestQueue(Request<T> req) {
	        req.setTag(TAG);
	        getRequestQueue().add(req);
	    }
	 
	    public void cancelPendingRequests(Object tag) {
	        if (mRequestQueue != null) {
	            mRequestQueue.cancelAll(tag);
	        }
	    }
	public static void calculateDeviceSize()
	{
		
	}
	public boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}


