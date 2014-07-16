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
 * ZM002      VIKALP PATEL     30/05/2014                       SUPPRESSED FRAGMENT WISE ACTION BAR MENU
 * ZM003      VIKALP PATEL     30/05/2014                       MOVE SEARCH IN TO SEARCH ACTIVITY
 * ZM004      VIKALP PATEL     09/06/2014                       GRIDVIEW INTO LISTVIEW
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
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
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.netdoers.zname.AppConstants;
import com.netdoers.zname.BuildConfig;
import com.netdoers.zname.R;
import com.netdoers.zname.Zname;
import com.netdoers.zname.beans.Contact;
import com.netdoers.zname.service.ImportContactsService;
import com.netdoers.zname.service.SyncCallStatusService;
import com.netdoers.zname.service.SyncContactsService;
import com.netdoers.zname.sqlite.DBConstant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author Vikalp Patel(vikalppatelce@yahoo.com)
 *
 */
public class ContactsFragment extends SherlockFragment implements OnRefreshListener {

	//View reference variable

	ListView contactsListView;
	ImageView searchClose;
	ProgressDialog progressDialog;
//	LinearLayout searchContactLayout; SU ZM003
//	EditText searchField; EU ZM003
//	GridView contactsGridView; COMMENTED ZM004 
	PullToRefreshLayout mPullToRefreshLayout;
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
	public static final String TAG = ContactsFragment.class.getSimpleName();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true); //COMMENTED ZM002
	}

	//	private PullToRefreshLayout mPullToRefreshLayout;
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Get the view from fragment_all_contacts.xml
		View view = inflater.inflate(R.layout.fragment_contacts, container, false);
		mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
//		contactsGridView = (GridView)view.findViewById(R.id.gridview_all_contacts); SU ZM004
		contactsListView = (ListView)view.findViewById(R.id.listview_all_contacts); //EU ZM004
		searchClose = (ImageView)view.findViewById(R.id.clear_srch_button);
//		searchField = (EditText) view.findViewById(R.id.search_txt); SU ZM003
//		searchContactLayout = (LinearLayout)view.findViewById(R.id.search_txt_layout); EU ZM003
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		 ActionBarPullToRefresh.from(getActivity())
         .allChildrenArePullable()
         .listener(this)
         .setup(mPullToRefreshLayout);
		styleFont = Typeface.createFromAsset(getActivity().getAssets(), AppConstants.fontStyle);
		
        // View Listeners
		imageLoader = ImageLoader.getInstance();
        // Initialize ImageLoader with configuration. Do it once.
//        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        imageLoader.init(Zname.getImageLoaderConfiguration());
        
        options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.def_contact) // resource or drawable
        .showImageForEmptyUri(R.drawable.def_contact) // resource or drawable
        .showImageOnFail(R.drawable.def_contact) //this is the image that will be displayed if download fails
        .cacheInMemory()
        .cacheOnDisc()
        .build();
        
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
		}else if(Zname.getPreferences().getRefreshContact()){
			Zname.getPreferences().setRefreshContact(false);
		}else{
			refreshContactsData();
			Intent pushPullService =  new Intent(Zname.getApplication().getApplicationContext(), SyncContactsService.class);
			getActivity().startService(pushPullService);
			Intent syncCallStatusService =  new Intent(Zname.getApplication().getApplicationContext(), SyncCallStatusService.class);
			getActivity().startService(syncCallStatusService);
		}
		
//		contactsGridView.setOnItemLongClickListener(new OnItemLongClickListener() { SU ZM004
		contactsListView.setOnItemLongClickListener(new OnItemLongClickListener() { //EU ZM004

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
		
//		contactsGridView.setOnItemClickListener(new OnItemClickListener() { SU ZM004
		contactsListView.setOnItemClickListener(new OnItemClickListener() { //EU ZM004

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
					profileIntent = new Intent(getActivity(), ProfileContactActivity.class);	
				}else{
					profileIntent = new Intent(getActivity(), ProfileUserActivity.class);
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
		getActivity().registerReceiver(broadcastImportContactsReceiver, new IntentFilter(ImportContactsService.BROADCAST_ACTION));
		getActivity().registerReceiver(broadcastSyncContactsReceiver, new IntentFilter(SyncContactsService.BROADCAST_ACTION));
		getActivity().registerReceiver(broadcastSyncCallStatusReceiver, new IntentFilter(SyncCallStatusService.BROADCAST_ACTION));
		refreshContactsData();
	}
	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(broadcastImportContactsReceiver);
		getActivity().unregisterReceiver(broadcastSyncContactsReceiver);
		getActivity().unregisterReceiver(broadcastSyncCallStatusReceiver);
	}

//	SC ZM002 	
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    menu.clear();
	    inflater.inflate(R.menu.menu_contacts, menu);
	}

//	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search:
			Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
			startActivity(searchIntent);
			return true;
		case R.id.action_add:
			Intent addZnameIntent = new Intent(getActivity(), AddZnameActivity.class);
			startActivity(addZnameIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
//	EC ZM002

	private BroadcastReceiver broadcastImportContactsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	updateUI(intent);       
        }
    };
    
    private void updateUI(Intent intent) {
    	String counter = intent.getStringExtra("text");
    	
    	if(progressDialog!=null)
    		progressDialog.dismiss();
    	
    	if(counter.equals("Refreshed")){
    		refreshContactsData();	
    	}
    	if(!Zname.getPreferences().getFirstTime()){
    		Intent pushPullService =  new Intent(Zname.getApplication().getApplicationContext(), SyncContactsService.class);
    		getActivity().startService(pushPullService);
    		Zname.getPreferences().setFirstTime(true);	
    	}
    }
    
    private BroadcastReceiver broadcastSyncContactsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	updatePushPullUI(intent);       
        }
    };
    
    private void updatePushPullUI(Intent intent) {
    	String counter = intent.getStringExtra("text"); 
    	if(counter.equals("Refreshed")){
    		refreshContactsData();	
    	}
    }
    
    private BroadcastReceiver broadcastSyncCallStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	updateCallStatusUI(intent);       
        }
    };
    
    private void updateCallStatusUI(Intent intent) {
    	String counter = intent.getStringExtra("text"); 
    	if(counter.equals("Refreshed")){
    		refreshContactsData();	
    	}
    }
    
//	ADD CONTACTS TO CONTACTS ADAPTER
	public void refreshContactsData()
	{
		Cursor cr = getActivity().getContentResolver().query(DBConstant.All_Contacts_Columns.CONTENT_URI, null, null, null, DBConstant.All_Contacts_Columns.COLUMN_DISPLAY_NAME + " ASC");
		if(cr.getCount() > 0)
		{
			contacts.clear();
			
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
		
//		contactAdapter = new ContactAdapter(getActivity(), R.id.gridview_all_contacts, contacts);SU ZM004
//		contactsGridView.setAdapter(contactAdapter); 
		contactAdapter = new ContactAdapter(getActivity(), R.id.listview_all_contacts, contacts);
		contactsListView.setAdapter(contactAdapter); //EU ZM004
	}
	
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
	
	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
        /**
         * Simulate Refresh with 4 seconds sleep
         */
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                	
                	if(!isMyServiceRunning(SyncCallStatusService.class)){
                		Intent syncCallStatusService =  new Intent(Zname.getApplication().getApplicationContext(), SyncCallStatusService.class);
            			getActivity().startService(syncCallStatusService);	
                	}
                	
                	if(!isMyServiceRunning(SyncContactsService.class)){
                		Intent pushPullService =  new Intent(Zname.getApplication().getApplicationContext(), SyncContactsService.class);
            			getActivity().startService(pushPullService);	
                	}
                	
                	if(BuildConfig.DEBUG){
                		Log.i("PushPullContactsService", "PushPullContactsService : "+isMyServiceRunning(SyncContactsService.class));
                		Log.i("SyncCallStatusService", "SyncCallStatusService : "+isMyServiceRunning(SyncCallStatusService.class));
                	}
                	
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                // Notify PullToRefreshLayout that the refresh has finished
                mPullToRefreshLayout.setRefreshComplete();
            }
        }.execute();
	}

	private boolean isMyServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
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
		Context context;

		public ContactAdapter(Context context, int textViewResourceId, ArrayList<Contact> items) {
			super(context, textViewResourceId, items);

			this.contactList = new ArrayList<Contact>();
			this.originalList = new ArrayList<Contact>();

			this.contactList.addAll(items);
			this.originalList.addAll(items);
			this.context = context;

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
//				SU ZM004
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
//				EU ZM004
				
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
