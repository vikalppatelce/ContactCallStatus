/* HISTORY
 * CATEGORY 		:- ACTIVITY
 * DEVELOPER		:- VIKALP PATEL
 * AIM			    :- ADD IPD ACTIVITY
 * DESCRIPTION 		:- SAVE IPD
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * 1000B      VIKALP PATEL    07/02/2014        RELEASE         ADD VIDEO EXTENSION
 * 1000E      VIKALP PATEL    15/02/2014        RELEASE         ADDED PASS HASH IN JSON
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname;

import android.os.Environment;

public class AppConstants {

	public interface URLS
	{
		//public static final String BASE_URL = "http://beta.manavit.com/adwallz/";
		//public static final String BASE_URL = "http://www.google.com/";
		//public static final String BASE_URL =  "http://www.netdoers.com/projects/smarthumanoid1/services/";
		//public static final String LOGIN_URL = "api.php";		
		
		/*
		 * TESTING SERVER URL
		 */
//		public static final String BASE_URL =  "http://www.netdoers.com/projects/smarthumanoid1/services/api.php";
//		public static final String MEDIA_BASE_URL =  "http://www.netdoers.com/projects/smarthumanoid1/services/file_upload.php";
//		http://myzname.netdoers.com/api/v1/znames/{40 chars api key}/profilepic
		//API V2
		public static final String BASE_URL =  "http://myzname.netdoers.com/api/v1/znames";
		public static final String MEDIA_BASE_URL =  "http://myzname.netdoers.com/api/v1/znames/";
		public static final String SPONSOR_URL =  "http://netdoers.com/projects/smarthumanoid1/images/appsponser/"; 
		public static final String SOCIAL_URL =  "http://netdoers.com/projects/smarthumanoid1/sharing/cases.php?user_id=";

		
		/*
		 * PRODUCTION SERVER URL
		 */
//		public static final String BASE_URL =  "https://www.smarthumanoid.com/consultant/services/api.php";
//		public static final String MEDIA_BASE_URL =  "https://www.smarthumanoid.com/consultant/services/file_upload.php";
//		public static final String SPONSOR_URL =  "https://www.smarthumanoid.com/consultant/images/appsponser/";
		
		//API V2
//		public static final String BASE_URL =  "https://www.smarthumanoid.com/consultant/services/api_v2.php";
//		public static final String MEDIA_BASE_URL =  "https://www.smarthumanoid.com/consultant/services/file_upload_v2.php";
//		public static final String SPONSOR_URL =  "https://www.smarthumanoid.com/consultant/images/appsponser/";
//		public static final String SOCIAL_URL =  "https://smarthumanoid.com/consultant/sharing/cases.php?user_id=";
		
		public static final String UPLOAD_URL = "expense_image_upload.php";
		public static final String MEDIA_UPLOAD_URL = "file_upload.php";
	}

	public interface TAGS
	{
		public interface INTENT
		{
			public static final String TAG_NAME ="name";
			public static final String TAG_ID ="_id";
			public static final String TAG_PHOTO ="photo";
			public static final String TAG_NUMBER ="number";
		}
	}
	
	public interface RESPONSES
	{
		public interface RegistrationResponse
		{
			public static String STATUS = "status";
			public static String ZNAME = "zname";
			public static String API_KEY="api_key";
			public static String ERROR = "errors";
		}
		
		public interface ProjectsResponse
		{
			public static String PID = "pid";
			public static String PROJECT = "project";
			public static String COMPANY = "company";
		}
		public interface VendorProjectsResponse
		{
			public static String PID = "pid";
			public static String PROJECT = "project_name";
			public static String COMPANY = "client_name";
		}
		
		public interface PreviousImagesResponse
		{
			public static String PROJECT = "project";
			public static String WORK_TITLE = "work_title";
			public static String IMAGE = "image";
			public static String ADDRESS = "address";
			public static String CITY = "city";
			public static String STATE = "state";
			public static String SIZE = "size";
		}
	}
	public static final String NETWORK_NOT_AVAILABLE = "Network not available";
	public static final String IMAGE_DIRECTORY_PATH = Environment.getExternalStorageDirectory().getPath()+"/Zname";
	public static final String IMAGE_DIRECTORY_PATH_DATA = Zname.getApplication().getApplicationContext().getFilesDir().getAbsolutePath();
	public static final String EXTENSION = ".jpg";
	public static final String VIDEO_EXTENSION = ".mp4";
	
//	public static final String fontStyle = "fonts/RobotoCondensedBold.ttf";
	public static final String fontStyle = "fonts/Georgia.ttf";
	
	public static final boolean DEBUG = false;
	
	public static final String res = "{\"success\":true,\"tables\":{\"service\":[\"1\",\"2\"],\"expense\":[\"1\",\"2\"],\"expense_image\":[\"1\",\"2\",\"3\",\"4\"],\"service_audio\":[\"1\",\"2\",\"3\"],\"location\":[\"1\",\"2\"]},\"lov\":{\"bank\":[\"ICICI\",\"HDFC\"],\"location\":[\"Lilavati\",\"Rehja\"],\"expense_category\":[\"Food Expense\",\"Office Expense\",\"Bill Payment\",\"Stationary\"]\"patient_type\":[\"Regular\",\"Occasional\"],\"payment_mode\":[\"Net Banking\",\"Cash\",\"Online Transfer\",\"Cheque\"],\"procedure\":[\"Procedure 1\",\"Procedure 2\"],\"referred_by\":[\"Jaykishan Parikh\",\"Mahendra Nagar\"],\"start_time\":[\"Morning\",\"Evening\"],\"surgery_level\":[\"Level 1\",\"Level 2\"],\"team_member\":[\"Rakesh Pratap\",\"Milan Shah\"],\"ward\":[\"General Ward\",\"Emergency Ward\"]}}";
	
	public static final String vendorPro = "[{\"pid\":\"175\",\"project\":\"Philips\",\"company\":\"Philips\"},{\"pid\":\"149\",\"project\":\"Vodafone UP\",\"company\":\"Vodafone\"},{\"pid\":\"148\",\"project\":\"Bajaj Allianz UP Phase 2\",\"company\":\"Bajaj Allianz\"},{\"pid\":\"142\",\"project\":\"Philips MP\",\"company\":\"Philips\"},{\"pid\":\"128\",\"project\":\"HDFC\",\"company\":\"HDFC\"},{\"pid\":\"122\",\"project\":\"Hindustan Unilever\",\"company\":\"Hindustan Unilever\"},{\"pid\":\"83\",\"project\":\"Makers-Raymond\",\"company\":\"Makers-Raymond\"},{\"pid\":\"79\",\"project\":\"HDFC\",\"company\":\"HDFC\"},{\"pid\":\"56\",\"project\":\"Vodafone\",\"company\":\"Vodafone\"},{\"pid\":\"50\",\"project\":\"CASA_Sarita\",\"company\":\"HDFC\"},{\"pid\":\"36\",\"project\":\"Vodafone\",\"company\":\"Vodafone\"},{\"pid\":\"35\",\"project\":\"Gold Loan_Sukirti\",\"company\":\"HDFC\"},{\"pid\":\"30\",\"project\":\"Gold Loan_Sarita\",\"company\":\"HDFC\"}]";
	public static final String preImg = "[{\"project\":\"Gold Loan_Sarita\",\"work_title\":\"Wall Painting\",\"image\":\"http:\\/\\/adwallz.co\\/admin\\/images\\/projects\\/allworks\\/1295257286DSC06495.JPG\",\"address\":\"HDFC Bank,Unnao\",\"city\":\"UNNAO\",\"state\":\"Uttar Pradesh\",\"size\":\"15.00 x 11.50\"},{\"project\":\"Gold Loan_Sarita\",\"work_title\":\"Wall Painting\",\"image\":\"http:\\/\\/adwallz.co\\/admin\\/images\\/projects\\/allworks\\/1295257396DSC06496.JPG\",\"address\":\"Unnao ,Near Lucknow Road.\",\"city\":\"UNNAO\",\"state\":\"Uttar Pradesh\",\"size\":\"26.00 x 8.50\"}]";
	public static final String proListByVendor = "[{\"pid\":\"148\",\"project_name\":\"Bajaj Allianz UP Phase 2\",\"client_name\":\"Bajaj Allianz\"},{\"pid\":\"50\",\"project_name\":\"CASA_Sarita\",\"client_name\":\"HDFC\"},{\"pid\":\"30\",\"project_name\":\"Gold Loan_Sarita\",\"client_name\":\"HDFC\"},{\"pid\":\"35\",\"project_name\":\"Gold Loan_Sukirti\",\"client_name\":\"HDFC\"},{\"pid\":\"79\",\"project_name\":\"HDFC\",\"client_name\":\"HDFC\"},{\"pid\":\"128\",\"project_name\":\"HDFC\",\"client_name\":\"HDFC\"},{\"pid\":\"122\",\"project_name\":\"Hindustan Unilever\",\"client_name\":\"Hindustan Unilever\"},{\"pid\":\"83\",\"project_name\":\"Makers-Raymond\",\"client_name\":\"Makers-Raymond\"},{\"pid\":\"175\",\"project_name\":\"Philips\",\"client_name\":\"Philips\"},{\"pid\":\"142\",\"project_name\":\"Philips MP\",\"client_name\":\"Philips\"},{\"pid\":\"36\",\"project_name\":\"Vodafone\",\"client_name\":\"Vodafone\"},{\"pid\":\"56\",\"project_name\":\"Vodafone\",\"client_name\":\"Vodafone\"},{\"pid\":\"149\",\"project_name\":\"Vodafone UP\",\"client_name\":\"Vodafone\"}]";
	public static final String projectsByVendorToAdd = "[{\"pid\":\"148\",\"project_name\":\"Bajaj Allianz UP Phase 2\",\"client_name\":\"Bajaj Allianz\"},{\"pid\":\"50\",\"project_name\":\"CASA_Sarita\",\"client_name\":\"HDFC\"},{\"pid\":\"30\",\"project_name\":\"Gold Loan_Sarita\",\"client_name\":\"HDFC\"},{\"pid\":\"35\",\"project_name\":\"Gold Loan_Sukirti\",\"client_name\":\"HDFC\"},{\"pid\":\"79\",\"project_name\":\"HDFC\",\"client_name\":\"HDFC\"},{\"pid\":\"128\",\"project_name\":\"HDFC\",\"client_name\":\"HDFC\"},{\"pid\":\"122\",\"project_name\":\"Hindustan Unilever\",\"client_name\":\"Hindustan Unilever\"},{\"pid\":\"83\",\"project_name\":\"Makers-Raymond\",\"client_name\":\"Makers-Raymond\"},{\"pid\":\"175\",\"project_name\":\"Philips\",\"client_name\":\"Philips\"},{\"pid\":\"142\",\"project_name\":\"Philips MP\",\"client_name\":\"Philips\"},{\"pid\":\"36\",\"project_name\":\"Vodafone\",\"client_name\":\"Vodafone\"},{\"pid\":\"56\",\"project_name\":\"Vodafone\",\"client_name\":\"Vodafone\"},{\"pid\":\"149\",\"project_name\":\"Vodafone UP\",\"client_name\":\"Vodafone\"}]";
	public static final String paintingType = "[\"highway\",\"wall\",\"shop\"]";
}

