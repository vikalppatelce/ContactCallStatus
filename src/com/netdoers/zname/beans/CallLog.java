/* HISTORY
 * CATEGORY 		:- BEAN | DATA TRANSFER OBJECT
 * DEVELOPER		:- VIKALP PATEL
 * AIM			    :- USES AS BEANS IN CALL LOG FRAGMENT
 * DESCRIPTION 		:- USE TO STORE CALL LOG IN FORM OF BEANS
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * 10001      VIKALP PATEL    16/05/2014                        CREATED
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.beans;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class CallLog implements Parcelable{
	
	private String callLogId; //CONTACT ID
	private String callLogName;
	private String callLogNumber;
	private String callLogType;
	private String callLogDate;
	private String callLogTime;
	private Uri callLogPhotoUri;
	
	public CallLog() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public CallLog(String callLogId, String callLogName, String callLogNumber,
			String callLogType, String callLogDate, Uri callLogPhotoUri) {
		super();
		this.callLogId = callLogId;
		this.callLogName = callLogName;
		this.callLogNumber = callLogNumber;
		this.callLogType = callLogType;
		this.callLogDate = callLogDate;
		this.callLogPhotoUri = callLogPhotoUri;
	}
	
	public CallLog(String callLogId, String callLogName, String callLogNumber,
			String callLogType, String callLogDate, String callLogTime,
			Uri callLogPhotoUri) {
		super();
		this.callLogId = callLogId;
		this.callLogName = callLogName;
		this.callLogNumber = callLogNumber;
		this.callLogType = callLogType;
		this.callLogDate = callLogDate;
		this.callLogTime = callLogTime;
		this.callLogPhotoUri = callLogPhotoUri;
	}
	
	public String getCallLogName() {
		return callLogName;
	}
	public void setCallLogName(String callLogName) {
		this.callLogName = callLogName;
	}
	public String getCallLogNumber() {
		return callLogNumber;
	}
	public void setCallLogNumber(String callLogNumber) {
		this.callLogNumber = callLogNumber;
	}
	public String getCallLogType() {
		return callLogType;
	}
	public void setCallLogType(String callLogType) {
		this.callLogType = callLogType;
	}
	public String getCallLogDate() {
		return callLogDate;
	}
	public void setCallLogDate(String callLogDate) {
		this.callLogDate = callLogDate;
	}
	public String getCallLogId() {
		return callLogId;
	}
	public void setCallLogId(String callLogId) {
		this.callLogId = callLogId;
	}
	public String getCallLogTime() {
		return callLogTime;
	}

	public void setCallLogTime(String callLogTime) {
		this.callLogTime = callLogTime;
	}

	public Uri getCallLogPhotoUri() {
		return callLogPhotoUri;
	}
	public void setCallLogPhotoUri(Uri callLogPhotoUri) {
		this.callLogPhotoUri = callLogPhotoUri;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(callLogId);
		dest.writeString(callLogName);
		dest.writeString(callLogNumber);
		dest.writeString(callLogType);
		dest.writeString(callLogDate);
		dest.writeString(callLogTime);
		dest.writeString(callLogPhotoUri.toString());
		
	}
	
	/**
	 * @param source
	 */
	public CallLog(Parcel source) {

		callLogId = source.readString();
		callLogName = source.readString();
		callLogNumber = source.readString();
		callLogType = source.readString();
		callLogDate = source.readString();
		callLogTime = source.readString();
		callLogPhotoUri = Uri.parse(source.readString());
	}

	public static final Parcelable.Creator<CallLog> CREATOR = new Parcelable.Creator<CallLog>() {

		@Override
		public CallLog createFromParcel(Parcel source) {
			return new CallLog(source);
		}

		@Override
		public CallLog[] newArray(int size) {
			return new CallLog[size];
		}

	};
}

