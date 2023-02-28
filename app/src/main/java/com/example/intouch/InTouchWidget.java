package com.example.intouch;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.intouch.settings.EmojiSelectionActivity;
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
    public static String WIDGET_PARTNER_MOOD_CHANGE = "WIDGET_PARTNER_MOOD_CHANGE";
    public static String WIDGET_MY_MOOD_CHANGE = "WIDGET_MY_MOOD_CHANGE";
    public static String WIDGET_MY_EMOJI_CHANGE = "WIDGET_EMOJI_CHANGE";
    public static String WIDGET_PARTNER_EMOJI_CHANGE = "WIDGET_MY_EMOJI_CHANGE";


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
    public static final int EMOJI_CHANGE = 109;
    public static final int PARTNER_COLOR_CHANGE = 110;


    private static int lastClick;

    private SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String MY_PREFERENCE = "InTouch";

    private static String partnerMood;
    private static String partnerEmoji;

    private static String first_emoji;
    private static String second_emoji;
    private static String third_emoji;
    private static String fourth_emoji;

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

        // setting up the widgets again for some reason
        setupOnClickListener(views,context,R.id.appwidget_mood_button,appWidgetId,LEFT_BTN);
        setupOnClickListener(views,context,R.id.right_emoji_btn,appWidgetId,RIGHT_BTN);
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

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.in_touch_widget);
        forceWidgetUpdate(context, views);
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

        int command=-1;
        if (bundle != null) {
            command = bundle.getInt("code");
            Log.i(TAG,"code " + command);
        }
        if(intent.getAction().equals(WIDGET_PARTNER_MOOD_CHANGE)){
            handlePartnerMoodChange(bundle.getString("mood"),context);
        }else if(intent.getAction().equals(WIDGET_MY_EMOJI_CHANGE)){
            handlePartnerEmojiChange(bundle.getString("emoji"),context);
        }
        else if(command == RIGHT_BTN){
            RightBtnAction(context, views);
        }else if(command == LEFT_BTN){
            LeftBtnAction(context, views);
        }else{
            lastClick = -1;
            if(command == HEART_BTN){
                EmojiButtonAction(context, views,HEART_BTN);
            }else if(command == CRY_BTN){
                EmojiButtonAction(context, views,CRY_BTN);
            }else if(command == HUG_BTN){
                EmojiButtonAction(context, views,HUG_BTN);
            }else if(command == MUSCLE_BTN){
                EmojiButtonAction(context, views,MUSCLE_BTN);
            }else if(command == NEGATIVE_COLOR_BTN){
                NegativeColorBtnAction(context, views);
            }else if(command == NEUTRAL_COLOR_BTN){
                NeutralColorBtnAction(context, views);
            }else if(command == POSITIVE_COLOR_BTN){
                PositiveColorBtnAction(context, views);
            }else if(command == EMOJI_CHANGE){
                Log.i(TAG,"emoji change broadcast received");
            }else if(command == PARTNER_COLOR_CHANGE){
                Log.i(TAG,"Partner change broadcast received");
            }
        }

    }

    private void handlePartnerMoodChange(String mood,Context context){
        Log.i(TAG,"Changing the partner mood button "+mood);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.in_touch_widget);
        hideLayouts(views);
        sharedpreferences  = context.getSharedPreferences(MY_PREFERENCE,Context.MODE_PRIVATE);
        sharedpreferences.edit().putString(WIDGET_PARTNER_MOOD_CHANGE,mood).apply();
        sharedpreferences.edit().putString(WIDGET_MY_EMOJI_CHANGE,"").apply();

        if(mood.equals("positive")){
            views.setInt(R.id.appwidget_mood_button, "setBackgroundResource", R.drawable.circle_green);
            UpdateWidget(context, views);
        }else if(mood.equals("negative")){
            views.setInt(R.id.appwidget_mood_button, "setBackgroundResource", R.drawable.circle_pink);
            UpdateWidget(context, views);
        }else if(mood.equals("neutral")){
            views.setInt(R.id.appwidget_mood_button, "setBackgroundResource", R.drawable.circle_white);
            UpdateWidget(context, views);
        }

    }

    private void handlePartnerEmojiChange(String emoji,Context context){
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.in_touch_widget);
        hideLayouts(views);
        sharedpreferences  = context.getSharedPreferences(MY_PREFERENCE,Context.MODE_PRIVATE);
        sharedpreferences.edit().putString(WIDGET_PARTNER_EMOJI_CHANGE,emoji).apply();
        views.setTextViewText(R.id.left_emoji_button, emoji);
        UpdateWidget(context, views);

        /*if(emoji.equals(context.getResources().getString(R.string.cry_emoji))){
            views.setTextViewText(R.id.left_emoji_button, context.getResources().getString(R.string.cry_emoji));
            UpdateWidget(context, views);
        }else if(emoji.equals(context.getResources().getString(R.string.heart_emoji))){
            views.setTextViewText(R.id.left_emoji_button, context.getResources().getString(R.string.heart_emoji));
            UpdateWidget(context, views);
        }else if(emoji.equals(context.getResources().getString(R.string.hug_emoji))){
            views.setTextViewText(R.id.left_emoji_button, context.getResources().getString(R.string.hug_emoji));
            UpdateWidget(context, views);
        }else if(emoji.equals(context.getResources().getString(R.string.muscle_emoji))){
            views.setTextViewText(R.id.left_emoji_button, context.getResources().getString(R.string.muscle_emoji));
            UpdateWidget(context, views);
        }else if(emoji.equals(context.getResources().getString(R.string.no_emoji))){
            views.setTextViewText(R.id.left_emoji_button, "");
            UpdateWidget(context, views);
        }*/
    }

    private static void RightBtnAction(Context context, RemoteViews views){
        if(lastClick==RIGHT_BTN){
            Log.i(TAG,"Closing layout");
            views.setViewVisibility(R.id.emoji_layout, View.INVISIBLE);
            forceWidgetUpdate(context, views);
            lastClick = -1;
            return;
        }
        Log.i(TAG,"Right Button pressed ");
        hideLayouts(views);
        ShowEmojis(views);
        forceWidgetUpdate(context, views);
        lastClick = RIGHT_BTN;
    }

    private static void LeftBtnAction(Context context, RemoteViews views){
        if(lastClick==LEFT_BTN){
            Log.i(TAG,"Closing layout");
            views.setViewVisibility(R.id.colors_layout, View.INVISIBLE);
            forceWidgetUpdate(context, views);
            lastClick = -1;
            return;
        }
        Log.i(TAG,"Left Button pressed ");
        hideLayouts(views);
        ShowColors(views);
        forceWidgetUpdate(context, views);
        lastClick = LEFT_BTN;
    }

    private static void HeartBtnAction(Context context, RemoteViews views){
        hideLayouts(views);
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
                        SharedPreferences prefs  = context.getSharedPreferences(MY_PREFERENCE,Context.MODE_PRIVATE);
                        prefs.edit().putString(WIDGET_MY_EMOJI_CHANGE, context.getResources().getString(R.string.heart_emoji)).apply();
                        views.setTextViewText(R.id.right_emoji_btn, context.getResources().getString(R.string.heart_emoji));
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
        hideLayouts(views);
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
                        SharedPreferences prefs  = context.getSharedPreferences(MY_PREFERENCE,Context.MODE_PRIVATE);
                        prefs.edit().putString(WIDGET_MY_EMOJI_CHANGE, context.getResources().getString(R.string.cry_emoji)).apply();
                        views.setTextViewText(R.id.right_emoji_btn, context.getResources().getString(R.string.cry_emoji));
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
        hideLayouts(views);
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
                        SharedPreferences prefs  = context.getSharedPreferences(MY_PREFERENCE,Context.MODE_PRIVATE);
                        prefs.edit().putString(WIDGET_MY_EMOJI_CHANGE, context.getResources().getString(R.string.hug_emoji)).apply();
                        views.setTextViewText(R.id.right_emoji_btn, context.getResources().getString(R.string.hug_emoji));
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
        hideLayouts(views);
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
                        SharedPreferences prefs  = context.getSharedPreferences(MY_PREFERENCE,Context.MODE_PRIVATE);
                        String teste = context.getResources().getString(R.string.muscle_emoji);
                        prefs.edit().putString(WIDGET_MY_EMOJI_CHANGE, context.getResources().getString(R.string.muscle_emoji)).apply();
                        views.setTextViewText(R.id.right_emoji_btn, context.getResources().getString(R.string.muscle_emoji));
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

    private static void EmojiButtonAction(Context context, RemoteViews views,int button){
        hideLayouts(views);
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
                        SharedPreferences prefs  = context.getSharedPreferences(MY_PREFERENCE,Context.MODE_PRIVATE);
                        String emoji = "";
                        if(button==HEART_BTN){
                            emoji=first_emoji;
                        }else if(button==HUG_BTN){
                            emoji=second_emoji;
                        }else if(button==CRY_BTN){
                            emoji=third_emoji;
                        }else if(button==MUSCLE_BTN){
                            emoji=fourth_emoji;
                        }
                        prefs.edit().putString(WIDGET_MY_EMOJI_CHANGE, emoji).apply();
                        views.setTextViewText(R.id.right_emoji_btn, emoji);
                        UpdateWidget(context, views);

                        /// Send notification to the user
                        FCMNotification notification = GenerateNewNotification(connection, "emoji",emoji);
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
        hideLayouts(views);
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
                        SharedPreferences prefs  = context.getSharedPreferences(MY_PREFERENCE,Context.MODE_PRIVATE);
                        prefs.edit().putString(WIDGET_MY_MOOD_CHANGE, "negative").apply();
                        prefs.edit().putString(WIDGET_PARTNER_EMOJI_CHANGE,"").apply();

                        //cleanup the widget
                        views.setInt(R.id.appwidget_mood_button, "setBackgroundResource", R.drawable.circle_pink);
                        updateColorsSelection(NEGATIVE_COLOR_BTN,views);
                        UpdateWidget(context, views);

                        ScreenManager.getInstance().UpdateColor(context.getResources().getColor(R.color.bad), context);
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
        hideLayouts(views);
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
                        SharedPreferences prefs  = context.getSharedPreferences(MY_PREFERENCE,Context.MODE_PRIVATE);
                        prefs.edit().putString(WIDGET_MY_MOOD_CHANGE, "neutral").apply();
                        prefs.edit().putString(WIDGET_PARTNER_EMOJI_CHANGE,"").apply();


                        //cleanup the widget
                        views.setInt(R.id.appwidget_mood_button, "setBackgroundResource", R.drawable.circle_white);
                        updateColorsSelection(NEUTRAL_COLOR_BTN,views);
                        ScreenManager.getInstance().UpdateColor(context.getResources().getColor(R.color.neutral), context);
                        UpdateWidget(context, views);

                        //Send notification to the user
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
        hideLayouts(views);
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
                        SharedPreferences prefs  = context.getSharedPreferences(MY_PREFERENCE,Context.MODE_PRIVATE);
                        prefs.edit().putString(WIDGET_MY_MOOD_CHANGE, "positive").apply();
                        prefs.edit().putString(WIDGET_PARTNER_EMOJI_CHANGE,"").apply();

                        //cleanup the widget
                        views.setInt(R.id.appwidget_mood_button, "setBackgroundResource", R.drawable.circle_green);
                        updateColorsSelection(POSITIVE_COLOR_BTN,views);
                        ScreenManager.getInstance().UpdateColor(context.getResources().getColor(R.color.good), context);
                        UpdateWidget(context, views);

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

    private static void updateColorsSelection(int color, RemoteViews view){
        if(color==NEGATIVE_COLOR_BTN){
            view.setViewVisibility(R.id.first_color_button,View.GONE);
            view.setViewVisibility(R.id.second_color_button,View.VISIBLE);
            view.setViewVisibility(R.id.third_color_button,View.VISIBLE);
        }else if(color == NEUTRAL_COLOR_BTN){
            view.setViewVisibility(R.id.first_color_button,View.VISIBLE);
            view.setViewVisibility(R.id.second_color_button,View.GONE);
            view.setViewVisibility(R.id.third_color_button,View.VISIBLE);
        }else if(color == POSITIVE_COLOR_BTN){
            view.setViewVisibility(R.id.first_color_button,View.VISIBLE);
            view.setViewVisibility(R.id.second_color_button,View.VISIBLE);
            view.setViewVisibility(R.id.third_color_button,View.GONE);
        }
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

       Log.i(TAG,"sending to "+receiverToken);

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
        //hideLayouts(views);
        forceWidgetUpdate(context, views);
    }

    private static void forceWidgetUpdate(Context context, RemoteViews views){
        Log.i(TAG,"Updating widget views");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, InTouchWidget.class));

        SharedPreferences prefs  = context.getSharedPreferences(MY_PREFERENCE,Context.MODE_PRIVATE);
        String my_emoji = prefs.getString(WIDGET_MY_EMOJI_CHANGE,"noemoji");
        String partner_emoji = prefs.getString(WIDGET_PARTNER_EMOJI_CHANGE,"noemoji");
        String partner_mood =prefs.getString(WIDGET_PARTNER_MOOD_CHANGE,"nomood");
        String my_mood = prefs.getString(WIDGET_MY_MOOD_CHANGE,"nomood");
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        setupOnClickListener(views,context,R.id.appwidget_mood_button,appWidgetIds[0],LEFT_BTN);
        setupOnClickListener(views,context,R.id.right_emoji_btn,appWidgetIds[0],RIGHT_BTN);
        setupOnClickListener(views,context,R.id.heart_button,appWidgetIds[0],HEART_BTN);
        setupOnClickListener(views,context,R.id.cry_button,appWidgetIds[0],CRY_BTN);
        setupOnClickListener(views,context,R.id.hug_button,appWidgetIds[0],HUG_BTN);
        setupOnClickListener(views,context,R.id.muscle_button,appWidgetIds[0],MUSCLE_BTN);
        setupOnClickListener(views,context,R.id.first_color_button,appWidgetIds[0], NEGATIVE_COLOR_BTN);
        setupOnClickListener(views,context,R.id.second_color_button,appWidgetIds[0], NEUTRAL_COLOR_BTN);
        setupOnClickListener(views,context,R.id.third_color_button,appWidgetIds[0], POSITIVE_COLOR_BTN);

        updateEmojiSelection(context,views);

        if(partner_mood.equals("positive")){
            views.setInt(R.id.appwidget_emoji_btn, "setBackgroundResource", R.drawable.circle_green);
            //UpdateWidget(context, views);
        }else if(partner_mood.equals("negative")){
            views.setInt(R.id.appwidget_emoji_btn, "setBackgroundResource", R.drawable.circle_pink);
            // UpdateWidget(context, views);
        }else if(partner_mood.equals("neutral")){
            views.setInt(R.id.appwidget_emoji_btn, "setBackgroundResource", R.drawable.circle_white);
            // UpdateWidget(context, views);
        }

        if(my_mood.equals("positive")){
            views.setInt(R.id.appwidget_mood_button, "setBackgroundResource", R.drawable.circle_green);
            //UpdateWidget(context, views);
        }else if(my_mood.equals("negative")){
            views.setInt(R.id.appwidget_mood_button, "setBackgroundResource", R.drawable.circle_pink);
            // UpdateWidget(context, views);
        }else if(my_mood.equals("neutral")){
            views.setInt(R.id.appwidget_mood_button, "setBackgroundResource", R.drawable.circle_white);
            // UpdateWidget(context, views);
        }

        views.setTextViewText(R.id.right_emoji_btn, my_emoji);
        views.setTextViewText(R.id.left_emoji_button, partner_emoji);

        /*if(partner_emoji.equals(context.getResources().getString(R.string.cry_emoji))){
            views.setTextViewText(R.id.left_emoji_button, context.getResources().getString(R.string.cry_emoji));
            // UpdateWidget(context, views);
        }else if(partner_emoji.equals(context.getResources().getString(R.string.heart_emoji))){
            views.setTextViewText(R.id.left_emoji_button, context.getResources().getString(R.string.heart_emoji));
            //  UpdateWidget(context, views);
        }else if(partner_emoji.equals(context.getResources().getString(R.string.hug_emoji))){
            views.setTextViewText(R.id.left_emoji_button, context.getResources().getString(R.string.hug_emoji));
            //  UpdateWidget(context, views);
        }else if(partner_emoji.equals(context.getResources().getString(R.string.muscle_emoji))){
            views.setTextViewText(R.id.left_emoji_button, context.getResources().getString(R.string.muscle_emoji));
        }else if(partner_emoji.equals("")){
            views.setTextViewText(R.id.left_emoji_button,"");
        }*/
        appWidgetManager.updateAppWidget(appWidgetIds, views);

    }

    private static void updateEmojiSelection(Context context, RemoteViews views){
        SharedPreferences prefs  = context.getSharedPreferences(MY_PREFERENCE,Context.MODE_PRIVATE);
        first_emoji = prefs.getString(EmojiSelectionActivity.FIRST_EMOJI_SELECTION,"noemoji");
        second_emoji = prefs.getString(EmojiSelectionActivity.SECOND_EMOJI_SELECTION,"noemoji");
        third_emoji = prefs.getString(EmojiSelectionActivity.THIRD_EMOJI_SELECTION,"noemoji");
        fourth_emoji = prefs.getString(EmojiSelectionActivity.FOURTH_EMOJI_SELECTION,"noemoji");

        views.setTextViewText(R.id.heart_button,first_emoji);
        views.setTextViewText(R.id.hug_button,second_emoji);
        views.setTextViewText(R.id.cry_button,third_emoji);
        views.setTextViewText(R.id.muscle_button,fourth_emoji);



    }

    /*private static void handleStaticPartnerMoodChange(Context context){

        SharedPreferences prefs  = context.getSharedPreferences(MY_PREFERENCE,Context.MODE_PRIVATE);
        String emoji = prefs.getString(WIDGET_EMOJI_CHANGE,"noemoji");
        String mood =prefs.getString(WIDGET_MOOD_CHANGE,"nomood");
        Log.v(TAG,"CURRENT PARTNER MOOD: "+mood);
        Log.v(TAG,"CURRENT PARTNER EMOJI: "+emoji);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.in_touch_widget);
        hideLayouts(views);
        if(mood.equals("positive")){
            views.setInt(R.id.appwidget_left_button, "setBackgroundResource", R.drawable.circle_green);
            //UpdateWidget(context, views);
        }else if(mood.equals("negative")){
            views.setInt(R.id.appwidget_left_button, "setBackgroundResource", R.drawable.circle_pink);
           // UpdateWidget(context, views);
        }else if(mood.equals("neutral")){
            views.setInt(R.id.appwidget_left_button, "setBackgroundResource", R.drawable.circle_white);
           // UpdateWidget(context, views);
        }

        if(emoji.equals(context.getResources().getString(R.string.cry_emoji))){
            views.setTextViewText(R.id.right_button_emoji, context.getResources().getString(R.string.cry_emoji));
           // UpdateWidget(context, views);
        }else if(emoji.equals(context.getResources().getString(R.string.heart_emoji))){
            views.setTextViewText(R.id.right_button_emoji, context.getResources().getString(R.string.heart_emoji));
          //  UpdateWidget(context, views);
        }else if(emoji.equals(context.getResources().getString(R.string.hug_emoji))){
            views.setTextViewText(R.id.right_button_emoji, context.getResources().getString(R.string.hug_emoji));
          //  UpdateWidget(context, views);
        }else if(emoji.equals(context.getResources().getString(R.string.muscle_emoji))){
            views.setTextViewText(R.id.right_button_emoji, context.getResources().getString(R.string.muscle_emoji));
         }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, InTouchWidget.class));
        appWidgetManager.updateAppWidget(appWidgetIds, views);

    }*/

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