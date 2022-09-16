package com.example.intouch;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.intouch.dao.DAOActivityLog;
import com.example.intouch.dao.DAOConnection;
import com.example.intouch.helpers.Callback;
import com.example.intouch.dbmodels.ActivityLog;
import com.example.intouch.dbmodels.Connection;
import com.example.intouch.models.FCMNotification;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class InTouchWidget extends AppWidgetProvider {

    public static String WIDGET_BUTTON = "WIDGET_BUTTON";
    private static final String TAG = "InTouchWidget";

    private static final int LEFT_BTN = 100;
    private static final int RIGHT_BTN = 101;
    private static final int HEART_BTN = 102;
    private static final int CRY_BTN = 103;
    private static final int HUG_BTN = 104;
    private static final int MUSCLE_BTN = 105;
    private static final int NEGATIVE_COLOR_BTN = 106;
    private static final int NEUTRAL_COLOR_BTN = 107;
    private static final int POSITIVE_COLOR_BTN = 108;

    static private void setupOnClickListener(RemoteViews view, Context context, int view_id, int appWidgetId, int code){

        Intent intent = new Intent(context,InTouchWidget.class);
        intent.setAction(WIDGET_BUTTON);
        intent.putExtra("code",code);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                code,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        view.setOnClickPendingIntent(view_id, pendingIntent );
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        CharSequence widgetText = context.getString(R.string.appwidget_right_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.in_touch_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        setupOnClickListener(views,context,R.id.appwidget_left_button,appWidgetId,LEFT_BTN);
        setupOnClickListener(views,context,R.id.appwidget_right_button,appWidgetId,RIGHT_BTN);
        setupOnClickListener(views,context,R.id.heart_button,appWidgetId,HEART_BTN);
        setupOnClickListener(views,context,R.id.cry_button,appWidgetId,CRY_BTN);
        setupOnClickListener(views,context,R.id.hug_button,appWidgetId,HUG_BTN);
        setupOnClickListener(views,context,R.id.muscle_button,appWidgetId,MUSCLE_BTN);
        setupOnClickListener(views,context,R.id.first_color_button,appWidgetId, NEGATIVE_COLOR_BTN);
        setupOnClickListener(views,context,R.id.second_color_button,appWidgetId, NEUTRAL_COLOR_BTN);
        setupOnClickListener(views,context,R.id.third_color_button,appWidgetId, POSITIVE_COLOR_BTN);

        hideLayouts(views);

        //views.setOnClickFillInIntent(R.id.appwidget_button,intent);
        Log.i(TAG,"Updating App Widget");

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @SuppressLint("ResourceType")
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context,intent);
        Log.i(TAG,intent.getAction());
        Bundle bundle = intent.getExtras();
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.in_touch_widget);

        int action=-1;
        if (bundle != null) {
            action = bundle.getInt("code");
            Log.i(TAG,"code " + action);
        }

        if(action == RIGHT_BTN){
            RightBtnAction(context, views);
        }else if(action == LEFT_BTN){
            LeftBtnAction(context, views);
        }else if(action == HEART_BTN){
            HeartBtnAction(context, views);
        }else if(action == CRY_BTN){
            CryBtnAction(context, views);
        }else if(action == HUG_BTN){
            HugBtnAction(context, views);
        }else if(action == MUSCLE_BTN){
            MuscleBtnAction(context, views);
        }else if(action == NEGATIVE_COLOR_BTN){
            NegativeColorBtnAction(context, views);
        }else if(action == NEUTRAL_COLOR_BTN){
            NeutralColorBtnAction(context, views);
        }else if(action == POSITIVE_COLOR_BTN){
            PositiveColorBtnAction(context, views);
        }
    }

    private static void RightBtnAction(Context context, RemoteViews views){
        Log.i(TAG,"Right Button pressed ");
        hideLayouts(views);
        ShowColors(views);
        forceWidgetUpdate(context, views);
    }

    private static void LeftBtnAction(Context context, RemoteViews views){
        Log.i(TAG,"Left Button pressed ");
        hideLayouts(views);
        ShowEmojis(views);
        forceWidgetUpdate(context, views);
    }

    private static void HeartBtnAction(Context context, RemoteViews views){
        GetConnectionInformation(context, new Callback<Connection>() {
            @Override
            public void execute(Connection connection) {
                ActivityLog activity = GenerateNewActivity("Change Emoji: Heart", connection);

                /// Add the activity log to db
                DAOActivityLog.getInstance().add(activity).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        /// Continue flow if activity log added to db
                        Log.i(TAG,"Heart Button pressed ");

                        views.setTextViewText(R.id.left_button_emoji, context.getResources().getString(R.string.heart_emoji));
                        UpdateWidget(context, views);

                        /// Send notification to the user
                        FCMNotification notification = GenerateNewNotification(connection, "emoji", context.getResources().getString(R.string.heart_emoji));
                        FCMSend.pushNotification(context, notification.deviceToken, notification.title, notification.message);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error on adding activity log connection.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private static void CryBtnAction(Context context, RemoteViews views){
        GetConnectionInformation(context, new Callback<Connection>() {
            @Override
            public void execute(Connection connection) {
                ActivityLog activity = GenerateNewActivity("Change Emoji: Cry", connection);

                /// Add the activity log to db
                DAOActivityLog.getInstance().add(activity).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        /// Continue flow if activity log added to db
                        Log.i(TAG,"Cry Button pressed ");

                        views.setTextViewText(R.id.left_button_emoji, context.getResources().getString(R.string.cry_emoji));
                        UpdateWidget(context, views);

                        /// Send notification to the user
                        FCMNotification notification = GenerateNewNotification(connection, "emoji", context.getResources().getString(R.string.cry_emoji));
                        FCMSend.pushNotification(context, notification.deviceToken, notification.title, notification.message);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error on adding activity log connection.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private static void HugBtnAction(Context context, RemoteViews views){
        GetConnectionInformation(context, new Callback<Connection>() {
            @Override
            public void execute(Connection connection) {
                ActivityLog activity = GenerateNewActivity("Change Emoji: Hug", connection);

                /// Add the activity log to db
                DAOActivityLog.getInstance().add(activity).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        /// Continue flow if activity log added to db
                        Log.i(TAG,"Hug Button pressed ");

                        views.setTextViewText(R.id.left_button_emoji, context.getResources().getString(R.string.hug_emoji));
                        UpdateWidget(context, views);

                        /// Send notification to the user
                        FCMNotification notification = GenerateNewNotification(connection, "emoji", context.getResources().getString(R.string.hug_emoji));
                        FCMSend.pushNotification(context, notification.deviceToken, notification.title, notification.message);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error on adding activity log connection.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private static void MuscleBtnAction(Context context, RemoteViews views){
        GetConnectionInformation(context, new Callback<Connection>() {
            @Override
            public void execute(Connection connection) {
                ActivityLog activity = GenerateNewActivity("Change Emoji: Muscle", connection);

                /// Add the activity log to db
                DAOActivityLog.getInstance().add(activity).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        /// Continue flow if activity log added to db
                        Log.i(TAG,"Muscle Button pressed ");

                        views.setTextViewText(R.id.left_button_emoji, context.getResources().getString(R.string.muscle_emoji));
                        UpdateWidget(context, views);

                        /// Send notification to the user
                        FCMNotification notification = GenerateNewNotification(connection, "emoji", context.getResources().getString(R.string.muscle_emoji));
                        FCMSend.pushNotification(context, notification.deviceToken, notification.title, notification.message);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error on adding activity log connection.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private static void NegativeColorBtnAction(Context context, RemoteViews views){
        GetConnectionInformation(context, new Callback<Connection>() {
            @Override
            public void execute(Connection connection) {
                ActivityLog activity = GenerateNewActivity("Change Color: Negative", connection);

                /// Add the activity log to db
                DAOActivityLog.getInstance().add(activity).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        /// Continue flow if activity log added to db
                        Log.i(TAG,"Negative Color Button pressed ");
                        views.setInt(R.id.appwidget_right_button, "setBackgroundResource", R.drawable.circle_pink);
                        UpdateWidget(context, views);
                        ScreenManager.getInstance().UpdateColor(Color.parseColor("#66ff2d55"), context);

                        /// Send notification to the user
                        FCMNotification notification = GenerateNewNotification(connection, "mood", "negative");
                        FCMSend.pushNotification(context, notification.deviceToken, notification.title, notification.message);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error on adding activity log connection.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private static void NeutralColorBtnAction(Context context, RemoteViews views){
        GetConnectionInformation(context, new Callback<Connection>() {
            @Override
            public void execute(Connection connection) {
                ActivityLog activity = GenerateNewActivity("Change Color: Neutral", connection);

                /// Add the activity log to db
                DAOActivityLog.getInstance().add(activity).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        /// Continue flow if activity log added to db
                        Log.i(TAG,"Neutral Button pressed ");
                        views.setInt(R.id.appwidget_right_button, "setBackgroundResource", R.drawable.circle_white);
                        ScreenManager.getInstance().UpdateColor(Color.parseColor("#66C3C3C3"), context);
                        UpdateWidget(context, views);

                        /// Send notification to the user
                        FCMNotification notification = GenerateNewNotification(connection, "mood", "neutral");
                        FCMSend.pushNotification(context, notification.deviceToken, notification.title, notification.message);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error on adding activity log connection.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private static void PositiveColorBtnAction(Context context, RemoteViews views){
        GetConnectionInformation(context, new Callback<Connection>() {
            @Override
            public void execute(Connection connection) {
                ActivityLog activity = GenerateNewActivity("Change Color: Positive", connection);

                /// Add the activity log to db
                DAOActivityLog.getInstance().add(activity).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        /// Continue flow if activity log added to db
                        Log.i(TAG,"Positive Button pressed ");
                        views.setInt(R.id.appwidget_right_button, "setBackgroundResource", R.drawable.circle_green);
                        UpdateWidget(context, views);
                        ScreenManager.getInstance().UpdateColor(Color.parseColor("#6672a682"), context);

                        /// Send notification to the user
                        FCMNotification notification = GenerateNewNotification(connection, "mood", "positive");
                        FCMSend.pushNotification(context, notification.deviceToken, notification.title, notification.message);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error on adding activity log connection.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private static ActivityLog GenerateNewActivity(String activityName, Connection connection){
        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
        String now = ISO_8601_FORMAT.format(new Date());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        ActivityLog log = new ActivityLog();
        log.dateTime = now;
        if(connection.firstUser.uid.equals(user.getUid())){
            log.senderEmail = connection.firstUser.email;
            log.receiverEmail = connection.secondUser.email;
        }else{
            log.senderEmail = connection.secondUser.email;
            log.receiverEmail = connection.firstUser.email;
        }
        log.activity = activityName;
        return log;
    }

    private static FCMNotification GenerateNewNotification(Connection connection, String title, String message){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String receiverToken = "";

        if(connection.firstUser.uid.equals(user.getUid())){
            receiverToken = connection.secondUser.deviceToken;
        }else{
            receiverToken = connection.firstUser.deviceToken;
        }

        return new FCMNotification(receiverToken, title, message);
    }

    private static void GetConnectionInformation(Context context, Callback<Connection> onSucces){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DAOConnection.getInstance().getConnectionByAUserUid(user.getUid(), new Callback<Connection>() {
            @Override
            public void execute(Connection connection) {
                onSucces.execute(connection);
            }
        }, new Callback() {
            @Override
            public void execute(Object object) {
                Toast.makeText(context, "Error on getting connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void UpdateWidget(Context context, RemoteViews views){
        hideLayouts(views);
        forceWidgetUpdate(context, views);
    }

    private static void forceWidgetUpdate(Context context, RemoteViews views){
        Log.i(TAG,"Updating widget views");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, InTouchWidget.class));
        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }

    private static void hideLayouts(RemoteViews views){
        views.setViewVisibility(R.id.emoji_layout, View.INVISIBLE);
        views.setViewVisibility(R.id.colors_layout, View.INVISIBLE);
    }

    private static void ShowEmojis(RemoteViews views){
        views.setViewVisibility(R.id.emoji_layout, View.VISIBLE);
    }

    private static void ShowColors(RemoteViews views){
        views.setViewVisibility(R.id.colors_layout, View.VISIBLE);
    }
}