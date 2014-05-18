package com.netdoers.zname.utils;

import java.util.Locale;

import android.annotation.SuppressLint;

@SuppressLint("DefaultLocale")
public class Utilities {
	public static boolean isRTL = false;

	public static void checkRTL()
	{
		Locale locale = Locale.getDefault();
        String lang = locale.getLanguage();
        if (lang == null) {
            lang = "en";
        }
        isRTL = lang.toLowerCase().equals("ar");
	}
}
