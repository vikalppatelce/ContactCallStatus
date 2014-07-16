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
 * ZM003      VIKALP PATEL     13/06/2014        HIGHLIGHT      HIGHLIGHTING SEARCH TEXT
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.netdoers.zname.AppConstants;
import com.netdoers.zname.R;
import com.netdoers.zname.beans.Contact;
import com.netdoers.zname.sqlite.DBConstant;
import com.netdoers.zname.utils.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * @author Vikalp Patel(vikalppatelce@yahoo.com)
 *
 */
public class SearchActivity extends FragmentActivity {

	//View reference variable
//	GridView contactsGridView; SU ZM002
	ListView contactsListView;//EU ZM002
	ImageView searchClose,searchBack;	
	EditText searchField;
	ImageLoader imageLoader;
	DisplayImageOptions options;
	
	
	//TYPEFACE
	static Typeface styleFont;

	//Android helping reference variable
	private ContactAdapter contactAdapter = null;
	
	//Helping reference variable
	private ArrayList<Contact> contacts = null;
	private LinkedHashMap<String, Contact> allContacts = new LinkedHashMap<String, Contact>();
	
	//Indexing for the list
	HashMap<String, Integer> alphaIndexer;
	String[] sections;
	
	//CONSTANTS
	public static final String TAG = SearchActivity.class.getSimpleName();
	public static final String SEARCH_TYPE = "searchType";
	public static final String GROUP_TYPE = "groupType";
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search);
		
		styleFont = Typeface.createFromAsset(getAssets(), AppConstants.fontStyle);
		
		imageLoader = ImageLoader.getInstance();
        // Initialize ImageLoader with configuration. Do it once.
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        
        options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.def_contact) // resource or drawable
        .showImageForEmptyUri(R.drawable.def_contact) // resource or drawable
        .showImageOnFail(R.drawable.def_contact) //this is the image that will be displayed if download fails
        .cacheInMemory()
        .cacheOnDisc()
        .build();
        
//		contactsGridView = (GridView)findViewById(R.id.gridview_all_contacts); SU ZM002
		contactsListView = (ListView)findViewById(R.id.listview_all_contacts); //EU ZM002
		searchClose = (ImageView)findViewById(R.id.search_clear);
		searchField = (EditText)findViewById(R.id.search_txt);
		searchBack = (ImageView)findViewById(R.id.search_back);

		contacts = new ArrayList<Contact>();
		
		
		refreshContactsData();	

		
//		contactsGridView.setOnItemLongClickListener(new OnItemLongClickListener() { SU ZM002
		contactsListView.setOnItemLongClickListener(new OnItemLongClickListener() { //EU ZM002

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
			
				// vibration for 100 milliseconds
				((Vibrator)getApplication().getApplicationContext().getSystemService(VIBRATOR_SERVICE)).vibrate(50);
				
				String viewTagNumber = view.getTag(R.id.TAG_CONTACT_NUMBER).toString();
				String viewTagDp = view.getTag(R.id.TAG_CONTACT_DP).toString();
				String viewTagName =  view.getTag(R.id.TAG_CONTACT_NAME).toString();
				
				showInputDialog(viewTagName,viewTagNumber,viewTagDp);
				return false;
			}
		});
		
		contactsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String viewTagId = view.getTag(R.id.TAG_CONTACT_ID).toString();
				String viewTagName = view.getTag(R.id.TAG_CONTACT_NAME).toString();
				String viewTagPhoto	= view.getTag(R.id.TAG_CONTACT_PHOTO_ID).toString();
				String viewTagNumber	= view.getTag(R.id.TAG_CONTACT_NUMBER).toString();
				String viewTagZname = view.getTag(R.id.TAG_CONTACT_ZNAME).toString(); 
				
				Intent profileIntent;
				if(viewTagZname.equalsIgnoreCase("Zname")){
					profileIntent = new Intent(SearchActivity.this, ProfileContactActivity.class);	
				}else{
					profileIntent = new Intent(SearchActivity.this, ProfileUserActivity.class);
					profileIntent.putExtra(AppConstants.TAGS.INTENT.TAG_CALL_STATUS, view.getTag(R.id.TAG_CONTACT_CALL_STATUS).toString());
					profileIntent.putExtra(AppConstants.TAGS.INTENT.TAG_ZNAME, view.getTag(R.id.TAG_CONTACT_ZNAME).toString());
				}
				
				profileIntent.putExtra(AppConstants.TAGS.INTENT.TAG_ID, viewTagId);
				profileIntent.putExtra(AppConstants.TAGS.INTENT.TAG_NAME, viewTagName);
				profileIntent.putExtra(AppConstants.TAGS.INTENT.TAG_PHOTO, viewTagPhoto);
				profileIntent.putExtra(AppConstants.TAGS.INTENT.TAG_NUMBER, viewTagNumber);
				profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				
				startActivity(profileIntent);
			}
		});
		
		searchField.requestFocus();
		searchField.setTypeface(styleFont);
		
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
	}
// LISTENERS
	
	public void onSearchClear(View v)
	{
		if(TextUtils.isEmpty(searchField.getText().toString())){
			finish();
		}else{
			searchField.setText("");	
		}
	}
	
	public void onSearchBack(View v)
	{
		finish();
	}
//	ADD CONTACTS TO CONTACTS ADAPTER
	
	public void refreshContactsData()
	{
		Cursor cr = getContentResolver().query(DBConstant.All_Contacts_Columns.CONTENT_URI, null, null, null, null);
		if(cr.getCount() > 0)
		{
			int intColumnId = cr.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_ID);
			int intColumnContactNumber = cr.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_NUMBER);
			int intColumnDisplayName = cr.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_DISPLAY_NAME);
			int intColumnZname = cr.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_ZNAME);
			int intColumnZnumber = cr.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_ZNAME_NUMBER);
			int intColumnZnameDp = cr.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_ZNAME_DP_URL_SMALL);
			int intColumnCallStatus = cr.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_CALL_STATUS);
			Contact c;
//			cr.moveToFirst();
			while(cr.moveToNext())
			{
				c = new Contact();
				c.setContactId(cr.getString(intColumnId));
				c.setContactNumber(
						TextUtils.isEmpty(cr.getString(intColumnZnumber))
						?cr.getString(intColumnContactNumber)
						:cr.getString(intColumnZnumber));
				c.setContactZname(cr.getString(intColumnZname));
				c.setContactName(cr.getString(intColumnDisplayName));
				c.setContactPhotoUri(Uri.parse(cr.getString(intColumnZnameDp)));
				c.setContactCallStatus(cr.getString(intColumnCallStatus));
				contacts.add(c);
			}
		}
		if(cr!=null){
			cr.close();
		}
		
//		contactAdapter = new ContactAdapter(SearchActivity.this, R.id.gridview_all_contacts, contacts); SU ZM002
		contactAdapter = new ContactAdapter(SearchActivity.this, R.id.listview_all_contacts, contacts);
//		contactsGridView.setAdapter(contactAdapter);
		contactsListView.setAdapter(contactAdapter); //EU ZM002
	}
	
	/*public void refreshGroupContactsData(int groupType)
	{
		Cursor cr;
		switch(groupType){
		case 0:
			cr = getContentResolver().query(DBConstant.Friends_Contacts_Columns.CONTENT_URI, null, null, null, DBConstant.Friends_Contacts_Columns.COLUMN_DISPLAY_NAME + " ASC");
			searchField.setHint("Search Friends");
			break;
		case 1:
			cr = getContentResolver().query(DBConstant.Family_Contacts_Columns.CONTENT_URI, null, null, null, DBConstant.Family_Contacts_Columns.COLUMN_DISPLAY_NAME + " ASC");
			searchField.setHint("Search Family");
			break;
		case 2:
			cr = getContentResolver().query(DBConstant.Work_Contacts_Columns.CONTENT_URI, null, null, null, DBConstant.Work_Contacts_Columns.COLUMN_DISPLAY_NAME + " ASC");
			searchField.setHint("Search Work");
			break;
		default:
			cr = getContentResolver().query(DBConstant.Friends_Contacts_Columns.CONTENT_URI, null, null, null, DBConstant.Friends_Contacts_Columns.COLUMN_DISPLAY_NAME + " ASC");
			break;
		}
		
		Cursor crAll = getContentResolver().query(DBConstant.All_Contacts_Columns.CONTENT_URI, null, null, null, null);
		Cursor cursor = null;
		if(cr.getCount() > 0)
		{
			contacts.clear();
			int intColumnId = 0;
			switch(groupType){
			case 0:
				intColumnId = cr.getColumnIndex(DBConstant.Friends_Contacts_Columns.COLUMN_CONTACT_ID);
				break;
			case 1:
				intColumnId = cr.getColumnIndex(DBConstant.Family_Contacts_Columns.COLUMN_CONTACT_ID);
				break;
			case 2:
				intColumnId = cr.getColumnIndex(DBConstant.Work_Contacts_Columns.COLUMN_CONTACT_ID);
				break;
			case 3:
				intColumnId = cr.getColumnIndex(DBConstant.Friends_Contacts_Columns.COLUMN_CONTACT_ID);
				break;
			default:
				break;
			}
			
			int intColumnContactNumber = crAll.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_NUMBER);
			int intColumnDisplayName = crAll.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_DISPLAY_NAME);
			int intColumnZname = crAll.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_ZNAME);
			int intColumnZnumber = crAll.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_ZNAME_NUMBER);
			int intColumnZnameDp = crAll.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_ZNAME_DP_URL_SMALL);
			if(crAll!=null){
				crAll.close();	
			}
			
			Contact c;
//			cr.moveToFirst();
			while(cr.moveToNext())
			{
				c = new Contact();
				c.setContactId(cr.getString(intColumnId));
				
				cursor = getContentResolver().query(DBConstant.All_Contacts_Columns.CONTENT_URI, null, DBConstant.All_Contacts_Columns.COLUMN_CONTACT_ID+"=?", new String[]{cr.getString(intColumnId)}, null);
				if(cursor.getCount() > 0)
				{
					cursor.moveToFirst();
					
					c.setContactNumber(
							TextUtils.isEmpty(cursor.getString(intColumnZnumber))
							?cursor.getString(intColumnContactNumber)
							:cursor.getString(intColumnZnumber));
					c.setContactZname(cursor.getString(intColumnZname));
					c.setContactName(cursor.getString(intColumnDisplayName));
					c.setContactPhotoUri(Uri.parse(cursor.getString(intColumnZnameDp)));
					contacts.add(c);
				}
				if(cursor!=null){
					cursor.close();
				}
			}
		}
		if(cr!=null){
			cr.close();
		}
		if(cursor!=null){
			cursor.close();
		}
		
		if(contacts.size() > 0){
//			contactAdapter = new ContactAdapter(getActivity(), R.id.gridview_friends, contacts); SU ZM004
//			contactsGridView.setAdapter(contactAdapter);	
			contactAdapter = new ContactAdapter(SearchActivity.this, R.id.listview_friends, contacts);
			contactsListView.setAdapter(contactAdapter); //EU ZM004
		}
	}*/
	
//	View Listeners
	
	public void showInputDialog(String name,String number,String photoUri)
	{
		final Dialog dialog = new Dialog(SearchActivity.this);
		try {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		} catch (Exception e) {
			Log.e("inputDialog", e.toString());
		}
		dialog.setContentView(R.layout.dialog_alert);
		
		TextView gridAlertName = (TextView)dialog.findViewById(R.id.grid_alert_display_name);
		TextView gridAlertCall = (TextView)dialog.findViewById(R.id.grid_alert_call);
		TextView gridAlertMessage = (TextView)dialog.findViewById(R.id.grid_alert_message);
		ImageView gridAlertImage = (ImageView)dialog.findViewById(R.id.grid_alert_contact_image);
		Button gridAlertCancel = (Button)dialog.findViewById(R.id.grid_alert_cancel);
		
		gridAlertMessage.setTypeface(styleFont);
		gridAlertCall.setTypeface(styleFont);
		gridAlertName.setTypeface(styleFont);
		gridAlertCancel.setTypeface(styleFont);
		
		final String contactNumber = number;
		
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
				Intent callIntent = new Intent(Intent.ACTION_DIAL);
				callIntent.setData(Uri.parse("tel:"+Uri.encode(contactNumber)));
				startActivity(callIntent);
			}
		});
		
		gridAlertMessage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"+Uri.encode(contactNumber)));
	            startActivity(smsIntent);				
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
//	VIEW ADAPTER
/*
 * All Contacts Adapter
 */
	// Contact adapter
	public class ContactAdapter extends ArrayAdapter<Contact> implements SectionIndexer {

		private ArrayList<Contact> contactList;
		private ArrayList<Contact> originalList;
		private ContactFilter filter;
		private Context context;

		public ContactAdapter(Context context, int textViewResourceId, ArrayList<Contact> items) {
			super(context, textViewResourceId, items);

			this.contactList = new ArrayList<Contact>();
			this.originalList = new ArrayList<Contact>();
			this.context = context;
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
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				view = vi.inflate(R.layout.grd_item_contact, null); SU ZM002
				view = vi.inflate(R.layout.item_list_contact, null); //EU ZM002
			}
			final Contact contact = contactList.get(position);
			if (contact != null) {
//				 SU ZM002
//				TextView displayName = (TextView) view.findViewById(R.id.grid_item_display_name);
//				ImageView displayPicture = (ImageView) view.findViewById(R.id.grid_item_display_picture);
//				TextView displayZname = (TextView) view.findViewById(R.id.grid_item_zname);
//				ImageView imgCall = (ImageView) view.findViewById(R.id.grid_item_call);
//				ImageView imgMsg = (ImageView) view.findViewById(R.id.grid_item_message);

				TextView displayName = (TextView) view.findViewById(R.id.list_item_display_name);
				final ImageView displayPicture = (ImageView) view.findViewById(R.id.list_item_display_picture);
				TextView displayZname = (TextView) view.findViewById(R.id.list_item_zname);
				ImageView imgCall = (ImageView) view.findViewById(R.id.list_item_call);
				ImageView imgMsg = (ImageView) view.findViewById(R.id.list_item_message);
//				EU ZM002
				
				if(contact.getContactPhotoUri().toString().contains("http")){
					((Activity) context).runOnUiThread(new Runnable() {

			            @Override
			            public void run() {
			                // TODO Auto-generated method stub
			                imageLoader.displayImage(contact.getContactPhotoUri().toString(), displayPicture, options);
			            }
			        });
				}else{
					displayPicture.setImageURI(contact.getContactPhotoUri());
					if (displayPicture.getDrawable() == null)
						displayPicture.setImageResource(R.drawable.def_contact);
				}

//				SU ZM003				
//				displayName.setText(contact.getContactName());
				if(!TextUtils.isEmpty(searchField.getText().toString())){
					displayName.setText(Utilities.highlight(searchField.getText().toString(), contact.getContactName()));	
				}else{
					displayName.setText(contact.getContactName());
				}
//				EU ZM003
				
				/*displayZname.setText(
						TextUtils.isEmpty(contact.getContactZname())
						?contact.getContactNumber().contains(",")
								?contact.getContactNumber().toString().substring(0, contact.getContactNumber().toString().indexOf(",")).replace("\"", "")
										:contact.getContactNumber().replace("\"", "")
						:contact.getContactZname()				
						);*/
				
				if(!TextUtils.isEmpty(contact.getContactZname())){
					displayZname.setVisibility(View.VISIBLE);
					displayZname.setText(contact.getContactZname());
				}else{
					displayZname.setVisibility(View.GONE);
				}
				
				imgCall.setImageResource(Integer.parseInt(contact.getContactCallStatus()) == 1
						?  R.drawable.zname_ic_call_selected_busy
						: Integer.parseInt(contact.getContactCallStatus()) == 0
						    ? R.drawable.zname_ic_call_selected_avail
						    : R.drawable.zname_ic_call_selected	   
						);
				
				if (imgCall.getDrawable() == null)
					imgCall.setImageResource(R.drawable.zname_ic_call_selected);
				
				displayName.setTypeface(styleFont);
				displayZname.setTypeface(styleFont);
				
				view.setTag(R.id.TAG_CONTACT_NUMBER, contact.getContactNumber().replace("\"", ""));
				view.setTag(R.id.TAG_CONTACT_DP, contact.getContactPhotoUri());
				view.setTag(R.id.TAG_CONTACT_NAME, contact.getContactName());
				view.setTag(R.id.TAG_CONTACT_ID, contact.getContactId());
				view.setTag(R.id.TAG_CONTACT_PHOTO_ID, contact.getContactPhotoUri());
				view.setTag(R.id.TAG_CONTACT_CALL_STATUS, contact.getContactCallStatus());
				view.setTag(R.id.TAG_CONTACT_ZNAME, TextUtils.isEmpty(contact.getContactZname())
						? "Zname"
						: contact.getContactZname());
				
				imgCall.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent callIntent = new Intent(Intent.ACTION_DIAL);
				          callIntent.setData(Uri.parse("tel:"+Uri.encode(contact.getContactNumber().replace("\"", ""))));
				          startActivity(callIntent);
					}
				});
				
				imgMsg.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"+Uri.encode(contact.getContactNumber().replace("\"", ""))));
			            startActivity(smsIntent);
					}
				});
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
						if (contact.toString().toLowerCase().contains(constraint)){
//							contact.setContactName(highlight(constraint.toString(), contact.getContactName()).toString());
							filteredItems.add(contact);
						}
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
}
