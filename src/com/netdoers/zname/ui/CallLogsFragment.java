package com.netdoers.zname.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
@SuppressLint("SimpleDateFormat")
public class CallLogsFragment extends SherlockFragment {

	//DECLARE VIEW
	ListView callLogsListView;
	ProgressBar callLogsProgress;
	
	//DECLARE COLLECTION
	private ArrayList<CallLog> arrayListCallLog = null;
	
	//DECLARE ADAPTER
	private CallLogAdapter callLogsAdapter = null;

	//CONSTANTS
	public static final String TAG = "CallLogsFragment";
	
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
			new AsyncLoadCallLogs().execute();
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

		@Override
		protected void onPreExecute() {
			callLogsProgress.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			getCallLogs(0);
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
	
	public void getCallLogs(int logType)
	{
		Cursor cursor = null;
		CallLog calllog;
		
		switch (logType) {
		case 0:
			cursor = Zname.getApplication().getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE +" DESC");
			break;
		case 1:
			cursor = Zname.getApplication().getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.INCOMING_TYPE+"?=1", null, android.provider.CallLog.Calls.DATE +" DESC");
			break;
		case 2:
			cursor = Zname.getApplication().getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.OUTGOING_TYPE+"?=1", null, android.provider.CallLog.Calls.DATE +" DESC");
			break;
		case 3:
			cursor = Zname.getApplication().getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.MISSED_TYPE+"?=1", null, android.provider.CallLog.Calls.DATE +" DESC");
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
				img.setImageResource(android.R.drawable.sym_call_missed);
				break;
			case 1:
				img.setImageResource(android.R.drawable.sym_call_incoming);
				break;
			case 2:
				img.setImageResource(android.R.drawable.sym_call_outgoing);
				break;
			case 3:
				img.setImageResource(android.R.drawable.sym_call_incoming);
				break;
			default:
				img.setImageResource(android.R.drawable.sym_action_chat);
				break;
			}
			
			return view;
		}
	}
}
