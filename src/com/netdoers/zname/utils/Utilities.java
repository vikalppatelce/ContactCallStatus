package com.netdoers.zname.utils;

import java.text.Normalizer;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;

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
	
	public static CharSequence highlight(String search, String originalText) {
	    // ignore case and accents
	    // the same thing should have been done for the search text
		search = search.toLowerCase();
	    String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();

	    int start = normalizedText.indexOf(search);
	    if (start < 0) {
	        // not found, nothing to to
	        return originalText;
	    } else {
	        // highlight each appearance in the original text
	        // while searching in normalized text
	        Spannable highlighted = new SpannableString(originalText);
	        while (start >= 0) {
	            int spanStart = Math.min(start, originalText.length());
	            int spanEnd = Math.min(start + search.length(), originalText.length());

	            highlighted.setSpan(new BackgroundColorSpan(Color.YELLOW), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

	            start = normalizedText.indexOf(search, spanEnd);
	        }

	        return highlighted;
	    }
	}
}
