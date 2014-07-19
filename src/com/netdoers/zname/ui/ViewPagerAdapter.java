package com.netdoers.zname.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.netdoers.zname.R;
import com.netdoers.zname.utils.PagerSlidingTabStrip.IconTabProvider;

/**
 * @author Vikalp Patel (vikalppatelce@yahoo.com)
 * @category Ui Helper
 * 
 */
public class ViewPagerAdapter extends FragmentPagerAdapter 
/**
 * PAGERSLIDINGTABSTIPS 
 */
implements IconTabProvider 
{
	// Declare the number of ViewPager pages
//	final int PAGE_COUNT = 5;
	final int PAGE_COUNT = 3;
	
	/**
	 * PAGERSLIDINGTABSTRIPS
	 */
	private final int[] ICONS = { R.drawable.tab_icon_zname_contact_selector, R.drawable.tab_icon_zname_friends_selector, 
			R.drawable.tab_icon_zname_call_log_selector };
	/**
	 * SLIDINGTABLAYOUT
	 */
	/*private int[] imageResId = {
	        R.drawable.tab_icon_zname_contact_selector,
	        R.drawable.tab_icon_zname_friends_selector,
	        R.drawable.tab_icon_zname_call_log_selected
	};

	@Override
	public CharSequence getPageTitle(int position) {
	    Drawable image = Zname.getApplication().getApplicationContext().getResources().getDrawable(imageResId[position]);
	    image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
	    SpannableString sb = new SpannableString(" ");
	    ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
	    sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    return sb;
	}*/
	/**
	 * SLIDINGTABLAYOUT
	 */

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
			}
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

	/**
	 * PAGERSLIDINGTABSTRIPS 
	 */
	@Override
	public int getPageIconResId(int position) {
		// TODO Auto-generated method stub
		return ICONS[position];
	}

}

