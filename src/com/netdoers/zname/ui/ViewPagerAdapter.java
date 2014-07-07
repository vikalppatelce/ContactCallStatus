package com.netdoers.zname.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author Vikalp Patel (vikalppatelce@yahoo.com)
 * @category Ui Helper
 * 
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

	// Declare the number of ViewPager pages
//	final int PAGE_COUNT = 5;
	final int PAGE_COUNT = 3;
	
	/**
	 * @param fm
	 */
	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
	 */
	@Override
	public Fragment getItem(int item) {
		switch (item) {

			// Open HomeFragment.java
		case 0:
			ContactsFragment homeFragment = new ContactsFragment();
			return homeFragment;
			// Open PlaceOrderFragment.java
		case 1:
			GroupsFragment groupsFragment = new GroupsFragment();
			return groupsFragment;
		case 2:
			CallLogsFragment callLogsFragment = new CallLogsFragment();
			return callLogsFragment;
		/*case 1:
			FriendsContactsFragment placeOrderFragment = new FriendsContactsFragment();
			return placeOrderFragment;

			// Open NewArrivalFragment.java
		case 2:
			FamilyContactsFragment newArrivalFragment = new FamilyContactsFragment();
			return newArrivalFragment;
		case 3:
			WorkContactsFragment aboutFragment = new WorkContactsFragment();
			return aboutFragment;
		case 4:
			CallLogsFragment callLogsFragment = new CallLogsFragment();
			return callLogsFragment;
*/		}
		return null;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return PAGE_COUNT;
	}

}

