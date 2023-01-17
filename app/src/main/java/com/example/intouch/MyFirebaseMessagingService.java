package com.example.intouch;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private LocalBroadcastManager broadcaster;
    public static String WIDGET_BUTTON = "WIDGET_BUTTON";
    private static final String UPDATING_WALLPAPER_ACTION = "UpdatingWallPaperAction";

    private static final String TAG = "Notification Service";

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
        super.onCreate();
    }

    @SuppressLint("NewApi")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.i(TAG, "TESTE BACKGROUND");
        Map<String, String> data = remoteMessage.getData();
        String mood = "";
        String emoji = "";

        String title = data.get("title");
        if(title.equals("mood")) {
            mood = data.get("body");
            Intent intent = new Intent(UPDATING_WALLPAPER_ACTION);
            intent.putExtra("type", title);
            intent.putExtra("mood", mood);
            broadcaster.sendBroadcast(intent);
            Log.i(TAG, "sending broadcast " + mood);
        }else if(title.equals("emoji")) {
            mood = data.get("body");
            Intent intent = new Intent(UPDATING_WALLPAPER_ACTION);
            intent.putExtra("type", title);
            intent.putExtra("emoji", mood);
            broadcaster.sendBroadcast(intent);
            Log.i(TAG, "sending broadcast " + mood);
        }


        super.onMessageReceived(remoteMessage);

        }
}
