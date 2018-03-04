package com.example.rahulberry.googlemaps;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

/**
 * Created by niall on 04/03/2018.
 */

public class NotificationHelper extends ContextWrapper {

    private static final String CHANNEL_ID = "com.example.rahulberry.googlemaps.ONE";
    private static final String CHANNEL_NAME = "Channel One";
    private NotificationManager manager;
    public NotificationHelper(Context base) {
        super(base);
    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public NotificationCompat.Builder getnotificationChannelNotification(String title, String body){
        return new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentText(body)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true);
    }
}