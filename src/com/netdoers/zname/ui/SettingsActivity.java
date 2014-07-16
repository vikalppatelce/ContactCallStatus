package com.netdoers.zname.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.netdoers.zname.AppConstants;
import com.netdoers.zname.R;
import com.netdoers.zname.Zname;
import com.netdoers.zname.utils.SquareImageView;

public class SettingsActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener{

	//CONSTANTS
	public static final String TAG=SettingsActivity.class.getSimpleName();
	
	//DECLARE VIEWS
	ActionBar mActionBar;
	
	//DECLARE STYLE TYPEFACE
	Typeface styleFont;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		PreferenceManager prefMgr = getPreferenceManager();
		addPreferencesFromResource(R.xml.settings);
		styleFont = Typeface.createFromAsset(getAssets(), AppConstants.fontStyle);
		
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setTitle("Settings");
		fontActionBar(mActionBar.getTitle().toString());
		
		////////////////////////////////////////////////////
		// FEEDBACK
		////////////////////////////////////////////////////
		
		Preference sendFeedback = prefMgr.findPreference("prefFeedback");
		if(sendFeedback!=null)
		{
			sendFeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				
				@Override
				public boolean onPreferenceClick(Preference preference) {
					// TODO Auto-generated method stub
					Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","feedback@smarthumanoid.com", null));
//	    			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "feedback@smarthumanoid.com" });
	    			String user = Zname.getApplication().getPreferences().getUserName();
	    			String body="";
	    			user = (!TextUtils.isEmpty(user))? user : "User not logged in";
	    			
	    			body = body.concat("From : " +user +"\n");
							try {
								body = body.concat("Device : " +android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL +"\n");
				    			body = body.concat("Android OS Version : " +android.os.Build.VERSION.RELEASE +"\n");
							} catch (Exception e) {
							}
	    			
					emailIntent.putExtra(Intent.EXTRA_TEXT, body);
					emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Send Feedback");
					startActivity(Intent.createChooser(emailIntent, "Send Feedback"));
					return false;
				}
			});
		}
		
		//////////////////////////////////////////////////
		// DEVELOPER OPTION
		/////////////////////////////////////////////////
		Preference dev = prefMgr.findPreference("prefDev");
		if(dev!=null)
		{
			dev.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				
				@Override
				public boolean onPreferenceClick(Preference preference) {
					// TODO Auto-generated method stub
				//	showDialog(DEV);
					dev();
					return false;
				}
			});
		}
		
	}
	
	 @Override
	protected void onResume(){
	        super.onResume();
	        // Set up a listener whenever a key changes
	        getPreferenceScreen().getSharedPreferences()
	            .registerOnSharedPreferenceChangeListener(this);
	    }
	 

	@Override
	protected void onPause() {
	        super.onPause();
	        // Unregister the listener whenever a key changes
	        getPreferenceScreen().getSharedPreferences()
           .unregisterOnSharedPreferenceChangeListener(this);
	    }
	public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        case android.R.id.home:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
	}

	/////////////////////////////////////////
	// ACTION BAR
	//////////////////////////////////////////
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
	
	//////////////////////////////////////////////////////
	// DEVELOPER OPTION - COPY DATABASE TO SDCARD
	//////////////////////////////////////////////////////
	//DEVELOPER OPTION
		public void dev()
		{
			try {
	            File sd = Environment.getExternalStorageDirectory();
	            File data = Environment.getDataDirectory();

	            if (sd.canWrite()) {
	                String currentDBPath = "/data/data/" + getPackageName() + "/databases/ZnameDB";
	                String backupDBPath = "ZnameDB_Dev.db";
	                File currentDB = new File(currentDBPath);
	                File backupDB = new File(sd, backupDBPath);

	                if (currentDB.exists()) {
	                    FileChannel src = new FileInputStream(currentDB).getChannel();
	                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
	                    dst.transferFrom(src, 0, src.size());
	                    src.close();
	                    dst.close();
	                    Toast.makeText(SettingsActivity.this, "Database Transfered!", Toast.LENGTH_SHORT).show();
	                }
	            }
	        } catch (Exception e) {
	        	Log.e(TAG, e.toString());
	        }

		}
}
