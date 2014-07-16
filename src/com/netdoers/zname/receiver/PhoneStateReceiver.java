package com.netdoers.zname.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.netdoers.zname.BuildConfig;
import com.netdoers.zname.service.IncomingOutgoingPushService;

public class PhoneStateReceiver extends BroadcastReceiver {
private static final String TAG = PhoneStateReceiver.class.getSimpleName();
public static final String INTENT_EXTRA = "number";
public static final String INTENT_IS_BUSY = "isBusy";
private static ComponentName comp;
/**
 * TRUE CALLER IMPLEMENTATION 
 */

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String str = intent.getAction();
		comp = new ComponentName(context.getPackageName(),IncomingOutgoingPushService.class.getName());
		
		if ("android.intent.action.PHONE_STATE".equals(str))
			trueCallerInComing(context, intent);

		if ("android.intent.action.NEW_OUTGOING_CALL".equals(str))
			trueCallerOutgoing(context, intent);
	}
	  private void trueCallerInComing(Context context, Intent intent){
	    String callState = intent.getStringExtra("state");
	    if ("RINGING".equals(callState)){
	    	
			String incomingNumber = intent.getStringExtra("incoming_number");
			intent.putExtra(INTENT_EXTRA, incomingNumber);
			intent.putExtra(INTENT_IS_BUSY, true);
			context.startService((intent.setComponent(comp)));
			
			if(BuildConfig.DEBUG)
				Log.i(TAG, "RINGING SENDS BUSY");
			
	    }else if ("OFFHOOK".equals(callState)){
	    	
	    	String incomingNumber = intent.getStringExtra("incoming_number");
			intent.putExtra(INTENT_EXTRA, incomingNumber);
			intent.putExtra(INTENT_IS_BUSY, true);
			context.startService((intent.setComponent(comp)));
			
			if (BuildConfig.DEBUG)
				Log.i(TAG, "OFFHOOK SENDS BUSY");
			
	    }else if("IDLE".equals(callState)){
	    	
	    	String incomingNumber = intent.getStringExtra("incoming_number");
			intent.putExtra(INTENT_EXTRA, incomingNumber);
			intent.putExtra(INTENT_IS_BUSY, false);
			context.startService((intent.setComponent(comp)));
			
			if (BuildConfig.DEBUG)
				Log.i(TAG, "IDLE SENDS AVAILABLE");
			
	    }
	   }
	  
	  private void trueCallerOutgoing(Context context, Intent intent)
	  {
	    String callState = intent.getStringExtra("state");
	    if ("RINGING".equals(callState)){
	    	
	    	String outgoingNumber = intent.getStringExtra("android.intent.extra.PHONE_NUMBER");
	    	intent.putExtra(INTENT_EXTRA, outgoingNumber);
			intent.putExtra(INTENT_IS_BUSY, true);
			context.startService((intent.setComponent(comp)));
	    	
			if (BuildConfig.DEBUG)
				Log.i(TAG, "RINGING SENDS BUSY");
			
	    }else if ("OFFHOOK".equals(callState)){
	    	
	    	String outgoingNumber = intent.getStringExtra("android.intent.extra.PHONE_NUMBER");
	    	intent.putExtra(INTENT_EXTRA, outgoingNumber);
			intent.putExtra(INTENT_IS_BUSY, true);
			context.startService((intent.setComponent(comp)));
	    	
			if (BuildConfig.DEBUG)
				Log.i(TAG, "OFFHOOK SENDS BUSY");
			
	    }else if("IDLE".equals(callState)){
	    	
	    	String outgoingNumber = intent.getStringExtra("android.intent.extra.PHONE_NUMBER");
	    	intent.putExtra(INTENT_EXTRA, outgoingNumber);
			intent.putExtra(INTENT_IS_BUSY, false);
			context.startService((intent.setComponent(comp)));
			
			if (BuildConfig.DEBUG)
				Log.i(TAG, "IDLE SENDS AVAILABLE");
			
	    }
	  }
	  
/**
 * STACKOVERFLOW PHONESTATERECEIVER WORKING WITH SAMSUNG ST-7652
 */


/*    @Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER); // outgoing call
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
			Log.i(TAG, "CALL OUT:" + phoneNumber);
			onOutgoingCall(tm.getCallState(), intent);
		} else {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
			onIncomingCall(tm.getCallState(), intent);
		}
	}

   public void onIncomingCall(int callState , Intent intent){
       switch (callState) {
       case TelephonyManager.CALL_STATE_RINGING:  // incoming call
       	Log.i(TAG, "CALL STATE RINGING" + String.valueOf(callState) + " incoming number " + intent.getStringExtra("incoming_number"));
           incomingFlag = true;
           incoming_number = intent.getStringExtra("incoming_number");
           Log.i(TAG, "RINGING :" + incoming_number);
           break;
       case TelephonyManager.CALL_STATE_OFFHOOK:
       	Log.i(TAG, "CALL STATE OFFHOOK" + String.valueOf(callState) + " incoming number " + intent.getStringExtra("incoming_number"));
           if (incomingFlag) {
               Log.i(TAG, "incoming ACCEPT :" + incoming_number);
           }
           break;

       case TelephonyManager.CALL_STATE_IDLE:
       	Log.i(TAG, "CALL STATE IDLE" + String.valueOf(callState) + " incoming number " + intent.getStringExtra("incoming_number"));
           if (incomingFlag) {     // hang up
               Log.i(TAG, "incoming IDLE, number:" + incoming_number);
           }
           break;
       }
   }
   
   public void onOutgoingCall(int callState , Intent intent){
       switch (callState) {
       case TelephonyManager.CALL_STATE_RINGING:  // incoming call
       	Log.i(TAG, "CALL STATE RINGING" + String.valueOf(callState) + " outgoing number " + intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
           incomingFlag = true;
           incoming_number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
           Log.i(TAG, "RINGING :" + incoming_number);
           break;
       case TelephonyManager.CALL_STATE_OFFHOOK:
       	Log.i(TAG, "CALL STATE OFFHOOK" + String.valueOf(callState) + " outgoing number " + intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
           if (incomingFlag) {
               Log.i(TAG, "incoming ACCEPT :" + incoming_number);
           }
           break;

       case TelephonyManager.CALL_STATE_IDLE:
       	Log.i(TAG, "CALL STATE IDLE" + String.valueOf(callState) + " outgoing number " + intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
           if (incomingFlag) {     // hang up
               Log.i(TAG, "incoming IDLE, number:" + incoming_number);
           }
           break;
       }
   }*/


}