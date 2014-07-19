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
 * 
 *
 * *****************************************METHODS INFORMATION******************************************************** 
 * ********************************************************************************************************************
 * DEVELOPER		  METHOD								DESCRIPTION
 * ********************************************************************************************************************
 * VIKALP PATEL       onCreate                   			CALLED AT TIME OF ACTIVITY CREATION(COME FRONT)
 * VIKALP PATEL       initUi                     			INITIALISING UI COMPONENTS FROM XML OR JAVA CODE
 * VIKALP PATEL       setFontStyle                  		SETTING FONT STYLE ON UI COMPONENETS
 * VIKALP PATEL       setUniversalImageLoader               SETTING UNIVERSAL IMAGE LOADER CONFIGURATION
 * VIKALP PATEL       setEventListeners			            SETTING EVENT LISTENER ON UI COMPONENTS
 * VIKALP PATEL       isNetworkAvailable		            WHETHER NETWORK AVAILABLE OR NOT
 * VIKALP PATEL       onSearchClear				            CALLED ON SEARCH CLEAR
 * VIKALP PATEL       onSearchBack				            CALLED ON SEARCH BACK
 * ********************************************************************************************************************
 */
package com.netdoers.zname.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.netdoers.zname.AppConstants;
import com.netdoers.zname.R;
import com.netdoers.zname.Zname;
import com.netdoers.zname.beans.ZnameSearch;
import com.netdoers.zname.service.RestClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * @author Vikalp Patel(vikalppatelce@yahoo.com)
 *
 */
@SuppressLint("NewApi")
public class AddZnameActivity extends FragmentActivity {
	private ListView mListView;
	private ImageView mSearchClose, mSearchBack;
	private EditText mSearchField;
	private ProgressBar mProgress;
	ImageLoader imageLoader;
	DisplayImageOptions options;
	
	//TYPEFACE
	static Typeface styleFont;
	
	//CONSTANTS
	public static final String TAG = AddZnameActivity.class.getSimpleName();
	
	//COLLECTIONS
	private ArrayList<ZnameSearch> arrZnameSearch;
	ArrayAdapter<CharSequence> emptyAdapter;
	String emptyItems[] = {"No Results Found"};
	////////////////////////////////////////////////
	// CALLED WHEN ACTIVITY CREATED
	////////////////////////////////////////////////
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_zname);
		
		initUi();
		arrZnameSearch = new ArrayList<ZnameSearch>();
		
		setUniversalImageLoader();
		setFontStyle();
		mSearchField.requestFocus();
		
		setEventListeners();
				
		emptyAdapter = new CustomArrayAdapter<CharSequence>(this, emptyItems);
	}
	
		private boolean isNetworkAvailable() {
		    ConnectivityManager connectivityManager 
		          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
		}
		
	private void setUniversalImageLoader(){
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

	private void setFontStyle(){
		styleFont = Typeface.createFromAsset(getAssets(), AppConstants.fontStyle);
		mSearchField.setTypeface(styleFont);
	}
	
	private void initUi(){
		mListView = (ListView)findViewById(R.id.listview_search_zname);
		mSearchClose = (ImageView)findViewById(R.id.search_clear);
		mSearchField = (EditText)findViewById(R.id.search_txt);
		mSearchBack = (ImageView)findViewById(R.id.search_back);
		mProgress = (ProgressBar)findViewById(R.id.list_view_search_progress);
	}
	
	private void setEventListeners(){
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				try{
					Intent znameProfileIntent = new Intent(AddZnameActivity.this, ProfileAddZnameActivity.class);
					String zName = view.getTag(R.id.TAG_CONTACT_ZNAME).toString();
					String name = view.getTag(R.id.TAG_CONTACT_NAME).toString();
					String displayPicture = view.getTag(R.id.TAG_CONTACT_DP).toString();
					
					znameProfileIntent.putExtra(AppConstants.TAGS.INTENT.TAG_NAME, name);
					znameProfileIntent.putExtra(AppConstants.TAGS.INTENT.TAG_ID, zName);
					znameProfileIntent.putExtra(AppConstants.TAGS.INTENT.TAG_PHOTO, displayPicture);
					if(!TextUtils.isEmpty(zName))
						startActivity(znameProfileIntent);
				}catch(NullPointerException e){
					Log.e(TAG, e.toString());
				}
			}
		});
		
		// Add text listner to the edit text for filtering the List
		mSearchField.addTextChangedListener(new TextWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		// call the filter with the current text on the editbox
			if(mSearchField.getText().toString().length() > 2){
				if(isNetworkAvailable()){
					try{
						mListView.setAdapter(null);	
					}catch(Exception e){
						Log.e(TAG, e.toString());
					}
					
					if(Build.VERSION.SDK_INT > 11){
						new SearchZnameTask(mSearchField.getText().toString(),imageLoader,options).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
					}else{
						new SearchZnameTask(mSearchField.getText().toString(),imageLoader,options).execute();
					}
				}
			}
		}
		});
	}
	///////////////////////////////////////////////
	// LISTENER OBJECTS ON VIEW
	///////////////////////////////////////////////
	
	public void onSearchClear(View v){
		mSearchField.setText("");
	}
	
	public void onSearchBack(View v){
		finish();
	}
	
	private void parseSearchJSON(JSONObject jsonObject){
		try {
			JSONArray userArray = jsonObject.getJSONArray("users");
			for(int i=0; i< userArray.length();i++){
				JSONObject userObj = (JSONObject) userArray.get(i);
                ZnameSearch item = new ZnameSearch();
                item.setFullName(userObj.getString("full_name"));
                item.setzName(userObj.getString("zname"));
                item.setImagePath(userObj.getString("profile_pic"));
                arrZnameSearch.add(item);	
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public class SearchZnameTask extends AsyncTask<Void, Void, Void>{
		String verifyZname;
		boolean successvalue = false;
		ImageLoader imageLoader;
		DisplayImageOptions options;
		String errorvalue;
		
		public SearchZnameTask(String verifyZname,ImageLoader imageLoader,DisplayImageOptions options){
			this.verifyZname=verifyZname;
			this.imageLoader = imageLoader;
			this.options = options;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
//		JSONObject dataToSend = RequestBuilder.getZnameAvaliabeData(verifyZname);
		Log.i(TAG, AppConstants.URLS.AVAILABLE_URL+verifyZname);
			synchronized (this) {
				try {
					String url = AppConstants.URLS.SEARCH_URL+Zname.getPreferences().getApiKey()+"/users/"+verifyZname;
					Log.i(TAG, url);
					String str = RestClient.getData(url);
					Log.i(TAG, str);
					JSONObject object = new JSONObject(str);
					arrZnameSearch.clear();
					parseSearchJSON(object);
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
			mProgress.setVisibility(View.VISIBLE);
			mListView.invalidate();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(arrZnameSearch.size()>0){
				mProgress.setVisibility(View.GONE);
				mListView.setAdapter(new SearchAdapter(arrZnameSearch,AddZnameActivity.this));
			}else{
				mProgress.setVisibility(View.GONE);
				mListView.setAdapter(emptyAdapter);
			}
		}
		
	}

	static class CustomArrayAdapter<T> extends ArrayAdapter<T>
	{
	    public CustomArrayAdapter(Context ctx, T [] objects)
	    {
	        super(ctx, android.R.layout.simple_list_item_1, objects);
	    }
	    //other constructors
	    @Override
		public TextView getView(int position, View convertView, ViewGroup parent) {
	    	TextView v = (TextView) super.getView(position, convertView, parent);
	    	v.setTypeface(styleFont);
	    	v.setGravity(Gravity.CENTER);
	    	v.setTextColor(Color.GRAY);
	    	v.setTextSize(16f);
	    	v.setPadding(15, 15, 0, 15);
	    	return v;
	    	}
	}
	
	public class SearchAdapter extends BaseAdapter {

	    ArrayList<ZnameSearch> arrayListSearchAdapter = null;
	    Context context;
	    public SearchAdapter(ArrayList<ZnameSearch> arrayListSearchAdapter, Context context) {
	        this.arrayListSearchAdapter = arrayListSearchAdapter;
	        this.context = context;
	    }
	    
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrayListSearchAdapter.size();
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;

			LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.item_list_search, null); //EA ZM004
			
			TextView txtFullName = (TextView)view.findViewById(R.id.list_item_search_display_name);
			TextView txtZname = (TextView)view.findViewById(R.id.list_item_search_zname);
			final ImageView imgZname = (ImageView)view.findViewById(R.id.list_item_search_display_picture);
			
			txtFullName.setTypeface(styleFont);
			txtZname.setTypeface(styleFont);
		
			txtFullName.setText(arrayListSearchAdapter.get(position).getFullName());
			txtZname.setText(arrayListSearchAdapter.get(position).getzName());
			
			((Activity) context).runOnUiThread(new Runnable() {

	            @Override
	            public void run() {
	                // TODO Auto-generated method stub
	                imageLoader.displayImage(arrayListSearchAdapter.get(position).getImagePath(), imgZname, options);
	            }
	        });

			view.setTag(R.id.TAG_CONTACT_NAME, arrayListSearchAdapter.get(position).getFullName());
			view.setTag(R.id.TAG_CONTACT_ZNAME, arrayListSearchAdapter.get(position).getzName());
			view.setTag(R.id.TAG_CONTACT_DP, arrayListSearchAdapter.get(position).getImagePath());
			
			return view;
		}
	}

}
