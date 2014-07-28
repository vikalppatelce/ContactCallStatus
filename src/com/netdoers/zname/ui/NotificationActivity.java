/* HISTORY
 * CATEGORY			 :- BASE ACTIVITY
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- ACTIVITY FOR VIEW PAGER + FRAGMENTS
 * NOTE: ROOT OF THE CONTACTS SCREEN. [ALL, FRIENDS, FAMILY, WORK, RANDOM] 
 * ISSUE: https://github.com/JakeWharton/ActionBarSherlock/issues/828
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * ZM001      VIKALP PATEL     16/05/2014                       CREATED
 * ZM002      VIKALP PATEL     09/06/2014                       MIGRATION: GRIDVIEW INTO LISTVIEW
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.netdoers.zname.AppConstants;
import com.netdoers.zname.BuildConfig;
import com.netdoers.zname.R;
import com.netdoers.zname.Zname;
import com.netdoers.zname.beans.NotificationDTO;
import com.netdoers.zname.service.RequestBuilder;
import com.netdoers.zname.service.RestClient;
import com.netdoers.zname.sqlite.DBConstant;
import com.netdoers.zname.utils.JSONFileWriter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * @author Vikalp Patel(vikalppatelce@yahoo.com)
 * 
 */
@SuppressLint("NewApi")
public class NotificationActivity extends SherlockFragmentActivity {
	private ListView mListView;
	private ProgressBar mProgress;
	private ImageLoader imageLoader;
	private ActionBar mActionBar;
	private DisplayImageOptions options;
	
	//ADAPTER
	private NotificationAdapter mAdapter;

	// TYPEFACE
	static Typeface styleFont;

	// CONSTANTS
	public static final String TAG = NotificationActivity.class.getSimpleName();

	// COLLECTIONS
	ArrayList<NotificationDTO> arrListNotification;

	// //////////////////////////////////////////////
	// CALLED WHEN ACTIVITY CREATED
	// //////////////////////////////////////////////

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification);

        initUi();
        setFontStyle();
		setUniversalImageLoader();
		setActionBar("Notification");
		arrListNotification = new ArrayList<NotificationDTO>();
		
		if(isNetworkAvailable()){
			arrListNotification.clear();
			new FetchNotificationTask(imageLoader, options).execute();
		}
	}

	private void setActionBar(String str){
		mActionBar = getSupportActionBar();
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setTitle(str);
		fontActionBar(mActionBar.getTitle().toString());
	}
	
	private void initUi(){
		mListView = (ListView) findViewById(R.id.listview_notification);
		mProgress = (ProgressBar) findViewById(R.id.list_view_notification_progress);
	}
	
	private void setFontStyle(){
		styleFont = Typeface.createFromAsset(getAssets(),AppConstants.fontStyle);
	}
	
	private void setUniversalImageLoader(){
		imageLoader = ImageLoader.getInstance();
		// Initialize ImageLoader with configuration. Do it once.
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));

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
	
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null
				&& activeNetworkInfo.isConnectedOrConnecting();
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

	private void parseNotificationJSON(JSONObject jsonObject) {
		try {
			JSONArray userArray = jsonObject.getJSONArray("pending_requests");
			NotificationDTO notificationDTO;
			if (BuildConfig.DEBUG)
				Log.i(TAG,
						"Pending Requests :" + String.valueOf(userArray.length()));
			for (int i = 0; i < userArray.length(); i++) {
				JSONObject userObj = (JSONObject) userArray.get(i);
				notificationDTO = new NotificationDTO();
				notificationDTO.setZname(userObj.getString("zname"));
				notificationDTO.setImagePath(userObj.getString("profile_pic"));
				notificationDTO.setFullName(userObj.getString("full_name"));
				arrListNotification.add(notificationDTO);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// /////////////////////////////////////////////
	// LISTENER OBJECTS ON VIEW
	// /////////////////////////////////////////////

	public class FetchNotificationTask extends AsyncTask<Void, Void, Void> {
		boolean successvalue = false;
		ImageLoader imageLoader;
		DisplayImageOptions options;
		String errorvalue;

		public FetchNotificationTask(ImageLoader imageLoader,
				DisplayImageOptions options) {
			this.imageLoader = imageLoader;
			this.options = options;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			// JSONObject dataToSend =
			// RequestBuilder.getZnameAvaliabeData(verifyZname);
//			Log.i(TAG, AppConstants.URLS.AVAILABLE_URL);
			synchronized (this) {
				try {
					String url = AppConstants.URLS.PENDING_URL+ Zname.getPreferences().getApiKey() + "/pendingrequests";
					Log.i(TAG, url);
					String str = RestClient.getData(url);
					Log.i(TAG, str.toString());
					JSONObject object = new JSONObject(str.toString());
					parseNotificationJSON(object);
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
			mProgress.setVisibility(View.GONE);
			if(arrListNotification.size() > 0){
				mAdapter = new NotificationAdapter(arrListNotification, NotificationActivity.this);
				mListView.setAdapter(mAdapter);
			}
		}

	}

	
	public class ApproveTask extends AsyncTask<Void, Void, Void> {
		boolean successvalue = false;
		String errorvalue;
		String zname;
		String fullName;
		String profilepic;
		String znumber;
		int position;
		ProgressDialog progressDialog;

		public ApproveTask(String zname,
				String fullName, String profilepic ,int position) {
			this.zname = zname;
			this.fullName = fullName;
			this.profilepic = profilepic;
			this.position = position;
			
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			synchronized (this) {
				try {
					String url = AppConstants.URLS.APPROVE_URL+Zname.getPreferences().getApiKey()+"/approverequests";
					Log.i(TAG, url);
					JSONObject dataToSend  = RequestBuilder.getAddRequestData(zname);
					String str = RestClient.postData(url, dataToSend);
					Log.i(TAG, str.toString());
					JSONObject object = new JSONObject(str.toString());
					if(!object.getBoolean("status")){
							errorvalue = object.getString("errors");
					}else{
						successvalue = true;
						znumber = object.getString("contact_number");
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
			progressDialog = new ProgressDialog(NotificationActivity.this);
			progressDialog.setMessage("Approving");
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(progressDialog!=null)
				progressDialog.dismiss();
			
			arrListNotification.remove(position);
			mAdapter.notifyDataSetChanged();
			
			showEditNameDialog(zname,fullName,znumber,profilepic);
		}

	}
	
	public void showEditNameDialog(final String zname, final String fullName, final String znumber, final String profilepic){
		final Dialog dialog = new Dialog(NotificationActivity.this);
		try {
			dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		} catch (Exception e) {
			Log.e("Dialog", e.toString());
		}
		dialog.setContentView(R.layout.dialog_fullname_update_layout);
		final EditText updateTxt = (EditText)dialog.findViewById(R.id.dialog_fullname_update_txt);
		Button btnUpdateOk = (Button)dialog.findViewById(R.id.dialog_fullname_update_ok);
		Button btnUpdateCancel = (Button)dialog.findViewById(R.id.dialog_fullname_update_cancel);
		TextView dialogTitle = (TextView)dialog.findViewById(R.id.dialog_fullname_update_title);
		
		dialogTitle.setText("Full name");
		updateTxt.setText(fullName);
		
		dialogTitle.setTypeface(styleFont);
		updateTxt.setTypeface(styleFont);
		btnUpdateOk.setTypeface(styleFont);
		btnUpdateCancel.setTypeface(styleFont);

		final ContentValues values = new ContentValues();
		values.put(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_ID, System.currentTimeMillis());
		values.put(DBConstant.All_Contacts_Columns.COLUMN_ZNAME, zname);
		values.put(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_NUMBER, "\""+ znumber+"\"");
		values.put(DBConstant.All_Contacts_Columns.COLUMN_ZNAME_NUMBER, "\"" + znumber + "\"");
		values.put(DBConstant.All_Contacts_Columns.COLUMN_ZNAME_DP_URL_SMALL, profilepic);
		
		btnUpdateCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				values.put(DBConstant.All_Contacts_Columns.COLUMN_DISPLAY_NAME, fullName);
				Zname.getApplication().getApplicationContext().getContentResolver().insert(DBConstant.All_Contacts_Columns.CONTENT_URI, values);
				try{
					JSONFileWriter.jsonFriendList(
							String.valueOf(System.currentTimeMillis()), zname,
							znumber, fullName, profilepic);
				}catch(Exception e){
					Log.e(TAG, e.toString());
				}
				dialog.dismiss();
				Toast.makeText(Zname.getApplication().getApplicationContext(),  fullName + " has been added in your contacts", Toast.LENGTH_SHORT).show();
			}
		});
		
		btnUpdateOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			if(!TextUtils.isEmpty(updateTxt.getText().toString())){
				values.put(DBConstant.All_Contacts_Columns.COLUMN_DISPLAY_NAME, updateTxt.getText().toString());
				Zname.getApplication().getApplicationContext().getContentResolver().insert(DBConstant.All_Contacts_Columns.CONTENT_URI, values);
				try{
						JSONFileWriter.jsonFriendList(
								String.valueOf(System.currentTimeMillis()),
								zname, znumber, updateTxt.getText().toString(),
								profilepic);
				}catch(Exception e){
					Log.e(TAG, e.toString());
				}
				dialog.dismiss();
				Toast.makeText(Zname.getApplication().getApplicationContext(),  fullName + " has been added in your contacts", Toast.LENGTH_SHORT).show();
			}
			else{
				updateTxt.setError("Please enter valid name");
			}
			}
		});
		
		dialog.show();
	}
	public class NotificationAdapter extends BaseAdapter {

		ArrayList<NotificationDTO> arrayListNotificationAdapter = null;
		Context context;

		public NotificationAdapter(ArrayList<NotificationDTO> arrayListNotificationAdapter,
				Context context) {
			this.arrayListNotificationAdapter = arrayListNotificationAdapter;
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrayListNotificationAdapter.size();
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;

			LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.item_list_notification, null); // EA ZM004

			TextView txtZname = (TextView) view
					.findViewById(R.id.list_item_notification_zname);
			TextView txtFullName = (TextView) view
					.findViewById(R.id.list_item_notification_display_name);
			final ImageView imgZname = (ImageView) view
					.findViewById(R.id.list_item_notification_display_picture);
			ImageView imgApprove = (ImageView) view.findViewById(R.id.list_item_notification_approve);
			ImageView imgCancel = (ImageView) view.findViewById(R.id.list_item_notification_cancel);

			txtZname.setTypeface(styleFont);
			txtFullName.setTypeface(styleFont);
			txtZname.setText(arrayListNotificationAdapter.get(position).getZname());
			txtFullName.setText(arrayListNotificationAdapter.get(position).getFullName());

			((Activity) context).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					imageLoader
							.displayImage(arrayListNotificationAdapter.get(position)
									.getImagePath(), imgZname, options);
				}
			});

			imgApprove.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new ApproveTask(arrayListNotificationAdapter.get(position).getZname(), arrayListNotificationAdapter.get(position).getFullName(),arrayListNotificationAdapter.get(position).getImagePath(),position).execute();
/*					NetworkVolley nVolley = new NetworkVolley();
					final HashMap<String, String> params = new HashMap<String, String>();
					params.put("zname", arrayListNotificationAdapter.get(position).getZname());
					VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.APPROVE_URL+Zname.getPreferences().getApiKey()+"/approverequests", new JSONObject(params),
						       new Listener<JSONObject>() {
						           @Override
						           public void onResponse(JSONObject response) {
						               try {
						            	   VolleyLog.v("Request: %s", new JSONObject(params).toString());
						                   VolleyLog.v("Response:%n %s", response.toString(4));
						                   if(response.getBoolean("status")){
						                	   arrListNotification.remove(position);
						                	   mAdapter.notifyDataSetChanged();
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
				
			});
			
			imgZname.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent profileIntent = new Intent(NotificationActivity.this, ProfileApproveUserActivity.class);
					profileIntent.putExtra(AppConstants.TAGS.INTENT.TAG_ID, arrayListNotificationAdapter.get(position).getZname());
					profileIntent.putExtra(AppConstants.TAGS.INTENT.TAG_NAME, arrayListNotificationAdapter.get(position).getFullName());
					profileIntent.putExtra(AppConstants.TAGS.INTENT.TAG_PHOTO, arrayListNotificationAdapter.get(position).getImagePath());
					profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(profileIntent);	
				}
			});
			
			return view;
		}
	}

}
