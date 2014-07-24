package com.netdoers.zname.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.netdoers.zname.AppConstants;
import com.netdoers.zname.BuildConfig;
import com.netdoers.zname.R;
import com.netdoers.zname.Zname;
import com.netdoers.zname.service.RequestBuilder;
import com.netdoers.zname.service.RestClient;
import com.netdoers.zname.utils.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileUserActivity extends SherlockFragmentActivity implements OnRefreshListener{

	// DECLARE VIEWS
	private CircleImageView mCircleImgProfile;
	private ActionBar mActionBar;
	private TextView mContactName, mUserName, mBtnCall, mBtnMsg, mStatus,mStatusHead;
	private ImageView mImgCall;
	private RelativeLayout mCall, mMsg;
	private PullToRefreshLayout mPullToRefreshLayout;

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

		initUi();
		initPullToRefresh();
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
		
		setFontStyle();
		setActionBar("Profile");
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
		
		new FetchProfileDataAysnc(intentZname).execute();
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

	private void initUi(){
		mCircleImgProfile = (CircleImageView) findViewById(R.id.activity_profile_user_img);
		mContactName = (TextView) findViewById(R.id.activity_profile_user_name);
		mUserName = (TextView) findViewById(R.id.activity_profile_user_zname);
		mBtnCall = (TextView) findViewById(R.id.activity_profile_user_call_txt);
		mBtnMsg = (TextView) findViewById(R.id.activity_profile_user_msg_txt);
		mStatus = (TextView)findViewById(R.id.activity_profile_user_status);
		mStatusHead = (TextView)findViewById(R.id.activity_profile_user_status_head);
		mCall = (RelativeLayout)findViewById(R.id.activity_profile_user_call);
		mMsg = (RelativeLayout)findViewById(R.id.activity_profile_user_msg);
		mImgCall = (ImageView)findViewById(R.id.fragment_profile_user_call_img);
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.activity_profile_user_ptr_layout);
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
		mContactName.setTypeface(styleFont);
		mUserName.setTypeface(styleFont);
		mBtnCall.setTypeface(styleFont);
		mBtnMsg.setTypeface(styleFont);
		mStatus.setTypeface(styleFont);
		mStatusHead.setTypeface(styleFont);
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
				Intent callIntent = new Intent(Intent.ACTION_DIAL);
				callIntent.setData(Uri.parse("tel:"+Uri.encode(intentNumber.replace("\"", ""))));
				startActivity(callIntent);	
			}
		});
		
		mCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent callIntent = new Intent(Intent.ACTION_DIAL);
		        callIntent.setData(Uri.parse("tel:"+Uri.encode(intentNumber.replace("\"", ""))));
		        startActivity(callIntent);
			}
		});
		
		mCircleImgProfile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onPhotoView();
			}
		});
	}
	
	private void initPullToRefresh(){
		ActionBarPullToRefresh.from(this)
        .allChildrenArePullable()
        .listener(this)
        .setup(mPullToRefreshLayout);
	}
	
	private void setPullToRefreshLoader(){
		mPullToRefreshLayout.setRefreshing(true);
	}
	
	private void onPhotoView(){
		Intent photoViewIntent = new Intent(ProfileUserActivity.this, PhotoViewActivity.class);
    	photoViewIntent.putExtra(PhotoViewActivity.mIntentPhoto, intentPhoto);
    	startActivity(photoViewIntent);
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
	
	public void setStatusEditIcon(String statusType){
		try{
			if(!TextUtils.isEmpty(statusType)){
				switch (Integer.parseInt(statusType)) {
				case 0:
					mStatus.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_hint_random), null, null, null);
					break;
				case 1:
					mStatus.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_hint_read), null, null, null);
					break;
				case 2:
					mStatus.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_hint_places), null, null, null);
					break;
				case 3:
					mStatus.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_hint_work), null, null, null);
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
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

	private String buildZnameStringRequest(String intentZname){
		return "{\"zname\":["+"\""+intentZname+"\""+"]}";
	}
	
	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		new FetchProfileDataAysnc(intentZname).execute();
	}
	
	private class FetchProfileDataAysnc extends AsyncTask<Void, Void, Void>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setPullToRefreshLoader();
		}

		private String zname;
		private String callStatus;
		private String status;
		private String statusType;
		private static final String ASYNC_TAG = "FetchProfileDataAysnc";
		public FetchProfileDataAysnc(String zname){
			this.zname = zname;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				String url = AppConstants.URLS.GET_STATUS_URL+Zname.getPreferences().getApiKey()+"/users/status";
				JSONObject dataToSend = RequestBuilder.getSyncCallData(zname);
				String str = RestClient.postData(url, dataToSend);
				if (BuildConfig.DEBUG) {
					Log.i(ASYNC_TAG, url);
					Log.i("Request-->", dataToSend.toString());
					Log.i("Response-->", str);
				}
				JSONObject userObj = new JSONObject(str);

				try {
					callStatus = userObj.getString("call_status");
					status = userObj.getString("status_text");
					statusType = userObj.getString("status_category");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
				Log.e(ASYNC_TAG, e.toString());
			}
			return null;
		}
		
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
			if (Integer.parseInt(callStatus) == 0) {
				mBtnCall.setText("Available");
				mImgCall.setImageResource(R.drawable.zname_ic_call_selected_avail);
			} else {
				mBtnCall.setText("Busy");
				mImgCall.setImageResource(R.drawable.zname_ic_call_selected_busy);
			}
			if(!TextUtils.isEmpty(status)){
				mStatus.setVisibility(View.VISIBLE);
				mStatus.setText(status);
				setStatusEditIcon(statusType);
			}
			
            mPullToRefreshLayout.setRefreshComplete();
        }
	}
}
