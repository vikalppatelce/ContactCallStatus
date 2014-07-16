/* HISTORY
 * CATEGORY			 :- BROADCAST RECEIVER
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- TRIGGER WHEN INCOMING CALL COMES ON DEVICE
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

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.netdoers.zname.BuildConfig;
import com.netdoers.zname.service.IncomingOutgoingPushService;

/**
 * Created by dinhthao on 6/25/13.
 */
public class ReceiverInComingCall extends BroadcastReceiver {
    
	public  static Context con;
    private static ComponentName comp;
    private static Intent inte;
    private boolean isReceived;
    private int counter;
    
    public static final String TAG ="ReceiverIncomingCall";
    public static final String INTENT_EXTRA = "number";
    public static final String INTENT_IS_BUSY = "isBusy";
    private static String strIncomingNumber = null;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        con = context;
        inte = intent;
        try{
            TelephonyManager phone = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            comp = new ComponentName(context.getPackageName(),IncomingOutgoingPushService.class.getName());
            isReceived = false;
            counter = 0;
    		try {
				if(!Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())){
					MyPhoneStateListener PhoneListener = new MyPhoneStateListener();
		            phone.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);	
		            /*if(BuildConfig.DEBUG)
		            	Log.i(TAG, "Service :" +comp +  " TelephonyManager :" +phone + " intent.getAction() " + intent.getAction());*/
				}
			} catch (NullPointerException e) {
				Log.e(TAG, e.toString());
			}
        }catch (Exception e){
        	Log.e(TAG, e.toString());
        }
    }

    private class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            
            switch(state){
            case TelephonyManager.CALL_STATE_RINGING:
            	Log.i(TAG, "Outside: isReceived: "+ isReceived + "counter: " +counter);
            	try{
                	if(!isReceived){
                		counter++;
                		if(counter == 1){
                	    	strIncomingNumber = incomingNumber;
        					inte.putExtra(INTENT_EXTRA, incomingNumber);
        					inte.putExtra(INTENT_IS_BUSY, true);
        					con.startService((inte.setComponent(comp)));
        					isReceived = true;
        					if(BuildConfig.DEBUG)
        						Toast.makeText(con, "Incoming:" +incomingNumber+ " Busy", Toast.LENGTH_LONG).show();
        					Log.i(TAG, "Inside: isReceived: "+ isReceived + "counter: " +counter);
                		}
            	    }
            	}catch(Exception e){
            		Log.e(TAG, e.toString());
            	}
            	break;
            	
            case TelephonyManager.CALL_STATE_OFFHOOK:
            	break;
            	
            case TelephonyManager.CALL_STATE_IDLE:
            	try{
            		if (!TextUtils.isEmpty(strIncomingNumber)) {
    					inte.putExtra(INTENT_EXTRA, strIncomingNumber);
    					inte.putExtra(INTENT_IS_BUSY, false);
    					con.startService((inte.setComponent(comp)));
    					strIncomingNumber=null;
    					if(BuildConfig.DEBUG)
    						Toast.makeText(con, "End Incoming: "+strIncomingNumber + " Available", Toast.LENGTH_LONG).show();
    					
    				}
            	}catch(Exception e){
            		Log.e(TAG, e.toString());
            	}
            	break;
            }
            /*if(state == TelephonyManager.CALL_STATE_RINGING){
            	    if(!isReceived){
            	    	strIncomingNumber = incomingNumber;
    					inte.putExtra(INTENT_EXTRA, incomingNumber);
    					inte.putExtra(INTENT_IS_BUSY, true);
    					con.startService((inte.setComponent(comp)));
    					isReceived = true;
    					if(BuildConfig.DEBUG)
    						Toast.makeText(con, "Incoming:" +incomingNumber+ " Busy", Toast.LENGTH_LONG).show();
            	    }
            }

            //when my phone answer...
            if(state == TelephonyManager.CALL_STATE_OFFHOOK){
            }

            //end call
            if(state == TelephonyManager.CALL_STATE_IDLE){
				if (!TextUtils.isEmpty(strIncomingNumber)) {
					inte.putExtra(INTENT_EXTRA, strIncomingNumber);
					inte.putExtra(INTENT_IS_BUSY, false);
					con.startService((inte.setComponent(comp)));
					strIncomingNumber=null;
					if(BuildConfig.DEBUG)
						Toast.makeText(con, "End Incoming: "+strIncomingNumber + " Available", Toast.LENGTH_LONG).show();
					
				}
            }*/
            
         }
    }
}