/* HISTORY
 * CATEGORY			 :- BASE ACTIVITY
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- ACTIVITY FOR VIEW PAGER + FRAGMENTS
 * NOTE: ROOT OF THE CONTACTS SCREEN. [ALL, FRIENDS, FAMILY, WORK, CALL LOG] 
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * ZM001      VIKALP PATEL     16/05/2014                       CREATED
 * ZM002      VIKALP PATEL     03/06/2014                       SUPPRESSED DRAWER OVER OVERFLOW MENU
 * ZM003      VIKALP PATEL     01/07/2014                       ADDED SYNCPHONEBOOK SERVICE
 * --------------------------------------------------------------------------------------------------------------------
 */

package com.netdoers.zname.ui;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.netdoers.zname.AppConstants;
import com.netdoers.zname.BuildConfig;
import com.netdoers.zname.R;
import com.netdoers.zname.Zname;
import com.netdoers.zname.service.NetworkVolley;
import com.netdoers.zname.service.NetworkVolley.VolleyGetJsonRequest;
import com.netdoers.zname.service.RequestBuilder;
import com.netdoers.zname.service.RestClient;
import com.netdoers.zname.service.SyncPhoneBookService;
import com.netdoers.zname.utils.CircleImageView;
import com.netdoers.zname.utils.PagerSlidingTabStrip;
import com.netdoers.zname.utils.SlidingTabLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author Vikalp Patel(vikalppatelce@yahoo.com)
 * 
 */
public class MotherActivity extends SherlockFragmentActivity {

	// Declare Variables
	private ActionBar mActionBar;
	private ViewPager mPager;
	private PagerSlidingTabStrip pagerSlidingTabStrp;
	private Tab tab;
	private SlidingTabLayout mSlidingTabLayout; // SLIDINGTABLAYOUT
	ImageLoader imageLoader;
	DisplayImageOptions options;

	// TYPEFACE
	Typeface styleFont;

	// CONSTANTS
	public static final String TAG = MotherActivity.class.getSimpleName();
	MySimpleArrayAdapter mDrawerAdapter;

	// SA GCM
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	// String SENDER_ID = "777045980104";
	String SENDER_ID = AppConstants.GCM_SENDER_ID;

	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	SharedPreferences prefs;
	Context context;

	String regid;
	// EA GCM

	// SU ZM002 //DRAWER LAYOUT
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mDrawerTitles;
	private String[] mDrawerDetailTitles;

	// EU ZM002

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the view from activity_main.xml
		setContentView(R.layout.activity_mother);

		styleFont = Typeface.createFromAsset(getAssets(),
				AppConstants.fontStyle);

		setUniversalImageLoader();

		// Activate Navigation Mode Tabs
		mActionBar = getSupportActionBar();

		// Locate ViewPager in activity_main.xml
		mPager = (ViewPager) findViewById(R.id.pager);
		ViewPager.SimpleOnPageChangeListener ViewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				// Find the ViewPager Position
				switch (position) {
				case 0:
					setMotherActionBarTitle(getString(R.string.str_all_contacts_fragment));
					break;
				case 1:
					setMotherActionBarTitle(getString(R.string.str_group_contacts_fragment));
					break;
				case 2:
					setMotherActionBarTitle(getString(R.string.str_call_logs_fragment));
					break;
				}
			}
		};

		// Locate the adapter class called ViewPagerAdapter.java
		ViewPagerAdapter viewpageradapter = new ViewPagerAdapter(
				getSupportFragmentManager());
		// Set the View Pager Adapter into ViewPager
		mPager.setAdapter(viewpageradapter);

		/**
		 * SLIDINGTABSLAYOUT
		 */
		/*
		 * mSlidingTabLayout = (SlidingTabLayout)
		 * findViewById(R.id.sliding_tabs);
		 * mSlidingTabLayout.setViewPager(mPager);
		 */

		/**
		 * PAGER SLIDING TAB STRIP
		 */
		pagerSlidingTabStrp = (PagerSlidingTabStrip) findViewById(R.id.pager_sliding_tab_strip);
		pagerSlidingTabStrp.setShouldExpand(true);
		pagerSlidingTabStrp.setViewPager(mPager);
		pagerSlidingTabStrp.setOnPageChangeListener(ViewPagerListener);

		// SA ZM003
		Zname.getPreferences().setLastSyncPhoneBook(
				String.valueOf(System.currentTimeMillis()));
		Intent syncPhoneBookIntent = new Intent(Zname.getApplication()
				.getApplicationContext(), SyncPhoneBookService.class);
		startService(syncPhoneBookIntent);
		// EA ZM003

		// SA GCM
		context = Zname.getApplication().getApplicationContext();

		if (isNetworkAvailable()) {
			if (BuildConfig.DEBUG) {
				Log.i("REG_ID",
						Zname.getSharedPreferences().getString(
								"registration_id", "Not yet Registered"));
				Log.i("VERSION", String.valueOf(getAppVersion(context)));
				Log.i("SENDER_ID", SENDER_ID);
			}

			if (checkPlayServices()) {
				gcm = GoogleCloudMessaging.getInstance(this);
				regid = getRegistrationId(context);
				Log.i("REG_ID", regid);
				if (TextUtils.isEmpty(regid)) {
					registerInBackground();
					// CA.getPreferences().setFirstTime(false);
					/*
					 * Toast.makeText(
					 * Zname.getApplication().getApplicationContext(),
					 * "Not yet registered to GCM", Toast.LENGTH_SHORT) .show();
					 */
				} /*
				 * else { Toast.makeText(
				 * Zname.getApplication().getApplicationContext(),
				 * "Registered to GCM", Toast.LENGTH_SHORT).show(); if
				 * (!Zname.getSharedPreferences().getBoolean(
				 * "isRegisteredToServer", false)) {
				 * sendRegistrationIdToBackend(); Toast.makeText(
				 * Zname.getApplication().getApplicationContext(),
				 * "Not yet registered to Server", Toast.LENGTH_SHORT).show(); }
				 * }
				 */
			} else {
				Log.i(TAG, "No valid Google Play Services APK found.");
			}
		}
		// EA GCM

		// SU ZM002 //DRAWER LAYOUT

		mTitle = mDrawerTitle = getTitle();
		mDrawerTitles = getResources()
				.getStringArray(R.array.drawer_menu_array);
		mDrawerDetailTitles = getResources().getStringArray(
				R.array.drawer_menu_detail_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerAdapter = new MySimpleArrayAdapter(this, mDrawerTitles,
				mDrawerDetailTitles, imageLoader, options);
		mDrawerList.setAdapter(mDrawerAdapter);
		// // mDrawerList.setAdapter(new ArrayAdapter<String>(this,
		// // R.layout.drawer_list_item, mPlanetTitles));
		// mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
				
		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, // host Activity
				mDrawerLayout, // DrawerLayout object
				R.drawable.ic_drawer, // nav drawer image to replace 'Up' caret
				R.string.drawer_open, // "open drawer" description for
										// accessibility
				R.string.drawer_close // "close drawer" description for
										// accessibility
		) {
			@Override
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
				// invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				supportInvalidateOptionsMenu();
				// invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		// EU ZM002
		getProfileImageVolley();
	}

	private void setUniversalImageLoader() {
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

	// SA GCM
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null
				&& activeNetworkInfo.isConnectedOrConnecting();
	}

	// D: CHECK PLAY SERVICES ON RESUME [PLAYSERVICES GCM]
	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
	}

	// D: CHECK PLAY SERVICES EXIST ON THE DEVICE OR NOT [PLAYSERVICES GCM]
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			/*
			 * if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
			 * GooglePlayServicesUtil.getErrorDialog(resultCode, this,
			 * PLAY_SERVICES_RESOLUTION_REQUEST).show(); } else { Log.i(TAG,
			 * "This device is not supported."); finish(); }
			 */
			return false;
		}
		return true;
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (TextUtils.isEmpty(registrationId)) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your app is up to you.
		// return
		// getSharedPreferences(NewHomeActivity.class.getSimpleName(),Context.MODE_PRIVATE);
		return Zname.getSharedPreferences();
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	public void registerInBackground() {
		new GCMRegisterTask().execute();
	}

	private class GCMRegisterTask extends AsyncTask<Void, Void, String> {
		String msg = "";

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(context);
				}
				regid = gcm.register(SENDER_ID);
				msg = "Device registered, registration ID=" + regid;

				// For this demo: we don't need to send it because the device
				// will send upstream messages to a server that echo back the
				// message using the 'from' address in the message.

				// Persist the regID - no need to register again.
				storeRegistrationId(context, regid);

				// You should send the registration ID to your server over HTTP,
				// so it can use GCM/HTTP or CCS to send messages to your app.
				// The request to your server should be authenticated if your
				// app
				// is using accounts.
				if (!Zname.getSharedPreferences().getBoolean(
						"isRegisteredToServer", false)) {
					sendRegistrationIdToBackend();
				} else {
					Log.i("AndroidToServer", "Already registered to server");
				}
			} catch (IOException ex) {
				msg = "Error :" + ex.getMessage();
				// If there is an error, don't just keep trying to register.
				// Require the user to click a button again, or perform
				// exponential back-off.
			}
			return msg;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.i("GCM", msg);
		}
	}

	private class SendToServerTask extends AsyncTask<JSONObject, Void, Void> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(JSONObject... params) {
			// TODO Auto-generated method stub
			JSONObject dataToSend = params[0];
			boolean status = false;
			try {
				String jsonStr = RestClient.postData(AppConstants.URLS.GCM_URL
						+ Zname.getPreferences().getApiKey()
						+ "/appregistraion", dataToSend);

				if (jsonStr != null) {
					JSONObject jsonObject = new JSONObject(new String(jsonStr));
					status = jsonObject.getBoolean("status");
					if (status) {
						try {
							// Getting JSON Array node
							// SERVERDEMO
							final SharedPreferences prefs = getGCMPreferences(context);
							Log.i(TAG, "Saving regId on server ");
							SharedPreferences.Editor editor = prefs.edit();
							editor.putBoolean("isRegisteredToServer", true);
							editor.commit();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				Log.e("PushServer", jsonStr.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use
	 * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
	 * since the device sends upstream messages to a server that echoes back the
	 * message using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend() {
		// Your implementation here.

		TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String currentSIMImsi = mTelephonyMgr.getDeviceId();

		JSONObject jsonObject = RequestBuilder
				.getPushNotificationData(currentSIMImsi);
		Log.e("PUSH REGID SERVER---->>>>>>>>>>", jsonObject.toString());
		SendToServerTask sendTask = new SendToServerTask();
		sendTask.execute(new JSONObject[] { jsonObject });
	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	// EA GCM

	public void getProfileImageVolley() {
		NetworkVolley nVolley = new NetworkVolley();
		final String urlProfileImage = AppConstants.URLS.MEDIA_BASE_URL
				+ Zname.getPreferences().getApiKey() + "/profilepic";
		VolleyGetJsonRequest req = nVolley.new VolleyGetJsonRequest(
				urlProfileImage, null, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							VolleyLog.v("Request: %s", urlProfileImage);
							VolleyLog.v("Response:%n %s", response.toString(4));
							Zname.getPreferences().setProfilePicPath(
									response.getString("url"));
							mDrawerAdapter.notifyDataSetChanged();
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
		Zname.getInstance().addToRequestQueue(req);
	}

	public void setMotherActionBarTitle(String s) {
		// mActionBar.setTitle(s);
		fontActionBar(s);
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

	public String getActionBarTitle() {
		try {
			int titleId;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				titleId = getResources().getIdentifier("action_bar_title",
						"id", "android");
			} else {
				titleId = R.id.abs__action_bar_title;
			}
			TextView yourTextView = (TextView) findViewById(titleId);
			return yourTextView.getText().toString();
		} catch (Exception e) {
			Log.e("ActionBar Style", e.toString());
			return null;
		}
	}

	// ///////////////////////////////////////////
	// ARRAY ADAPTER FOR DRAWER
	// ///////////////////////////////////////////

	public class MySimpleArrayAdapter extends ArrayAdapter<String> {
		private final Context context;
		private final String[] values;
		private final String[] values2;
		private ImageLoader imageLoader;
		private DisplayImageOptions options;

		public MySimpleArrayAdapter(Context context, String[] values,
				String[] values2, ImageLoader imageLoader,
				DisplayImageOptions options) {
			super(context, R.layout.item_list_drawer, values);
			this.context = context;
			this.values = values;
			this.values2 = values2;
			this.imageLoader = imageLoader;
			this.options = options;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.item_list_drawer, parent,
					false);

			FrameLayout mDrawerProfileLayout = (FrameLayout) rowView
					.findViewById(R.id.drawer_layout_profile);
			LinearLayout mDrawerMenuLayout = (LinearLayout) rowView
					.findViewById(R.id.drawer_layout_menu);

			final CircleImageView mDrawerProfileImageView = (CircleImageView) rowView
					.findViewById(R.id.drawer_profile_image);

			TextView mDrawerProfileFullNameView = (TextView) rowView
					.findViewById(R.id.drawer_profile_full_name);
			TextView mDrawerProfileZnameView = (TextView) rowView
					.findViewById(R.id.drawer_profile_zname);
			TextView mDrawerTitleView = (TextView) rowView
					.findViewById(R.id.drawer_layout_menu_title);
			TextView mDrawerTitleSubView = (TextView) rowView
					.findViewById(R.id.drawer_layout_menu_subtitle);
			ImageView mDrawerIconView = (ImageView) rowView
					.findViewById(R.id.drawer_layout_menu_icon);

			mDrawerTitleView.setTypeface(styleFont);
			mDrawerTitleSubView.setTypeface(styleFont);
			mDrawerProfileFullNameView.setTypeface(styleFont);
			mDrawerProfileZnameView.setTypeface(styleFont);
			// Change the icon for Windows and iPhone
			// String s = values[position];
			switch (position) {
			case 0:
				mDrawerMenuLayout.setVisibility(View.GONE);
				mDrawerProfileFullNameView.setText(Zname.getPreferences()
						.getFullName());
				mDrawerProfileZnameView.setText(Zname.getPreferences()
						.getUserName());

				if (!TextUtils.isEmpty(Zname.getPreferences()
						.getProfilePicPath())) {
					((Activity) context).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							imageLoader.displayImage(Zname.getPreferences()
									.getProfilePicPath(),
									mDrawerProfileImageView, options);
						}
					});
				}

				break;
			case 1:
				mDrawerProfileLayout.setVisibility(View.GONE);
				mDrawerMenuLayout.setVisibility(View.VISIBLE);
				mDrawerIconView
						.setImageResource(R.drawable.btn_nav_discovery_normal);
				mDrawerTitleView.setText(values[position]);
				mDrawerTitleSubView.setText(values2[position]);
				break;
			case 2:
				mDrawerProfileLayout.setVisibility(View.GONE);
				mDrawerMenuLayout.setVisibility(View.VISIBLE);
				mDrawerIconView
						.setImageResource(R.drawable.btn_nav_settings_normal);
				mDrawerTitleView.setText(values[position]);
				mDrawerTitleSubView.setText(values2[position]);
				break;
			case 3:
				mDrawerProfileLayout.setVisibility(View.GONE);
				mDrawerMenuLayout.setVisibility(View.VISIBLE);
				mDrawerIconView
						.setImageResource(R.drawable.btn_nav_invite_normal);
				mDrawerTitleView.setText(values[position]);
				mDrawerTitleSubView.setText(values2[position]);
				break;
			case 4:
				mDrawerProfileLayout.setVisibility(View.GONE);
				mDrawerMenuLayout.setVisibility(View.VISIBLE);
				mDrawerIconView
						.setImageResource(R.drawable.btn_nav_messages_normal);
				mDrawerTitleView.setText(values[position]);
				mDrawerTitleSubView.setText(values2[position]);
				break;
			default:
				break;
			}
			return rowView;
		}
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;

		// PlanetFragment fragment = new PlanetFragment();
		Bundle args = new Bundle();
		// args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
		// fragment.setArguments(args);

		Intent drawerIntent = null;

		switch (position) {
		case 0:
			drawerIntent = new Intent(this, ProfileActivity.class);
			break;
		case 1:
			drawerIntent = new Intent(this, NotificationActivity.class);
			break;
		case 2:
			drawerIntent = new Intent(this, SettingsActivity.class);
			break;
		case 3:
			drawerIntent = new Intent(Intent.ACTION_SEND);
			drawerIntent.setType("text/plain");
			drawerIntent
					.putExtra(
							Intent.EXTRA_TEXT,
							"Catch person call status using Z:name \n\n"
									+ "https://play.google.com/store/apps/details?id=com.netdoers.zname");
			break;
		default:
			break;
		}

		// // update selected item and title, then close the drawer
		// mDrawerList.setItemChecked(position, true);
		// setTitle(mPlanetTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList); // COMMENTED ZM002

		if (drawerIntent != null)
			startActivity(drawerIntent);
	}

	// //////////////////////////////////////////////
	// ACTIVITY CREATED : OPTIONS INSIDE ABS
	// /////////////////////////////////////////////

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_activity_mother, menu);
		// MenuItem overFlowMenu = menu.findItem(R.id.action_more);
		// MenuItem notificationMenu = menu.findItem(R.id.action_notification);
		return true;
	}

	// //////////////////////////////////////////////
	// OPTIONS IN ABS : SELECTED
	// /////////////////////////////////////////////
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// SU ZM002
			try {
				if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
					mDrawerLayout.closeDrawer(mDrawerList);
				} else {
					mDrawerLayout.openDrawer(mDrawerList);
				}
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
			// EU ZM002
			return true;
		case R.id.action_add:
			Intent addZnameIntent = new Intent(this, AddZnameActivity.class);
			startActivity(addZnameIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			try {
				// SU ZM002
				// if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				// mDrawerLayout.closeDrawer(mDrawerList);
				// } else {
				// mDrawerLayout.openDrawer(mDrawerList);
				// }
				// EU ZM002
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
			return true;
		} else {
			return super.onKeyUp(keyCode, event);
		}
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

}
