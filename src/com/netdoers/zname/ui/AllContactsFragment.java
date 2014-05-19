package com.netdoers.zname.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Set;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
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
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.netdoers.zname.R;
import com.netdoers.zname.Zname;
import com.netdoers.zname.async.ImportContactsTask;
import com.netdoers.zname.dto.Contact;
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

	//Android helping reference variable
	private ContactAdapter contactAdapter = null;
	
	//Helping reference variable
	private ArrayList<Contact> contacts = null;
	private LinkedHashMap<String, Contact> allContacts = new LinkedHashMap<String, Contact>();
	
	
	//Indexing for the list
	HashMap<String, Integer> alphaIndexer;
	String[] sections;
	
	
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
			new ImportContactsTask(getActivity(),false).execute();
			Zname.getPreferences().setFirstTime(true);
		}else{
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
			contactAdapter = new ContactAdapter(getActivity(), R.id.gridview_all_contacts, contacts);
			contactsGridView.setAdapter(contactAdapter);
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
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
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
	
//	View Listeners
	
	public void onCloseSearchLayout(View v)
	{
		searchContactLayout.setVisibility(View.GONE);
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
			return alphaIndexer.get(sections[section]);
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
