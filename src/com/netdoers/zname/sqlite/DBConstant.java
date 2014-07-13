/*HISTORY
* CATEGORY 			:- DATABASE
* DEVELOPER			:- VIKALP PATEL
* AIM 				:- SAVE CONTACTS
* DESCRIPTION 		:- CREATE ZNAME DB PROVIDER
* 
* S - START E- END C- COMMENTED U -EDITED A -ADDED
* --------------------------------------------------------------------------------------------------------------------
* INDEX 	DEVELOPER 		DATE 		FUNCTION		DESCRIPTION
* --------------------------------------------------------------------------------------------------------------------
* ZM001    VIKALP PATEL   16/05/2014                    CREATED
* --------------------------------------------------------------------------------------------------------------------
*/

package com.netdoers.zname.sqlite;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class DBConstant {

	public static final String DB_NAME = "ZnameDB";
	
	public static final String TABLE_ALL_CONTACTS 			    = "contacts";
	public static final String TABLE_GROUPS 					= "groups";
	public static final String TABLE_GROUP_CONTACTS				= "groupContacts";
	public static final String TABLE_STATUS 					= "user_status";
	
	
//	public static final Uri DISTINCT_CONTENT_URI = Uri.parse("content://"+ ZnameDB.AUTHORITY + "/contacts");
	public static class All_Contacts_Columns implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse("content://"+ ZnameDB.AUTHORITY + "/contacts");
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/contacts";
		
		public static final String COLUMN_ID 							= "_id";
		public static final String COLUMN_ZNAME_ID 					    = "_zname_id";
		public static final String COLUMN_CONTACT_ID				    = "_contact_id";
		public static final String COLUMN_DISPLAY_NAME					= "_display_name";
		public static final String COLUMN_ZNAME      					= "_zname";
		public static final String COLUMN_ZNAME_DP_URL_BIG 				= "_zname_dp_url_big";
		public static final String COLUMN_ZNAME_DP_URL_SMALL			= "_zname_dp_url_small";
		public static final String COLUMN_CONTACT_NUMBER				= "_contact_number";
		public static final String COLUMN_ZNAME_NUMBER 					= "_zname_number";
		public static final String COLUMN_CALL_STATUS 					= "_call_status";
		public static final String COLUMN_LAST_SPOKE 				    = "_last_spoke";
		public static final String COLUMN_SYNC_STATUS 					= "_status";
	}
	
	public static class Groups_Columns implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse("content://"+ ZnameDB.AUTHORITY + "/groups");
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/groups";

		public static final String COLUMN_ID 							= "_id";
		public static final String COLUMN_GROUP_ID 	 					= "_group_id";
		public static final String COLUMN_GROUP_NAME					= "_group_name";
		public static final String COLUMN_GROUP_DP						= "_group_dp";
	}
	public static class Groups_Contacts_Columns implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse("content://"+ ZnameDB.AUTHORITY + "/groupContacts");
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/groupContacts";

		public static final String COLUMN_ID 							= "_id";
		public static final String COLUMN_GROUP_ID 	 					= "_group_id";
		public static final String COLUMN_CONTACT_ID					= "_contact_id";
		public static final String COLUMN_NAME							= "_name";
	}
	
	public static class User_Status_Columns implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse("content://"+ ZnameDB.AUTHORITY + "/user_status");
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/user_status";

		public static final String COLUMN_ID 							= "_id";
		public static final String COLUMN_ZNAME_ID 						= "_zname_id";
		public static final String COLUMN_STATUS			     		= "_status";
		public static final String COLUMN_STATUS_TYPE    	     		= "status_type";
	}
}
