/* HISTORY
 * CATEGORY			 :- FAMILY FRAGMENT
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- FAMILY FRAGMENTS ATTACHED TO MOTHERACTIVITY USING VIEWPAGER + TABS
 * NOTE: 
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * ZM001      VIKALP PATEL     16/05/2014                       CREATED
 * ZM002      VIKALP PATEL     27/05/2014                       CLONED FROM FRIENDS FRAGMENT
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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.netdoers.zname.R;
import com.netdoers.zname.contactpicker.ContactPickerManager;
import com.netdoers.zname.dto.Contact;
import com.netdoers.zname.dto.ContactPicker;
import com.netdoers.zname.sqlite.DBConstant;

/**
 * @author Vikalp Patel(vikalppatelce@yahoo.com)
 *
 */
public class FamilyContactsFragment extends SherlockFragment {

	//DECLARE VARIABLES
	LinearLayout searchContactLayout;
	GridView contactsGridView;
	ImageView searchClose;	
	EditText searchField;

    //ADAPTER
	private ContactAdapter contactAdapter = null;
	
	//REFERENCES VARIABLE - HELPER
	private ArrayList<Contact> contacts = null;
	private LinkedHashMap<String, Contact> allContacts = new LinkedHashMap<String, Contact>();
	
	
	//INDEXING FOR THE LIST
	HashMap<String, Integer> alphaIndexer;
	String[] sections;

	//CONSTANTS
	public static final String TAG = "FriendsContactsFragment";
	public static final int ADD_CONTACT = 10001;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Get the view from fragmenttab2.xml
		View view = inflater.inflate(R.layout.fragment_friends, container, false);
		contactsGridView = (GridView)view.findViewById(R.id.gridview_friends);
		searchContactLayout = (LinearLayout)view.findViewById(R.id.friends_search_txt_layout);
		searchClose = (ImageView)view.findViewById(R.id.friends_clear_srch_button);
		searchField = (EditText) view.findViewById(R.id.friends_search_txt);
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
		
	// View Listeners
	searchClose.setOnClickListener(new OnClickListener() {
	@Override
		public void onClick(View v) {
		// TODO Auto-generated method stub
		onCloseSearchLayout(v);	
		}
	});
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
	contacts = new ArrayList<Contact>();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
			refreshContactsData();
	}
	
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    menu.clear();
	    inflater.inflate(R.menu.friends_contacts_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.friends_action_search:
			openSearchLayout();
			return true;
		case R.id.action_add:
			openAddContactsLayout();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
	public void openAddContactsLayout()
	{
		Intent addContacts = new Intent(getActivity(), ContactPickerManager.class);
		startActivityForResult(addContacts, ADD_CONTACT);
	}
	
//	ADD CONTACTS TO CONTACTS ADAPTER
	
	public void refreshContactsData()
	{
		Cursor cr = getActivity().getContentResolver().query(DBConstant.Family_Contacts_Columns.CONTENT_URI, null, null, null, DBConstant.Family_Contacts_Columns.COLUMN_DISPLAY_NAME + " ASC");
		Cursor crAll = getActivity().getContentResolver().query(DBConstant.All_Contacts_Columns.CONTENT_URI, null, null, null, null);
		Cursor cursor = null;
		if(cr.getCount() > 0)
		{
			contacts.clear();
			
			int intColumnId = cr.getColumnIndex(DBConstant.Family_Contacts_Columns.COLUMN_CONTACT_ID);
			int intColumnContactNumber = crAll.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_NUMBER);
			int intColumnZname = crAll.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_DISPLAY_NAME);
			int intColumnZnameDp = crAll.getColumnIndex(DBConstant.All_Contacts_Columns.COLUMN_CALL_STATUS);
			if(crAll!=null){
				crAll.close();	
			}
			
			Contact c;
			cr.moveToFirst();
			while(cr.moveToNext())
			{
				c = new Contact();
				c.setContactId(cr.getString(intColumnId));
				
				cursor = getActivity().getContentResolver().query(DBConstant.All_Contacts_Columns.CONTENT_URI, null, DBConstant.All_Contacts_Columns.COLUMN_CONTACT_ID+"=?", new String[]{cr.getString(intColumnId)}, null);
				if(cursor.getCount() > 0)
				{
					cursor.moveToFirst();
					
					c.setContactNumber(cursor.getString(intColumnContactNumber));
					c.setContactName(cursor.getString(intColumnZname));
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
			contactAdapter = new ContactAdapter(getActivity(), R.id.gridview_friends, contacts);
			contactsGridView.setAdapter(contactAdapter);	
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
								values.put(DBConstant.Family_Contacts_Columns.COLUMN_CONTACT_ID, contact.getContactId().toString());
								values.put(DBConstant.Family_Contacts_Columns.COLUMN_DISPLAY_NAME, contact.getContactName().toString());
								getActivity().getContentResolver().insert(DBConstant.Family_Contacts_Columns.CONTENT_URI, values);
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
	
}
