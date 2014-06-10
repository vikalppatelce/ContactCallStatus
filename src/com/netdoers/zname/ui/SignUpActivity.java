package com.netdoers.zname.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.MediaColumns;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.netdoers.zname.utils.ImageCompression;

public class SignUpActivity extends SherlockFragmentActivity {
	
	//DECLARE VIEW
	private EditText fullName, zName, zNumber;
	private TextView signUp,agreeTerms;
	private ImageView zNameDisplayPicture;
	private ActionBar mActionBar;
	//TYPEFACE
	static Typeface styleFont;
	//CONSTANT
	public static final int IMPORT_PICTURE 					= 10001;
	public static final String TAG 							= "SignUpActivity";
	//VARIABLES
	String picturePath = null;
	String strPicturePath = null;
	Uri currentFileUri,outputFileUri;
	String strZnameDp = null;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up_layout);
		
		//GET VIEW FROM LAYOUT
		fullName = (EditText)findViewById(R.id.sign_up_name);
		zName = (EditText)findViewById(R.id.sign_up_zname);
		zNumber = (EditText)findViewById(R.id.sign_up_zname_znumber);
		signUp = (TextView)findViewById(R.id.sign_up_button);
		agreeTerms = (TextView)findViewById(R.id.sign_up_txt_agree);
		zNameDisplayPicture = (ImageView)findViewById(R.id.sign_up_display_picture);
		
		agreeTerms.setText(Html.fromHtml(getString(R.string.SignUpAgree)));
		
		styleFont = Typeface.createFromAsset(getAssets(), AppConstants.fontStyle);
		
		fullName.setTypeface(styleFont);
		zName.setTypeface(styleFont);
		zNumber.setTypeface(styleFont);
		signUp.setTypeface(styleFont);
		agreeTerms.setTypeface(styleFont);
		
		//GET ACTION BAR
		mActionBar = getSupportActionBar();
		mActionBar.setTitle("Sign Up");
		fontActionBar("Sign Up");
		
		//LISTENERS
		signUp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onSignUp(v);
			}
		});
		
		zName.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
//				if(validateZname())
//					new ZnameAvailableTask(zName.getText().toString()).execute();
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
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
			if(validate()){
//				String strFullName = fullName.getText().toString().trim();
//				String strzName = zName.getText().toString().trim();
//				String strzNumber = zNumber.getText().toString().trim();
//				
//				TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//				String device_id = mTelephonyMgr.getDeviceId();
//				String device_IMSI = mTelephonyMgr.getSubscriberId();
//				String device_name = getDeviceName();
//				String myVersion = android.os.Build.VERSION.RELEASE;
//				
//				if(isNetworkAvailable()){
//					new RegistrationTask(this).execute(strFullName,strzName,strzNumber, strZnameDp ,device_id,device_IMSI,device_name,myVersion);
//					if(!TextUtils.isEmpty(strZnameDp))
//						new RegistrationUploadTask().execute();
//				
				Zname.getPreferences().setUserName("DEFAULT");
				Intent i = new Intent(SignUpActivity.this, MotherActivity.class);
				startActivity(i);
				finish();
				
//				}
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
	public void onDisplayPicture(View v)
	{
		
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMPORT_PICTURE);	
	}
	
	public boolean validate()
	{
		if(TextUtils.isEmpty(zName.getText().toString())){
			zName.setError("Please enter zname");
			zName.setFocusable(true);
			return false;
		}
		
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
		
		if(TextUtils.isEmpty(zNumber.getText().toString())){
			zNumber.setError("Please enter contact number");
			zNumber.setFocusable(true);
			return false;
		}
		
		if(TextUtils.isEmpty(fullName.getText().toString())){
			fullName.setError("Please enter name");
			fullName.setFocusable(true);
			return false;
		}
		
		if(TextUtils.isEmpty(strZnameDp)){
			Toast.makeText(this, "Please select display picture", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
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
	
	public void getImagePath()
	{
		File imageDirectory =null;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) 
		{
			imageDirectory = new File(AppConstants.IMAGE_DIRECTORY_PATH);
		}
		else
		{
			imageDirectory = new File(AppConstants.IMAGE_DIRECTORY_PATH_DATA);
		}

		imageDirectory.mkdirs();
		File tempFile = new File(imageDirectory, getVideoName()+ AppConstants.EXTENSION);
		outputFileUri = Uri.fromFile( tempFile );
		currentFileUri = outputFileUri;
	}

	public void copy(File src, File dst) throws IOException {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
	
	public static String getVideoName()
	{
		String name = "zname";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			name = sdf.format(new Date(System.currentTimeMillis()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == RESULT_OK) {
				if (requestCode == IMPORT_PICTURE) {
					Uri selectedImage = data.getData();
					String[] filePathColumn = { MediaColumns.DATA };
					Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
					cursor.moveToFirst();
					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					picturePath = cursor.getString(columnIndex);
					cursor.close();
					
					zNameDisplayPicture.setImageURI(Uri.parse(picturePath));
					
					getImagePath();
					try {
						copy(new File(picturePath), new File(currentFileUri.getPath()));
						strZnameDp = currentFileUri.getPath().toString().substring(currentFileUri.getPath().toString().lastIndexOf("/") + 1);
						strPicturePath = ImageCompression.compressImage(picturePath);;
						} 
					catch (IOException e) 
					{
						Log.e("IMPORT_PICTURE", e.toString());
					}
				}
			}
		}

	public class ZnameAvailableTask extends AsyncTask<Void, Void, Void>{
		String verifyZname;
		boolean successvalue = false;
		String errorvalue;
		
		public ZnameAvailableTask(String verifyZname){
			this.verifyZname=verifyZname;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
		JSONObject dataToSend = RequestBuilder.getZnameAvaliabeData(verifyZname);
		Log.i(TAG, dataToSend.toString());
				try {
					String str = RestClient.postData(AppConstants.URLS.BASE_URL, dataToSend);
					JSONObject object = new JSONObject(str);
					if(!(successvalue = object.getBoolean("available"))){
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

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(!successvalue){
				zName.setError("Already taken");
			}
		}
		
	}
	
	
	public class RegistrationTask extends AsyncTask<String, Void, RegistrationDTO>
	{
		Context context;
		ProgressDialog progressDialog;
		RegistrationDTO res = null;
		String errorvalue = null;
		boolean successvalue = false;
		public RegistrationTask(Context context){
			this.context = context;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("Registration...");
			progressDialog.show();
		}
		
		@Override
		protected RegistrationDTO doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONObject dataToSend = RequestBuilder.getRegistraionData(params[0], params[1], params[2], params[3], params[4],params[5],params[6],params[7]);
			Log.i(TAG, dataToSend.toString());
			try {
				String str = RestClient.postData(AppConstants.URLS.BASE_URL, dataToSend);
				res = ResponseParser.getRegistrationResponse(str);
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
			Log.i(TAG, res.toString());
			return res;
		}
		
		@Override
		protected void onPostExecute(RegistrationDTO result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if(progressDialog!=null)
				progressDialog.dismiss();
			
			if(result!=null && result.isStatus()){
				
				Zname.getPreferences().setUserName(result.getZname());
				Zname.getPreferences().setApiKey(res.getApikey());
				Zname.getPreferences().setUserNumber(zNumber.getText().toString());
				if(!TextUtils.isEmpty(strZnameDp)){
					new RegistrationUploadTask(SignUpActivity.this).execute();
				}
				else {
					Intent i = new Intent(SignUpActivity.this, MotherActivity.class);
					startActivity(i);
					finish();
				}
					
			}else{
				if(!TextUtils.isEmpty(errorvalue))
					Toast.makeText(SignUpActivity.this, errorvalue, Toast.LENGTH_SHORT).show();
				    zName.setError("Unavailable");
				    zName.requestFocus();
			}
		}
	}
	
	public class RegistrationUploadTask extends AsyncTask<Void, Void, Void>
	{
		Context context;
		ProgressDialog progressDialog;

		public RegistrationUploadTask(Context context){
			this.context = context;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("Registration...");
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try{
				if(!TextUtils.isEmpty(strZnameDp)){
					String typ = "profile_pic";
					if(!TextUtils.isEmpty(strPicturePath))
					{
						File f = new File(strPicturePath);
						String s = RestClient.uploadFile(typ, f, AppConstants.URLS.MEDIA_BASE_URL+Zname.getPreferences().getApiKey()+"/profilepic");
						Log.i("MediaUpload", s.toString());
					}
				}	
			}
			catch(Exception e){
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

			Intent i = new Intent(SignUpActivity.this, MotherActivity.class);
			startActivity(i);
			finish();
		}
	}
}
