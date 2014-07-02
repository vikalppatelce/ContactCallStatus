/* HISTORY
 * CATEGORY			 :- BASE ACTIVITY
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- ACTIVITY FOR VIEW PAGER + FRAGMENTS
 * NOTE: ROOT OF THE CONTACTS SCREEN. [ALL, FRIENDS, FAMILY, WORK, CALL LOG] 
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * ZM001      VIKALP PATEL     16/05/2014                       CREATED
 * ZM002      VIKALP PATEL     03/06/2014                       SUPPRESSED DRAWER OVER OVERFLOW MENU
 * ZM003      VIKALP PATEL     01/07/2014                       ADDED SYNCPHONEBOOK SERVICE
 * --------------------------------------------------------------------------------------------------------------------
 */

package com.netdoers.zname.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.netdoers.zname.AppConstants;
import com.netdoers.zname.R;
import com.netdoers.zname.Zname;
import com.netdoers.zname.service.ImportContactsService;
import com.netdoers.zname.service.SyncPhoneBookService;
import com.netdoers.zname.utils.PagerSlidingTabStrip;


/**
 * @author Vikalp Patel(vikalppatelce@yahoo.com)
 *
 */
public class MotherActivity extends SherlockFragmentActivity {

	// Declare Variables
	ActionBar mActionBar;
	ViewPager mPager;
	PagerSlidingTabStrip pagerSlidingTabStrp; 
	Tab tab;
	
	//TYPEFACE
	Typeface styleFont;
	
	//CONSTANTS
	public static final String TAG = "MotherActivity";
	
//	SU ZM002
//	private DrawerLayout mDrawerLayout;
//	private ListView mDrawerList;
//	private ActionBarDrawerToggle mDrawerToggle;
//
//	private CharSequence mDrawerTitle;
//	private CharSequence mTitle;
//	private String[] mPlanetTitles;
//	EU ZM002

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the view from activity_main.xml
		setContentView(R.layout.activity_mother);
		
		styleFont = Typeface.createFromAsset(getAssets(), AppConstants.fontStyle);
		
		// Activate Navigation Mode Tabs
		mActionBar = getSupportActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// Locate ViewPager in activity_main.xml
		mPager = (ViewPager) findViewById(R.id.pager);
		
		// Activate Fragment Manager
		FragmentManager fm = getSupportFragmentManager();

		// Capture ViewPager page swipes
		ViewPager.SimpleOnPageChangeListener ViewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				// Find the ViewPager Position
				mActionBar.setSelectedNavigationItem(position);
				switch(position)
				{
				case 0:
					setMotherActionBarTitle(getString(R.string.str_all_contacts_fragment));
					break;
				case 1:
					setMotherActionBarTitle(getString(R.string.str_friends_contacts_fragment));
					break;
				case 2:
					setMotherActionBarTitle(getString(R.string.str_family_contacts_fragment));
					break;
				case 3:
					setMotherActionBarTitle(getString(R.string.str_work_contacts_fragment));
					break;
				case 4:
					setMotherActionBarTitle(getString(R.string.str_call_logs_fragment));
					break;
				}
			}
		};

		mPager.setOnPageChangeListener(ViewPagerListener);
		// Locate the adapter class called ViewPagerAdapter.java
		ViewPagerAdapter viewpageradapter = new ViewPagerAdapter(fm);
		// Set the View Pager Adapter into ViewPager
		mPager.setAdapter(viewpageradapter);
		//PAGER SLIDING TAB STRIP
//		pagerSlidingTabStrp = (PagerSlidingTabStrip) findViewById(R.id.pager_sliding_tab_strip);
//		pagerSlidingTabStrp.setViewPager(mPager);
//		pagerSlidingTabStrp.setOnPageChangeListener(ViewPagerListener);
		
		// Capture tab button clicks
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				// Pass the position on tab click to ViewPager
				mPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
			}
		};

		// Create first Tab
//		tab = mActionBar.newTab().setText("Tab1").setTabListener(tabListener);
		tab = mActionBar.newTab().setIcon(R.drawable.tab_icon_zname_contact_selector).setTabListener(tabListener);
		mActionBar.addTab(tab);
		setMotherActionBarTitle(getString(R.string.str_all_contacts_fragment));
		
		// Create second Tab
//		tab = mActionBar.newTab().setText("Tab2").setTabListener(tabListener);
		tab = mActionBar.newTab().setIcon(R.drawable.tab_icon_zname_friends_selector).setTabListener(tabListener);
		mActionBar.addTab(tab);
		setMotherActionBarTitle(getString(R.string.str_friends_contacts_fragment));
		
		// Create third Tab
//		tab = mActionBar.newTab().setText("Tab3").setTabListener(tabListener);
		tab = mActionBar.newTab().setIcon(R.drawable.tab_icon_zname_family_selector).setTabListener(tabListener);
		mActionBar.addTab(tab);
		setMotherActionBarTitle(getString(R.string.str_family_contacts_fragment));
		
//		tab = mActionBar.newTab().setText("Tab4").setTabListener(tabListener);
		tab = mActionBar.newTab().setIcon(R.drawable.tab_icon_zname_work_selector).setTabListener(tabListener);
		mActionBar.addTab(tab);
		setMotherActionBarTitle(getString(R.string.str_work_contacts_fragment));
		
//		tab = mActionBar.newTab().setText("Tab4").setTabListener(tabListener);
		tab = mActionBar.newTab().setIcon(R.drawable.tab_icon_zname_call_log_selector).setTabListener(tabListener);
		mActionBar.addTab(tab);
		setMotherActionBarTitle(getString(R.string.str_call_logs_fragment));
		
//		SA ZM003
		Zname.getPreferences().setLastSyncPhoneBook(String.valueOf(System.currentTimeMillis()));
		Intent syncPhoneBookIntent =  new Intent(Zname.getApplication().getApplicationContext(), SyncPhoneBookService.class);
		startService(syncPhoneBookIntent);
//		EA ZM003
		
// 		SU ZM002
//		mTitle = mDrawerTitle = getTitle();
//		mPlanetTitles = getResources().getStringArray(R.array.planets_array);
//		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//		mDrawerList = (ListView) findViewById(R.id.left_drawer);
//
//		// set a custom shadow that overlays the main content when the drawer
//		// opens
//		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,GravityCompat.START);
//		// set up the drawer's list view with items and click listener
//		MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, mPlanetTitles);
//		mDrawerList.setAdapter(adapter);
////		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
////				R.layout.drawer_list_item, mPlanetTitles));
//		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


//		 enable ActionBar app icon to behave as action to toggle nav drawer

//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		getSupportActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
//		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
//		mDrawerLayout, /* DrawerLayout object */
//		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
//		R.string.drawer_open, /* "open drawer" description for accessibility */
//		R.string.drawer_close /* "close drawer" description for accessibility */
//		) {
//			@Override
//			public void onDrawerClosed(View view) {
//				getSupportActionBar().setTitle(mTitle);
//				supportInvalidateOptionsMenu();
//				getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//				// invalidateOptionsMenu(); // creates call to
//				// onPrepareOptionsMenu()
//			}
//
//			@Override
//			public void onDrawerOpened(View drawerView) {
//				getSupportActionBar().setTitle(mDrawerTitle);
//				supportInvalidateOptionsMenu();
//				getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//				// invalidateOptionsMenu(); // creates call to
//				// onPrepareOptionsMenu()
//			}
//		};
//		mDrawerLayout.setDrawerListener(mDrawerToggle);
//		EU ZM002
		
//		ContactsContentObserver contactsContentObserver = new ContactsContentObserver();
//		getContentResolver().registerContentObserver(android.provider.ContactsContract.Data.CONTENT_URI, false, contactsContentObserver);
//		
//		CallLogsContentObserver callLogsContentObserver = new CallLogsContentObserver();
//		getContentResolver().registerContentObserver(CallLog.CONTENT_URI, false, callLogsContentObserver);
	}
	
	/*
	 * DEPRECEATED MOVES INTO IMPORTCONTACTSERVICE 
	 */
	
	//////////////////////////////////////////////////////////////////////
	// CONTENT OBSERVER
	//////////////////////////////////////////////////////////////////////
//	private class ContactsContentObserver extends ContentObserver {
//        public ContactsContentObserver() {
//            super(null);
//        }
//        @Override
//        public void onChange(boolean selfChange) {
//            super.onChange(selfChange);
//            Zname.getPreferences().setRefreshContact(true);
//        }
//    }
//	
//	private class CallLogsContentObserver extends ContentObserver {
//        public CallLogsContentObserver() {
//            super(null);
//        }
//        @Override
//        public void onChange(boolean selfChange) {
//            super.onChange(selfChange);
//            Zname.getPreferences().setRefreshCallLogs(true);
//        }
//    }
	
	public void setMotherActionBarTitle(String s)
	{
//		mActionBar.setTitle(s);
		fontActionBar(s);
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
	
	public String getActionBarTitle(){
		try {
			int titleId;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				titleId = getResources().getIdentifier("action_bar_title","id", "android");
			} else {
				titleId = R.id.abs__action_bar_title;
			}
			TextView yourTextView = (TextView) findViewById(titleId);
			return yourTextView.getText().toString();
		} catch (Exception e) {
			Log.e("ActionBar Style", e.toString());
			return null;
		}
	}
	
	/////////////////////////////////////////////
	// ARRAY ADAPTER FOR DRAWER
	/////////////////////////////////////////////
	
	public class MySimpleArrayAdapter extends ArrayAdapter<String> {
		  private final Context context;
		  private final String[] values;

		  public MySimpleArrayAdapter(Context context, String[] values) {
		    super(context, R.layout.item_list_drawer, values);
		    this.context = context;
		    this.values = values;
		  }

		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
		    LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View rowView = inflater.inflate(R.layout.item_list_drawer, parent, false);
		    TextView textView = (TextView) rowView.findViewById(R.id.text1);
		    ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView1);
		    textView.setText(values[position]);
		    
		    textView.setTypeface(styleFont);
		    // Change the icon for Windows and iPhone
		    String s = values[position];
			switch (position) {
			case 0:
				imageView.setImageResource(R.drawable.ic_drawer_edit);
				break;
			case 1:
				imageView.setImageResource(R.drawable.ic_drawer_gear);
				break;
			default:
				break;
			}
		    return rowView;
		  }
		} 
	
	
	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		
//		PlanetFragment fragment = new PlanetFragment();
		Bundle args = new Bundle();
//		args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//		fragment.setArguments(args);

		Intent drawerIntent = null;
		
      switch (position) {
      case 0:
//          fragment = new AllContactsFragment();
          break;
      case 1:
//          fragment = new AllContactsFragment();
    	  drawerIntent = new Intent(this, SettingsActivity.class);
          break;
      case 2:
          fragment = new ContactsFragment();
          break;
      case 3:
          fragment = new ContactsFragment();
          break;
      case 4:
          fragment = new ContactsFragment();
          break;
      case 5:
          fragment = new ContactsFragment();
          break;
      case 6:
          fragment = new ContactsFragment();
          break;
      case 7:
//          fragment = new SendFeedbackFragment();
//          showFeedbackActivity();
          break;
      default:
          break;
      }
//      fragment.setArguments(args);
//		android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
//		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
//
//		// update selected item and title, then close the drawer
//		mDrawerList.setItemChecked(position, true);
//		setTitle(mPlanetTitles[position]);
//		mDrawerLayout.closeDrawer(mDrawerList); COMMENTED ZM002
		
		if(drawerIntent!=null)
			startActivity(drawerIntent);
	}
	
	////////////////////////////////////////////////
	// ACTIVITY CREATED : OPTIONS INSIDE ABS
	///////////////////////////////////////////////
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_friends_contacts, menu);
		return true;
	}
	
	////////////////////////////////////////////////
	// OPTIONS IN ABS : SELECTED
	///////////////////////////////////////////////
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
//			SU ZM002
//			try {
//				if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
//					mDrawerLayout.closeDrawer(mDrawerList);
//				} else {
//					mDrawerLayout.openDrawer(mDrawerList);
//				}
//			} catch (Exception e) {
//				Log.e(TAG, e.toString());
//			}
//			EU ZM002
			return true;
		case R.id.action_add:
			Intent addZnameIntent = new Intent(this, AddZnameActivity.class);
			startActivity(addZnameIntent);
			return true;
		case R.id.action_edit:
			Intent profileIntent = new Intent(this, ProfileActivity.class);
			startActivity(profileIntent);
			return true;
		case R.id.action_search:
			Intent searchIntent = new Intent(this,SearchActivity.class);
			String _title = getActionBarTitle(); 
			
			searchIntent.putExtra(SearchActivity.SEARCH_TYPE, _title);
			String value = "5";
			
			if(_title.equalsIgnoreCase(getString(R.string.str_all_contacts_fragment)) || _title.equalsIgnoreCase(getString(R.string.app_name))){
			}else{
				if(_title.equalsIgnoreCase(getString(R.string.str_friends_contacts_fragment))){
					value = "0";
			    }else {
			    	if(_title.equalsIgnoreCase(getString(R.string.str_family_contacts_fragment))){
			    		  value = "1";
			        }else{
				    value = "2";
			        }
			    }
			}
			
			searchIntent.putExtra(SearchActivity.GROUP_TYPE, value);
			startActivity(searchIntent);
			return true;
		case R.id.action_settings:
			Intent settingIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingIntent);
			return true;
		case R.id.action_notification:
			Intent notificationIntent = new Intent(this, NotificationActivity.class);
			startActivity(notificationIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_MENU) {
	    	try {
//	    		SU ZM002
//				if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
//					mDrawerLayout.closeDrawer(mDrawerList);
//				} else {
//					mDrawerLayout.openDrawer(mDrawerList);
//				}
//	    		EU ZM002
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
	        return true;
	    } else {
	        return super.onKeyUp(keyCode, event);
	    }
	}
	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
//		mDrawerToggle.syncState();   COMMENTED ZM002
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
//		mDrawerToggle.onConfigurationChanged(newConfig); COMMENTED ZM002
	}
	
}
