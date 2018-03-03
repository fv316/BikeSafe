package com.example.rahulberry.googlemaps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import com.example.rahulberry.googlemaps.all_map_tests.MainActivity;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by rahulberry on 28/02/2018.
 */

public class SmsListener extends BroadcastReceiver {
    public static final String TAG = "OnReceive Called";

    @Override
    public void onReceive(Context context, Intent intent) {
        String string = "boob";
        Log.i(TAG, "in Receiver. intent.getAction():" + intent.getAction());
        if (intent.getAction()
                .equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras(); // ---get the SMS message passed
            // in---
            if (bundle != null) {
                // get sms objects
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus.length == 0) {
                    return;
                }
                try {
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < pdus.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        sb.append(messages[i].getMessageBody());
                        String sender = messages[0].getOriginatingAddress();
                        String message = sb.toString();
                        BusProvider.getInstance().post(new coordinates(message));
                    }
                } catch (Exception e) {
                     Log.d("Exception caught",e.getMessage());
                }
            }

        }
    }

}
