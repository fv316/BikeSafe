package com.example.rahulberry.googlemaps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.example.rahulberry.googlemaps.all_map_tests.MapsActivity;

/**
 * Created by niall on 13/02/2018.
 */

public class SmsBroadcastReceiver extends BroadcastReceiver {
    MapFragment mapFragment;

    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();

                if (address == "+447541241808") {
                    mapFragment = new MapFragment();
                    smsMessageStr = smsBody;
                    Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();
                    MapsActivity inst = MapsActivity.instance();
                    inst.updateList(smsMessageStr);
                }
            }
        }
    }
}
