/* HISTORY
 * CATEGORY			 :- CONTACT PICKER | HELPER
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- HIDE SOFT KEYBOARD
 * DESCRIPTION       :- HIDE SOFT KEYBOARD INSIDE CONTACT PICKER
 * JAVA BENEFITS     :- HIGHLY COHESIVE | LOOSELY COUPLED | GOOD DESIGN PATTERN
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * H0001      VIKALP PATEL     16/05/2014                       CREATED 
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.contactpicker;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class Util {

	/**
	 * @param activity
	 *  Hide Soft Keyboard
	 */
	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}

}
