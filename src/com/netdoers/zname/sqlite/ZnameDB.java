/* HISTORY
 * CATEGORY			 :- DATABASE
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- CONTENT PROVIDER
 * NOTE:  
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * ZMDB1      VIKALP PATEL     16/05/2014                       CREATED
 * ZMDB2      VIKALP PATEL     05/06/2014                       ADDED TABLE : USERSTATUS
 * --------------------------------------------------------------------------------------------------------------------
 */


package com.netdoers.zname.sqlite;

import java.util.HashMap;

import com.netdoers.zname.BuildConfig;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class ZnameDB extends ContentProvider{
	
	public static final String AUTHORITY = "com.netdoers.zname.SQLite.ZnameDB";
	
	private static final UriMatcher sUriMatcher;
	private static final String TAG = "ZnameDB";
	
	private static final int ALLCONTACTS = 1;
	private static final int FRIENDSCONTACTS = 2;
	private static final int FAMILYCONTACTS = 3;
	private static final int WORKCONTACTS = 4;
	private static final int RANDOMCONTACTS = 5;
	private static final int USERSTATUS = 6;
	
	private static HashMap<String, String> allContactsProjectionMap;
	private static HashMap<String, String> friendsContactsProjectionMap;
	private static HashMap<String, String> familyContactsProjectionMap;
	private static HashMap<String, String> workContactsProjectionMap;
	private static HashMap<String, String> randomContactsProjectionMap;
	private static HashMap<String, String> userStatusProjectionMap;
	
	private static class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, DBConstant.DB_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			
			//allcontacts
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append("CREATE TABLE ");
			strBuilder.append(DBConstant.TABLE_ALL_CONTACTS);
			strBuilder.append('(');
			strBuilder.append(DBConstant.All_Contacts_Columns.COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," );
			strBuilder.append(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_ID +" TEXT UNIQUE," );
			strBuilder.append(DBConstant.All_Contacts_Columns.COLUMN_ZNAME_ID +" TEXT ," );
			strBuilder.append(DBConstant.All_Contacts_Columns.COLUMN_DISPLAY_NAME +" TEXT ," );
			strBuilder.append(DBConstant.All_Contacts_Columns.COLUMN_ZNAME +" TEXT ," );
			strBuilder.append(DBConstant.All_Contacts_Columns.COLUMN_ZNAME_DP_URL_BIG +" TEXT ," );
			strBuilder.append(DBConstant.All_Contacts_Columns.COLUMN_ZNAME_DP_URL_SMALL +" TEXT ," );
			strBuilder.append(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_NUMBER +" TEXT ," );
			strBuilder.append(DBConstant.All_Contacts_Columns.COLUMN_ZNAME_NUMBER +" TEXT ," );
			strBuilder.append(DBConstant.All_Contacts_Columns.COLUMN_CALL_STATUS +" TEXT ," );
			strBuilder.append(DBConstant.All_Contacts_Columns.COLUMN_LAST_SPOKE +" TEXT ," );
			strBuilder.append(DBConstant.All_Contacts_Columns.COLUMN_SYNC_STATUS +" NUMBER" );
			strBuilder.append(')');
			db.execSQL(strBuilder.toString());
			if (BuildConfig.DEBUG) {
				Log.i(TAG, strBuilder.toString());
			}
			
			//friendsContacts
			strBuilder = new StringBuilder();
			strBuilder.append("CREATE TABLE ");
			strBuilder.append(DBConstant.TABLE_FRIENDS_CONTACTS);
			strBuilder.append('(');
			strBuilder.append(DBConstant.Friends_Contacts_Columns.COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," ); 
			strBuilder.append(DBConstant.Friends_Contacts_Columns.COLUMN_CONTACT_ID +" TEXT UNIQUE," );
			strBuilder.append(DBConstant.Friends_Contacts_Columns.COLUMN_DISPLAY_NAME +" TEXT ," );
			strBuilder.append(DBConstant.Friends_Contacts_Columns.COLUMN_ZNAME_ID +" TEXT" );
			strBuilder.append(')');
			db.execSQL(strBuilder.toString());
			if (BuildConfig.DEBUG) {
				Log.i(TAG, strBuilder.toString());
			}
			
			//familyContacts
			strBuilder = new StringBuilder();
			strBuilder.append("CREATE TABLE ");
			strBuilder.append(DBConstant.TABLE_FAMILY_CONTACTS);
			strBuilder.append('(');
			strBuilder.append(DBConstant.Family_Contacts_Columns.COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," ); 
			strBuilder.append(DBConstant.Family_Contacts_Columns.COLUMN_CONTACT_ID +" TEXT UNIQUE," );
			strBuilder.append(DBConstant.Family_Contacts_Columns.COLUMN_DISPLAY_NAME +" TEXT ," );
			strBuilder.append(DBConstant.Family_Contacts_Columns.COLUMN_ZNAME_ID +" TEXT" );
			strBuilder.append(')');
			db.execSQL(strBuilder.toString());
			if (BuildConfig.DEBUG) {
				Log.i(TAG, strBuilder.toString());
			}
			
			//workContacts
			strBuilder = new StringBuilder();
			strBuilder.append("CREATE TABLE ");
			strBuilder.append(DBConstant.TABLE_WORK_CONTACTS);
			strBuilder.append('(');
			strBuilder.append(DBConstant.Work_Contacts_Columns.COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," ); 
			strBuilder.append(DBConstant.Work_Contacts_Columns.COLUMN_CONTACT_ID +" TEXT UNIQUE," );
			strBuilder.append(DBConstant.Work_Contacts_Columns.COLUMN_DISPLAY_NAME +" TEXT ," );
			strBuilder.append(DBConstant.Work_Contacts_Columns.COLUMN_ZNAME_ID +" TEXT" );
			strBuilder.append(')');
			db.execSQL(strBuilder.toString());
			if (BuildConfig.DEBUG) {
				Log.i(TAG, strBuilder.toString());
			}
			
			//randomContacts
			strBuilder = new StringBuilder();
			strBuilder.append("CREATE TABLE ");
			strBuilder.append(DBConstant.TABLE_RANDOM_CONTACTS);
			strBuilder.append('(');
			strBuilder.append(DBConstant.Random_Contacts_Columns.COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," ); 
			strBuilder.append(DBConstant.Random_Contacts_Columns.COLUMN_CONTACT_ID +" TEXT UNIQUE," );
			strBuilder.append(DBConstant.Random_Contacts_Columns.COLUMN_DISPLAY_NAME +" TEXT ," );
			strBuilder.append(DBConstant.Random_Contacts_Columns.COLUMN_ZNAME_ID +" TEXT" );
			strBuilder.append(')');
			db.execSQL(strBuilder.toString());
			if (BuildConfig.DEBUG) {
				Log.i(TAG, strBuilder.toString());
			}
			
			//userstatus
			strBuilder = new StringBuilder();
			strBuilder.append("CREATE TABLE ");
			strBuilder.append(DBConstant.TABLE_STATUS);
			strBuilder.append('(');
			strBuilder.append(DBConstant.User_Status_Columns.COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," ); 
			strBuilder.append(DBConstant.User_Status_Columns.COLUMN_ZNAME_ID +" TEXT ," );
			strBuilder.append(DBConstant.User_Status_Columns.COLUMN_STATUS +" TEXT ," );
			strBuilder.append(DBConstant.User_Status_Columns.COLUMN_STATUS_TYPE +" NUMBER DEFAULT 0" );
			strBuilder.append(')');
			db.execSQL(strBuilder.toString());
			if (BuildConfig.DEBUG) {
				Log.i(TAG, strBuilder.toString());
			}
		}

		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DBConstant.TABLE_ALL_CONTACTS);
			db.execSQL("DROP TABLE IF EXISTS " + DBConstant.TABLE_FRIENDS_CONTACTS);
			db.execSQL("DROP TABLE IF EXISTS " + DBConstant.TABLE_FAMILY_CONTACTS);
			db.execSQL("DROP TABLE IF EXISTS " + DBConstant.TABLE_WORK_CONTACTS);
			db.execSQL("DROP TABLE IF EXISTS " + DBConstant.TABLE_RANDOM_CONTACTS);
			db.execSQL("DROP TABLE IF EXISTS " + DBConstant.TABLE_STATUS);
			
			onCreate(db);
		}
	}

	/* VERSION      DATABASE_VERSION      MODIFIED            BY
	 * ----------------------------------------------------------------
	 * V 0.0.1             1              16/05/14        VIKALP PATEL
	 * -----------------------------------------------------------------
	 */
	private static final int DATABASE_VERSION = 1;
		
	OpenHelper openHelper;


	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case ALLCONTACTS:
			count = db.delete(DBConstant.TABLE_ALL_CONTACTS, where, whereArgs);
			break;
		case FRIENDSCONTACTS:
			count = db.delete(DBConstant.TABLE_FRIENDS_CONTACTS, where, whereArgs);
			break;
		case FAMILYCONTACTS:
			count = db.delete(DBConstant.TABLE_FAMILY_CONTACTS, where, whereArgs);
			break;
		case WORKCONTACTS:
			count = db.delete(DBConstant.TABLE_WORK_CONTACTS, where, whereArgs);
			break;
		case RANDOMCONTACTS:
			count = db.delete(DBConstant.TABLE_RANDOM_CONTACTS, where, whereArgs);
			break;
		case USERSTATUS:
			count = db.delete(DBConstant.TABLE_STATUS, where, whereArgs);
			break;
		
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}


	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch (sUriMatcher.match(uri)) {
		case ALLCONTACTS:
			return DBConstant.All_Contacts_Columns.CONTENT_TYPE;
		case FRIENDSCONTACTS:
			return DBConstant.Friends_Contacts_Columns.CONTENT_TYPE;
		case FAMILYCONTACTS:
			return DBConstant.Family_Contacts_Columns.CONTENT_TYPE;
		case WORKCONTACTS:
			return DBConstant.Work_Contacts_Columns.CONTENT_TYPE;
		case RANDOMCONTACTS:
			return DBConstant.Random_Contacts_Columns.CONTENT_TYPE;
		case USERSTATUS:
			return DBConstant.User_Status_Columns.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}


	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// TODO Auto-generated method stub
		if (sUriMatcher.match(uri) != ALLCONTACTS && sUriMatcher.match(uri) != FRIENDSCONTACTS
					&& sUriMatcher.match(uri) != FAMILYCONTACTS && sUriMatcher.match(uri) != USERSTATUS
			&& sUriMatcher.match(uri) != WORKCONTACTS && sUriMatcher.match(uri) != RANDOMCONTACTS) 
		{ 
			throw new IllegalArgumentException("Unknown URI " + uri); 
		}
		
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} 
		else {
			values = new ContentValues();
		}
		
		SQLiteDatabase db = openHelper.getWritableDatabase();
		long rowId = 0;
		
		switch (sUriMatcher.match(uri)) 
		{
			case ALLCONTACTS:
				 rowId = db.insertWithOnConflict(DBConstant.TABLE_ALL_CONTACTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				if (rowId > 0) 
				{
					Uri noteUri = ContentUris.withAppendedId(DBConstant.All_Contacts_Columns.CONTENT_URI, rowId);
					getContext().getContentResolver().notifyChange(noteUri, null);
					return noteUri;
				}
				break;
			case FRIENDSCONTACTS:
				 rowId = db.insertWithOnConflict(DBConstant.TABLE_FRIENDS_CONTACTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				if (rowId > 0) 
				{
					Uri noteUri = ContentUris.withAppendedId(DBConstant.Friends_Contacts_Columns.CONTENT_URI, rowId);
					getContext().getContentResolver().notifyChange(noteUri, null);
					return noteUri;
				}
				break;
			case FAMILYCONTACTS:
				 rowId = db.insertWithOnConflict(DBConstant.TABLE_FAMILY_CONTACTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				if (rowId > 0) 
				{
					Uri noteUri = ContentUris.withAppendedId(DBConstant.Family_Contacts_Columns.CONTENT_URI, rowId);
					getContext().getContentResolver().notifyChange(noteUri, null);
					return noteUri;
				}
				break;				
			case WORKCONTACTS:
				 rowId = db.insertWithOnConflict(DBConstant.TABLE_WORK_CONTACTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				if (rowId > 0) 
				{
					Uri noteUri = ContentUris.withAppendedId(DBConstant.Work_Contacts_Columns.CONTENT_URI, rowId);
					getContext().getContentResolver().notifyChange(noteUri, null);
					return noteUri;
				}
				break;
			case RANDOMCONTACTS:
				 rowId = db.insertWithOnConflict(DBConstant.TABLE_RANDOM_CONTACTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				if (rowId > 0) 
				{
					Uri noteUri = ContentUris.withAppendedId(DBConstant.Random_Contacts_Columns.CONTENT_URI, rowId);
					getContext().getContentResolver().notifyChange(noteUri, null);
					return noteUri;
				}
				break;
			case USERSTATUS:
				 rowId = db.insertWithOnConflict(DBConstant.TABLE_STATUS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				if (rowId > 0) 
				{
					Uri noteUri = ContentUris.withAppendedId(DBConstant.User_Status_Columns.CONTENT_URI, rowId);
					getContext().getContentResolver().notifyChange(noteUri, null);
					return noteUri;
				}
				break;
				
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
				
		}
		throw new SQLException("Failed to insert row into " + uri);
	}


	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		openHelper 		= new OpenHelper(getContext());
		return true;
	}


	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (sUriMatcher.match(uri)) {
		case ALLCONTACTS:
			qb.setTables(DBConstant.TABLE_ALL_CONTACTS);
			qb.setProjectionMap(allContactsProjectionMap);
			break;
		case FRIENDSCONTACTS:
			qb.setTables(DBConstant.TABLE_FRIENDS_CONTACTS);
			qb.setProjectionMap(friendsContactsProjectionMap);
			break;
		case FAMILYCONTACTS:
			qb.setTables(DBConstant.TABLE_FAMILY_CONTACTS);
			qb.setProjectionMap(familyContactsProjectionMap);
			break;
		case WORKCONTACTS:
			qb.setTables(DBConstant.TABLE_WORK_CONTACTS);
			qb.setProjectionMap(workContactsProjectionMap);
			break;
		case RANDOMCONTACTS:
			qb.setTables(DBConstant.TABLE_RANDOM_CONTACTS);
			qb.setProjectionMap(randomContactsProjectionMap);
			break;
		case USERSTATUS:
			qb.setTables(DBConstant.TABLE_STATUS);
			qb.setProjectionMap(userStatusProjectionMap);
			break;
		
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
//		SQLiteDatabase db = openHelper.getReadableDatabase(); //COMMENTED 1000A
		SQLiteDatabase db = openHelper.getWritableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}


	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int count = -1;
		switch (sUriMatcher.match(uri)) {
		case ALLCONTACTS:
			count = db.update(DBConstant.TABLE_ALL_CONTACTS, values, where, whereArgs);
			break;
		case FRIENDSCONTACTS:
			count = db.update(DBConstant.TABLE_FRIENDS_CONTACTS, values, where, whereArgs);
			break;
		case FAMILYCONTACTS:
			count = db.update(DBConstant.TABLE_FAMILY_CONTACTS, values, where, whereArgs);
			break;
		case WORKCONTACTS:
			count = db.update(DBConstant.TABLE_WORK_CONTACTS, values, where, whereArgs);
			break;
		case RANDOMCONTACTS:
			count = db.update(DBConstant.TABLE_RANDOM_CONTACTS, values, where, whereArgs);
			break;
		case USERSTATUS:
			count = db.update(DBConstant.TABLE_STATUS, values, where, whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	static {
		
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, DBConstant.TABLE_ALL_CONTACTS, ALLCONTACTS);
		sUriMatcher.addURI(AUTHORITY, DBConstant.TABLE_FRIENDS_CONTACTS, FRIENDSCONTACTS);

		sUriMatcher.addURI(AUTHORITY, DBConstant.TABLE_FAMILY_CONTACTS, FAMILYCONTACTS);

		sUriMatcher.addURI(AUTHORITY, DBConstant.TABLE_WORK_CONTACTS, WORKCONTACTS);
		sUriMatcher.addURI(AUTHORITY, DBConstant.TABLE_RANDOM_CONTACTS, RANDOMCONTACTS);
		
		sUriMatcher.addURI(AUTHORITY, DBConstant.TABLE_STATUS, USERSTATUS);

		allContactsProjectionMap = new HashMap<String, String>();
		allContactsProjectionMap.put(DBConstant.All_Contacts_Columns.COLUMN_ID, DBConstant.All_Contacts_Columns.COLUMN_ID);
		allContactsProjectionMap.put(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_ID, DBConstant.All_Contacts_Columns.COLUMN_CONTACT_ID);
		allContactsProjectionMap.put(DBConstant.All_Contacts_Columns.COLUMN_ZNAME_ID, DBConstant.All_Contacts_Columns.COLUMN_ZNAME_ID);
		allContactsProjectionMap.put(DBConstant.All_Contacts_Columns.COLUMN_DISPLAY_NAME, DBConstant.All_Contacts_Columns.COLUMN_DISPLAY_NAME);
		allContactsProjectionMap.put(DBConstant.All_Contacts_Columns.COLUMN_ZNAME, DBConstant.All_Contacts_Columns.COLUMN_ZNAME);
		allContactsProjectionMap.put(DBConstant.All_Contacts_Columns.COLUMN_ZNAME_DP_URL_BIG, DBConstant.All_Contacts_Columns.COLUMN_ZNAME_DP_URL_BIG);
		allContactsProjectionMap.put(DBConstant.All_Contacts_Columns.COLUMN_ZNAME_DP_URL_SMALL, DBConstant.All_Contacts_Columns.COLUMN_ZNAME_DP_URL_SMALL);
		allContactsProjectionMap.put(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_NUMBER, DBConstant.All_Contacts_Columns.COLUMN_CONTACT_NUMBER);
		allContactsProjectionMap.put(DBConstant.All_Contacts_Columns.COLUMN_ZNAME_NUMBER, DBConstant.All_Contacts_Columns.COLUMN_ZNAME_NUMBER);
		allContactsProjectionMap.put(DBConstant.All_Contacts_Columns.COLUMN_CALL_STATUS, DBConstant.All_Contacts_Columns.COLUMN_CALL_STATUS);
		allContactsProjectionMap.put(DBConstant.All_Contacts_Columns.COLUMN_LAST_SPOKE, DBConstant.All_Contacts_Columns.COLUMN_LAST_SPOKE);
		allContactsProjectionMap.put(DBConstant.All_Contacts_Columns.COLUMN_SYNC_STATUS, DBConstant.All_Contacts_Columns.COLUMN_SYNC_STATUS);
		
		friendsContactsProjectionMap = new HashMap<String, String>();
		friendsContactsProjectionMap.put(DBConstant.Friends_Contacts_Columns.COLUMN_ID, DBConstant.Friends_Contacts_Columns.COLUMN_ID);
		friendsContactsProjectionMap.put(DBConstant.Friends_Contacts_Columns.COLUMN_CONTACT_ID, DBConstant.Friends_Contacts_Columns.COLUMN_CONTACT_ID);
		friendsContactsProjectionMap.put(DBConstant.Friends_Contacts_Columns.COLUMN_DISPLAY_NAME, DBConstant.Friends_Contacts_Columns.COLUMN_DISPLAY_NAME);
		friendsContactsProjectionMap.put(DBConstant.Friends_Contacts_Columns.COLUMN_ZNAME_ID, DBConstant.Friends_Contacts_Columns.COLUMN_ZNAME_ID);

		familyContactsProjectionMap = new HashMap<String, String>();
		familyContactsProjectionMap.put(DBConstant.Family_Contacts_Columns.COLUMN_ID, DBConstant.Family_Contacts_Columns.COLUMN_ID);
		familyContactsProjectionMap.put(DBConstant.Family_Contacts_Columns.COLUMN_CONTACT_ID, DBConstant.Family_Contacts_Columns.COLUMN_CONTACT_ID);
		familyContactsProjectionMap.put(DBConstant.Family_Contacts_Columns.COLUMN_DISPLAY_NAME, DBConstant.Family_Contacts_Columns.COLUMN_DISPLAY_NAME);
		familyContactsProjectionMap.put(DBConstant.Family_Contacts_Columns.COLUMN_ZNAME_ID, DBConstant.Family_Contacts_Columns.COLUMN_ZNAME_ID);

		workContactsProjectionMap = new HashMap<String, String>();
		workContactsProjectionMap.put(DBConstant.Work_Contacts_Columns.COLUMN_ID, DBConstant.Work_Contacts_Columns.COLUMN_ID);
		workContactsProjectionMap.put(DBConstant.Work_Contacts_Columns.COLUMN_CONTACT_ID, DBConstant.Work_Contacts_Columns.COLUMN_CONTACT_ID);
		workContactsProjectionMap.put(DBConstant.Work_Contacts_Columns.COLUMN_DISPLAY_NAME, DBConstant.Work_Contacts_Columns.COLUMN_DISPLAY_NAME);
		workContactsProjectionMap.put(DBConstant.Work_Contacts_Columns.COLUMN_ZNAME_ID, DBConstant.Work_Contacts_Columns.COLUMN_ZNAME_ID);

		randomContactsProjectionMap = new HashMap<String, String>();
		randomContactsProjectionMap.put(DBConstant.Random_Contacts_Columns.COLUMN_ID, DBConstant.Random_Contacts_Columns.COLUMN_ID);
		randomContactsProjectionMap.put(DBConstant.Random_Contacts_Columns.COLUMN_CONTACT_ID, DBConstant.Random_Contacts_Columns.COLUMN_CONTACT_ID);
		randomContactsProjectionMap.put(DBConstant.Random_Contacts_Columns.COLUMN_DISPLAY_NAME, DBConstant.Random_Contacts_Columns.COLUMN_DISPLAY_NAME);
		randomContactsProjectionMap.put(DBConstant.Random_Contacts_Columns.COLUMN_ZNAME_ID, DBConstant.Random_Contacts_Columns.COLUMN_ZNAME_ID);
		
		userStatusProjectionMap = new HashMap<String, String>();
		userStatusProjectionMap.put(DBConstant.User_Status_Columns.COLUMN_ID, DBConstant.User_Status_Columns.COLUMN_ID);
		userStatusProjectionMap.put(DBConstant.User_Status_Columns.COLUMN_ZNAME_ID, DBConstant.User_Status_Columns.COLUMN_ZNAME_ID);
		userStatusProjectionMap.put(DBConstant.User_Status_Columns.COLUMN_STATUS, DBConstant.User_Status_Columns.COLUMN_STATUS);
		userStatusProjectionMap.put(DBConstant.User_Status_Columns.COLUMN_STATUS_TYPE, DBConstant.User_Status_Columns.COLUMN_STATUS_TYPE);

		}	
}
