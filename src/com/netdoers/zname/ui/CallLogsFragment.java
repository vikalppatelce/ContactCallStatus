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
 * ZM002      VIKALP PATEL     09/06/2014                       SUPPRESSED MENU : DATE
 * ZM003      VIKALP PATEL     16/06/2014                       SUPPRESSED MENU ON SCROLL
 * ZM004      VIKALP PATEL     16/06/2014                       CHANGED LIST LAYOUT WITH CALL AND MESSAGE BUTTON
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.netdoers.zname.AppConstants;
import com.netdoers.zname.R;
import com.netdoers.zname.Zname;
import com.netdoers.zname.beans.CallLog;
import com.netdoers.zname.sqlite.DBConstant;

/**
 * @author Vikalp Patel(vikalppatelce@yahoo.com)
 *
 */
@SuppressLint({ "SimpleDateFormat", "ValidFragment" })
public class CallLogsFragment extends SherlockFragment /*implements OnRefreshListener*/{

	//DECLARE VIEW
	ListView callLogsListView;
	ProgressBar callLogsProgress;
	LinearLayout callLogsMenu;
	ImageView callLogsAll, callLogsMissed, callLogsIncoming, callLogsOutGoing;//, callLogsDate; COMMENTED ZM002
	PullToRefreshLayout mPullToRefreshLayout;	
	
	//DECLARE COLLECTION
	private ArrayList<CallLog> arrayListCallLog = null;
	
	//TYPEFACE 
	static Typeface styleFont;
	
	//DECLARE ADAPTER
	private CallLogAdapter callLogsAdapter = null;

	//CONSTANTS
	public static final String TAG = CallLogsFragment.class.getSimpleName();

	//DECLARE VARIABLES
	private int mLastFirstVisibleItem;
	private boolean mIsScrollingUp;
	private String strLogDate;
	private int saveLogType;

	//CONTENT OBSERVER;
	CallLogContentObserver callLogContentObserver;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Get the view from fragment_call_logs.xml
		View view = inflater.inflate(R.layout.fragment_call_logs, container, false);
		callLogsListView = (ListView)view.findViewById(R.id.call_logs_list_view);
		callLogsProgress = (ProgressBar)view.findViewById(R.id.call_logs_progress);
		callLogsMenu = (LinearLayout)view.findViewById(R.id.call_logs_menu);
		callLogsAll = (ImageView)view.findViewById(R.id.call_logs_menu_all);
		callLogsIncoming = (ImageView)view.findViewById(R.id.call_logs_menu_incoming);
		callLogsOutGoing = (ImageView)view.findViewById(R.id.call_logs_menu_outgoing);
		callLogsMissed = (ImageView)view.findViewById(R.id.call_logs_menu_missed);
//		mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.call_pull_to_refresh);
//		callLogsDate = (ImageView)view.findViewById(R.id.call_logs_menu_date); COMMENTED ZM002
		return view;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		styleFont = Typeface.createFromAsset(getActivity().getAssets(), AppConstants.fontStyle);
		
		callLogContentObserver = new CallLogContentObserver();
		
// VIEW LISTENERS
//		ActionBarPullToRefresh.from(getActivity())
//        .allChildrenArePullable()
//        .listener(this)
//        .setup(mPullToRefreshLayout);
		
		callLogsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				String _name = view.getTag(R.id.TAG_CONTACT_NAME).toString().equalsIgnoreCase("Unknown") 
						  ? "Unknown"
						 :view.getTag(R.id.TAG_CONTACT_NAME).toString();
				
				Intent callLogDetailIntent = new Intent(getActivity(), CallLogsDetailsActivity.class);
				callLogDetailIntent.putExtra(AppConstants.TAGS.INTENT.TAG_NUMBER, view.getTag(R.id.TAG_CONTACT_NUMBER).toString());
				callLogDetailIntent.putExtra(AppConstants.TAGS.INTENT.TAG_NAME, _name);
				startActivity(callLogDetailIntent);
			}
		});
		
//		 SC ZM003
		callLogsListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				final ListView lw = callLogsListView;

			    if (view.getId() == lw.getId()) {
			        final int currentFirstVisibleItem = lw.getFirstVisiblePosition();

			        if (currentFirstVisibleItem > mLastFirstVisibleItem) {
			            mIsScrollingUp = false;
			            callLogsMenu.setVisibility(View.GONE);
			        } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
			            mIsScrollingUp = true;
			            callLogsMenu.setVisibility(View.VISIBLE);
			        }
			        mLastFirstVisibleItem = currentFirstVisibleItem;
			    } 
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
			}
		});
//		 EC ZM003
		
		callLogsAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getCallAll();
			}
		});
		
		callLogsIncoming.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getCallIncoming();
			}
		});
		
		callLogsOutGoing.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getCallOutgoing();
			}
		});
		
		callLogsMissed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getCallMissed();
			}
		});
		
//		 SC ZM002
//		callLogsDate.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				getCallDate();
//			}
//		});
//		 EC ZM002
	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getActivity().getContentResolver().registerContentObserver(
				android.provider.CallLog.CONTENT_URI, true,
				callLogContentObserver);
		
		if(Zname.getPreferences().getRefreshCallLogs()){
			Zname.getPreferences().setRefreshCallLogs(false);
			arrayListCallLog = null;
		}
		
		if(arrayListCallLog == null){
			arrayListCallLog = new ArrayList<CallLog>();
			new AsyncLoadCallLogs(0,"ALL").execute();
		}
		else{
			if(callLogsAdapter!=null){
				callLogsProgress.setVisibility(View.GONE);
				callLogsListView.setAdapter(callLogsAdapter);
				
				if(!TextUtils.isEmpty(String.valueOf(saveLogType))){
					switch(saveLogType){
					case 0:
						callLogsAll.setImageResource(R.drawable.btn_ic_call_selected);
						break;
					case 1:
						callLogsIncoming.setImageResource(R.drawable.btn_ic_incoming_selected);
						break;
					case 2:
						callLogsOutGoing.setImageResource(R.drawable.btn_ic_outgoing_selected);
						break;
					case 3:
						callLogsMissed.setImageResource(R.drawable.btn_ic_missed_selected);
						break;
					default:
							break;
					}
				}
			}
		}
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getActivity().getContentResolver().unregisterContentObserver(callLogContentObserver);
	}
	//ASYNCTASK -> LOAD CALL LOGS
	
	private class AsyncLoadCallLogs extends AsyncTask<Void, Void, Void>
	{
		private int logType = 0;
		private String strLog = null;
		
		public AsyncLoadCallLogs(int logType, String strLog){
			this.logType = logType;
			this.strLog = strLog;
			saveLogType = logType;
		}
		@Override
		protected void onPreExecute() {
			callLogsProgress.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			getCallLogs(logType,strLog);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// set contact adapter
			
			// set the progress to GONE
			callLogsProgress.setVisibility(View.GONE);
			callLogsAll.setImageResource(R.drawable.btn_ic_call);
			callLogsIncoming.setImageResource(R.drawable.btn_ic_incoming);
			callLogsOutGoing.setImageResource(R.drawable.btn_ic_outgoing);
			callLogsMissed.setImageResource(R.drawable.btn_ic_missed);
			
			if (arrayListCallLog != null && arrayListCallLog.size() > 0) {
				callLogsAdapter = new CallLogAdapter(arrayListCallLog);
				callLogsListView.setAdapter(callLogsAdapter);
			}
			
			switch(logType){
			case 0:
				callLogsAll.setImageResource(R.drawable.btn_ic_call_selected);
				break;
			case 1:
				callLogsIncoming.setImageResource(R.drawable.btn_ic_incoming_selected);
				break;
			case 2:
				callLogsOutGoing.setImageResource(R.drawable.btn_ic_outgoing_selected);
				break;
			case 3:
				callLogsMissed.setImageResource(R.drawable.btn_ic_missed_selected);
				break;
			default:
					break;
			}
		}
	}
	
	public void getCallAll(){
		arrayListCallLog.clear();
		new AsyncLoadCallLogs(0,"ALL").execute();
	}
	
	public void getCallIncoming(){
		arrayListCallLog.clear();
		new AsyncLoadCallLogs(1,"INCOMING").execute();
	}
	public void getCallOutgoing(){
		arrayListCallLog.clear();
		new AsyncLoadCallLogs(2,"OUTGOING").execute();
	}
	public void getCallMissed(){
		arrayListCallLog.clear();
		new AsyncLoadCallLogs(3,"MISSED").execute();
	}
	
	private class AsyncObserverCallLog extends AsyncTask<Void, Void, Void>
	{
		private int logType = 0;
		private String strLog = null;
		
		public AsyncObserverCallLog(int logType, String strLog){
			this.logType = logType;
			this.strLog = strLog;
		}
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			getCallLogs(logType,strLog);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// set contact adapter
			// set the progress to GONE
			if (arrayListCallLog != null) {
				callLogsAdapter = new CallLogAdapter(arrayListCallLog);
				callLogsListView.setAdapter(callLogsAdapter);
			}
		}
	}
	public void getCallDate(){
		arrayListCallLog.clear();
		DialogFragment newFragment = new FromDatePickerFragment();
		newFragment.show(getSherlockActivity().getSupportFragmentManager(), "From Date");
	}
	
	
	public void getCallLogs(int logType , String strDate)
	{
		Cursor cursor = null;
		CallLog calllog;
		
		switch (logType) {
		case 0://ALL
			cursor = Zname.getApplication().getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.TYPE+"=1" +" OR "+ android.provider.CallLog.Calls.TYPE+"=2" + " OR " + android.provider.CallLog.Calls.TYPE+"=3", null, android.provider.CallLog.Calls.DATE +" DESC");
			break;
		case 1://INCOMING
			cursor = Zname.getApplication().getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.TYPE+"=1", null, android.provider.CallLog.Calls.DATE +" DESC");
			break;
		case 2://OUTGOING
			cursor = Zname.getApplication().getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.TYPE+"=2", null, android.provider.CallLog.Calls.DATE +" DESC");
			break;
		case 3://MISSED
			cursor = Zname.getApplication().getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.TYPE+"=3", null, android.provider.CallLog.Calls.DATE +" DESC");
			break;
		case 4://DATE
			cursor = Zname.getApplication().getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.DATE+">"+strDate, null, android.provider.CallLog.Calls.DATE +" ASC");
			break;
		default:
			cursor = Zname.getApplication().getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE +" DESC");
			break;
		}
		
		if (cursor.getCount() > 0) {
			int callLogName = cursor.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME);
			int callLogNumber = cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
			int callLogType = cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE);
			int callLogDate = cursor.getColumnIndex(android.provider.CallLog.Calls.DATE);
//			int callLogDuration = cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION);
			
			arrayListCallLog.clear();
			while(cursor.moveToNext()){
				if (StringUtils.isAlphanumeric(cursor.getString(callLogNumber))){
					continue;
				}
				if(cursor.getString(callLogNumber).contains("-") && !StringUtils.isAlpha(cursor.getString(callLogNumber))){
						continue;	
				}
				calllog = new CallLog();
				String _callLogDate = getCallLogDate(Long.valueOf(cursor.getString(callLogDate)));
				calllog.setCallLogName(cursor.getString(callLogName));
				calllog.setCallLogNumber(cursor.getString(callLogNumber));
				calllog.setCallLogType(cursor.getString(callLogType));
				calllog.setCallLogDate(_callLogDate);
//				calllog.setCallLogDate(getCallLogDate(Long.valueOf(cursor.getString(callLogDate))));
				calllog.setCallLogTime(getCallLogTime(Long.valueOf(cursor.getString(callLogDate))));
//				try {
//					String photoFromNumber=null;
//					try {
//						photoFromNumber = getContactPhotoFromNumber(cursor.getString(callLogNumber));
//					} catch (Exception e) {
//						Log.e(TAG, e.toString());
//					}
//					calllog.setCallLogPhotoUri(
//							photoFromNumber!=null 
//							? Uri.parse(photoFromNumber) 
//							: null
//							);
//					
//				} catch (Exception e) {
//					Log.e(TAG, e.toString());
//				}
				arrayListCallLog.add(calllog);
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
	
	
	//AYSNCTASK HELPER METHOD
	//GET CONTACT ID FROM CALL LOG
	public static String getContactPhotoFromNumber(String contactNumber) {
		String photoString = null;
		int phoneContactID = new Random().nextInt();
		Cursor cursor = null;
		Cursor contactLookupCursor = Zname.getApplication().getContentResolver().query(Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contactNumber)),new String[] { PhoneLookup.DISPLAY_NAME,PhoneLookup._ID }, null, null, null);
		if(contactLookupCursor.getCount() > 0){
			contactLookupCursor.moveToFirst();
				try {
					phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(PhoneLookup._ID));
					cursor = Zname.getApplication().getContentResolver().query(DBConstant.All_Contacts_Columns.CONTENT_URI,null,DBConstant.All_Contacts_Columns.COLUMN_CONTACT_ID+ "=?",new String[] { String.valueOf(phoneContactID) },null);
					if (cursor.getCount() > 0) {
						cursor.moveToFirst();
						photoString = cursor.getString(cursor.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_CALL_STATUS));
					}
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
		}
		if (cursor != null) {
			cursor.close();
		}
		if (contactLookupCursor != null) {
			contactLookupCursor.close();
		}
		return photoString;
	}
	
//	DIALOGFRAGMENT DATE PICKER
	class FromDatePickerFragment extends DialogFragment implements
	OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
	//Use the current time as the default values for the picker
			int day,mnth,yr;
				final Calendar c = Calendar.getInstance();
				yr = c.get(Calendar.YEAR);
				mnth = c.get(Calendar.MONTH);
				day = c.get(Calendar.DAY_OF_MONTH);
			// Create a new instance of TimePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, yr, mnth, day); 
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
		int dayOfMonth) {
			// 	TODO Auto-generated method stub
			strLogDate = null;
			String strDate;
			monthOfYear++;
			if (dayOfMonth < 10) {
				strDate = "0" + dayOfMonth + "/";
			} else {
				strDate = dayOfMonth + "/";
			}
			if (monthOfYear < 10) {
				strDate += "0" + monthOfYear + "/";
			} else {
				strDate += monthOfYear + "/";
			}

			strDate += year;
			
			SimpleDateFormat dF = new SimpleDateFormat("dd/MM/yyyy");
			Date date = null;
			try {
				date = dF.parse(strDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(date!=null)
				strLogDate = String.valueOf(date.getTime());
			
			if(!TextUtils.isEmpty(strLogDate))
				new AsyncLoadCallLogs(4,strLogDate).execute();
		}
	}
//	CONTENT OBSERVER
	private class CallLogContentObserver extends ContentObserver {
        public CallLogContentObserver() {
            super(null);
        }
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new AsyncObserverCallLog(0, "ALL");
        }
    }
//	@Override
//	public void onRefreshStarted(View view) {
//		// TODO Auto-generated method stub
//        /**
//         * Simulate Refresh with 4 seconds sleep
//         */
//        new AsyncTask<Void, Void, Void>() {
//
//            @Override
//            protected Void doInBackground(Void... params) {
//                try {
//                	if(arrayListCallLog == null){
//            			arrayListCallLog = new ArrayList<CallLog>();
//            			getCallLogs(0,"ALL");
//            		}
//                	else{
//                		getCallLogs(0,"ALL");
//                	}
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void result) {
//                super.onPostExecute(result);
//                // Notify PullToRefreshLayout that the refresh has finished
//                mPullToRefreshLayout.setRefreshComplete();
//                if (arrayListCallLog != null) {
//    				callLogsAdapter = new CallLogAdapter(arrayListCallLog);
//    				callLogsListView.setAdapter(callLogsAdapter);
//    			}
//            }
//        }.execute();
//	}
	
	//ADAPTER CALL LOG
	@SuppressLint("SimpleDateFormat")
	public class CallLogAdapter extends BaseAdapter {

	    ArrayList<CallLog> arrayListCallLogsAdapter = null;
	    
	    public CallLogAdapter(ArrayList<CallLog> arrayListCallLogsAdapter) {
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

			LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			view = vi.inflate(R.layout.depreceated_list_item_call_log, null); // SA ZM004
			view = vi.inflate(R.layout.item_list_call_log, null); //EA ZM004
			
			TextView t1 = (TextView)view.findViewById(R.id.list_item_call_log_name);
//			TextView t2 = (TextView)view.findViewById(R.id.list_item_call_log_number); COMMENTED ZM004
			TextView t3 = (TextView)view.findViewById(R.id.list_item_call_log_date);
			TextView t4 = (TextView)view.findViewById(R.id.list_item_call_log_header_separator);
			ImageView imgLogType = (ImageView)view.findViewById(R.id.list_item_call_log_type);
			ImageView imgCall = (ImageView)view.findViewById(R.id.list_item_call_log_call); // SA ZM004
			ImageView imgMsg = (ImageView)view.findViewById(R.id.list_item_call_log_message);//EA ZM004
//			ImageView img = (ImageView)view.findViewById(R.id.grid_item_display_picture);
			

			String _name = arrayListCallLog.get(position).getCallLogName()!=null ? arrayListCallLog.get(position).getCallLogName(): "Unknown";
		
			if(_name.equalsIgnoreCase("Unknown")){ //SA ZM004
				_name = arrayListCallLog.get(position).getCallLogNumber();
			}// EA ZM004
			
			t1.setText(_name);
//			t2.setText(arrayListCallLog.get(position).getCallLogNumber()); COMMENTED ZM004
			t3.setText(arrayListCallLog.get(position).getCallLogTime());
			
			t1.setTypeface(styleFont);
//			t2.setTypeface(styleFont); //COMMENTED ZM004
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
				t4.setText(arrayListCallLog.get(position).getCallLogDate());
				
			try {
				if(arrayListCallLog.get(position).getCallLogDate().equalsIgnoreCase(arrayListCallLog.get(position-1).getCallLogDate())){
					t4.setVisibility(view.GONE);
				}else{
					t4.setVisibility(view.VISIBLE);
					t4.setText(arrayListCallLog.get(position).getCallLogDate());
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				Log.e(TAG, e.toString());
			}
			
			switch(Integer.parseInt(arrayListCallLog.get(position).getCallLogType()))
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
			view.setTag(R.id.TAG_CONTACT_NUMBER, arrayListCallLog.get(position).getCallLogNumber());
			view.setTag(R.id.TAG_CONTACT_NAME, _name);
//			SA ZM004
			imgCall.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent callIntent = new Intent(Intent.ACTION_DIAL);
		            callIntent.setData(Uri.parse("tel:"+Uri.encode(arrayListCallLog.get(position).getCallLogNumber())));
		            startActivity(callIntent);
				}
			});
			
			imgMsg.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"+Uri.encode(arrayListCallLog.get(position).getCallLogNumber())));
		            startActivity(smsIntent);
				}
			});
//			EA ZM004
			return view;
		}
	}
}
