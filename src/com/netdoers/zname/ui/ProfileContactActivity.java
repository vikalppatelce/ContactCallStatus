package com.netdoers.zname.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.netdoers.zname.AppConstants;
import com.netdoers.zname.R;
import com.netdoers.zname.utils.CircleImageView;
import com.netdoers.zname.utils.ScrollableListView;

public class ProfileContactActivity extends SherlockFragmentActivity {

	// DECLARE VIEWS
	CircleImageView mCircleImgProfile;
	ActionBar mActionBar;
	TextView mListViewHead, mContactName;
	ScrollableListView mListView;

	// ADAPTER
	ContactNumberAdapter mListAdapter;

	// DECLARE STYLE TYPEFACE
	Typeface styleFont;

	// DECLARE VARIABLES
	String intentID;
	String intentName;
	String intentPhoto;
	String intentNumber;
	String arrayNumber[] = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_contact);

		mCircleImgProfile = (CircleImageView) findViewById(R.id.activity_profile_contact_image);
		mContactName = (TextView) findViewById(R.id.activity_profile_contact_name);
		mListViewHead = (TextView) findViewById(R.id.activity_profile_contact_listview_head);
		mListView = (ScrollableListView) findViewById(R.id.activity_profile_contact_listview);

		styleFont = Typeface.createFromAsset(getAssets(),
				AppConstants.fontStyle);

		intentID = getIntent().getStringExtra(AppConstants.TAGS.INTENT.TAG_ID);
		intentName = getIntent().getStringExtra(
				AppConstants.TAGS.INTENT.TAG_NAME);
		intentPhoto = getIntent().getStringExtra(
				AppConstants.TAGS.INTENT.TAG_PHOTO);
		intentNumber = getIntent().getStringExtra(
				AppConstants.TAGS.INTENT.TAG_NUMBER);

		if (intentNumber.contains(",")) {
			arrayNumber = intentNumber.split(",");
		} else {
			arrayNumber = new String[1];
			arrayNumber[0] = intentNumber;
		}

		setActionBar("Profile");
		setFontStyle();

		mContactName.setText(intentName);
		// mCircleImgProfile.setImageURI(Uri.parse(intentPhoto));
		try {
			mCircleImgProfile.setImageBitmap(getBitmapFromUri(Uri
					.parse(intentPhoto)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			mCircleImgProfile.setImageResource(R.drawable.def_contact);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mListAdapter = new ContactNumberAdapter(this,
				R.id.activity_profile_contact_listview, arrayNumber);
		mListView.setAdapter(mListAdapter);

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_profile_contact, menu);
		// MenuItem overFlowMenu = menu.findItem(R.id.action_more);
		// MenuItem notificationMenu = menu.findItem(R.id.action_notification);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_invite:
			sendInvitation();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public Bitmap getBitmapFromUri(Uri uri) throws FileNotFoundException,
			IOException {
		InputStream input = getContentResolver().openInputStream(uri);
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inDither = true;// optional
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
		input = this.getContentResolver().openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();
		return bitmap;
	}

	public void setActionBar(String str) {
		mActionBar = getSupportActionBar();
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setTitle(str);
		fontActionBar(mActionBar.getTitle().toString());
	}

	public void setFontStyle() {
		mContactName.setTypeface(styleFont);
		mListViewHead.setTypeface(styleFont);
	}

	public void fontActionBar(String str) {
		try {
			int titleId;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				titleId = getResources().getIdentifier("action_bar_title",
						"id", "android");
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

	public void sendInvitation() {
		if (arrayNumber.length > 1) {
			showDialogToChooseFrom();
		} else {
			showDialogSendInvitation();
		}
	}

	public void showDialogToChooseFrom() {
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(
				ProfileContactActivity.this);
		builderSingle.setTitle("Select number to invite");
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				ProfileContactActivity.this,
				android.R.layout.select_dialog_singlechoice);

		if (arrayNumber.length > 0) {
			for (int i = 0; i < arrayNumber.length; i++)
				arrayAdapter.add(arrayNumber[i]);
		}

		builderSingle.setNegativeButton("CANCEL",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builderSingle.setAdapter(arrayAdapter,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String strName = arrayAdapter.getItem(which);
						AlertDialog.Builder builderInner = new AlertDialog.Builder(
								ProfileContactActivity.this);
						builderInner.setMessage("We will send an invitation to "+strName);
						builderInner.setTitle("Send Invitation");
						builderInner.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						builderInner.show();
					}
				});
		builderSingle.show();
	}

	public void showDialogSendInvitation(){
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(
				ProfileContactActivity.this);
		builderSingle.setTitle("Send Invitation");
		builderSingle.setMessage("We will send invitation to "+arrayNumber[0].toString());
		builderSingle.setNegativeButton("CANCEL",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		
		builderSingle.setPositiveButton("SEND",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				});
		
		builderSingle.show();
	}
	
	public class ContactNumberAdapter extends ArrayAdapter<String> {

		private String arrayContactNumber[];

		public ContactNumberAdapter(Context context, int textViewResourceId,
				String[] objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			arrayContactNumber = objects;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;

			if (view == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.item_list_profile_contact, null);
			}

			if (arrayContactNumber.length > 0) {
				TextView mContactNumber = (TextView) view
						.findViewById(R.id.list_item_contact_number);
				ImageView mImgCall = (ImageView) view
						.findViewById(R.id.list_item_img_call);
				ImageView mImgMsg = (ImageView) view
						.findViewById(R.id.list_item_img_msg);

				mContactNumber.setTypeface(styleFont);

				mContactNumber.setText(arrayContactNumber[position]);

				mImgCall.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent callIntent = new Intent(Intent.ACTION_DIAL);
						callIntent.setData(Uri.parse("tel:"
								+ Uri.encode(arrayContactNumber[position])));
						startActivity(callIntent);
					}
				});

				mImgMsg.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent smsIntent = new Intent(
								Intent.ACTION_VIEW,
								Uri.parse("sms:"
										+ Uri.encode(arrayContactNumber[position])));
						startActivity(smsIntent);
					}
				});
			}
			return view;

		}
	}
}
