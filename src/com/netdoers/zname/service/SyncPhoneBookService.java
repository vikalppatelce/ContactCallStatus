/* HISTORY
 * CATEGORY			 :- SERVICE
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- IMPORT CONTACT SERVICE + CONTENT OBSERVER ON CONTACTS.CONTACTSCONTRACTS.DATA
 * DESCRIPTION       :- SERVICE START ONCE REGISTRATION IS DONE. CALLED ONCE TO IMPORT CONTACTS.
 * 
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * ZM001      VIKALP PATEL     02/07/2014                       CREATED 
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

public class SyncPhoneBookService extends Service {

	private static final String TAG = SyncPhoneBookService.class.getSimpleName();

	// CONTENT OBSERVER;
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
		return START_STICKY;
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
		Zname.getApplication().getContentResolver()
				.unregisterContentObserver(contactsContentObserver);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// onStartService();
		contactsContentObserver = new ContactsContentObserver();
		Zname.getApplication()
				.getContentResolver()
				.registerContentObserver(
						android.provider.ContactsContract.Data.CONTENT_URI,
						false, contactsContentObserver);
		if (BuildConfig.DEBUG) {
			Log.i(TAG, String.valueOf(System.currentTimeMillis()));
		}
	}

	private class ContactsContentObserver extends ContentObserver {

		public ContactsContentObserver() {
			super(null);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			// if((System.currentTimeMillis() -
			// Integer.parseInt(Zname.getPreferences().getLastSyncPhoneBook()))
			// > 120000){
			new SyncPhoneBookTask().execute();
			if (BuildConfig.DEBUG)
				Log.i(TAG,
						"onChange() "
								+ String.valueOf(System.currentTimeMillis()));
			// }
		}
	}

	public class SyncPhoneBookTask extends AsyncTask<Void, Void, String> {

		private LinkedHashMap<String, Contact> allContacts = new LinkedHashMap<String, Contact>();
		private ArrayList<Contact> contacts = new ArrayList<Contact>();
		private static final String TAG = "SyncPhoneBookTask";

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String n = null;
			Zname.getPreferences().setLastSyncPhoneBook(
					String.valueOf(System.currentTimeMillis()));
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
				getContactsNewApi();
			} else {
				getContactsOldApi();
			}
			return n;
		}

		/**
		 * @see Get Contacts for 3.0+
		 * 
		 */
		private void getContactsNewApi() {
			ContentResolver cr = Zname.getApplication().getContentResolver();

			String selection = Data.HAS_PHONE_NUMBER + " > '" + ("0") + "'";

			Cursor cur = cr.query(Data.CONTENT_URI, new String[] {
					Data.CONTACT_ID, Data.MIMETYPE, Contacts.DISPLAY_NAME,
					Phone.NUMBER }, selection, null, Contacts.DISPLAY_NAME);

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
						// set contactId
						contact.setContactId(id);
						// set photoUri
						contact.setContactPhotoUri(getContactPhotoUri(Long
								.parseLong(id)));
					}

					if (mimeType.equals(StructuredName.CONTENT_ITEM_TYPE)) {
						// set name
						contact.setContactName(cur.getString(cur
								.getColumnIndex(Contacts.DISPLAY_NAME)));
					}

					if (mimeType.equals(Phone.CONTENT_ITEM_TYPE)) {
						// set phone number
						// SU ZM002
						// contact.setContactNumber(cur.getString(cur.getColumnIndex(Phone.NUMBER)).replaceAll("\\D+",""));
						String s = cur.getString(
								cur.getColumnIndex(Phone.NUMBER)).replaceAll(
								"\\D", "");
						if (contact.getContactNumber().toString().length() == 0) {
							if (s.length() == 12 || s.length() == 11) {
								contact.setContactNumber("\""
										+ s.substring(s.length() - 10,
												s.length()) + "\"");
							} else {
								contact.setContactNumber("\"" + s + "\"");
							}
							// contact.setContactNumber(cur.getString(cur.getColumnIndex(Phone.NUMBER)).replaceAll("\\D",
							// ""));
						} else {
							if (s.length() == 12 || s.length() == 11) {
								contact.setContactNumber(contact
										.getContactNumber()
										.toString()
										.concat(", ")
										.concat("\"")
										.concat(s.substring(s.length() - 10,
												s.length())).concat("\""));// One
																			// can
																			// add
																			// possible
																			// contacts
																			// "(-/,"
							} else {
								contact.setContactNumber(contact
										.getContactNumber().toString()
										.concat(", ").concat("\"").concat(s)
										.concat("\""));
							}
						}
						// EU ZM002
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

			ContentValues values;
			for (Contact _contact : contacts) {

				Cursor cursor = Zname
						.getApplication()
						.getContentResolver()
						.query(DBConstant.All_Contacts_Columns.CONTENT_URI,
								null,
								DBConstant.All_Contacts_Columns.COLUMN_CONTACT_NUMBER
										+ "=?",
								new String[] { _contact.getContactNumber() },
								null);

				if (cursor.getCount() <= 0) {
					cursor.moveToFirst();

					Zname.getApplication()
							.getContentResolver()
							.delete(DBConstant.All_Contacts_Columns.CONTENT_URI,
									DBConstant.All_Contacts_Columns.COLUMN_CONTACT_NUMBER
											+ "=?",
									new String[] { _contact.getContactNumber() });

					Log.i(TAG, "Updating zname phonebook");

					values = new ContentValues();
					values.put(
							DBConstant.All_Contacts_Columns.COLUMN_CONTACT_ID,
							_contact.getContactId());
					values.put(
							DBConstant.All_Contacts_Columns.COLUMN_CONTACT_NUMBER,
							_contact.getContactNumber());
					values.put(
							DBConstant.All_Contacts_Columns.COLUMN_DISPLAY_NAME,
							_contact.getContactName());
					values.put(
							DBConstant.All_Contacts_Columns.COLUMN_ZNAME_DP_URL_SMALL,
							_contact.getContactPhotoUri().toString());
					Zname.getApplication()
							.getContentResolver()
							.insert(DBConstant.All_Contacts_Columns.CONTENT_URI,
									values);

					if (cursor != null)
						cursor.close();
				}
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

					// set contactId
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
					// SU ZM002

					if (cursorPhone.moveToFirst()) {
						do {
							String s = cursorPhone
									.getString(
											cursorPhone
													.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
									.replaceAll("\\D", "");
							if (contact.getContactNumber().toString().length() == 0) {
								if (s.length() == 12 || s.length() == 11) {
									contact.setContactNumber("\""
											+ s.substring(s.length() - 10,
													s.length()) + "\"");
								} else {
									contact.setContactNumber("\"" + s + "\"");
								}
							} else {
								if (s.length() == 12 || s.length() == 11) {
									contact.setContactNumber(contact
											.getContactNumber()
											.toString()
											.concat(", ")
											.concat("\"")
											.concat(s.substring(
													s.length() - 10, s.length()))
											.concat("\""));
								} else {
									contact.setContactNumber(contact
											.getContactNumber().toString()
											.concat(", ").concat("\"")
											.concat(s).concat("\""));
								}
							}
						} while (cursorPhone.moveToNext());
					}

					if (cursorPhone != null) {
						cursorPhone.close();
					}
					// EU ZM002

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

			ContentValues values;
			for (Contact _contact : contacts) {

				Cursor _cursor = Zname
						.getApplication()
						.getContentResolver()
						.query(DBConstant.All_Contacts_Columns.CONTENT_URI,
								null,
								DBConstant.All_Contacts_Columns.COLUMN_CONTACT_NUMBER
										+ "=?",
								new String[] { _contact.getContactNumber() },
								null);

				if (_cursor.getCount() <= 0) {
					_cursor.moveToFirst();

					Zname.getApplication()
							.getContentResolver()
							.delete(DBConstant.All_Contacts_Columns.CONTENT_URI,
									DBConstant.All_Contacts_Columns.COLUMN_CONTACT_NUMBER
											+ "=?",
									new String[] { _contact.getContactNumber() });

					Log.i(TAG, "Updating zname phonebook");

					values = new ContentValues();
					values.put(
							DBConstant.All_Contacts_Columns.COLUMN_CONTACT_ID,
							_contact.getContactId());
					values.put(
							DBConstant.All_Contacts_Columns.COLUMN_CONTACT_NUMBER,
							_contact.getContactNumber());
					values.put(
							DBConstant.All_Contacts_Columns.COLUMN_DISPLAY_NAME,
							_contact.getContactName());
					values.put(
							DBConstant.All_Contacts_Columns.COLUMN_ZNAME_DP_URL_SMALL,
							_contact.getContactPhotoUri().toString());
					Zname.getApplication()
							.getContentResolver()
							.insert(DBConstant.All_Contacts_Columns.CONTENT_URI,
									values);
				}

				if (_cursor != null)
					_cursor.close();
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
