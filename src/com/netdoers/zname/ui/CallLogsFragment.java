package com.netdoers.zname.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.netdoers.zname.R;
import com.netdoers.zname.Zname;
import com.netdoers.zname.dto.CallLog;
import com.netdoers.zname.sqlite.DBConstant;

/**
 * @author Vikalp Patel(vikalppatelce@yahoo.com)
 *
 */
@SuppressLint({ "SimpleDateFormat", "ValidFragment" })
public class CallLogsFragment extends SherlockFragment {

	//DECLARE VIEW
	ListView callLogsListView;
	ProgressBar callLogsProgress;
	LinearLayout callLogsMenu;
	ImageView callLogsAll, callLogsMissed, callLogsIncoming, callLogsOutGoing, callLogsDate;
	
	//DECLARE COLLECTION
	private ArrayList<CallLog> arrayListCallLog = null;
	
	//DECLARE ADAPTER
	private CallLogAdapter callLogsAdapter = null;

	//CONSTANTS
	public static final String TAG = "CallLogsFragment";

	//DECLARE VARIABLES
	private int mLastFirstVisibleItem;
	private boolean mIsScrollingUp;
	private String strLogDate;

	
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
		callLogsDate = (ImageView)view.findViewById(R.id.call_logs_menu_date);
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
		
// VIEW LISTENERS
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
		
		callLogsDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getCallDate();
			}
		});


	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
			}
		}
	}
	
	//ASYNCTASK -> LOAD CALL LOGS
	
	private class AsyncLoadCallLogs extends AsyncTask<Void, Void, Void>
	{
		private int logType = 0;
		private String strLog = null;
		
		public AsyncLoadCallLogs(int logType, String strLog){
			this.logType = logType;
			this.strLog = strLog;
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
			if (arrayListCallLog != null) {
				callLogsAdapter = new CallLogAdapter(arrayListCallLog);
				callLogsListView.setAdapter(callLogsAdapter);
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
		case 0:
			cursor = Zname.getApplication().getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE +" DESC");
			break;
		case 1:
			cursor = Zname.getApplication().getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.TYPE+"=0", null, android.provider.CallLog.Calls.DATE +" DESC");
			break;
		case 2:
			cursor = Zname.getApplication().getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.TYPE+"=1", null, android.provider.CallLog.Calls.DATE +" DESC");
			break;
		case 3:
			cursor = Zname.getApplication().getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.TYPE+"=2", null, android.provider.CallLog.Calls.DATE +" DESC");
			break;
		case 4:
			cursor = Zname.getApplication().getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.DATE+">"+strDate, null, android.provider.CallLog.Calls.DATE +" ASC");
			break;
		default:
			cursor = Zname.getApplication().getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE +" DESC");
			break;
		}
		
		int callLogName = cursor.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME);
		int callLogNumber = cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
		int callLogType = cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE);
		int callLogDate = cursor.getColumnIndex(android.provider.CallLog.Calls.DATE);
//		int callLogDuration = cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION);
		
		cursor.moveToFirst();
		while(cursor.moveToNext()){
			calllog = new CallLog();
			calllog.setCallLogName(cursor.getString(callLogName));
			calllog.setCallLogNumber(cursor.getString(callLogNumber));
			calllog.setCallLogType(cursor.getString(callLogType));
			calllog.setCallLogDate(getCallLogDate(Long.valueOf(cursor.getString(callLogDate))));
			calllog.setCallLogTime(getCallLogTime(Long.valueOf(cursor.getString(callLogDate))));
//			try {
//				String photoFromNumber=null;
//				try {
//					photoFromNumber = getContactPhotoFromNumber(cursor.getString(callLogNumber));
//				} catch (Exception e) {
//
//				}
//				calllog.setCallLogPhotoUri(
//						photoFromNumber!=null 
//						? Uri.parse(photoFromNumber) 
//						: null
//						);
//				
//			} catch (Exception e) {
//				Log.e(TAG, e.toString());
//			}
			arrayListCallLog.add(calllog);
		}
		
	}

	//GET DATA TIME FROM EPOCHTIME
	public String getCallLogDate(long milliseconds) {
		String formattedDate = null;
		Date date = new Date(milliseconds);
		//return DateFormat.getDateTimeInstance().format(new Date());
		formattedDate = String.valueOf(date.getDate());
		formattedDate = formattedDate + "/" + String.valueOf(date.getMonth());
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
		while (contactLookupCursor.moveToNext()) {
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;

			LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.list_item_call_log, null);
			
			TextView t1 = (TextView)view.findViewById(R.id.list_item_call_log_name);
			TextView t2 = (TextView)view.findViewById(R.id.list_item_call_log_number);
			TextView t3 = (TextView)view.findViewById(R.id.list_item_call_log_date);
			TextView t4 = (TextView)view.findViewById(R.id.list_item_call_log_header_separator);
			ImageView img = (ImageView)view.findViewById(R.id.list_item_call_log_type);
			
			t1.setText(arrayListCallLog.get(position).getCallLogName());
			t2.setText(arrayListCallLog.get(position).getCallLogNumber());
			t3.setText(arrayListCallLog.get(position).getCallLogTime());
			
//			img.setImageURI(
//					arrayListCallLog.get(position).getCallLogPhotoUri()!=null
//					? arrayListCallLog.get(position).getCallLogPhotoUri()
//					: null		
//					);
			if(position == 0)
				t4.setText(arrayListCallLog.get(position).getCallLogDate());
				
			try {
				if(arrayListCallLog.get(position).getCallLogDate().equalsIgnoreCase(arrayListCallLog.get(position-1).getCallLogDate())){
					t4.setVisibility(view.GONE);
				}else{
					t4.setVisibility(view.VISIBLE);
					t4.setText(arrayListCallLog.get(position).getCallLogDate());
				}
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
			
			switch(Integer.parseInt(arrayListCallLog.get(position).getCallLogType()))
			{
			case 0:
				img.setImageResource(R.drawable.btn_ic_missed_selector);
				break;
			case 1:
				img.setImageResource(R.drawable.btn_ic_incoming_selector);
				break;
			case 2:
				img.setImageResource(R.drawable.btn_ic_outgoing_selector);
				break;
			case 3:
				img.setImageResource(R.drawable.btn_ic_call_selector);
				break;
			default:
				img.setImageResource(android.R.drawable.sym_action_chat);
				break;
			}
			
			return view;
		}
	}
}
