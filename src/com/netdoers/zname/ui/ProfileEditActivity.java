package com.netdoers.zname.ui;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.netdoers.zname.AppConstants;
import com.netdoers.zname.R;
import com.netdoers.zname.Zname;
import com.netdoers.zname.service.RequestBuilder;
import com.netdoers.zname.service.RestClient;
import com.netdoers.zname.utils.Utilities;

public class ProfileEditActivity extends SherlockFragmentActivity {

	// DECLARE VIEWS
	ActionBar mActionBar;
	TextView mName, mNumber;
	Button mBtnSave;
	
	//CONSTANTS
	public static final String TAG = ProfileEditActivity.class.getSimpleName();

	// DECLARE STYLE TYPEFACE
	Typeface styleFont;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_edit);

		initUi();
		
		setFontStyle();
		setActionBar("Edit Profile");
		
		mName.append(Zname.getPreferences().getFullName());
		mNumber.append(Zname.getPreferences().getUserNumber());
		
		setListeners();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void initUi(){
		mName = (TextView)findViewById(R.id.activity_profile_edit_name);
		mNumber = (TextView)findViewById(R.id.activity_profile_edit_number);
		mBtnSave = (Button)findViewById(R.id.activity_profile_edit_save);
	}
	
	public void setActionBar(String str) {
		mActionBar = getSupportActionBar();
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setTitle(str);
		fontActionBar(mActionBar.getTitle().toString());
	}

	public void setFontStyle() {
		styleFont = Typeface.createFromAsset(getAssets(),AppConstants.fontStyle);
		mName.setTypeface(styleFont);
		mNumber.setTypeface(styleFont);
		mBtnSave.setTypeface(styleFont);
	}

	public void setListeners(){
		mBtnSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String isValidName[], isValidNumber[];
				isValidName =  validateName(mName.getText().toString());
				isValidNumber = validateContact(mNumber.getText().toString());
				
				if(isValidName[0].equalsIgnoreCase("true") || isValidNumber[0].equalsIgnoreCase("true")){
					TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					String device_id = mTelephonyMgr.getDeviceId();
					String device_IMSI = mTelephonyMgr.getSubscriberId();
					String device_name = Utilities.getDeviceName();
					String myVersion = android.os.Build.VERSION.RELEASE;
					
					if(isNetworkAvailable())
						new ProfileUpdateTask(ProfileEditActivity.this, mName.getText().toString(), mNumber.getText().toString()).execute(device_id, device_IMSI, device_name, myVersion, mName.getText().toString(), mNumber.getText().toString());
					
				}else{
                        if(!TextUtils.isEmpty(isValidNumber[1])){
                        	mNumber.setError(isValidNumber[1]);
                        }
                        
                        if(!TextUtils.isEmpty(isValidName[1])){
                        	mName.setError(isValidName[1]);
                        }
				}
			}
		});
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	
	public void fontActionBar(String str) {
		try {
			int titleId;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				titleId = getResources().getIdentifier("action_bar_title",
						"id", "android");
			} else {
				titleId = R.id.abs__action_bar_title;
			}
			TextView yourTextView = (TextView) findViewById(titleId);
			yourTextView.setText(str);
			yourTextView.setTypeface(styleFont);
		} catch (Exception e) {
			Log.e("ActionBar Style", e.toString());
		}
	}
	
	public String[] validateName(String s){
		String str[] = new String[2];
		str[0] = "true";
		if(TextUtils.isEmpty(s)){
			str[0]="false";
			str[1]="Name must not be empty";
		}
		if(s.equalsIgnoreCase(Zname.getPreferences().getFullName())){
			str[0]="false";
			str[1]="Must not be same name";
		}
		return str;
	}
	
	public String[] validateContact(String s){
		String str[] = new String[2];
		str[0] = "true";
		if(TextUtils.isEmpty(s)){
			str[0] = "false";
			str[1] = "Contact must not be empty";
		}
		if(s.equalsIgnoreCase(Zname.getPreferences().getUserNumber())){
			str[0] = "false";
			str[1] = "Must not be same number";
		}
		if(s.length() != 10){
			str[0]="false";
			str[1]="Must be of 10 digits";
		}
		return str;
	}
   
	public class ProfileUpdateTask extends AsyncTask<String, Void, Void>{

		String errorValue,name,number;
		boolean successValue = false;
		ProgressDialog progressDialog;
		Context context;
		
		public ProfileUpdateTask(Context context, String name, String number){
			this.context = context;
			this.name=name;
			this.number=number;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("Updating...");
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONObject dataToSend = RequestBuilder.getProfileUpdateData(params[0], params[1], params[2], params[3], params[4], params[5]);
			Log.i(TAG, dataToSend.toString());
			Log.i(TAG, AppConstants.URLS.UPDATE_URL+Zname.getPreferences().getApiKey());
			try {
				String str = RestClient.putData(AppConstants.URLS.UPDATE_URL+Zname.getPreferences().getApiKey(), dataToSend);
				JSONObject object = new JSONObject(str);
				if(!(successValue = object.getBoolean("status"))){
					try{
						errorValue = object.getString("errors");
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
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(progressDialog!=null)
				progressDialog.dismiss();
			
			if(successValue){
				Zname.getPreferences().setFullName(name);
				Zname.getPreferences().setUserNumber(number);
				mName.setText(Zname.getPreferences().getFullName());
				mNumber.setText(Zname.getPreferences().getUserNumber());
			}
		}
	}
	
	
}
