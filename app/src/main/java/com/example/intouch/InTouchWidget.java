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
    private static final int PINK_COLOR_BTN = 106;
    private static final int GRAY_COLOR_BTN = 107;
    private static final int GREEN_COLOR_BTN = 108;

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
        setupOnClickListener(views,context,R.id.first_color_button,appWidgetId,PINK_COLOR_BTN);
        setupOnClickListener(views,context,R.id.second_color_button,appWidgetId,GRAY_COLOR_BTN);
        setupOnClickListener(views,context,R.id.third_color_button,appWidgetId,GREEN_COLOR_BTN);

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
            Log.i(TAG,"Left Button pressed ");

            //Changing the wallpaper here as a test
/*
            final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            try {
                wallpaperManager.setResource(R.drawable.custom_wallpaper);
            } catch (IOException e) {
                e.printStackTrace();
            }
            forceWidgetUpdate(context,views);*/
            hideLayouts(views);
            ShowColors(views);
            forceWidgetUpdate(context, views);
        }else if(action == LEFT_BTN){
            Log.i(TAG,"Right Button pressed ");
            hideLayouts(views);
            ShowEmojis(views);
            forceWidgetUpdate(context, views);
        }else if(action == HEART_BTN){
            Log.i(TAG,"Heart Button pressed ");

            views.setTextViewText(R.id.left_button_emoji, context.getResources().getString(R.string.heart_emoji));
            UpdateWidget(context, views);
        }else if(action == CRY_BTN){
            Log.i(TAG,"Cry Button pressed ");

            views.setTextViewText(R.id.left_button_emoji, context.getResources().getString(R.string.cry_emoji));
            UpdateWidget(context, views);
        }else if(action == HUG_BTN){
            Log.i(TAG,"Hug Button pressed ");

            views.setTextViewText(R.id.left_button_emoji, context.getResources().getString(R.string.hug_emoji));
            UpdateWidget(context, views);
        }else if(action == MUSCLE_BTN){
            Log.i(TAG,"Muscle Button pressed ");

            views.setTextViewText(R.id.left_button_emoji, context.getResources().getString(R.string.muscle_emoji));
            UpdateWidget(context, views);
        }else if(action == PINK_COLOR_BTN){
            Log.i(TAG,"PINK Button pressed ");
            views.setInt(R.id.appwidget_right_button, "setBackgroundResource", R.drawable.circle_pink);
            UpdateWidget(context, views);
            ScreenManager.getInstance().UpdateColor(Color.parseColor("#ff2d55"), context);
        }else if(action == GRAY_COLOR_BTN){
            Log.i(TAG,"Gray Button pressed ");
            views.setInt(R.id.appwidget_right_button, "setBackgroundResource", R.drawable.circle_white);
            UpdateWidget(context, views);
            ScreenManager.getInstance().UpdateColor(Color.parseColor("#C3C3C3"), context);
        }else if(action == GREEN_COLOR_BTN){
            Log.i(TAG,"Green Button pressed ");
            views.setInt(R.id.appwidget_right_button, "setBackgroundResource", R.drawable.circle_green);
            UpdateWidget(context, views);
            ScreenManager.getInstance().UpdateColor(Color.parseColor("#72a682"), context);
        }
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