/* HISTORY
 * CATEGORY			 :- SERVICE
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- IMPORT CONTACT SERVICE + CONTENT OBSERVER ON CONTACTS.CONTACTSCONTRACTS.DATA
 * NOTE: DEPRECATED AS OF NOW
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * ZM001      VIKALP PATEL     16/05/2014                       CREATED  - DEPRECATED
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.util.Log;

import com.netdoers.zname.BuildConfig;
import com.netdoers.zname.Zname;
import com.netdoers.zname.dto.Contact;
import com.netdoers.zname.sqlite.DBConstant;

public class ImportContactsService extends Service {

	private static final String TAG = "ImportContactsService";
	private boolean isFromObserver = false;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);
		try {
			isFromObserver = intent.getAction().toString()
					.equalsIgnoreCase("fromObserver") ? true : false;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
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
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		onStartService();
		if (BuildConfig.DEBUG) {
			Log.i(TAG, String.valueOf(System.currentTimeMillis()));
		}
	}

	public void onStartService() {
		new ImportContactsTask(Zname.getApplication().getApplicationContext(),
				isFromObserver);
	}

	class ImportContactsTask extends AsyncTask<Void, Void, String> {
		private Context context;
		private ProgressDialog progressDialog;
		private boolean isFromObserver = false;
		private LinkedHashMap<String, Contact> allContacts = new LinkedHashMap<String, Contact>();
		private ArrayList<Contact> contacts = new ArrayList<Contact>();
		private static final String TAG = "ImportContactsTask";

		public ImportContactsTask(Context context, boolean isFromObserver) {
			this.context = context;
			this.isFromObserver = isFromObserver;
		}

		protected void onPreExecute() {
			super.onPreExecute();
			{
				if (!isFromObserver) {
					progressDialog = new ProgressDialog(context);
					progressDialog.setMessage("Importing Contacts");
					progressDialog.show();
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

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (!isFromObserver) {
				progressDialog.dismiss();
			}
		}

		/*
		   * 
		   */
		private void getContactsNewApi() {

			ContentResolver cr = Zname.getApplication().getContentResolver();
			String selection = Data.HAS_PHONE_NUMBER + " > '" + ("0") + "'";

			Cursor cur = cr.query(Data.CONTENT_URI, new String[] {
					Data.CONTACT_ID, Data.MIMETYPE, Email.ADDRESS,
					Contacts.DISPLAY_NAME, Phone.NUMBER }, selection, null,
					Contacts.DISPLAY_NAME);

			Contact contact;
			if (cur.getCount() > 0) {

				while (cur.moveToNext()) {

					String id = cur.getString(cur
							.getColumnIndex(Data.CONTACT_ID));

					String mimeType = cur.getString(cur
							.getColumnIndex(Data.MIMETYPE));

					if (allContacts.containsKey(id)) {
						// update contact
						contact = allContacts.get(id);
					} else {
						contact = new Contact();
						allContacts.put(id, contact);
						// set photoUri
						contact.setContactPhotoUri(getContactPhotoUri(Long
								.parseLong(id)));
					}

					if (mimeType.equals(StructuredName.CONTENT_ITEM_TYPE))
						// set name
						contact.setContactName(cur.getString(cur
								.getColumnIndex(Contacts.DISPLAY_NAME)));

					if (mimeType.equals(Phone.CONTENT_ITEM_TYPE)) {
						// set phone munber
						contact.setContactNumber(cur.getString(
								cur.getColumnIndex(Phone.NUMBER)).replaceAll(
								"[+]", ""));
					}
					// if (mimeType.equals(Email.CONTENT_ITEM_TYPE))
					// // set email
					// contact.setContactEmail(cur.getString(cur.getColumnIndex(Email.ADDRESS)));
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
					// && _contact.getContactEmail() == null
					contacts.remove(_contact);
					break;
				}
			}

			Zname.getApplication()
					.getContentResolver()
					.delete(DBConstant.All_Contacts_Columns.CONTENT_URI, null,
							null);

			ContentValues values;
			int i = 1;
			for (Contact _contact : contacts) {
				values = new ContentValues();
				values.put(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_ID, i);
				values.put(
						DBConstant.All_Contacts_Columns.COLUMN_CONTACT_NUMBER,
						_contact.getContactNumber());
				values.put(DBConstant.All_Contacts_Columns.COLUMN_DISPLAY_NAME,
						_contact.getContactName());
				values.put(DBConstant.All_Contacts_Columns.COLUMN_CALL_STATUS,
						_contact.getContactPhotoUri().toString());
				i += 1;
				Zname.getApplication()
						.getContentResolver()
						.insert(DBConstant.All_Contacts_Columns.CONTENT_URI,
								values);
			}
		}

		/**
		 * @see Get Contacts for 2.2+ DATA.HAS_PHONE_NUMBER was not added in <
		 *      3.0
		 */
		private void getContactsOldApi() {

			Uri uri = ContactsContract.Contacts.CONTENT_URI;
			String[] projection = new String[] { ContactsContract.Contacts._ID,
					ContactsContract.Contacts.DISPLAY_NAME };
			// String selection =
			// null;//ContactsContract.Contacts.HAS_PHONE_NUMBER + " = '" +
			// ("1") + "'";
			String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER
					+ " > '" + ("0") + "'";
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
					if (cursorPhone.moveToFirst()) {
						contact.setContactNumber(cursorPhone
								.getString(
										cursorPhone
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
								.replaceAll("[+]", ""));
					}
					cursorPhone.close();

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
					// && _contact.getContactEmail() == null
					contacts.remove(_contact);
					break;
				}
			}
		}

		// Get contact photo URI for contactId
		/**
		 * @param contactId
		 * @return photoUri
		 */
		public Uri getContactPhotoUri(long contactId) {
			Uri photoUri = ContentUris.withAppendedId(Contacts.CONTENT_URI,
					contactId);
			photoUri = Uri.withAppendedPath(photoUri,
					Contacts.Photo.CONTENT_DIRECTORY);
			return photoUri;
		}
	}
}
