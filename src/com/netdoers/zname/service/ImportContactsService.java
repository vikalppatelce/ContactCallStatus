/* HISTORY
 * CATEGORY			 :- SERVICE
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- IMPORT CONTACT SERVICE + CONTENT OBSERVER ON CONTACTS.CONTACTSCONTRACTS.DATA
 * 
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * ZM001      VIKALP PATEL     16/05/2014                       CREATED 
 * ZM002      VIKALP PATEL     04/06/2014                       ADDED ALL CONTACTS IN BEANS
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.util.Log;

import com.netdoers.zname.BuildConfig;
import com.netdoers.zname.Zname;
import com.netdoers.zname.beans.Contact;
import com.netdoers.zname.sqlite.DBConstant;

public class ImportContactsService extends Service {

	private static final String TAG = "ImportContactsService";
	public static final String BROADCAST_ACTION = "com.netdoers.zname.AllContactsFragment";
	Intent broadCastIntent;
	
	//CONTENT OBSERVER;
	ContactsContentObserver contactsContentObserver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);
		return START_NOT_STICKY;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Zname.getApplication().getContentResolver().unregisterContentObserver(contactsContentObserver);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		onStartService();
		
		contactsContentObserver = new ContactsContentObserver();
		Zname.getApplication().getContentResolver().registerContentObserver(android.provider.ContactsContract.Data.CONTENT_URI, false, contactsContentObserver);
		if (BuildConfig.DEBUG) {
			Log.i(TAG, String.valueOf(System.currentTimeMillis()));
		}
	}

	public void onStartService() {
		new ImportContactsTask(false).execute();
	}
	
	private class ContactsContentObserver extends ContentObserver {

        public ContactsContentObserver() {
            super(null);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Zname.getPreferences().setRefreshContact(true);
            new ImportContactsTask(true).execute();
        }
    }

	private void DisplayLoggingInfo(String message) {
    	Log.d(TAG, "entered DisplayLoggingInfo");
    	broadCastIntent = new Intent(BROADCAST_ACTION);
    	broadCastIntent.putExtra("text", message);
    	sendBroadcast(broadCastIntent);
    }

	public class ImportContactsTask extends AsyncTask<Void, Void, String>
	{
		
		  private LinkedHashMap<String, Contact> allContacts = new LinkedHashMap<String, Contact>();
		  private  ArrayList<Contact> contacts = new ArrayList<Contact>();
		  private static final String TAG ="ImportContactsTask";
		  private boolean isFromObserver = false;

		  ImportContactsTask(boolean isFromObserver)
		  {
			  this.isFromObserver = isFromObserver;
		  }
		  protected void onPreExecute()
		  {
		    super.onPreExecute();
		    {
				if (BuildConfig.DEBUG) {
					Log.e(TAG, String.valueOf(isFromObserver));
				}
		    }
		  }
		  @Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub
			  String n = null;
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
				getContactsNewApi();
			} else {
				getContactsOldApi();
			}
			return n;
			}
		  
		  protected void onPostExecute(String result)
		  {
		    super.onPostExecute(result);
		    if(!isFromObserver){
		    	DisplayLoggingInfo("Refreshed");	
		    }
		  }
		  
		  /**
			 * @see Get Contacts for 3.0+
			 * 
			 */
		private void getContactsNewApi() {
			ContentResolver cr = Zname.getApplication().getContentResolver();

			String selection = Data.HAS_PHONE_NUMBER + " > '" + ("0") + "'";

			Cursor cur = cr.query(Data.CONTENT_URI, new String[] { Data.CONTACT_ID,
					Data.MIMETYPE,
					Contacts.DISPLAY_NAME, Phone.NUMBER }, selection, null,
					Contacts.DISPLAY_NAME);

			Contact contact;
			if (cur.getCount() > 0) {
				while (cur.moveToNext()) {
					String id = cur.getString(cur.getColumnIndex(Data.CONTACT_ID));
					String mimeType = cur.getString(cur
							.getColumnIndex(Data.MIMETYPE));

					if (allContacts.containsKey(id)) {
						// update contact
						contact = allContacts.get(id);
					} else {
						contact = new Contact();
						allContacts.put(id, contact);
						//set contactId
						contact.setContactId(id);
						// set photoUri
						contact.setContactPhotoUri(getContactPhotoUri(Long
								.parseLong(id)));
					}

					if (mimeType.equals(StructuredName.CONTENT_ITEM_TYPE)) {
						// set name
						contact.setContactName(cur.getString(cur.getColumnIndex(Contacts.DISPLAY_NAME)));
					}

					if (mimeType.equals(Phone.CONTENT_ITEM_TYPE)) {
						// set phone number
//						SU ZM002
//						contact.setContactNumber(cur.getString(cur.getColumnIndex(Phone.NUMBER)).replaceAll("\\D+",""));
						if (contact.getContactNumber().toString().length() == 0) {
							contact.setContactNumber(cur.getString(cur.getColumnIndex(Phone.NUMBER)).replaceAll("\\D", ""));
						} else {
							contact.setContactNumber(contact.getContactNumber().toString().concat(", ").concat(cur.getString(cur.getColumnIndex(Phone.NUMBER)).replaceAll("\\D", "")));//One can add possible contacts "(-/,"
						}
//						EU ZM002
					}
				}
			}

			cur.close();
			// get contacts from hashmap
			contacts.clear();
			contacts.addAll(allContacts.values());

			// remove self contact
			for (Contact _contact : contacts) {
				if (_contact.getContactName() == null
						&& _contact.getContactNumber() == null) {
					contacts.remove(_contact);
					break;
				}
			}

			Zname.getApplication()
					.getContentResolver()
					.delete(DBConstant.All_Contacts_Columns.CONTENT_URI, null, null);

			ContentValues values;
			for (Contact _contact : contacts) {
				values = new ContentValues();
				values.put(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_ID, _contact.getContactId());
				values.put(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_NUMBER,
						_contact.getContactNumber());
				values.put(DBConstant.All_Contacts_Columns.COLUMN_DISPLAY_NAME,
						_contact.getContactName());
				values.put(DBConstant.All_Contacts_Columns.COLUMN_CALL_STATUS,
						_contact.getContactPhotoUri().toString());
				Zname.getApplication()
						.getContentResolver()
						.insert(DBConstant.All_Contacts_Columns.CONTENT_URI, values);
			}
		}

			/**
			 * @see Get Contacts for 2.2+
			 * DATA.HAS_PHONE_NUMBER was not added in < 3.0
			 */
		private void getContactsOldApi() {

			Uri uri = ContactsContract.Contacts.CONTENT_URI;
			String[] projection = new String[] { ContactsContract.Contacts._ID,
					ContactsContract.Contacts.DISPLAY_NAME };
			String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " > '"
					+ ("0") + "'";
			String[] selectionArgs = null;
			String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
					+ " COLLATE LOCALIZED ASC";

			ContentResolver contectResolver = Zname.getApplication()
					.getContentResolver();

			Cursor cursor = contectResolver.query(uri, projection, selection,
					selectionArgs, sortOrder);
			Contact contact;
			// Load contacts one by one
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					contact = new Contact();
					String id = cursor.getString(cursor
							.getColumnIndex(ContactsContract.Contacts._ID));

					//set contactId
					contact.setContactId(id);
					
					contact.setContactPhotoUri(getContactPhotoUri(Long
							.parseLong(id)));

					String[] phoneProj = new String[] {
							ContactsContract.CommonDataKinds.Phone.NUMBER,
							ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };
					Cursor cursorPhone = contectResolver.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							phoneProj,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = ?", new String[] { id }, null);
//					SU ZM002
//					if (cursorPhone.moveToFirst()) {
//						contact.setContactNumber(cursorPhone
//								.getString(
//										cursorPhone
//												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
//								.replaceAll("\\D+",""));
//					}
//					cursorPhone.close();
					
					if (cursorPhone.moveToFirst()) {
						do {
							if (contact.getContactNumber().toString().length() == 0) {
								contact.setContactNumber(cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\D", ""));
							} else {
								contact.setContactNumber(contact.getContactNumber().toString().concat(", ").concat(cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\D", "")));
							}	
						} while (cursorPhone.moveToNext());
					}
					
					if (cursorPhone != null) {
						cursorPhone.close();
					}
//					EU ZM002

					contact.setContactName(cursor.getString(cursor
							.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

					allContacts.put(id, contact);
					cursor.moveToNext();
				}
			}
			cursor.close();
			// get contacts from hashmap
			contacts.clear();
			contacts.addAll(allContacts.values());

			// remove self contact
			for (Contact _contact : contacts) {

				if (_contact.getContactName() == null
						&& _contact.getContactNumber() == null) {
					contacts.remove(_contact);
					break;
				}
			}
			
			Zname.getApplication()
					.getContentResolver()
					.delete(DBConstant.All_Contacts_Columns.CONTENT_URI, null, null);

			ContentValues values;
			for (Contact _contact : contacts) {
				values = new ContentValues();
				values.put(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_ID,
						_contact.getContactId());
				values.put(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_NUMBER,
						_contact.getContactNumber());
				values.put(DBConstant.All_Contacts_Columns.COLUMN_DISPLAY_NAME,
						_contact.getContactName());
				values.put(DBConstant.All_Contacts_Columns.COLUMN_CALL_STATUS,
						_contact.getContactPhotoUri().toString());
				Zname.getApplication()
						.getContentResolver()
						.insert(DBConstant.All_Contacts_Columns.CONTENT_URI, values);
			}
		}
			// Get contact photo URI for contactId
			/**
			 * @param contactId
			 * @return photoUri
			 */
			public Uri getContactPhotoUri(long contactId) {
				Uri photoUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
				photoUri = Uri.withAppendedPath(photoUri, Contacts.Photo.CONTENT_DIRECTORY);
				return photoUri;
			}
		}
}
