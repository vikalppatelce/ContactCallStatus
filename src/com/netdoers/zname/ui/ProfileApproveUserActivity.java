package com.netdoers.zname.ui;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

public class ProfileApproveUserActivity extends SherlockFragmentActivity {

	// DECLARE VIEWS
	CircleImageView mCircleImgProfile;
	ActionBar mActionBar;
	TextView mContactName, mUserName, mBtnApprove, mBtnReject;

	// DECLARE STYLE TYPEFACE
	Typeface styleFont;

	// IMAGELOADER
	ImageLoader imageLoader;
	DisplayImageOptions options;

	// DECLARE VARIABLES
	String intentName;
	String intentPhoto;
	String intentZname;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_approve);

		mCircleImgProfile = (CircleImageView) findViewById(R.id.activity_profile_approve_img);
		mContactName = (TextView) findViewById(R.id.activity_profile_approve_name);
		mUserName = (TextView) findViewById(R.id.activity_profile_approve_zname);
		mBtnApprove = (TextView) findViewById(R.id.activity_profile_approve_call_txt);
		mBtnReject = (TextView) findViewById(R.id.activity_profile_approve_msg_txt);

		styleFont = Typeface.createFromAsset(getAssets(),
				AppConstants.fontStyle);

		setUniversalImageLoader();

		intentName = getIntent().getStringExtra(
				AppConstants.TAGS.INTENT.TAG_NAME);
		intentPhoto = getIntent().getStringExtra(
				AppConstants.TAGS.INTENT.TAG_PHOTO);
		
		intentZname = getIntent().getStringExtra(
				AppConstants.TAGS.INTENT.TAG_ZNAME);
		

		setActionBar("Profile");
		setFontStyle();

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
		inflater.inflate(R.menu.menu_profile_contact, menu);
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
		mBtnApprove.setTypeface(styleFont);
		mBtnReject.setTypeface(styleFont);
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
