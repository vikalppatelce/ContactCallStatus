package com.netdoers.zname.ui;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
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

public class ProfileAddZnameActivity extends SherlockFragmentActivity{
	
	//DECLARE VIEWS
	CircleImageView mCircleImgProfile;
	TextView mName, mUserName, mBtnAddTxt;
	RelativeLayout mBtnAdd;
	ActionBar mActionBar;
	ImageLoader imageLoader;
	DisplayImageOptions options;

	
	//DECLARE STYLE TYPEFACE
	Typeface styleFont;
	
	//DECLARE VARIABLES
	String intentZname;
	String intentName;
	String intentPhoto;
	
	//CONSTANTS
	public static final String TAG = ProfileAddZnameActivity.class.getSimpleName();
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_add_zname);
		
		initUi();
		
		intentName = getIntent().getStringExtra(AppConstants.TAGS.INTENT.TAG_NAME);
		intentZname = getIntent().getStringExtra(AppConstants.TAGS.INTENT.TAG_ID);
		intentPhoto = getIntent().getStringExtra(AppConstants.TAGS.INTENT.TAG_PHOTO);
	
		setUniversalImageLoader();
		setFontStyle();
		setActionBar("Profile");

		runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                imageLoader.displayImage(intentPhoto, mCircleImgProfile, options);
            }
        });
		
		setEventListeners();
	}
	@Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
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
	
	public void setFontStyle(){
		styleFont = Typeface.createFromAsset(getAssets(), AppConstants.fontStyle);
		mBtnAddTxt.setTypeface(styleFont);
		mName.setTypeface(styleFont);
		mUserName.setTypeface(styleFont);
	}
	
	public void initUi(){
		mCircleImgProfile = (CircleImageView)findViewById(R.id.activity_profile_add_img);
		mName = (TextView)findViewById(R.id.activity_profile_add_name);
		mUserName = (TextView)findViewById(R.id.activity_profile_add_zname);
		mBtnAddTxt = (TextView)findViewById(R.id.activity_profile_add_friend_txt);
		mBtnAdd = (RelativeLayout)findViewById(R.id.activity_profile_add_btn);
	}
	
	public void setEventListeners(){
		mBtnAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onAddFriend(v);
			}
		});
	}
	
	public void setUniversalImageLoader(){
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
	
	@SuppressLint("NewApi")
	public void onAddFriend(View v){
		if(Build.VERSION.SDK_INT > 11){
			new SendRequestTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
		}else{
			new SendRequestTask().execute();
		}
		/*NetworkVolley nVolley = new NetworkVolley();
		String url = AppConstants.URLS.REQUEST_URL+Zname.getPreferences().getApiKey()+"/addrequest";
		AddFriendRequest req = nVolley.new AddFriendRequest(url, RequestBuilder.getAddRequestData(intentZname),
			       new Listener<JSONObject>() {
			           @Override
			           public void onResponse(JSONObject response) {
			               try {
			                   VolleyLog.v("Response:%n %s", response.toString(4));
			                   if(response.getBoolean("status")){
			                	   txtAddZname.setText("Pending"); 	   
								}
			               } catch (JSONException e) {
			                   e.printStackTrace();
			               }
			           }
			       }, new ErrorListener() {
			           @Override
			           public void onErrorResponse(VolleyError error) {
			               VolleyLog.e("Error: ", error.getMessage());
			           }
			       });
		Zname.getInstance().addToRequestQueue(req);*/
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	
	public class SendRequestTask extends AsyncTask<Void, Void, Void>{
		boolean successvalue = false;
		String errorvalue;
		ProgressDialog progressDialog;
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			synchronized (this) {
				try {
					String url = AppConstants.URLS.REQUEST_URL+Zname.getPreferences().getApiKey()+"/addrequest";
					JSONObject dataToSend = RequestBuilder.getAddRequestData(intentZname);
					String str = RestClient.postData(url, dataToSend);
					if(BuildConfig.DEBUG){
						Log.i(TAG, url);
						Log.i(TAG, dataToSend.toString());
						Log.i(TAG, str);
					}
					JSONObject object = new JSONObject(str);
					if(object.getBoolean("status")){
						successvalue = true;
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
			progressDialog = new ProgressDialog(ProfileAddZnameActivity.this);
			progressDialog.setMessage("Please wait a moment");
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(successvalue){
				mBtnAddTxt.setText("Pending");
				Toast.makeText(Zname.getApplication().getApplicationContext(), intentName + " request sent for approval ", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(Zname.getApplication().getApplicationContext(), intentName + " request already sent ", Toast.LENGTH_SHORT).show();
			}
			
			if(progressDialog!=null)
				progressDialog.dismiss();
		}
		
	}

}
