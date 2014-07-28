package com.netdoers.zname.service;

import org.json.JSONObject;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.netdoers.zname.R;
import com.netdoers.zname.sqlite.DBConstant;
import com.netdoers.zname.ui.MotherActivity;
import com.netdoers.zname.ui.NotificationActivity;
import com.netdoers.zname.utils.JSONFileWriter;

public class GcmIntentService extends IntentService {
    
	public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    
    static final String TAG = "GCMService";
    static final String MESSAGE_TYPE_REQUEST = "pending_request";
    static final String MESSAGE_TYPE_APPROVAL = "approval";
    String notificationContent = null;
    String notificationTitle = null;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

//        sendNotification("Received: " + extras.toString());
        Log.i(TAG, "Received: " + extras.toString());
        
        
        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) 
            {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) 
            {
                sendNotification("Deleted messages on server: " +extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) 
            {
                // This loop represents the service doing some work.
                for (int i=0; i<5; i++) {
                    Log.i(TAG, "Working... " + (i+1)+ "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                sendNotification("Received: " + extras.toString());
                Log.i(TAG, "Received: " + extras.toString());
            }
            else if (MESSAGE_TYPE_REQUEST.equalsIgnoreCase(messageType)) 
            {
                // This loop represents the service doing some work.                
                Log.i(TAG, "Working..." + SystemClock.elapsedRealtime());
				try {
					String message = extras.getString("message");
					JSONObject msgJsonObject = new JSONObject(message);
//	                sendNotification(msgJsonObject.getString("zname") + " sent your request for approval");
	                sendNotification("Zname", msgJsonObject.getString("zname") + " sent your request for approval", messageType);
				} 
				catch (Exception e){
				}
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
//                sendNotification("Received: " + extras.toString());
                Log.i(TAG, "Received: " + extras.toString());
            }
            else if (MESSAGE_TYPE_APPROVAL.equalsIgnoreCase(messageType)) 
            {
                // This loop represents the service doing some work.
                
                Log.i(TAG, "Working..." + SystemClock.elapsedRealtime());
				try {
//					sendNotification("Received: " + extras.toString());
					String message = extras.getString("message");
					JSONObject msgJsonObject = new JSONObject(message);
					ContentValues values = new ContentValues();
					values.put(DBConstant.All_Contacts_Columns.COLUMN_CONTACT_ID, System.currentTimeMillis());
					values.put(DBConstant.All_Contacts_Columns.COLUMN_ZNAME, msgJsonObject.getString("zname"));
					values.put(DBConstant.All_Contacts_Columns.COLUMN_ZNAME_NUMBER, msgJsonObject.getString("contact_number"));
					values.put(DBConstant.All_Contacts_Columns.COLUMN_ZNAME_DP_URL_SMALL, msgJsonObject.getString("profile_pic"));
					values.put(DBConstant.All_Contacts_Columns.COLUMN_DISPLAY_NAME, msgJsonObject.getString("full_name"));
					getContentResolver().insert(DBConstant.All_Contacts_Columns.CONTENT_URI, values);
					sendNotification("Zname", msgJsonObject.getString("zname") + " added you as a friend", messageType);
					try{
						JSONFileWriter.jsonFriendList(
								String.valueOf(System.currentTimeMillis()),
								msgJsonObject.getString("zname"),
								msgJsonObject.getString("contact_number"),
								msgJsonObject.getString("full_name"),
								msgJsonObject.getString("profile_pic"));
					}catch(Exception e){
						Log.i(TAG, e.toString());
					}
				} 
				catch (Exception e){
				}
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                
                // Post notification of received message.
//                sendNotification("New Notification", notificationContent, "notification");
                Log.i(TAG, "Received: " + extras.toString());
            }
            
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String title , String content , String where ) {
        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent;
        if(where.equalsIgnoreCase(MESSAGE_TYPE_REQUEST)){
        	contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, NotificationActivity.class), 0);	
        }
        else{
        	contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, MotherActivity.class), 0);
        }
        

        NotificationCompat.Builder mBuilder =new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
//        .setContentTitle("GCM Notification")
        .setContentTitle(title)
        .setAutoCancel(true)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(title))
        .setContentText(content);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
    
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, NotificationActivity.class), 0);	
        

        NotificationCompat.Builder mBuilder =new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
//        .setContentTitle("GCM Notification")
        .setContentTitle("Zname")
        .setAutoCancel(true)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
