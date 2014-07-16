package com.netdoers.zname.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.netdoers.zname.AppConstants;
import com.netdoers.zname.R;
import com.netdoers.zname.Zname;
import com.netdoers.zname.beans.RegistrationDTO;
import com.netdoers.zname.service.RequestBuilder;
import com.netdoers.zname.service.ResponseParser;
import com.netdoers.zname.service.RestClient;

public class SignInActivity extends SherlockFragmentActivity {
	
	//DECLARE VIEW
	private EditText zName;
	private TextView signTxtHead, signTxtVerify;
	private Button signIn;
	private ActionBar mActionBar;
	
	
	//TYPEFACE
	static Typeface styleFont;
	
	//CONSTANT
	public static final String TAG 							= SignInActivity.class.getSimpleName();
	public static final String CODE                         = "code";
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in_layout);
		
		//GET VIEW FROM LAYOUT
		zName = (EditText)findViewById(R.id.sign_in_txt_zname);
		signTxtHead = (TextView)findViewById(R.id.sign_in_txt_head);
		signTxtVerify = (TextView)findViewById(R.id.sign_in_txt_verify);
		signIn = (Button)findViewById(R.id.sign_in_btn);
		
		
		styleFont = Typeface.createFromAsset(getAssets(), AppConstants.fontStyle);
		
		zName.setTypeface(styleFont);
		signTxtHead.setTypeface(styleFont);
		signTxtVerify.setTypeface(styleFont);
		signIn.setTypeface(styleFont);
		
		//GET ACTION BAR
		mActionBar = getSupportActionBar();
		mActionBar.setTitle("Sign In");
		fontActionBar("Sign In");
		
		//LISTENERS
		signIn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onSignUp(v);
			}
		});
	}

	public void fontActionBar(String str)
	{
		try {
			int titleId;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				titleId = getResources().getIdentifier("action_bar_title","id", "android");
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
	public void onSignUp(View v)
	{
		try {
			if(validateZname()){
				zName.setError(null);
				if(isNetworkAvailable()){
					TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					String device_id = mTelephonyMgr.getDeviceId();
					String device_IMSI = mTelephonyMgr.getSubscriberId();
					String device_name = getDeviceName();
					String myVersion = android.os.Build.VERSION.RELEASE;
					
					new SignInTask(this).execute(device_id,device_IMSI,device_name,myVersion,zName.getText().toString().trim());	
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}
	
    public String getDeviceName() {
    	  try
    	  {
    		  String manufacturer = Build.MANUFACTURER;
        	  String model = Build.MODEL;
        	  if (model.startsWith(manufacturer)) {
        	    return capitalize(model);
        	  } else {
        	    return capitalize(manufacturer) + " " + model;
        	  }
    	  }
    	  catch(Exception e)
    	  {
    		  return "Device Unidentified";
    	  }
    	}
    
  	private String capitalize(String s) {
  		try {
  			if (s == null || s.length() == 0) {
  				return "";
  			}
  			char first = s.charAt(0);
  			if (Character.isUpperCase(first)) {
  				return s;
  			} else {
  				return Character.toUpperCase(first) + s.substring(1);
  			}

  		} catch (Exception e) {
  			e.printStackTrace();
  				return "";
  		}
  	} 
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	
	public boolean validateZname()
	{
		if(!TextUtils.isEmpty(zName.getText().toString()) && zName.getText().toString().trim().length() < 2){
			zName.setError("Please enter atleast 3 characters");
			zName.setFocusable(true);
			return false;
		}
		
		if(!TextUtils.isEmpty(zName.getText().toString()) && !zName.getText().toString().trim().matches("^[a-zA-Z0-9]+")){
			zName.setError("Must start with alphabets or number");
			zName.setFocusable(true);
			return false;
		}
		if(!TextUtils.isEmpty(zName.getText().toString()) && !zName.getText().toString().trim().matches("[a-zA-Z0-9-.&]+")){
			zName.setError("Alphanumeric  - & . are allowed");
			zName.setFocusable(true);
			return false;
		}
		return true;
	}

	
	public class VerificationSMSReceiver extends BroadcastReceiver {
        // Get the object of SmsManager
        final SmsManager sms = SmsManager.getDefault();
        public void onReceive(Context context, Intent intent) {
            // Retrieves a map of extended data from the intent.
            final Bundle bundle = intent.getExtras();
            try {
                if (bundle != null) {
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                        String senderNum = phoneNumber;
                        String message = currentMessage.getDisplayMessageBody();
                        Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);
                       // Show Alert
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, 
                                     "senderNum: "+ senderNum + ", message: " + message, duration);
                        toast.show();
                    } // end for loop
                  } // bundle is null
    
            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" +e);
            }
        }    
    }
	
	public class SignInTask extends AsyncTask<String, Void, Void>
	{
		Context context;
		ProgressDialog progressDialog;
		RegistrationDTO res = null;
		String errorvalue = null;
		String codevalue = null;
		boolean successvalue = false;
		public SignInTask(Context context){
			this.context = context;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("Sign In...");
			progressDialog.show();
		}
		
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
//			device_id,device_IMSI,device_name,myVersion,zName.getText().toString().trim()
			JSONObject dataToSend = RequestBuilder.getSignInData(params[0], params[1], params[2], params[3], params[4]);
			Log.i(TAG, dataToSend.toString());
			try {
				String str = RestClient.postData(AppConstants.URLS.SIGN_IN_URL, dataToSend);
				res = ResponseParser.getRegistrationResponse(str);
				JSONObject object = new JSONObject(str);
				if(!(successvalue = object.getBoolean("status"))){
					try{
						errorvalue = object.getString("errors");
					}
					catch(JSONException e){
						Log.e(TAG, e.toString());
					}
				}else{
					codevalue = object.getString("code");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e(TAG, e.toString());
			}
			Log.i(TAG, res.toString());
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(progressDialog!=null)
				progressDialog.dismiss();
			
			if(!TextUtils.isEmpty(errorvalue)){
				Toast.makeText(SignInActivity.this, errorvalue, Toast.LENGTH_SHORT).show();
			    zName.setError("Unavailable");
			    zName.requestFocus();
			}else{
				Zname.getPreferences().setUserName(zName.getText().toString());
				Intent i = new Intent(SignInActivity.this, VerificationActivity.class);
				i.putExtra(CODE, codevalue);
				startActivity(i);
				finish();				
			}
		}
	}

}
