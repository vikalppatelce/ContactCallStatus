package com.netdoers.zname.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.netdoers.zname.AppConstants;
import com.netdoers.zname.R;
import com.netdoers.zname.Zname;
import com.netdoers.zname.utils.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileUserActivity extends SherlockFragmentActivity {

	// DECLARE VIEWS
	CircleImageView mCircleImgProfile;
	ActionBar mActionBar;
	TextView mContactName, mUserName, mBtnCall, mBtnMsg;
	RelativeLayout mCall, mMsg;

	// DECLARE STYLE TYPEFACE
	Typeface styleFont;

	// IMAGELOADER
	ImageLoader imageLoader;
	DisplayImageOptions options;

	// DECLARE VARIABLES
	String intentID;
	String intentName;
	String intentPhoto;
	String intentNumber;
	String intentCallStatus;
	String intentZname;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_user);

		mCircleImgProfile = (CircleImageView) findViewById(R.id.activity_profile_user_img);
		mContactName = (TextView) findViewById(R.id.activity_profile_user_name);
		mUserName = (TextView) findViewById(R.id.activity_profile_user_zname);
		mBtnCall = (TextView) findViewById(R.id.activity_profile_user_call_txt);
		mBtnMsg = (TextView) findViewById(R.id.activity_profile_user_msg_txt);
		mCall = (RelativeLayout)findViewById(R.id.activity_profile_user_call);
		mMsg = (RelativeLayout)findViewById(R.id.activity_profile_user_msg);

		styleFont = Typeface.createFromAsset(getAssets(),
				AppConstants.fontStyle);

		setUniversalImageLoader();

		intentID = getIntent().getStringExtra(AppConstants.TAGS.INTENT.TAG_ID);
		intentName = getIntent().getStringExtra(
				AppConstants.TAGS.INTENT.TAG_NAME);
		intentPhoto = getIntent().getStringExtra(
				AppConstants.TAGS.INTENT.TAG_PHOTO);
		intentNumber = getIntent().getStringExtra(
				AppConstants.TAGS.INTENT.TAG_NUMBER);
		intentZname = getIntent().getStringExtra(
				AppConstants.TAGS.INTENT.TAG_ZNAME);
		intentCallStatus = getIntent().getStringExtra(
				AppConstants.TAGS.INTENT.TAG_CALL_STATUS);

		setActionBar("Profile");
		setFontStyle();
		setListeners();

		mContactName.setText(intentName);
		mUserName.setText(intentZname);
		
		runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                imageLoader.displayImage(intentPhoto, mCircleImgProfile, options);
            }
        });
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_profile_user, menu);
		// MenuItem overFlowMenu = menu.findItem(R.id.action_more);
		// MenuItem notificationMenu = menu.findItem(R.id.action_notification);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_share_user:
			shareUser();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void setActionBar(String str) {
		mActionBar = getSupportActionBar();
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setTitle(str);
		fontActionBar(mActionBar.getTitle().toString());
	}

	public void setFontStyle() {
		mContactName.setTypeface(styleFont);
		mUserName.setTypeface(styleFont);
		mBtnCall.setTypeface(styleFont);
		mBtnMsg.setTypeface(styleFont);
	}

	public void setUniversalImageLoader() {
		imageLoader = ImageLoader.getInstance();
		// Initialize ImageLoader with configuration. Do it once.
		// imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		imageLoader.init(Zname.getImageLoaderConfiguration());

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.def_contact) // resource or
															// drawable
				.showImageForEmptyUri(R.drawable.def_contact) // resource or
																// drawable
				.showImageOnFail(R.drawable.def_contact) // this is the image
															// that will be
															// displayed if
															// download fails
				.cacheInMemory().cacheOnDisc().build();
	}

	public void setListeners(){
		mMsg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void shareUser(){
		Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,
        		"Catch "+intentName +" on Z:name as "+ intentZname + "\n\n"
        		+ "https://play.google.com/store/apps/details?id=com.netdoers.zname"
        				);
        startActivity(intent);
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

}
