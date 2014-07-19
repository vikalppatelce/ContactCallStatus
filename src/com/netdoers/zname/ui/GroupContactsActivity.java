/* HISTORY
 * CATEGORY			 :- FRIENDS GROUP FRAGMENT
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- FRIENDS CONTACT FRAGMENTS ATTACHED TO MOTHERACTIVITY USING VIEWPAGER + TABS
 * NOTE:			 BASE FRAGMENT FOR GROUPS 
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * ZM001      VIKALP PATEL     16/05/2014                       CREATED
 * ZM002      VIKALP PATEL     30/05/2014                       SUPPRESSED FRAGMENT WISE ACTION BAR MENU
 * ZM003      VIKALP PATEL     30/05/2014                       MOVE SEARCH INTO SEARCH ACTIVITY
 * ZM004      VIKALP PATEL     09/06/2014                       MIGRATION : GRIDVIEW INTO LISTVIEW
 * ZM005      VIKALP PATEL     20/06/2014                       HIDE ADD TO GROUP ON SCROLL
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.netdoers.zname.AppConstants;
import com.netdoers.zname.R;
import com.netdoers.zname.beans.Contact;
import com.netdoers.zname.beans.ContactPicker;
import com.netdoers.zname.contactpicker.ContactPickerManager;
import com.netdoers.zname.sqlite.DBConstant;
import com.netdoers.zname.utils.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * @author Vikalp Patel(vikalppatelce@yahoo.com)
 *
 */
public class GroupContactsActivity extends FragmentActivity {
	
	//DECLARE VARIABLES
	private ListView mListView;
	private Button mAddContact;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private EditText mSearchTxt;
	private TextView mTitleTxt;
	private LinearLayout mActionBarLayout, mSearchLayout;
	private ImageView mSearchImg, mAddContactImg, mSearchClose, mActionBarBack, mSearchBack;
	
	//TYPEFACE
	static Typeface styleFont;
	
    //ADAPTER
	private ContactAdapter mAdapter = null;
	
	//REFERENCES VARIABLE - HELPER
	private ArrayList<Contact> contacts = null;
	private LinkedHashMap<String, Contact> allContacts = new LinkedHashMap<String, Contact>();
	private int mLastFirstVisibleItem; 
	private boolean mIsScrollingUp;	 
	
	//INTENT VARIABLES
	private String mIntentGroupID;
	private String mIntentGroupName;
	private String mIntentGroupDp;
	
	//INDEXING FOR THE LIST
	HashMap<String, Integer> alphaIndexer;
	String[] sections;

	//CONSTANTS
	public static final String TAG = GroupContactsActivity.class.getSimpleName();
	public static final int ADD_CONTACT = 10001;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_group_contacts);
		
		initUi();
		setUniversalImageLoader();
		setFontStyle();
		
		contacts = new ArrayList<Contact>();
		
		mIntentGroupID = getIntent().getStringExtra(AppConstants.TAGS.INTENT.TAG_ID);
		mIntentGroupName = getIntent().getStringExtra(AppConstants.TAGS.INTENT.TAG_NAME);
		mIntentGroupDp = getIntent().getStringExtra(AppConstants.TAGS.INTENT.TAG_PHOTO);
		
		mTitleTxt.setText(mIntentGroupName);
		
        setEventListeners();	
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
			refreshContactsData();
	}

	@Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }
	
	private void setUniversalImageLoader(){
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
	}
	
	private void initUi(){
		mListView = (ListView)findViewById(R.id.listview_friends); //EU ZM004
		mAddContact = (Button)findViewById(R.id.friends_btn_add);
		mTitleTxt = (TextView)findViewById(R.id.group_contacts_actionbar_title);
		mSearchTxt = (EditText)findViewById(R.id.group_contacts_searchlayout_txt);
		mActionBarLayout = (LinearLayout)findViewById(R.id.group_contacts_actionbar);
		mSearchLayout = (LinearLayout)findViewById(R.id.group_contacts_searchlayout);
		
		mSearchBack = (ImageView)findViewById(R.id.group_contacts_searchlayout_back);
		mActionBarBack = (ImageView)findViewById(R.id.group_contacts_actionbar_back);
		mSearchClose = (ImageView)findViewById(R.id.group_contacts_searchlayout_clear);
		mSearchImg = (ImageView)findViewById(R.id.group_contacts_actionbar_search);
		mAddContactImg = (ImageView)findViewById(R.id.group_contacts_actionbar_add);
	}
	
	private void setFontStyle(){
		styleFont = Typeface.createFromAsset(getAssets(), AppConstants.fontStyle);
		mTitleTxt.setTypeface(styleFont);
		mSearchTxt.setTypeface(styleFont);
	}
	
	private void setEventListeners(){
		mSearchImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onSearchClick();
			}
		});
		
		mSearchBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			onSearchBack2();	
			}
		});
		
		mSearchClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onSearchClear(v);
			}
		});
		
		mActionBarBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onSearchBack1();
			}
		});
		
		mAddContactImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openAddContactsLayout();
			}
		});
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {// EU ZM004
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

		mListView.setOnItemClickListener(new OnItemClickListener() {

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
					profileIntent = new Intent(GroupContactsActivity.this, ProfileContactActivity.class);	
				}else{
					profileIntent = new Intent(GroupContactsActivity.this, ProfileUserActivity.class);
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

	mAddContact.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			openAddContactsLayout();
		}
	});
	
	mSearchTxt.addTextChangedListener(new TextWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		// call the filter with the current text on the editbox
			if(mAdapter!=null)
				mAdapter.getFilter().filter(s.toString());
			}
		});
	}
	
	public void onSearchClick(){
		mActionBarLayout.setVisibility(View.GONE);
		mSearchLayout.setVisibility(View.VISIBLE);
		mSearchTxt.requestFocus();
	}
	
	public void openAddContactsLayout()
	{
		Intent mAddContacts = new Intent(this, ContactPickerManager.class);
		startActivityForResult(mAddContacts, ADD_CONTACT);
	}
	
	public void onSearchBack1(){
		finish();
	}

	public void onSearchBack2(){
		finish();
	}
	
	public void onSearchClear(View v)
	{
		if(TextUtils.isEmpty(mSearchTxt.getText().toString())){
			mSearchLayout.setVisibility(View.GONE);
			mActionBarLayout.setVisibility(View.VISIBLE);
		}else{
			mSearchTxt.setText("");	
		}
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
//	ALERT DIALOG
	public void showInputDialog(String name,String number,String photoUri)
	{
		final Dialog dialog = new Dialog(this);
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
		
		gridAlertName.setText(name);
		
		gridAlertMessage.setTypeface(styleFont);
		gridAlertCall.setTypeface(styleFont);
		gridAlertName.setTypeface(styleFont);
		gridAlertCancel.setTypeface(styleFont);
		
		if (!TextUtils.isEmpty(photoUri)) {
			gridAlertImage.setImageURI(Uri.parse(photoUri));
		}
		
		final String contactNumber = number; 
		
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
	
//	ADD CONTACTS TO CONTACTS ADAPTER
	
	public void refreshContactsData()
	{
		Cursor cr = getContentResolver().query(DBConstant.Groups_Contacts_Columns.CONTENT_URI, null, DBConstant.Groups_Contacts_Columns.COLUMN_GROUP_ID +"=?", new String[]{mIntentGroupID}, DBConstant.Groups_Contacts_Columns.COLUMN_NAME + " ASC");
		Cursor crAll = getContentResolver().query(DBConstant.All_Contacts_Columns.CONTENT_URI, null, null, null, null);
		Cursor cursor = null;
		if(cr.getCount() > 0)
		{
			contacts.clear();
			
			int intColumnId = cr.getColumnIndex(DBConstant.Groups_Contacts_Columns.COLUMN_CONTACT_ID);
			int intColumnContactNumber = crAll.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_NUMBER);
			int intColumnDisplayName = crAll.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_DISPLAY_NAME);
			int intColumnZname = crAll.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_ZNAME);
			int intColumnZnumber = crAll.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_ZNAME_NUMBER);
			int intColumnZnameDp = crAll.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_ZNAME_DP_URL_SMALL);
			int intColumnCallStatus = crAll.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_CALL_STATUS);
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
					c.setContactCallStatus(cursor.getString(intColumnCallStatus));
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
//			mAdapter = new ContactAdapter(getActivity(), R.id.gridview_friends, contacts); SU ZM004
//			contactsGridView.setAdapter(mAdapter);	
			mAdapter = new ContactAdapter(this, R.id.listview_friends, contacts);
			mListView.setAdapter(mAdapter); //EU ZM004
		}
	}
	
// ON ACTIVITY RESULT
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ADD_CONTACT) {
			if (resultCode == Activity.RESULT_OK) {
				if (data.hasExtra(ContactPicker.CONTACTS_DATA)) {
					ArrayList<ContactPicker> contacts = data.getParcelableArrayListExtra(ContactPicker.CONTACTS_DATA);
					if(contacts != null) {
						Iterator<ContactPicker> iterContacts = contacts.iterator();
						while (iterContacts.hasNext()) {
								final ContactPicker contact = iterContacts.next();
								ContentValues values = new ContentValues();
								try {
								Cursor cr = getContentResolver().query(DBConstant.Groups_Contacts_Columns.CONTENT_URI, null, DBConstant.Groups_Contacts_Columns.COLUMN_GROUP_ID+"=?" + " AND " + DBConstant.Groups_Contacts_Columns.COLUMN_CONTACT_ID+ "=?", new String[]{mIntentGroupID,contact.getContactId().toString()}, null);
								if(cr.getCount() == 0){
									values.put(DBConstant.Groups_Contacts_Columns.COLUMN_GROUP_ID, mIntentGroupID);
									values.put(DBConstant.Groups_Contacts_Columns.COLUMN_CONTACT_ID, contact.getContactId().toString());
									values.put(DBConstant.Groups_Contacts_Columns.COLUMN_NAME, contact.getContactName().toString());
									getContentResolver().insert(DBConstant.Groups_Contacts_Columns.CONTENT_URI, values);									
								}
								if(cr!=null)
									cr.close();
								
							} catch (Exception e) {
								Log.e(TAG, e.toString());
							}
						}
						refreshContactsData();						
					}
				}
			}
		}
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
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				view = vi.inflate(R.layout.grd_item_contact, null); SU ZM004
				view = vi.inflate(R.layout.item_list_contact, null); //EU ZM004
			}
			final Contact contact = contactList.get(position);
			if (contact != null) {
				TextView displayName = (TextView) view.findViewById(R.id.list_item_display_name);
				final ImageView displayPicture = (ImageView) view.findViewById(R.id.list_item_display_picture);
				TextView displayZname = (TextView) view.findViewById(R.id.list_item_zname);
				ImageView imgCall = (ImageView) view.findViewById(R.id.list_item_call);
				ImageView imgMsg = (ImageView) view.findViewById(R.id.list_item_message);
//				 EU ZM004
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

				if(!TextUtils.isEmpty(mSearchTxt.getText().toString())){
					displayName.setText(Utilities.highlight(mSearchTxt.getText().toString(), contact.getContactName()));	
				}else{
					displayName.setText(contact.getContactName());
				}
				
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
	
}
