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
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.netdoers.zname.AppConstants;
import com.netdoers.zname.R;
import com.netdoers.zname.beans.Contact;
import com.netdoers.zname.beans.ContactPicker;
import com.netdoers.zname.contactpicker.ContactPickerManager;
import com.netdoers.zname.sqlite.DBConstant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * @author Vikalp Patel(vikalppatelce@yahoo.com)
 *
 */
public class FriendsContactsFragment extends SherlockFragment {
	
	//DECLARE VARIABLES
	
//	GridView contactsGridView; SU ZM004
	ListView contactsListView; //EU ZM004
	Button addContact;
//	ImageView searchClose; SU ZM003
//	LinearLayout searchContactLayout;
//	EditText searchField;  EU ZM003
	ImageLoader imageLoader;
	DisplayImageOptions options;
	//TYPEFACE
	static Typeface styleFont;
	
    //ADAPTER
	private ContactAdapter contactAdapter = null;
	
	//REFERENCES VARIABLE - HELPER
	private ArrayList<Contact> contacts = null;
	private LinkedHashMap<String, Contact> allContacts = new LinkedHashMap<String, Contact>();
	private int mLastFirstVisibleItem; //SA ZM005
	private boolean mIsScrollingUp;	 //EA ZM005
	
	//INDEXING FOR THE LIST
	HashMap<String, Integer> alphaIndexer;
	String[] sections;

	//CONSTANTS
	public static final String TAG = "FriendsContactsFragment";
	public static final int ADD_CONTACT = 10001;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setHasOptionsMenu(true); COMMENTED ZM002
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Get the view from fragmenttab2.xml
		View view = inflater.inflate(R.layout.fragment_friends, container, false);
//		contactsGridView = (GridView)view.findViewById(R.id.gridview_friends); SU ZM004
		contactsListView = (ListView)view.findViewById(R.id.listview_friends); //EU ZM004
		addContact = (Button) view.findViewById(R.id.friends_btn_add);
//		searchContactLayout = (LinearLayout)view.findViewById(R.id.friends_search_txt_layout); SU ZM003
//		searchClose = (ImageView)view.findViewById(R.id.friends_clear_srch_button);
//		searchField = (EditText) view.findViewById(R.id.friends_search_txt); EU ZM003
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
		
		imageLoader = ImageLoader.getInstance();
        // Initialize ImageLoader with configuration. Do it once.
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        
        options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.def_contact) // resource or drawable
        .showImageForEmptyUri(R.drawable.def_contact) // resource or drawable
        .showImageOnFail(R.drawable.def_contact) //this is the image that will be displayed if download fails
        .cacheInMemory()
        .cacheOnDisc()
        .build();
        
	// View Listeners
//		SU ZM003
//	searchClose.setOnClickListener(new OnClickListener() {
//	@Override
//		public void onClick(View v) {
//		// TODO Auto-generated method stub
//		onCloseSearchLayout(v);	
//		}
//	});
//	searchField.addTextChangedListener(new TextWatcher() {
//		@Override
//		public void afterTextChanged(Editable s) {
//		}
//		@Override
//		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//		}
//		@Override
//		public void onTextChanged(CharSequence s, int start, int before, int count) {
//			// call the filter with the current text on the editbox
//				try {
//					contactAdapter.getFilter().filter(s.toString());
//				} catch (Exception e) {
//					Log.e(TAG, e.toString());
//				}
//		}
//	});
//		EU ZM003
//		contactsGridView.setOnItemLongClickListener(new OnItemLongClickListener() { SU ZM004
		contactsListView.setOnItemLongClickListener(new OnItemLongClickListener() {// EU ZM004
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

		contactsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String viewTagId = view.getTag(R.id.TAG_CONTACT_ID).toString();
				String viewTagName = view.getTag(R.id.TAG_CONTACT_NAME).toString();
				String viewTagPhoto	= view.getTag(R.id.TAG_CONTACT_PHOTO_ID).toString();
				String viewTagNumber	= view.getTag(R.id.TAG_CONTACT_NUMBER).toString();
				Intent profileIntent = new Intent(getActivity(), ProfileNotZnameActivity.class);
				profileIntent.putExtra(AppConstants.TAGS.INTENT.TAG_ID, viewTagId);
				profileIntent.putExtra(AppConstants.TAGS.INTENT.TAG_NAME, viewTagName);
				profileIntent.putExtra(AppConstants.TAGS.INTENT.TAG_PHOTO, viewTagPhoto);
				profileIntent.putExtra(AppConstants.TAGS.INTENT.TAG_NUMBER, viewTagNumber);
				profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(profileIntent);
			}
		});

	addContact.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			openAddContactsLayout();
		}
	});
	
//	 SA ZM005
	contactsListView.setOnScrollListener(new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			final ListView lw = contactsListView;
		    if (view.getId() == lw.getId()) {
		        final int currentFirstVisibleItem = lw.getFirstVisiblePosition();
		        if (currentFirstVisibleItem > mLastFirstVisibleItem) {
		            mIsScrollingUp = false;
		            addContact.setVisibility(View.GONE);
		        } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
		            mIsScrollingUp = true;
		            addContact.setVisibility(View.VISIBLE);
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
//	 EA ZM005
	
	contacts = new ArrayList<Contact>();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
			refreshContactsData();
	}
//	SC ZM002
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//	    super.onCreateOptionsMenu(menu, inflater);
//	    menu.clear();
//	    inflater.inflate(R.menu.friends_contacts_menu, menu);
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case R.id.friends_action_search:
//			openSearchLayout();
//			return true;
//		case R.id.action_add:
//			openAddContactsLayout();
//			return true;
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}
//	EC ZM002
	
//	View Listeners
//	SU ZM003
//	public void onCloseSearchLayout(View v)
//	{
//		searchContactLayout.setVisibility(View.GONE);
//		searchField.setText("");
//	}
//
//	public void openSearchLayout()
//	{
//		searchContactLayout.setVisibility(View.VISIBLE);
//	}
//	EU ZM003
	public void openAddContactsLayout()
	{
		Intent addContacts = new Intent(getActivity(), ContactPickerManager.class);
		startActivityForResult(addContacts, ADD_CONTACT);
	}
	
//	ALERT DIALOG
	public void showInputDialog(String name,String number,String photoUri)
	{
		final Dialog dialog = new Dialog(getActivity());
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
		Cursor cr = getActivity().getContentResolver().query(DBConstant.Friends_Contacts_Columns.CONTENT_URI, null, null, null, DBConstant.Friends_Contacts_Columns.COLUMN_DISPLAY_NAME + " ASC");
		Cursor crAll = getActivity().getContentResolver().query(DBConstant.All_Contacts_Columns.CONTENT_URI, null, null, null, null);
		Cursor cursor = null;
		if(cr.getCount() > 0)
		{
			contacts.clear();
			
			int intColumnId = cr.getColumnIndex(DBConstant.Friends_Contacts_Columns.COLUMN_CONTACT_ID);
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
				
				cursor = getActivity().getContentResolver().query(DBConstant.All_Contacts_Columns.CONTENT_URI, null, DBConstant.All_Contacts_Columns.COLUMN_CONTACT_ID+"=?", new String[]{cr.getString(intColumnId)}, null);
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
			contactAdapter = new ContactAdapter(getActivity(), R.id.listview_friends, contacts);
			contactsListView.setAdapter(contactAdapter); //EU ZM004
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
								values.put(DBConstant.Friends_Contacts_Columns.COLUMN_CONTACT_ID, contact.getContactId().toString());
								values.put(DBConstant.Friends_Contacts_Columns.COLUMN_DISPLAY_NAME, contact.getContactName().toString());
								getActivity().getContentResolver().insert(DBConstant.Friends_Contacts_Columns.CONTENT_URI, values);
								refreshContactsData();
							} catch (Exception e) {
								Log.e(TAG, e.toString());
							}
						}
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
				LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				view = vi.inflate(R.layout.grd_item_contact, null); SU ZM004
				view = vi.inflate(R.layout.item_list_contact, null); //EU ZM004
			}
			final Contact contact = contactList.get(position);
			if (contact != null) {
//				 SU ZM004
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

				displayName.setText(contact.getContactName());
				displayZname.setText(
						TextUtils.isEmpty(contact.getContactZname())
						?contact.getContactNumber().contains(",")
								?contact.getContactNumber().toString().substring(0, contact.getContactNumber().toString().indexOf(",")).replace("\"", "")
										:contact.getContactNumber().replace("\"", "")
						:contact.getContactZname()
							);
				
				displayName.setTypeface(styleFont);
				displayZname.setTypeface(styleFont);
				
				view.setTag(R.id.TAG_CONTACT_NUMBER, contact.getContactNumber().replace("\"", ""));
				view.setTag(R.id.TAG_CONTACT_DP, contact.getContactPhotoUri());
				view.setTag(R.id.TAG_CONTACT_NAME, contact.getContactName());
				view.setTag(R.id.TAG_CONTACT_ID, contact.getContactId());
				view.setTag(R.id.TAG_CONTACT_PHOTO_ID, contact.getContactPhotoUri());
				
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
