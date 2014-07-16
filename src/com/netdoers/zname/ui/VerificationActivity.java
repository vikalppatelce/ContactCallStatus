package com.netdoers.zname.ui;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.netdoers.zname.AppConstants;
import com.netdoers.zname.R;
import com.netdoers.zname.Zname;
import com.netdoers.zname.service.RequestBuilder;
import com.netdoers.zname.service.RestClient;
import com.netdoers.zname.ui.SignInActivity.VerificationSMSReceiver;

public class VerificationActivity extends SherlockFragmentActivity {
	
	//DECLARE VIEW
	private TextView codeTxt, codeTxtVerify;
	private Button proceed;
	private ActionBar mActionBar;
	private VerificationSMSReceiver messageReceiver;
	
	//TYPEFACE
	static Typeface styleFont;
	//CONSTANT
	public static final String TAG 							= VerificationActivity.class.getSimpleName();
	
	String intentCode;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verification_layout);
		
		//GET VIEW FROM LAYOUT
		codeTxt = (TextView)findViewById(R.id.verification_txt_zname);
		codeTxtVerify = (TextView)findViewById(R.id.verification_txt_verify);
		proceed = (Button)findViewById(R.id.verification_btn);
		
		
		styleFont = Typeface.createFromAsset(getAssets(), AppConstants.fontStyle);
		
		codeTxt.setTypeface(styleFont);
		codeTxtVerify.setTypeface(styleFont);
		proceed.setTypeface(styleFont);
		
		//GET ACTION BAR
		mActionBar = getSupportActionBar();
		mActionBar.setTitle("Verification");
		fontActionBar("Verification");
		
		intentCode = getIntent().getStringExtra(SignInActivity.CODE);
		
		if(!TextUtils.isEmpty(intentCode))
			codeTxt.setText(intentCode);
		//LISTENERS
		proceed.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onVerification(v);
			}
		});
		
		messageReceiver = new VerificationSMSReceiver();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(messageReceiver);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(messageReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
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
	public void onVerification(View v)
	{
		try {
				if(isNetworkAvailable()){
					new VerificationTask(codeTxt.getText().toString(), this).execute();	
				}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
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

	public class VerificationTask extends AsyncTask<Void, Void, Void>{
		String code;
		String apiKey;
		String znumber;
		String fullName;
		boolean successvalue = false;
		String errorvalue;
		private Context context;
		private ProgressDialog progressDialog;
		
		public VerificationTask(String code, Context context){
			this.code=code;
			this.context = context;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
//		JSONObject dataToSend = RequestBuilder.getZnameAvaliabeData(verifyZname);
		Log.i(TAG, AppConstants.URLS.VERIFY_URL);
				try {
//					String dataToSend = "\"{\"zname\": \""+Zname.getPreferences().getUserName()+"\""+ "\"code\": \""+code+"\"";
					JSONObject dataToSend = RequestBuilder.getVerificationData(Zname.getPreferences().getUserName(), code);
					String str = RestClient.postData(AppConstants.URLS.VERIFY_URL,dataToSend);
					JSONObject object = new JSONObject(str);
					apiKey 		= object.getString("api_key");
					fullName 	= object.getString("full_name");
					znumber 	= object.getString("contact_number");
				} catch (Exception e) {
				Log.e(TAG, e.toString());
				}
			return null;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("Verification...");
			progressDialog.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(progressDialog!=null)
				progressDialog.dismiss();
			
			if(TextUtils.isEmpty(apiKey)){
				Toast.makeText(context, "Invalid Verification Code", Toast.LENGTH_SHORT).show();
			}
			else{
				Zname.getPreferences().setApiKey(apiKey);
				Zname.getPreferences().setFullName(fullName);
				Zname.getPreferences().setUserNumber(znumber);
				Intent signIn = new Intent(VerificationActivity.this,MotherActivity.class);
				startActivity(signIn);
				finish();
			}
		}
	}
}
