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
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.netdoers.zname.BuildConfig;
import com.netdoers.zname.R;
import com.netdoers.zname.Zname;
import com.netdoers.zname.dto.Contact;
import com.netdoers.zname.service.ImportContactsService;
import com.netdoers.zname.sqlite.DBConstant;

/**
 * @author Vikalp Patel(vikalppatelce@yahoo.com)
 *
 */
public class AllContactsFragment extends SherlockFragment /*implements OnRefreshListener */{

	//View reference variable
	GridView contactsGridView;
	LinearLayout searchContactLayout;
	ImageView searchClose;	
	EditText searchField;
	ProgressDialog progressDialog;

	//Android helping reference variable
	private ContactAdapter contactAdapter = null;
	
	//Helping reference variable
	private ArrayList<Contact> contacts = null;
	private LinkedHashMap<String, Contact> allContacts = new LinkedHashMap<String, Contact>();
	
	
	//Indexing for the list
	HashMap<String, Integer> alphaIndexer;
	String[] sections;
	
	//CONSTANTS
	public static final String TAG = "AllContactsFragment";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	//	private PullToRefreshLayout mPullToRefreshLayout;
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Get the view from fragment_all_contacts.xml
		View view = inflater.inflate(R.layout.fragment_all_contacts, container, false);
//		mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
		contactsGridView = (GridView)view.findViewById(R.id.gridview_all_contacts);
		searchContactLayout = (LinearLayout)view.findViewById(R.id.search_txt_layout);
		searchClose = (ImageView)view.findViewById(R.id.clear_srch_button);
		searchField = (EditText) view.findViewById(R.id.search_txt);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
//		 ActionBarPullToRefresh.from(getActivity())
//         .allChildrenArePullable()
//         .listener(this)
//         .setup(mPullToRefreshLayout);
		
        // View Listeners
		searchClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			onCloseSearchLayout(v);	
			}
		});

		contacts = new ArrayList<Contact>();
		
		/*
		 * ImportContactsTask AyncTask execute when Zname is first time launched
		 */
		if(!Zname.getPreferences().getFirstTime()){
			progressDialog  = new ProgressDialog(getActivity());
			progressDialog.setMessage("Importing Contacts");
			progressDialog.setCancelable(false);
			progressDialog.show();
			Intent i =  new Intent(Zname.getApplication().getApplicationContext(), ImportContactsService.class);
			getActivity().startService(i);
			Zname.getPreferences().setFirstTime(true);
		}else if(Zname.getPreferences().getRefreshContact()){
			Zname.getPreferences().setRefreshContact(false);
		}else{
			refreshContactsData();
		}
		
		// Add text listner to the edit text for filtering the List
		searchField.addTextChangedListener(new TextWatcher() {
					@Override
					public void afterTextChanged(Editable s) {
					}
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						// call the filter with the current text on the editbox
						contactAdapter.getFilter().filter(s.toString());
					}
				});
		
		contactsGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
			
				// vibration for 100 milliseconds
				((Vibrator)getActivity().getApplication().getApplicationContext().getSystemService(getActivity().VIBRATOR_SERVICE)).vibrate(50);
				
				String viewTagNumber = view.getTag(R.id.TAG_CONTACT_NUMBER).toString();
				String viewTagDp = view.getTag(R.id.TAG_CONTACT_DP).toString();
				String viewTagName =  view.getTag(R.id.TAG_CONTACT_NAME).toString();
				
				showInputDialog(viewTagName,viewTagNumber,viewTagDp);
				return false;
			}
		});
		/*
		 * Import Contact Service
		 */
//		   Intent importContactService = new Intent(getActivity(),ImportContactsService.class);
//		   importContactService.setClass(getActivity(), ImportContactsService.class);
//         importContactService.setAction("fromFragment");
//         getActivity().startService(importContactService);
		
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
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getActivity().registerReceiver(broadcastReceiver, new IntentFilter(ImportContactsService.BROADCAST_ACTION));
//		getActivity().supportInvalidateOptionsMenu();
		if(BuildConfig.DEBUG){
				Log.i(TAG, "getActivity().supportInvalidateOptionsMenu();");
		}
	}
	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(broadcastReceiver);
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    menu.clear();
	    inflater.inflate(R.menu.all_contacts_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search:
			openSearchLayout();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	updateUI(intent);       
        }
    };
    
    private void updateUI(Intent intent) {
    	String counter = intent.getStringExtra("text"); 
    	if(counter.equals("Refreshed")){
    		progressDialog.dismiss();
    		refreshContactsData();	
    	}
    }
//	ADD CONTACTS TO CONTACTS ADAPTER
	
	public void refreshContactsData()
	{
		Cursor cr = getActivity().getContentResolver().query(DBConstant.All_Contacts_Columns.CONTENT_URI, null, null, null, null);
		if(cr.getCount() > 0)
		{
			int intColumnId = cr.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_ID);
			int intColumnContactNumber = cr.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_NUMBER);
			int intColumnZname = cr.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_DISPLAY_NAME);
			int intColumnZnameDp = cr.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_CALL_STATUS);
			Contact c;
			cr.moveToFirst();
			while(cr.moveToNext())
			{
				c = new Contact();
				c.setContactId(cr.getString(intColumnId));
				c.setContactNumber(cr.getString(intColumnContactNumber));
				c.setContactName(cr.getString(intColumnZname));
				c.setContactPhotoUri(Uri.parse(cr.getString(intColumnZnameDp)));
				contacts.add(c);
			}
		}
		if(cr!=null){
			cr.close();
		}
		
		contactAdapter = new ContactAdapter(getActivity(), R.id.gridview_all_contacts, contacts);
		contactsGridView.setAdapter(contactAdapter);
	}
	
	
//	View Listeners
	public void onCloseSearchLayout(View v)
	{
		searchContactLayout.setVisibility(View.GONE);
		searchField.setText("");
	}

	public void openSearchLayout()
	{
		searchContactLayout.setVisibility(View.VISIBLE);
	}
	
	public void showInputDialog(String name,String number,String photoUri)
	{
		final Dialog dialog = new Dialog(getActivity());
		try {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		} catch (Exception e) {
			Log.e("inputDialog", e.toString());
		}
		dialog.setContentView(R.layout.grid_view_alert_dialog);
		
		TextView gridAlertName = (TextView)dialog.findViewById(R.id.grid_alert_display_name);
		TextView gridAlertCall = (TextView)dialog.findViewById(R.id.grid_alert_call);
		TextView gridAlertMessage = (TextView)dialog.findViewById(R.id.grid_alert_message);
		ImageView gridAlertImage = (ImageView)dialog.findViewById(R.id.grid_alert_contact_image);
		Button gridAlertCancel = (Button)dialog.findViewById(R.id.grid_alert_cancel);
		
		gridAlertName.setText(name);
		if (!TextUtils.isEmpty(photoUri)) {
			gridAlertImage.setImageURI(Uri.parse(photoUri));
		}
		
		if (gridAlertImage.getDrawable() == null)
			gridAlertImage.setImageResource(R.drawable.def_contact);
		
		gridAlertCall.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		gridAlertMessage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		gridAlertCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		
		
		dialog.show();
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
//                    Thread.sleep(4000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void result) {
//                super.onPostExecute(result);
//
//                // Notify PullToRefreshLayout that the refresh has finished
//                mPullToRefreshLayout.setRefreshComplete();
//            }
//        }.execute();
//	}
	
//	VIEW ADAPTER
/*
 * All Contacts Adapter
 */
	// Contact adapter
	public class ContactAdapter extends ArrayAdapter<Contact> implements SectionIndexer {

		private ArrayList<Contact> contactList;
		private ArrayList<Contact> originalList;
		private ContactFilter filter;

		public ContactAdapter(Context context, int textViewResourceId, ArrayList<Contact> items) {
			super(context, textViewResourceId, items);

			this.contactList = new ArrayList<Contact>();
			this.originalList = new ArrayList<Contact>();

			this.contactList.addAll(items);
			this.originalList.addAll(items);

			// indexing
			alphaIndexer = new HashMap<String, Integer>();
			int size = contactList.size();

			for (int x = 0; x < size; x++) {
				String s = contactList.get(x).getContactName();

				if(s!=null && !TextUtils.isEmpty(s))
				{
					// get the first letter of the store
					String ch = s.substring(0, 1);
					// convert to uppercase otherwise lowercase a -z will be sorted
					// after upper A-Z
					ch = ch.toUpperCase();

					// HashMap will prevent duplicates
					alphaIndexer.put(ch, x);
				}
			}

			Set<String> sectionLetters = alphaIndexer.keySet();

			// create a list from the set to sort
			ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
			Collections.sort(sectionList);
			sections = new String[sectionList.size()];
			sectionList.toArray(sections);
		}

		@Override
		public Filter getFilter() {
			if (filter == null) {
				filter = new ContactFilter();
			}
			return filter;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;

			if (view == null) {
				LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.grd_item_contact, null);
			}
			final Contact contact = contactList.get(position);
			if (contact != null) {
				TextView displayName = (TextView) view.findViewById(R.id.grid_item_display_name);
				ImageView displayPicture = (ImageView) view.findViewById(R.id.grid_item_display_picture);
				TextView displayZname = (TextView) view.findViewById(R.id.grid_item_zname);

				displayPicture.setImageURI(contact.getContactPhotoUri());

				if (displayPicture.getDrawable() == null)
					displayPicture.setImageResource(R.drawable.def_contact);

				displayName.setText(contact.getContactName());
				displayZname.setText(contact.getContactNumber());
				view.setTag(R.id.TAG_CONTACT_NUMBER, contact.getContactNumber());
				view.setTag(R.id.TAG_CONTACT_DP, contact.getContactPhotoUri());
				view.setTag(R.id.TAG_CONTACT_NAME, contact.getContactName());
			}
			return view;
		}

		@Override
		public int getPositionForSection(int section) {
			try {
				if (section > sections.length - 1) {
			        return 0;
			    } else {
			        return alphaIndexer.get(sections[section]);
			    }
			} catch (Exception e) {
				Log.e(TAG,e.toString());
				return 0;
			}
		}

		@Override
		public int getSectionForPosition(int position) {
			return 0;
		}

		@Override
		public Object[] getSections() {
			return sections;
		}

		// Contacts filter
		private class ContactFilter extends Filter {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {

				constraint = constraint.toString().toLowerCase();
				FilterResults result = new FilterResults();
				if (constraint != null && constraint.toString().length() > 0) {
					ArrayList<Contact> filteredItems = new ArrayList<Contact>();

					for (int i = 0, l = originalList.size(); i < l; i++) {
						Contact contact = originalList.get(i);
						if (contact.toString().toLowerCase().contains(constraint))
							filteredItems.add(contact);
					}
					result.count = filteredItems.size();
					result.values = filteredItems;
				} else {
					synchronized (this) {
						result.values = originalList;
						result.count = originalList.size();
					}
				}
				return result;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {

				contactList = (ArrayList<Contact>) results.values;
				notifyDataSetChanged();
				clear();
				for (int i = 0, l = contactList.size(); i < l; i++)
					add(contactList.get(i));
				notifyDataSetInvalidated();
			}
		}
	}
	
	/*
	 * DEPRECEATED MOVES INTO IMPORT CONTACT SERVICES
	 */
	
	//////////////////////////////////////////////////////////////////////////
	//IMPORT CONTACTS ASYNC TASK
	/////////////////////////////////////////////////////////////////////////
//	public class ImportFragmentContactsTask extends AsyncTask<Void, Void, String>
//	{
//		  private Context context;
//		  private ProgressDialog progressDialog;
//		  private LinkedHashMap<String, Contact> allContacts = new LinkedHashMap<String, Contact>();
//		  private  ArrayList<Contact> contacts = new ArrayList<Contact>();
//		  private static final String TAG ="ImportContactsTask";
//
//		  public ImportFragmentContactsTask(Context context)
//		  {
//		    this.context = context;
//		  }
//		  protected void onPreExecute()
//		  {
//		    super.onPreExecute();
//		    {
//					try {
//						progressDialog = new ProgressDialog(context);
//						progressDialog.setMessage("Refreshing Contacts");
//						progressDialog.setCancelable(false);
//						progressDialog.show();
//					} catch (Exception e) {
//						Log.e(TAG, e.toString());
//					}
//		    }
//		  }
//		  @Override
//			protected String doInBackground(Void... params) {
//				// TODO Auto-generated method stub
//			  String n = null;
//			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
//				getContactsNewApi();
//			} else {
//				getContactsOldApi();
//			}
//			return n;
//			}
//		  
//		  protected void onPostExecute(String result)
//		  {
//		    super.onPostExecute(result);
//				try {
//					progressDialog.dismiss();
//					refreshContactsData();
//				} catch (Exception e) {
//					Log.e(TAG, e.toString());
//				}
//		  }
//		  
//		  /**
//			 * @see Get Contacts for 3.0+
//			 * 
//			 */
//		private void getContactsNewApi() {
//			ContentResolver cr = Zname.getApplication().getContentResolver();
//
//			String selection = Data.HAS_PHONE_NUMBER + " > '" + ("0") + "'";
//
//			Cursor cur = cr.query(Data.CONTENT_URI, new String[] { Data.CONTACT_ID,
//					Data.MIMETYPE,
//					Contacts.DISPLAY_NAME, Phone.NUMBER }, selection, null,
//					Contacts.DISPLAY_NAME);
//
//			Contact contact;
//			if (cur.getCount() > 0) {
//				while (cur.moveToNext()) {
//					String id = cur.getString(cur.getColumnIndex(Data.CONTACT_ID));
//					String mimeType = cur.getString(cur
//							.getColumnIndex(Data.MIMETYPE));
//
//					if (allContacts.containsKey(id)) {
//						// update contact
//						contact = allContacts.get(id);
//					} else {
//						contact = new Contact();
//						allContacts.put(id, contact);
//						//set contactId
//						contact.setContactId(id);
//						// set photoUri
//						contact.setContactPhotoUri(getContactPhotoUri(Long
//								.parseLong(id)));
//					}
//
//					if (mimeType.equals(StructuredName.CONTENT_ITEM_TYPE)) {
//						// set name
//						contact.setContactName(cur.getString(cur.getColumnIndex(Contacts.DISPLAY_NAME)));
//					}
//
//					if (mimeType.equals(Phone.CONTENT_ITEM_TYPE)) {
//						// set phone number
//						contact.setContactNumber(cur.getString(cur.getColumnIndex(Phone.NUMBER)).replaceAll("\\D+",""));
//					}
//				}
//			}
//
//			cur.close();
//			// get contacts from hashmap
//			contacts.clear();
//			contacts.addAll(allContacts.values());
//
//			// remove self contact
//			for (Contact _contact : contacts) {
//				if (_contact.getContactName() == null
//						&& _contact.getContactNumber() == null) {
//					contacts.remove(_contact);
//					break;
//				}
//			}
//
//			Zname.getApplication()
//					.getContentResolver()
//					.delete(DBConstant.All_Contacts_Columns.CONTENT_URI, null, null);
//
//			ContentValues values;
//			for (Contact _contact : contacts) {
//				values = new ContentValues();
//				values.put(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_ID, _contact.getContactId());
//				values.put(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_NUMBER,
//						_contact.getContactNumber());
//				values.put(DBConstant.All_Contacts_Columns.COLUMN_DISPLAY_NAME,
//						_contact.getContactName());
//				values.put(DBConstant.All_Contacts_Columns.COLUMN_CALL_STATUS,
//						_contact.getContactPhotoUri().toString());
//				Zname.getApplication()
//						.getContentResolver()
//						.insert(DBConstant.All_Contacts_Columns.CONTENT_URI, values);
//			}
//		}
//
//			/**
//			 * @see Get Contacts for 2.2+
//			 * DATA.HAS_PHONE_NUMBER was not added in < 3.0
//			 */
//		private void getContactsOldApi() {
//
//			Uri uri = ContactsContract.Contacts.CONTENT_URI;
//			String[] projection = new String[] { ContactsContract.Contacts._ID,
//					ContactsContract.Contacts.DISPLAY_NAME };
//			String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " > '"
//					+ ("0") + "'";
//			String[] selectionArgs = null;
//			String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
//					+ " COLLATE LOCALIZED ASC";
//
//			ContentResolver contectResolver = Zname.getApplication()
//					.getContentResolver();
//
//			Cursor cursor = contectResolver.query(uri, projection, selection,
//					selectionArgs, sortOrder);
//			Contact contact;
//			// Load contacts one by one
//			if (cursor.moveToFirst()) {
//				while (!cursor.isAfterLast()) {
//					contact = new Contact();
//					String id = cursor.getString(cursor
//							.getColumnIndex(ContactsContract.Contacts._ID));
//
//					contact.setContactPhotoUri(getContactPhotoUri(Long
//							.parseLong(id)));
//
//					String[] phoneProj = new String[] {
//							ContactsContract.CommonDataKinds.Phone.NUMBER,
//							ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };
//					Cursor cursorPhone = contectResolver.query(
//							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//							phoneProj,
//							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
//									+ " = ?", new String[] { id }, null);
//					if (cursorPhone.moveToFirst()) {
//						contact.setContactNumber(cursorPhone
//								.getString(
//										cursorPhone
//												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
//								.replaceAll("\\D+",""));
//					}
//					cursorPhone.close();
//
//					contact.setContactName(cursor.getString(cursor
//							.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
//
//					allContacts.put(id, contact);
//					cursor.moveToNext();
//				}
//			}
//			cursor.close();
//			// get contacts from hashmap
//			contacts.clear();
//			contacts.addAll(allContacts.values());
//
//			// remove self contact
//			for (Contact _contact : contacts) {
//
//				if (_contact.getContactName() == null
//						&& _contact.getContactNumber() == null) {
//					contacts.remove(_contact);
//					break;
//				}
//			}
//			
//			Zname.getApplication()
//					.getContentResolver()
//					.delete(DBConstant.All_Contacts_Columns.CONTENT_URI, null, null);
//
//			ContentValues values;
//			for (Contact _contact : contacts) {
//				values = new ContentValues();
//				values.put(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_ID,
//						_contact.getContactId());
//				values.put(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_NUMBER,
//						_contact.getContactNumber());
//				values.put(DBConstant.All_Contacts_Columns.COLUMN_DISPLAY_NAME,
//						_contact.getContactName());
//				values.put(DBConstant.All_Contacts_Columns.COLUMN_CALL_STATUS,
//						_contact.getContactPhotoUri().toString());
//				Zname.getApplication()
//						.getContentResolver()
//						.insert(DBConstant.All_Contacts_Columns.CONTENT_URI, values);
//			}
//		}
//			// Get contact photo URI for contactId
//			/**
//			 * @param contactId
//			 * @return photoUri
//			 */
//			public Uri getContactPhotoUri(long contactId) {
//				Uri photoUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
//				photoUri = Uri.withAppendedPath(photoUri, Contacts.Photo.CONTENT_DIRECTORY);
//				return photoUri;
//			}
//		}
}
