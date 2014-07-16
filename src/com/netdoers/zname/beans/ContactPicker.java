/* HISTORY
 * CATEGORY 		:- BEAN | DATA TRANSFER OBJECT
 * DEVELOPER		:- VIKALP PATEL
 * AIM			    :- USES AS BEANS IN CONTACTS PICKER FRAGMENTS [ADD CONTACT TO GROUP] 
 * DESCRIPTION 		:- USE TO STORE CONTACT IN FORM OF BEANS
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * 10001      VIKALP PATEL    16/05/2014                        CREATED
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.beans;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class ContactPicker implements Parcelable {

	private String contactId;
	private String contactName;
	private String contactNumber="";
	private Bitmap contactPhoto;
	private Uri contactPhotoUri;
	private String contactEmail;
	public static final String CONTACTS_DATA = "CONTACTS_DATA";

	boolean selected = false;

	/**
	 * 
	 */
	public ContactPicker() {
		;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	/**
	 * @return
	 */
	public String getContactName() {
		return contactName;
	}

	/**
	 * @return contactNumber
	 */
	public String getContactNumber() {
		return contactNumber;
	}

	/**
	 * @return contactPhoto
	 */
	public Bitmap getContactPhoto() {
		return contactPhoto;
	}

	/**
	 * @return contactPhotoUri
	 */
	public Uri getContactPhotoUri() {
		return contactPhotoUri;
	}

	/**
	 * @param contactName
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	/**
	 * @param contactNumber
	 */
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	/**
	 * @param contactPhoto
	 */
	public void setContactPhoto(Bitmap contactPhoto) {
		this.contactPhoto = contactPhoto;

	}

	/**
	 * @param contactPhotoUri
	 */
	public void setContactPhotoUri(Uri contactPhotoUri) {
		this.contactPhotoUri = contactPhotoUri;
	}

//	public String getContactEmail() {
//		return contactEmail;
//	}
//
//	public void setContactEmail(String contactEmail) {
//		this.contactEmail = contactEmail;
//	}

	/**
	 * @return selected
	 * Whether contact is selected or not
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 * Setting contact is selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return contactName + " " + contactNumber + " ";
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(contactId);
		dest.writeString(contactName);
		dest.writeString(contactNumber);
		dest.writeString(contactPhotoUri.toString());
		dest.writeString(contactEmail);

	}

	/**
	 * @param source
	 */
	public ContactPicker(Parcel source) {

		contactId = source.readString();
		contactName = source.readString();
		contactNumber = source.readString();
		contactPhotoUri = Uri.parse(source.readString());
		contactEmail = source.readString();
	}

	public static final Parcelable.Creator<ContactPicker> CREATOR = new Parcelable.Creator<ContactPicker>() {

		@Override
		public ContactPicker createFromParcel(Parcel source) {
			return new ContactPicker(source);
		}

		@Override
		public ContactPicker[] newArray(int size) {
			return new ContactPicker[size];
		}

	};

}
