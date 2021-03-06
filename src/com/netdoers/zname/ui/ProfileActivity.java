package com.netdoers.zname.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.MediaColumns;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.netdoers.zname.AppConstants;
import com.netdoers.zname.R;
import com.netdoers.zname.Zname;
import com.netdoers.zname.beans.RegistrationDTO;
import com.netdoers.zname.service.RequestBuilder;
import com.netdoers.zname.service.ResponseParser;
import com.netdoers.zname.service.RestClient;
import com.netdoers.zname.utils.CircleImageView;
import com.netdoers.zname.utils.ImageCompression;
import com.netdoers.zname.utils.SegmentedButton;
import com.netdoers.zname.utils.SegmentedButton.OnClickListenerSegmentedButton;
import com.netdoers.zname.utils.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileActivity extends SherlockFragmentActivity{
	
	//DECLARE VIEWS
	private CircleImageView mCircleImgProfile;
	private ActionBar mActionBar;
//	private SegmentedButton statusSegmentedButton;
//	private ScrollableGridView statusSelectGrid;
	private TextView statusEdit;
	private TextView  zNumberTxt, statusHeadTxt, statusSelectHead, nameTxt, znameTxt;
	private ImageView statusUpdate,  imageUpdate;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	
	//DECLARE STYLE TYPEFACE
	Typeface styleFont;
	
	//DECLARE VARIABLES
	String intentID;
	String intentName;
	String intentPhoto;
	String picturePath;
	String strPicturePath;
	String strZnameDp;
	Uri currentFileUri,outputFileUri;
	
	//CONSTANTS
	final static String TAG = ProfileActivity.class.getSimpleName();
	public static final int IMPORT_PICTURE 					= 10001;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		initUi();
		try {
			zNumberTxt.setText(Zname.getPreferences().getUserNumber());
			nameTxt.setText(Zname.getPreferences().getFullName());
			znameTxt.setText(Zname.getPreferences().getUserName());
			statusEdit.setText(Zname.getPreferences().getUserStatus());
			setStatusEditIcon(Zname.getPreferences().getUserStatusType());
		} catch (NullPointerException e) {
			Log.e(TAG, e.toString());
		}
				
		intentName = Zname.getPreferences().getUserName();

		setUniversalImageLoader();
		setFontStyle();
		setActionBar("Profile");

		/*statusSegmentedButton.clearButtons();
		statusSegmentedButton.addButtons(
                getString(R.string.str_profile_status_work),
                getString(R.string.str_profile_status_read),
                getString(R.string.str_profile_status_place),
                getString(R.string.str_profile_status_random)
                );

        // First button is selected
		statusSegmentedButton.setPushedButtonIndex(0);

    	Cursor cr = null;
			cr = getContentResolver().query(DBConstant.User_Status_Columns.CONTENT_URI, null, DBConstant.User_Status_Columns.COLUMN_STATUS_TYPE+"=0", null, DBConstant.User_Status_Columns.COLUMN_ID + " DESC");
		if(cr.getCount() > 0){
			cr.moveToFirst();
			statusEdit.setText(cr.getString(cr.getColumnIndex(DBConstant.User_Status_Columns.COLUMN_STATUS)));
		}else{
			statusEdit.setText("");	
		}
		if(cr!=null){
			cr.close();
		}

			
		onStatusAdapter(0);*/

		new GetProfileImageTask(this, imageLoader, options).execute();
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				imageLoader.displayImage(Zname.getPreferences().getProfilePicPath(), mCircleImgProfile, options);
			}
		});

		
		zNumberTxt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onNameUpdate(v);
			}
		});
		
		nameTxt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onContactUpdate(v);
			}
		});
		
		imageUpdate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onProfilePicUpdate(v);
			}
		});
		
        // Some example click handlers. Note the click won't get executed
        // if the segmented button is already selected (dark blue)
		/*statusSegmentedButton.setOnClickListener(new OnClickListenerSegmentedButton() {
            @Override
            public void onClick(int index) {
            	Cursor cr = null;
				if (index == 0) {//work
					cr = getContentResolver().query(DBConstant.User_Status_Columns.CONTENT_URI, null, DBConstant.User_Status_Columns.COLUMN_STATUS_TYPE+"=0", null, DBConstant.User_Status_Columns.COLUMN_ID + " DESC");
					if(cr.getCount() > 0){
						cr.moveToFirst();
						statusEdit.setText(cr.getString(cr.getColumnIndex(DBConstant.User_Status_Columns.COLUMN_STATUS)));
					}else{
						statusEdit.setText("");	
					}
//					statusEdit.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_hint_work), null, null, null);
//					statusEdit.setHint(getString(R.string.str_profile_status_work_hint));
					
					onStatusAdapter(0);
				} else if (index == 1) {//read
					cr = getContentResolver().query(DBConstant.User_Status_Columns.CONTENT_URI, null, DBConstant.User_Status_Columns.COLUMN_STATUS_TYPE+"=1", null, DBConstant.User_Status_Columns.COLUMN_ID + " DESC");
					if(cr.getCount() > 0){
						cr.moveToFirst();
						statusEdit.setText(cr.getString(cr.getColumnIndex(DBConstant.User_Status_Columns.COLUMN_STATUS)));
					}else{
						statusEdit.setText("");	
					}
//					statusEdit.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_hint_read), null, null, null);
//					statusEdit.setHint(getString(R.string.str_profile_status_read_hint));
					onStatusAdapter(1);
				}else if (index == 2) {//place
					cr = getContentResolver().query(DBConstant.User_Status_Columns.CONTENT_URI, null, DBConstant.User_Status_Columns.COLUMN_STATUS_TYPE+"=2", null, DBConstant.User_Status_Columns.COLUMN_ID + " DESC");
					if(cr.getCount() > 0){
						cr.moveToFirst();
						statusEdit.setText(cr.getString(cr.getColumnIndex(DBConstant.User_Status_Columns.COLUMN_STATUS)));
					}else{
						statusEdit.setText("");	
					}
//					statusEdit.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_hint_places), null, null, null);
//					statusEdit.setHint(getString(R.string.str_profile_status_place_hint));
					onStatusAdapter(2);
				} else if (index == 3){//random
					cr = getContentResolver().query(DBConstant.User_Status_Columns.CONTENT_URI, null, DBConstant.User_Status_Columns.COLUMN_STATUS_TYPE+"=3", null, DBConstant.User_Status_Columns.COLUMN_ID + " DESC");
					if(cr.getCount() > 0){
						cr.moveToFirst();
						statusEdit.setText(cr.getString(cr.getColumnIndex(DBConstant.User_Status_Columns.COLUMN_STATUS)));
					}else{
						statusEdit.setText("");	
					}
//					statusEdit.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_hint_random), null, null, null);
//					statusEdit.setHint(getString(R.string.str_profile_status_random_hint));
					onStatusAdapter(3);
				}
				if(cr!=null){
					cr.close();
				}
            }
        });*/
		
//		statusUpdate.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				onUpdate(v);
//			}
//		});
	}
	@Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		onStatusAdapter(0);
		zNumberTxt.setText(Zname.getPreferences().getUserNumber());
		nameTxt.setText(Zname.getPreferences().getFullName());
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_profile, menu);
		// MenuItem overFlowMenu = menu.findItem(R.id.action_more);
		// MenuItem notificationMenu = menu.findItem(R.id.action_notification);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        case android.R.id.home:
            finish();
            return true;
        case R.id.action_profile_edit:
        	Intent profileEditIntent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
        	startActivity(profileEditIntent);
        	return true;
        case R.id.action_profile_view:
        	Intent photoViewIntent = new Intent(ProfileActivity.this, PhotoViewActivity.class);
        	photoViewIntent.putExtra(PhotoViewActivity.mIntentPhoto, Zname.getPreferences().getProfilePicPath());
        	startActivity(photoViewIntent);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	public void initUi(){
		mCircleImgProfile = (CircleImageView)findViewById(R.id.fragment_profile_img_zname);
		statusEdit = (TextView)findViewById(R.id.fragment_profile_zname_edit_status);
		zNumberTxt = (TextView)findViewById(R.id.fragment_profile_txt_call_1);
		nameTxt = (TextView)findViewById(R.id.fragment_profile_txt_name);
		znameTxt = (TextView)findViewById(R.id.fragment_profile_txt_zname);
		statusUpdate = (ImageView)findViewById(R.id.fragment_profile_zname_status_update);
		imageUpdate = (ImageView)findViewById(R.id.fragment_profile_img_change);
		statusHeadTxt = (TextView)findViewById(R.id.fragment_profile_txt_zname_status_head);
		/*statusSelectHead = (TextView)findViewById(R.id.fragment_profile_zname_update_gridview_head);
		statusSegmentedButton = (SegmentedButton)findViewById(R.id.segmented);
		statusSelectGrid = (ScrollableGridView)findViewById(R.id.fragment_profile_zname_update_gridview);
		statusSelectGrid.setExpanded(true);*/
	}
	
	public void setFontStyle(){
		styleFont = Typeface.createFromAsset(getAssets(), AppConstants.fontStyle);
		
		statusEdit.setTypeface(styleFont);
		zNumberTxt.setTypeface(styleFont);
		nameTxt.setTypeface(styleFont);
		znameTxt.setTypeface(styleFont);
		statusHeadTxt.setTypeface(styleFont);
//		statusSelectHead.setTypeface(styleFont);
	}
	
	public void setActionBar(String str){
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setTitle(str);
		
		fontActionBar(mActionBar.getTitle().toString());
	}
	
	public void setUniversalImageLoader(){
		imageLoader = ImageLoader.getInstance();
        // Initialize ImageLoader with configuration. Do it once.
        imageLoader.init(Zname.getImageLoaderConfiguration());
        
        options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.def_contact) // resource or drawable
        .showImageForEmptyUri(R.drawable.def_contact) // resource or drawable
        .showImageOnFail(R.drawable.def_contact) //this is the image that will be displayed if download fails
        .cacheInMemory()
        .cacheOnDisc()
        .build();
	}
	
	/*public void onUpdateStatusSegment(String s){
		if(!TextUtils.isEmpty(s)){
			ContentValues values = new ContentValues();
			values.put(DBConstant.User_Status_Columns.COLUMN_STATUS, s);
			values.put(DBConstant.User_Status_Columns.COLUMN_STATUS_TYPE, statusSegmentedButton.getSelectedButtonIndex());
			getContentResolver().insert(DBConstant.User_Status_Columns.CONTENT_URI, values);
			
			onStatusAdapter(statusSegmentedButton.getSelectedButtonIndex());
			
			statusEdit.setText(s);
		}
	}*/
	
	/*public void onStatusUpdate(View v){
		switch(statusSegmentedButton.getSelectedButtonIndex()){
		case 0: // watch
			showUpdateDialog(getString(R.string.str_profile_status_update), getString(R.string.str_profile_status_work_hint), getResources().getDrawable(R.drawable.ic_hint_work), 0);
			break;
		case 1: // read
			showUpdateDialog(getString(R.string.str_profile_status_update), getString(R.string.str_profile_status_read_hint), getResources().getDrawable(R.drawable.ic_hint_read), 0);
			break;
		case 2: //place
			showUpdateDialog(getString(R.string.str_profile_status_update), getString(R.string.str_profile_status_place_hint), getResources().getDrawable(R.drawable.ic_hint_places), 0);
			break;
		case 3:	//random
			showUpdateDialog(getString(R.string.str_profile_status_update), getString(R.string.str_profile_status_random_hint), getResources().getDrawable(R.drawable.ic_hint_random), 0);
			break;
		default:
			break;
		}
	}*/
	
	public void onContactUpdate(View v){
		showUpdateDialog(getString(R.string.str_profile_update_contact),Zname.getPreferences().getUserNumber(),getResources().getDrawable(R.drawable.accounts_glyph_phone_default),4);
	}
	
	public void onNameUpdate(View v){
		showUpdateDialog(getString(R.string.str_profile_update_name),Zname.getPreferences().getFullName(),getResources().getDrawable(R.drawable.accounts_glyph_username_default),5);
	}
	
	public void onProfilePicUpdate(View v){
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMPORT_PICTURE);
	}
	
	public void showUpdateDialog(String _title, String _hint, Drawable drawable, final int statusType)
	{
		final Dialog dialog = new Dialog(ProfileActivity.this);
		try {
			dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		} catch (Exception e) {
			Log.e("Dialog", e.toString());
		}
		dialog.setContentView(R.layout.dialog_profile_update_layout);
		
		final EditText updateTxt = (EditText)dialog.findViewById(R.id.dialog_profile_update_txt);
		Button btnUpdateOk = (Button)dialog.findViewById(R.id.dialog_profile_update_ok);
		Button btnUpdateCancel = (Button)dialog.findViewById(R.id.dialog_profile_update_cancel);
		TextView dialogTitle = (TextView)dialog.findViewById(R.id.dialog_profile_update_title);
		
		dialogTitle.setText(_title);
//		updateTxt.setText(_hint);
		updateTxt.append(_hint);
		updateTxt.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
		
		dialogTitle.setTypeface(styleFont);
		updateTxt.setTypeface(styleFont);
		btnUpdateOk.setTypeface(styleFont);
		btnUpdateCancel.setTypeface(styleFont);
		
		btnUpdateOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String isValid[];
				
				switch(statusType){ //ADD VALIDATION 
				case 4: // CONTACT
					isValid = validateContact(updateTxt.getText().toString());
					break;
				case 5: // NAME
					isValid = validateName(updateTxt.getText().toString());
					break;
				default: //STATUS
					isValid = validateStatus(statusType, updateTxt.getText().toString());
						break;
				}
				
				if(isValid[0].equalsIgnoreCase("true")){
					
					TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					String device_id = mTelephonyMgr.getDeviceId();
					String device_IMSI = mTelephonyMgr.getSubscriberId();
					String device_name = Utilities.getDeviceName();
					String myVersion = android.os.Build.VERSION.RELEASE;
					
					switch(statusType){
					case 4:
						if(isNetworkAvailable()){
							new UpdateProfileTask(ProfileActivity.this, statusType, updateTxt.getText().toString().trim()).execute(device_id,device_IMSI,device_name,myVersion,updateTxt.getText().toString().trim());
						}
						break;
					case 5:
						if(isNetworkAvailable()){
							new UpdateProfileTask(ProfileActivity.this, statusType, updateTxt.getText().toString().trim()).execute(device_id,device_IMSI,device_name,myVersion,updateTxt.getText().toString().trim());
						}
						break;
					default:
//						onUpdateStatusSegment(updateTxt.getText().toString());
						break;
					}
					dialog.dismiss();
				}
				else{
					updateTxt.setError(isValid[1]);
				}
				
			}
		});
		
		btnUpdateCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
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
	
	public String[] validateStatus(int type, String s){
		String str[] = new String [2];
		str[0] = "true";
		return str;
	}

	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	
	public void onStatusUpdate(View v){
		showUpdateStatusDialog();
	}
	
/*	public void showDialogToChooseStatus() {
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(
				ProfileActivity.this);
		builderSingle.setTitle(getResources().getString(R.string.str_profile_status_title));
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				ProfileActivity.this,
				android.R.layout.select_dialog_singlechoice);

		arrayAdapter.add(getResources().getString(R.string.str_profile_status_random));
		arrayAdapter.add(getResources().getString(R.string.str_profile_status_read));
		arrayAdapter.add(getResources().getString(R.string.str_profile_status_place));
		arrayAdapter.add(getResources().getString(R.string.str_profile_status_work));

		builderSingle.setNegativeButton("CANCEL",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builderSingle.setAdapter(arrayAdapter,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String strName = arrayAdapter.getItem(which);
						showUpdateStatusDialog(strName, which);
					}
				});
		builderSingle.show();
	}*/

	public void showUpdateStatusDialog()
	{
		final Dialog dialog = new Dialog(ProfileActivity.this);
		try {
			dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		} catch (Exception e) {
			Log.e("Dialog", e.toString());
		}
		dialog.setContentView(R.layout.dialog_profile_status);
		
		final EditText mUpdateTxt = (EditText)dialog.findViewById(R.id.dialog_profile_status_txt);
		Button mBtnUpdateOk = (Button)dialog.findViewById(R.id.dialog_profile_status_ok);
		Button mBtnUpdateCancel = (Button)dialog.findViewById(R.id.dialog_profile_status_cancel);
		final SegmentedButton mStatusSegmentedButton = (SegmentedButton)dialog.findViewById(R.id.dialog_profile_status_segmented);
		
		mStatusSegmentedButton.clearButtons();
		mStatusSegmentedButton.addButtons(
                getString(R.string.str_profile_status_work),
                getString(R.string.str_profile_status_read),
                getString(R.string.str_profile_status_place),
                getString(R.string.str_profile_status_random)
                );

        // First button is selected
		mStatusSegmentedButton.setPushedButtonIndex(0);

		mStatusSegmentedButton.setOnClickListener( new OnClickListenerSegmentedButton() {
            @Override
            public void onClick(int index) {
            	switch (index) {
        		case 0:
        			mUpdateTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_hint_random), null, null, null);
        			break;
        		case 1:
        			mUpdateTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_hint_read), null, null, null);
        			break;
        		case 2:
        			mUpdateTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_hint_places), null, null, null);
        			break;
        		case 3:
        			mUpdateTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_hint_work), null, null, null);
        			break;
        		}
              }
            });
		
		
		
		mUpdateTxt.setTypeface(styleFont);
		mBtnUpdateOk.setTypeface(styleFont);
		mBtnUpdateCancel.setTypeface(styleFont);
		
		mBtnUpdateOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!TextUtils.isEmpty(mUpdateTxt.getText().toString())){
					if(isNetworkAvailable()){
						new UpdateStatusAsync(ProfileActivity.this, mUpdateTxt.getText().toString(), String.valueOf(mStatusSegmentedButton.getSelectedButtonIndex())).execute();
						dialog.dismiss();
					}else{
						Toast.makeText(ProfileActivity.this, getResources().getString(R.string.str_no_internet), Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		mBtnUpdateCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	
	
	/*public void onStatusAdapter(int statusType){
		Cursor c = getContentResolver().query(DBConstant.User_Status_Columns.CONTENT_URI, null, DBConstant.User_Status_Columns.COLUMN_STATUS_TYPE+"="+statusType, null, DBConstant.User_Status_Columns.COLUMN_ID + " DESC");
		if(c.getCount() > 0){
			ArrayList<String> arrWorkStatus = new ArrayList<String>();
			int counter = 0;
			c.moveToFirst();
			do {
				arrWorkStatus.add(c.getString(c.getColumnIndex(DBConstant.User_Status_Columns.COLUMN_STATUS)));
				counter++;
			} while (c.moveToNext() && counter < 5);
			statusSelectGrid.setAdapter(new StatusAdapter(this, arrWorkStatus));
		}
		else{
			ArrayList<String> arrWorkStatus = new ArrayList<String>();
			arrWorkStatus.add("");
		    statusSelectGrid.setAdapter(new StatusAdapter(this, arrWorkStatus));
		}
		if (c != null) {
			c.close();
		}
	}*/
	
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

	public void getImagePath()
	{
		File imageDirectory =null;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)){
			imageDirectory = new File(AppConstants.IMAGE_DIRECTORY_PATH);
		}
		else{
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
	
	private void galleryAddPic() {
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    File f = new File(currentFileUri.getPath());
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
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
					
					getImagePath();
					try {
						copy(new File(picturePath), new File(currentFileUri.getPath()));
						strZnameDp = picturePath.toString().substring(picturePath.toString().lastIndexOf("/") + 1);
						strPicturePath = ImageCompression.compressImage(picturePath);;
						galleryAddPic();
						
						new UpdateProfilePictureUploadTask(ProfileActivity.this).execute();
						} 
					catch (IOException e) 
					{
						Log.e("IMPORT_PICTURE", e.toString());
					}
				}
			}
		}

	public void setStatusEditIcon(String statusType){
		try{
			if(!TextUtils.isEmpty(statusType))
				switch (Integer.parseInt(statusType)) {
				case 0:
					statusEdit.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_hint_random), null, null, null);
					break;
				case 1:
					statusEdit.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_hint_read), null, null, null);
					break;
				case 2:
					statusEdit.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_hint_places), null, null, null);
					break;
				case 3:
					statusEdit.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_hint_work), null, null, null);
					break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public class UpdateProfilePictureUploadTask extends AsyncTask<Void, Void, Void>
	{
		Context context;
		ProgressDialog progressDialog;

		public UpdateProfilePictureUploadTask(Context context){
			this.context = context;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("Uploading");
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
			
			mCircleImgProfile.setImageURI(Uri.parse("file:///"+picturePath));
		}
	}
	public class UpdateProfileTask extends AsyncTask<String, Void, RegistrationDTO>
	{
		Context context;
		ProgressDialog progressDialog;
		RegistrationDTO res = null;
		String errorvalue = null;
		boolean successvalue = false;
		int updateType;
		String updateValue;
		public UpdateProfileTask(Context context, int updateType,String updateValue){
			this.context = context;
			this.updateType = updateType;
			this.updateValue = updateValue;
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
		protected RegistrationDTO doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONObject dataToSend = RequestBuilder.getProfileUpdateData(params[0], params[1], params[2], params[3], params[4], String.valueOf(updateType));
			Log.i(TAG, dataToSend.toString());
			Log.i(TAG, AppConstants.URLS.UPDATE_URL+Zname.getPreferences().getApiKey());
			try {
				String str = RestClient.putData(AppConstants.URLS.UPDATE_URL+Zname.getPreferences().getApiKey(), dataToSend);
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
				switch(updateType){
				case 4:
					Zname.getPreferences().setUserNumber(updateValue);
					zNumberTxt.setText(Zname.getPreferences().getUserNumber());
					break;
				case 5:
					Zname.getPreferences().setFullName(updateValue);
					nameTxt.setText(Zname.getPreferences().getFullName());
					break;
				}
			}else{
				if(!TextUtils.isEmpty(errorvalue))
					Toast.makeText(ProfileActivity.this, errorvalue, Toast.LENGTH_SHORT).show();
			}
			
//			onStatusAdapter(statusSegmentedButton.getSelectedButtonIndex());
		}
	}

	public class GetProfileImageTask extends AsyncTask<Void, Void, Void> {
		Context context;
		ImageLoader imageLoader;
		DisplayImageOptions options;
		String imageUrlPath;
		
		public GetProfileImageTask(Context context,
				ImageLoader imageLoader, DisplayImageOptions options) {
			this.context = context;
			this.imageLoader = imageLoader;
			this.options = options;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			synchronized (this) {
				try {
					String url = AppConstants.URLS.MEDIA_BASE_URL+Zname.getPreferences().getApiKey()+"/profilepic";
					Log.i(TAG, url);
					String str = RestClient.getData(url);
					Log.i(TAG, str.toString());
					JSONObject object = new JSONObject(str.toString());
					try{
						imageUrlPath = object.getString("url");
						Zname.getPreferences().setProfilePicPath(imageUrlPath);
					}catch(Exception e){
					}
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
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
			if(!TextUtils.isEmpty(imageUrlPath)){
				if(!Zname.getPreferences().getProfilePicPath().equalsIgnoreCase(imageUrlPath)){
					imageLoader.displayImage(intentPhoto, mCircleImgProfile, options);
				}
			}
		}

	}
	
	private class UpdateStatusAsync extends AsyncTask<Void, Void, Void>{
		private Context mContext;
		private String status;
		private String statusType;
		private ProgressDialog mProgressDialog;
		private boolean successValue = false;
		private String errorValue;
	
		
		public UpdateStatusAsync(Context context, String status, String statusType){
			mContext=context;
			this.status =status;
			this.statusType=statusType;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage("Updating...");
			mProgressDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONObject dataToSend = RequestBuilder.getStatusData(status, statusType);
			try {
				String str = RestClient.postData(AppConstants.URLS.STATUS_URL+Zname.getPreferences().getApiKey()+"/status", dataToSend);
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
			
			if(successValue){
				Zname.getPreferences().setUserStatus(status);
				Zname.getPreferences().setUserStatusType(statusType);
				statusEdit.setText(Zname.getPreferences().getUserStatus());
				setStatusEditIcon(Zname.getPreferences().getUserStatusType());
			}
			if(mProgressDialog!=null)
				mProgressDialog.dismiss();
		}

	}
	
	
	public class StatusAdapter extends BaseAdapter{
		ArrayList<String> arrStatus;
		
		public StatusAdapter(Context context, ArrayList<String> arrStatus) {
			this.arrStatus = arrStatus;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrStatus.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
	        View v;
	        final TextView statusTxt;
	        if (convertView == null) {
	            LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = li.inflate(R.layout.item_grid_status, null);

	        } else {
	            v = convertView;
	        }
	        statusTxt = (TextView) v.findViewById(R.id.grid_item_status_txt);
	        statusTxt.setTypeface(styleFont);
	        statusTxt.setText(arrStatus.get(position).toString());
	        return v;
		}
		
	}
}
