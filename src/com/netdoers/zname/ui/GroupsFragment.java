/* HISTORY
 * CATEGORY			 :- FRIENDS GROUP FRAGMENT
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- FRIENDS CONTACT FRAGMENTS ATTACHED TO MOTHERACTIVITY USING VIEWPAGER + TABS
 * NOTE:			 BASE FRAGMENT FOR GROUPS 
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * ZM001      VIKALP PATEL     16/05/2014                       CREATED
 * ZM002      VIKALP PATEL     30/05/2014                       SUPPRESSED FRAGMENT WISE ACTION BAR MENU
 * ZM003      VIKALP PATEL     30/05/2014                       MOVE SEARCH INTO SEARCH ACTIVITY
 * ZM004      VIKALP PATEL     09/06/2014                       MIGRATION : GRIDVIEW INTO LISTVIEW
 * ZM005      VIKALP PATEL     20/06/2014                       HIDE ADD TO GROUP ON SCROLL
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.ui;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.netdoers.zname.AppConstants;
import com.netdoers.zname.BuildConfig;
import com.netdoers.zname.R;
import com.netdoers.zname.Zname;
import com.netdoers.zname.beans.GroupDTO;
import com.netdoers.zname.sqlite.DBConstant;

/**
 * @author Vikalp Patel(vikalppatelce@yahoo.com)
 * 
 */
public class GroupsFragment extends SherlockFragment {

	// DECLARE VARIABLES
	private ListView mListView; 
	private Button mAddGroup;

	// TYPEFACE
	static Typeface styleFont;

	// ADAPTER
	private GroupListAdapter mAdapter = null;

	// REFERENCES VARIABLE - HELPER
	private ArrayList<GroupDTO> arrListGroups = null;

	// CONSTANTS
	public static final String TAG = GroupsFragment.class.getSimpleName();
	public static final int ADD_CONTACT = 10001;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Get the view from fragmenttab2.xml
		View view = inflater
				.inflate(R.layout.fragment_groups, container, false);
		mListView = (ListView) view.findViewById(R.id.listview_groups); // EU
																				// ZM004
		mAddGroup = (Button) view.findViewById(R.id.fragment_groups_add);
		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		setFontStyle();
        setEventListeners();
        
		arrListGroups = new ArrayList<GroupDTO>();
		
//		registerForContextMenu(mListView);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshGroupsData();
	}

	/*@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    android.view.MenuInflater inflater = getSherlockActivity().getMenuInflater();
	    inflater.inflate(R.menu.context_menu_groups_fragment, menu);
	}
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.menu_groups_delete:
	        	showAlertDialogDelete(arrListGroups.get(info.position).getGroupId(), arrListGroups.get(info.position).getGroupName());
	            return true;
	        case R.id.menu_groups_edit:
	        	showAlertDialogEdit(arrListGroups.get(info.position).getGroupId(), arrListGroups.get(info.position).getGroupName());
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}*/
	
	private void setEventListeners(){
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String viewTagId = view.getTag(R.id.TAG_GROUP_ID).toString();
				String viewTagName = view.getTag(R.id.TAG_GROUP_NAME).toString();
				String viewTagPhoto = view.getTag(R.id.TAG_GROUP_DP).toString();
				Intent groupIntent = new Intent(getActivity(),GroupContactsActivity.class);
				groupIntent.putExtra(AppConstants.TAGS.INTENT.TAG_ID,viewTagId);
				groupIntent.putExtra(AppConstants.TAGS.INTENT.TAG_NAME,viewTagName);
				groupIntent.putExtra(AppConstants.TAGS.INTENT.TAG_PHOTO,viewTagPhoto);
				startActivity(groupIntent);
			}
		});
		
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() { 

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
			
				// vibration for 100 milliseconds
				((Vibrator)getActivity().getApplication().getApplicationContext().getSystemService(getActivity().VIBRATOR_SERVICE)).vibrate(50);
				
				String viewTagId = view.getTag(R.id.TAG_GROUP_ID).toString();
				String viewTagName = view.getTag(R.id.TAG_GROUP_NAME).toString();
				
				showAlertModifyDialog(viewTagName, viewTagId);
				return false;
			}
		});

		mAddGroup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openAddGroupsLayout();
			}
		});
	}
	
	private void setFontStyle(){
		styleFont = Typeface.createFromAsset(getActivity().getAssets(),AppConstants.fontStyle);
	}
	
	public void openAddGroupsLayout() {
		showInputDialog();
	}

	// ALERT DIALOG
	public void showInputDialog() {
		final Dialog dialog = new Dialog(getActivity());
		try {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		} catch (Exception e) {
			Log.e("inputDialog", e.toString());
		}
		dialog.setContentView(R.layout.dialog_add_group);

		TextView groupTitle = (TextView) dialog
				.findViewById(R.id.dialog_add_group_title);
		final EditText groupName = (EditText) dialog
				.findViewById(R.id.dialog_group_name_txt);
		Button groupAdd = (Button) dialog
				.findViewById(R.id.dialog_group_name_ok);
		Button groupCancel = (Button) dialog
				.findViewById(R.id.dialog_group_name_cancel);

		groupTitle.setTypeface(styleFont);
		groupName.setTypeface(styleFont);
		groupAdd.setTypeface(styleFont);
		groupCancel.setTypeface(styleFont);

		groupAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(groupName.getText().toString())) {
					ContentValues values = new ContentValues();
					values.put(DBConstant.Groups_Columns.COLUMN_GROUP_ID,
							System.currentTimeMillis());
					values.put(DBConstant.Groups_Columns.COLUMN_GROUP_NAME,
							groupName.getText().toString().trim());
					getActivity().getContentResolver().insert(
							DBConstant.Groups_Columns.CONTENT_URI, values);

					refreshGroupsData();

					dialog.dismiss();
				} else {
					groupName.setError("Please enter valid group name");
				}
			}
		});

		groupCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	public void showAlertModifyDialog(final String name,final String id)
	{
		final Dialog dialog = new Dialog(getActivity());
		try {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		} catch (Exception e) {
			Log.e("inputDialog", e.toString());
		}
		dialog.setContentView(R.layout.dialog_display_info);
		
		TextView mDialogTitle = (TextView)dialog.findViewById(R.id.dialog_display_info_title);
		TextView mDialogText = (TextView)dialog.findViewById(R.id.dialog_display_into_txt);
		Button mDialogBtnEdit = (Button)dialog.findViewById(R.id.dialog_display_info_ok);
		Button mDialogBtnDelete = (Button)dialog.findViewById(R.id.dialog_display_info_cancel);
		
		mDialogTitle.setText(name);
		
		mDialogTitle.setTypeface(styleFont);
		mDialogText.setTypeface(styleFont);
		mDialogBtnEdit.setTypeface(styleFont);
		mDialogBtnDelete.setTypeface(styleFont);
		
		mDialogBtnDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showAlertDialogDelete(id, name);
				dialog.dismiss();
			}
		});
		
		mDialogBtnEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showAlertDialogEdit(id, name);
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}

	public void showAlertDialogEdit(final String viewTagId,final String viewTagName) {
		final Dialog dialog = new Dialog(getActivity());
		try {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		} catch (Exception e) {
			Log.e("inputDialog", e.toString());
		}
		dialog.setContentView(R.layout.dialog_add_group);

		TextView groupTitle = (TextView) dialog
				.findViewById(R.id.dialog_add_group_title);
		final EditText groupName = (EditText) dialog
				.findViewById(R.id.dialog_group_name_txt);
		Button groupAdd = (Button) dialog
				.findViewById(R.id.dialog_group_name_ok);
		Button groupCancel = (Button) dialog
				.findViewById(R.id.dialog_group_name_cancel);

		groupTitle.setTypeface(styleFont);
		groupName.setTypeface(styleFont);
		groupAdd.setTypeface(styleFont);
		groupCancel.setTypeface(styleFont);
		
		groupName.setText(viewTagName);

		groupAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(groupName.getText().toString())) {
					ContentValues values = new ContentValues();
					values.put(DBConstant.Groups_Columns.COLUMN_GROUP_NAME, groupName.getText().toString());
					int c = getActivity().getContentResolver().update(DBConstant.Groups_Columns.CONTENT_URI, values, DBConstant.Groups_Columns.COLUMN_GROUP_ID+"=?", new String[]{viewTagId});

					refreshGroupsData();
		        	mAdapter.notifyDataSetChanged();
		        	dialog.dismiss();
		        	
		        	if(BuildConfig.DEBUG)
		        		Log.i(TAG, "Group "+viewTagName + "changed to "+ groupName.getText().toString() +" "+c);
					
				} else {
					groupName.setError("Please enter valid group name");
				}
			}
		});

		groupCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void showAlertDialogDelete(final String viewTagId, String viewTagName){
		
		final Dialog dialog = new Dialog(getActivity());
		try {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		} catch (Exception e) {
			Log.e("inputDialog", e.toString());
		}
		dialog.setContentView(R.layout.dialog_delete_info);
		
		TextView mDialogTitle = (TextView)dialog.findViewById(R.id.dialog_delete_info_title);
		TextView mDialogText = (TextView)dialog.findViewById(R.id.dialog_delete_into_txt);
		Button mDialogBtnCancel = (Button)dialog.findViewById(R.id.dialog_delete_info_cancel);
		Button mDialogBtnOk = (Button)dialog.findViewById(R.id.dialog_delete_info_ok);
		
		mDialogText.setText("Do want to delete group "+ viewTagName +"?");
		
		mDialogTitle.setTypeface(styleFont);
		mDialogText.setTypeface(styleFont);
		mDialogBtnCancel.setTypeface(styleFont);
		mDialogBtnOk.setTypeface(styleFont);
		
		mDialogBtnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		    	getActivity().getContentResolver().delete(DBConstant.Groups_Columns.CONTENT_URI, DBConstant.Groups_Columns.COLUMN_GROUP_ID +"=?", new String[]{viewTagId});
	        	getActivity().getContentResolver().delete(DBConstant.Groups_Contacts_Columns.CONTENT_URI, DBConstant.Groups_Contacts_Columns.COLUMN_GROUP_ID +"=?" , new String[]{viewTagId});
	        	refreshGroupsData();
	        	mAdapter.notifyDataSetChanged();
	        	dialog.cancel();
			}
		});
		
		mDialogBtnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		dialog.show();
	}

	
	// ADD CONTACTS TO CONTACTS ADAPTER

	public void refreshGroupsData() {
		Cursor cr = getActivity().getContentResolver().query(
				DBConstant.Groups_Columns.CONTENT_URI, null, null, null,
				DBConstant.Groups_Columns.COLUMN_GROUP_NAME + " ASC");
		if (cr.getCount() > 0) {
			arrListGroups.clear();

			int intColumnGroupId = cr
					.getColumnIndex(DBConstant.Groups_Columns.COLUMN_GROUP_ID);
			int intColumnGroupName = cr
					.getColumnIndex(DBConstant.Groups_Columns.COLUMN_GROUP_NAME);
			int intColumnGroupDp = cr
					.getColumnIndex(DBConstant.Groups_Columns.COLUMN_GROUP_DP);

			GroupDTO group;
			while (cr.moveToNext()) {
				group = new GroupDTO();

				group.setGroupId(cr.getString(intColumnGroupId));
				group.setGroupName(cr.getString(intColumnGroupName));
				group.setGroupDp(cr.getString(intColumnGroupDp));
				
				arrListGroups.add(group);
			}
		}else{
			arrListGroups.clear();
		}
		if (cr != null) {
			cr.close();
		}

		if (arrListGroups.size() > 0) {
			mAdapter = new GroupListAdapter(arrListGroups);
			mListView.setAdapter(mAdapter); // EU ZM004
		}
	}

	// VIEW ADAPTER
	/*
	 * Group List Adapter
	 */

	public class GroupListAdapter extends BaseAdapter {
		ArrayList<GroupDTO> arrListGroups;

		public GroupListAdapter(ArrayList<GroupDTO> arrListGroups) {
			this.arrListGroups = arrListGroups;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrListGroups.size();
			// return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;

			LayoutInflater vi = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.item_list_groups, null);

			TextView txtGroupName = (TextView) view
					.findViewById(R.id.list_item_group_name);

			txtGroupName.setTypeface(styleFont);

			txtGroupName.setText(arrListGroups.get(position).getGroupName());

			view.setTag(R.id.TAG_GROUP_ID, arrListGroups.get(position)
					.getGroupId());
			view.setTag(R.id.TAG_GROUP_NAME, arrListGroups.get(position)
					.getGroupName());
//			view.setTag(R.id.TAG_GROUP_DP, arrListGroups.get(position)
//					.getGroupDp());
			view.setTag(R.id.TAG_GROUP_DP, "groupDp");

			return view;

		}
	}

}
