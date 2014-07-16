/* HISTORY
 * CATEGORY			 :- BROADCAST RECEIVER
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- TRIGGER WHEN OUTGOING CALL FROM DEVICE
 * DESCRIPTION       :- TRIGGER BUSY DATA TO WEB-SERVICE TWICE, ONCE WHEN USER IS BUSY AND OTHER WHEN USER IS AGAIN AVAILABLE
 * JAVA BENEFITS     :- HIGHLY COHESIVE | LOOSLY COUPLED | GOOD DESIGN PATTERN
 * LIMITATION        :- SEND DATA OVER WIFI OR HIGH SPEED INTERNET
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * BR001      VIKALP PATEL     16/05/2014                       CREATED 
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.receiver;

import com.netdoers.zname.BuildConfig;
import com.netdoers.zname.service.IncomingOutgoingPushService;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class ReceiverOutGoingCall extends BroadcastReceiver {

	private Context con;
    private static ComponentName comp;
    private static Intent inte;
    private boolean isReceived = false;
    private int counter = 0;
    public static final String TAG = "ReceiverOutgoingCall";
    
    private static String phoneNumber;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        con = context;
        inte = intent;
        
        phoneNumber =  intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        comp = new ComponentName(context.getPackageName(),IncomingOutgoingPushService.class.getName());
        
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        MyPhoneStateListener phoneStateListener = new MyPhoneStateListener();
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        
/*        if(BuildConfig.DEBUG)
        	Log.i(TAG, "Service :" +comp + " phoneNumber :" +phoneNumber + " TelephonyManager :" +telephonyManager);*/
    }

    private class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch(state){
            case TelephonyManager.CALL_STATE_RINGING:
            	if(BuildConfig.DEBUG)
            		Log.i(TAG, "CALL_STATE_RINGING "+" counter " + counter+ " IncomingNumber " +incomingNumber + " isReceived " +isReceived);
            	
            	try{
            		if(!isReceived){
						inte.putExtra(ReceiverInComingCall.INTENT_EXTRA, phoneNumber);
						inte.putExtra(ReceiverInComingCall.INTENT_IS_BUSY, true);
						con.startService((inte.setComponent(comp)));
						isReceived = true;
						if(BuildConfig.DEBUG)
							Toast.makeText(con, "Call Outgoing : " + phoneNumber + " Busy", Toast.LENGTH_LONG).show();
//						Log.i(TAG, "Call State" + state + " TelephonyManager.CALL_STATE_RINGING");
				}
            	}catch(Exception e){
            	Log.e(TAG, e.toString());	
            	}
            	break;
            	
            case TelephonyManager.CALL_STATE_OFFHOOK:
            	if(BuildConfig.DEBUG)
            		Log.i(TAG, "CALL_STATE_OFFHOOK "+" counter " + counter+ " IncomingNumber " +incomingNumber + " isReceived " +isReceived);
            	
            	try{
            		if(!isReceived){
						inte.putExtra(ReceiverInComingCall.INTENT_EXTRA, phoneNumber);
						inte.putExtra(ReceiverInComingCall.INTENT_IS_BUSY, true);
						con.startService((inte.setComponent(comp)));
						isReceived = true;
						if(BuildConfig.DEBUG)
							Toast.makeText(con, "Call Outgoing : " + phoneNumber + " Busy", Toast.LENGTH_LONG).show();
//						Log.i(TAG, "Call State" + state + " TelephonyManager.CALL_STATE_OFFHOOK");
				}
            	}catch(Exception e){
            		Log.e(TAG, e.toString());
            	}
            	break;
            	
            case TelephonyManager.CALL_STATE_IDLE:
            	if(BuildConfig.DEBUG)
            		Log.i(TAG, "CALL_STATE_IDLE "+" counter " + counter+ " IncomingNumber " +incomingNumber + " isReceived " +isReceived);
            	
            	try{
            		if (!TextUtils.isEmpty(phoneNumber)) {
                		counter++;
    					if(counter == 2){
    						inte.putExtra(ReceiverInComingCall.INTENT_EXTRA, phoneNumber);
    						inte.putExtra(ReceiverInComingCall.INTENT_IS_BUSY, false);
    						con.startService((inte.setComponent(comp)));
    						phoneNumber=null;
    						counter = 0;
    						isReceived = false; //ADDED
    						if(BuildConfig.DEBUG)
    							Toast.makeText(con, "Outgoing End: " + phoneNumber + " Available", Toast.LENGTH_LONG).show();
//    						Log.i(TAG, "Call State" + state + " TelephonyManager.CALL_STATE_IDLE");
    					}
    				}
            	}catch(Exception e){
            		Log.e(TAG, e.toString());
            	}
            	break;
            }
//            if(state == TelephonyManager.CALL_STATE_RINGING){
//					if(!isReceived){
//						if(!TextUtils.isEmpty(phoneNumber)){
//							inte.putExtra(ReceiverInComingCall.INTENT_EXTRA, phoneNumber);
//							inte.putExtra(ReceiverInComingCall.INTENT_IS_BUSY, true);
//							con.startService((inte.setComponent(comp)));
//							isReceived = true;
//							if(BuildConfig.DEBUG)
//								Toast.makeText(con, "Call Outgoing : " + phoneNumber + " Busy", Toast.LENGTH_LONG).show();
//						}
//					}
//            }
//            
//            //start call
//            if(state == TelephonyManager.CALL_STATE_OFFHOOK){
//                return;
//            }
//
//            //end call
//           if(state == TelephonyManager.CALL_STATE_IDLE){
//               if (!TextUtils.isEmpty(phoneNumber)) {
//					inte.putExtra(ReceiverInComingCall.INTENT_EXTRA, phoneNumber);
//					inte.putExtra(ReceiverInComingCall.INTENT_IS_BUSY, false);
//					con.startService((inte.setComponent(comp)));
//					phoneNumber=null;
//					if(BuildConfig.DEBUG)
//						Toast.makeText(con, "Outgoing End: " + phoneNumber + " Available", Toast.LENGTH_LONG).show();
//				}
//            }
        }
    }
}