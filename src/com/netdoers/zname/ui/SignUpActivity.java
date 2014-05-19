package com.netdoers.zname.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.netdoers.zname.AppConstants;
import com.netdoers.zname.R;
import com.netdoers.zname.Zname;
import com.netdoers.zname.async.ImportContactsTask;

public class SignUpActivity extends SherlockFragmentActivity {
	
	//DECLARE VIEW
	EditText fullName, zName, zNumber;
	TextView signUp;
	ImageView zNameDisplayPicture;
	ActionBar mActionBar;
	
	//TYPEFACE
	Typeface stylefontActionBar;
	
	//CONSTANT
	public static final int IMPORT_PICTURE 					= 10001;
	public static final String TAG 							= "SignUpActivity";
	
	//VARIABLES
	String picturePath = null;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up_layout);
		
		stylefontActionBar = Typeface.createFromAsset(getAssets(), AppConstants.fontStyle);
	
		//GET VIEW FROM LAYOUT
		fullName = (EditText)findViewById(R.id.sign_up_name);
		zName = (EditText)findViewById(R.id.sign_up_zname);
		zNumber = (EditText)findViewById(R.id.sign_up_zname_znumber);
		signUp = (TextView)findViewById(R.id.sign_up_button);
		zNameDisplayPicture = (ImageView)findViewById(R.id.sign_up_display_picture);
		
		
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
			yourTextView.setTypeface(stylefontActionBar);
		} catch (Exception e) {
			Log.e("ActionBar Style", e.toString());
		}
	}
	public void onSignUp(View v)
	{
		try {
			if(validate()){
				if(!Zname.getPreferences().getFirstTime()){
					try {
						new ImportContactsTask(SignUpActivity.this,false).execute();
						Zname.getPreferences().setFirstTime(true);
//						finish();
					} catch (Exception e) {
						Log.e(TAG, e.toString());
					}
				}
				else{
					Intent i = new Intent(SignUpActivity.this, MotherActivity.class);
					startActivity(i);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
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
		
		if(!TextUtils.isEmpty(zName.getText().toString()) && !zName.getText().toString().trim().matches("[a-zA-Z0-9-.:&]+")){
			zName.setError("Alphanumeric  - : & . are allowed");
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
		return true;
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
				}
			}
		}
	
}
