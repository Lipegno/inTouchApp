package com.example.intouch;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class InTouchAppService extends Service {

    private static final String TAG = "InTouchService";
    private static final String UPDATING_WALLPAPER_ACTION = "UpdatingWallPaperAction";
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       // throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
        );

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText("Service is running")
                .setContentTitle("Service enabled")
                .setSmallIcon(R.drawable.circle);

        startForeground(1001, notification.build());

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onCreate(){
        Toast.makeText(this,"InTouchApp Service Created",Toast.LENGTH_SHORT).show();
       // wreceiver =  new WallPaperStatusReceiver();
        //IntentFilter filter = new IntentFilter();
        //filter.addAction(UPDATING_WALLPAPER_ACTION);
        //registerReceiver(wreceiver,filter);

        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter(UPDATING_WALLPAPER_ACTION)
        );

        Log.i(TAG, "Receiver registered");
        ScreenManager.getInstance().initWallpapers(getApplicationContext());

        super.onCreate();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle data = intent.getExtras();
            String type = data.getString("type");
            if(type.equals("mood")){
                String mood = data.getString("mood");
                Log.i(TAG,"Type : "+type+" Mood: "+mood);
                ScreenManager.getInstance().UpdatePartnerColor(mood,getApplicationContext());
                Intent i = new Intent(context, InTouchWidget.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
// since it seems the onUpdate() is only fired on that:
                int[] ids = AppWidgetManager.getInstance(getApplication())
                        .getAppWidgetIds(new ComponentName(getApplication(), InTouchWidget.class));
                i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                i.putExtra("mood",mood);
                i.setAction(InTouchWidget.WIDGET_PARTNER_MOOD_CHANGE);
                sendBroadcast(i);
            } else if(type.equals("emoji")){
                Log.i(TAG, "Changing emojis");
                String mood = data.getString("emoji");
                Intent i = new Intent(context, InTouchWidget.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
// since it seems the onUpdate() is only fired on that:
                int[] ids = AppWidgetManager.getInstance(getApplication())
                        .getAppWidgetIds(new ComponentName(getApplication(), InTouchWidget.class));
                i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                i.putExtra("emoji",mood);
                i.setAction(InTouchWidget.WIDGET_EMOJI_CHANGE);
                sendBroadcast(i);
            }


        }
    };


    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }


}