/* HISTORY
 * CATEGORY			 :- SERVICE
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- IMPORT CONTACT SERVICE + CONTENT OBSERVER ON CONTACTS.CONTACTSCONTRACTS.DATA
 * 
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * ZM001      VIKALP PATEL     16/05/2014                       CREATED 
 * ZM002      VIKALP PATEL     16/06/2014                       CHANGE LAYOUT
 * --------------------------------------------------------------------------------------------------------------------
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
 * VIKALP PATEL       onBackPressed				            ON PRESSING BACK
 * VIKALP PATEL       getCallLogDetail			            GETTING CALL LOGS FROM CONTENT PROVIDER
 * VIKALP PATEL       getCallLogDate			            GETTING CALL LOG DATE FROM EPOCH TIME
 * VIKALP PATEL       getCallLogTime			            GETTING CALL LOGS TIME FROM EPOCH TIME
 * ********************************************************************************************************************
 */
package com.netdoers.zname.ui;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.netdoers.zname.AppConstants;
import com.netdoers.zname.R;
import com.netdoers.zname.beans.CallLog;

public class CallLogsDetailsActivity extends SherlockFragmentActivity {

	//VIEW
	private ListView mListView;
	private ProgressBar mProgress;
	private ActionBar mActionBar;
	
	//DECLARE COLLECTION
	private ArrayList<CallLog> arrayListCallLogDetail = null;
		
	//ADAPTER
	private CallLogDetailAdapter mAdapter = null;
	
	//TYPEFACE
	static Typeface styleFont;
	
	//CONSTANTS
	public static final String TAG = CallLogsDetailsActivity.class.getSimpleName();
	
	//VARIABLES
	private String mIntentName=null, mIntentNumber=null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_logs_detail);
		
		initUi();
		setFontStyle();
		
		mIntentName = getIntent().getStringExtra(AppConstants.TAGS.INTENT.TAG_NAME);
		mIntentNumber = getIntent().getStringExtra(AppConstants.TAGS.INTENT.TAG_NUMBER);
		
        setActionBar(mIntentName);
        
		arrayListCallLogDetail = new ArrayList<CallLog>();
		
		new AsyncLoadCallLogDetail(mIntentNumber).execute();
		
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
	
	private void initUi(){
		mListView = (ListView)findViewById(R.id.call_logs_detail_list_view);
		mProgress = (ProgressBar)findViewById(R.id.call_logs_detail_progress);
	}
	
	private void setFontStyle(){
		styleFont = Typeface.createFromAsset(getAssets(), AppConstants.fontStyle);
	}
	
	private void setActionBar(String str){
		mActionBar = getSupportActionBar();
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setTitle(str);
		fontActionBar(mActionBar.getTitle().toString());
	}
	
	private void setEventListeners(){
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Intent callIntent = new Intent(Intent.ACTION_CALL);
		        callIntent.setData(Uri.parse("tel:"+Uri.encode(mIntentNumber)));
		        startActivity(callIntent);
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
	public void getCallLogDetail(String _number) {
		Cursor cursor = null;
		CallLog calllog;
		
		cursor = getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null,android.provider.CallLog.Calls.NUMBER+"=?", new String[]{_number}, android.provider.CallLog.Calls.DATE + " DESC");
		
		if(cursor.getCount() > 0){
			int callLogName = cursor.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME);
			int callLogNumber = cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
			int callLogType = cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE);
			int callLogDate = cursor.getColumnIndex(android.provider.CallLog.Calls.DATE);
			
			while(cursor.moveToNext()){
				calllog = new CallLog();
				calllog.setCallLogName(cursor.getString(callLogName));
				calllog.setCallLogNumber(cursor.getString(callLogNumber));
				calllog.setCallLogType(cursor.getString(callLogType));
				calllog.setCallLogDate(getCallLogDate(Long.valueOf(cursor.getString(callLogDate))));
				calllog.setCallLogTime(getCallLogTime(Long.valueOf(cursor.getString(callLogDate))));
				arrayListCallLogDetail.add(calllog);
			}
		}
		if(cursor!=null)
			cursor.close();
	}
	//GET DATA TIME FROM EPOCHTIME
		@SuppressWarnings("deprecation")
		public String getCallLogDate(long milliseconds) {
			String formattedDate = null;
			Date date = new Date(milliseconds);
			//return DateFormat.getDateTimeInstance().format(new Date());
			formattedDate = String.valueOf(date.getDate());
			formattedDate = formattedDate + "/" + String.valueOf(date.getMonth()+1);
			formattedDate = formattedDate + "/" + String.valueOf(date.getYear()+1900);
			return formattedDate;
		}
		
		public String getCallLogTime(long milliseconds) {
			String formattedTime = null;
			Date date = new Date(milliseconds);
			formattedTime = String.valueOf(date.getHours());
			String formattedMinutes =
					String.valueOf(date.getMinutes()).length() == 1 
					? "0"+ String.valueOf(date.getMinutes()) 
					: String.valueOf(date.getMinutes());
			formattedTime = formattedTime + ":" + formattedMinutes;
			return formattedTime;
		}
	
	private class AsyncLoadCallLogDetail extends AsyncTask<Void, Void, Void>
	{
		private String number = null;
		
		public AsyncLoadCallLogDetail(String number){
			this.number = number;
		}
		@Override
		protected void onPreExecute() {
			mProgress.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			getCallLogDetail(number);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// set contact adapter
			// set the progress to GONE
			mProgress.setVisibility(View.GONE);
			if (arrayListCallLogDetail != null && arrayListCallLogDetail.size() > 0) {
				mAdapter = new CallLogDetailAdapter(arrayListCallLogDetail);
				mListView.setAdapter(mAdapter);
			}
		}
	}

	
	public class CallLogDetailAdapter extends BaseAdapter {

	    ArrayList<CallLog> arrayListCallLogsAdapter = null;
	    
	    public CallLogDetailAdapter(ArrayList<CallLog> arrayListCallLogsAdapter) {
	        this.arrayListCallLogsAdapter=arrayListCallLogsAdapter;
	    }
	    
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrayListCallLogsAdapter.size();
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
//			view = vi.inflate(R.layout.depreceated_list_item_call_log, null); //SA ZM002
			view = vi.inflate(R.layout.item_list_call_log, null); //EA ZM002
			
			TextView t1 = (TextView)view.findViewById(R.id.list_item_call_log_name);
//			TextView t2 = (TextView)view.findViewById(R.id.list_item_call_log_number); // COMMENTED ZM002
			TextView t3 = (TextView)view.findViewById(R.id.list_item_call_log_date);
			TextView t4 = (TextView)view.findViewById(R.id.list_item_call_log_header_separator);
			ImageView imgLogType = (ImageView)view.findViewById(R.id.list_item_call_log_type);
			ImageView imgCall = (ImageView)view.findViewById(R.id.list_item_call_log_call); //SA ZM002
			ImageView imgMsg = (ImageView)view.findViewById(R.id.list_item_call_log_message); //EA ZM002
//			ImageView img = (ImageView)view.findViewById(R.id.grid_item_display_picture);

			String _name = arrayListCallLogDetail.get(position).getCallLogName()!=null ? arrayListCallLogDetail.get(position).getCallLogName(): "Unknown";
			
			if(_name.equalsIgnoreCase("unknown")){ //SA ZM002
				_name = arrayListCallLogDetail.get(position).getCallLogNumber();
			} //EA ZM002
			
			t1.setText(_name);
//			t2.setText(arrayListCallLogDetail.get(position).getCallLogNumber());// COMMENTED ZM002
			t3.setText(arrayListCallLogDetail.get(position).getCallLogTime());
			
			t1.setTypeface(styleFont);
//			t2.setTypeface(styleFont); //COMMENTED ZM002
			t3.setTypeface(styleFont);
			t4.setTypeface(styleFont);
			
//			img.setImageURI(
//					arrayListCallLog.get(position).getCallLogPhotoUri()!=null
//					? arrayListCallLog.get(position).getCallLogPhotoUri()
//					: null		
//					);
//			img.setImageURI(arrayListCallLog.get(position).getCallLogPhotoUri());
//			
//			if(img.getDrawable() == null)
//				img.setImageResource(R.drawable.def_contact);
			
			if(position == 0)
				t4.setText(arrayListCallLogDetail.get(position).getCallLogDate());
				
			try {
				if(arrayListCallLogDetail.get(position).getCallLogDate().equalsIgnoreCase(arrayListCallLogDetail.get(position-1).getCallLogDate())){
					t4.setVisibility(view.GONE);
				}else{
					t4.setVisibility(view.VISIBLE);
					t4.setText(arrayListCallLogDetail.get(position).getCallLogDate());
				}
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
			
			switch(Integer.parseInt(arrayListCallLogDetail.get(position).getCallLogType()))
			{
			case 0:
				imgLogType.setImageResource(R.drawable.btn_ic_call_selector);
				break;
			case 1:
				imgLogType.setImageResource(R.drawable.btn_ic_incoming_selector);
				break;
			case 2:
				imgLogType.setImageResource(R.drawable.btn_ic_outgoing_selector);
				break;
			case 3:
				imgLogType.setImageResource(R.drawable.btn_ic_missed_selector);
				break;
			default:
				imgLogType.setImageResource(R.drawable.btn_ic_call_selector);
				break;
			}
			
			view.setTag(R.id.TAG_CALL_LOG_POSITION, position);
			
//			SA ZM002
			imgCall.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent callIntent = new Intent(Intent.ACTION_DIAL);
		            callIntent.setData(Uri.parse("tel:"+Uri.encode(arrayListCallLogDetail.get(position).getCallLogNumber())));
		            startActivity(callIntent);
				}
			});
			
			imgMsg.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"+Uri.encode(arrayListCallLogDetail.get(position).getCallLogNumber())));
		            startActivity(smsIntent);
				}
			});
//			EA ZM002
			return view;
		}
	}
}
