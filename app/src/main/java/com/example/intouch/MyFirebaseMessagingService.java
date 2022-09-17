package com.example.intouch;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @SuppressLint("NewApi")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String text = remoteMessage.getNotification().getBody();
        String CHANNEL_ID = "MESSAGE";
        CharSequence name;

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Message Notification",
                NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Context context = getApplicationContext();
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true);
        NotificationManagerCompat.from(this).notify(1, notification.build());

/*
        private static final int LEFT_BTN = 100;
        private static final int RIGHT_BTN = 101;
        private static final int HEART_BTN = 102;
        private static final int CRY_BTN = 103;
        private static final int HUG_BTN = 104;
        private static final int MUSCLE_BTN = 105;
        private static final int PINK_COLOR_BTN = 106;
        private static final int GRAY_COLOR_BTN = 107;
        private static final int GREEN_COLOR_BTN = 108;

 */

        //AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);


        // TO DO
        // If title is "mood"
        // check if text is positive/negative/neutral and change the colors of partner side accordingly

        // If the title is "emoji"
        // update partner emoji with the hex code (which is in the text variable)

        super.onMessageReceived(remoteMessage);
        }
}
