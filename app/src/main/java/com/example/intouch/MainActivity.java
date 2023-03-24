package com.example.intouch;

import static com.example.intouch.InTouchWidget.WIDGET_MY_MOOD_CHANGE;
import static com.example.intouch.InTouchWidget.WIDGET_PARTNER_MOOD_CHANGE;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.intouch.auth.CreateAccountActivity;
import com.example.intouch.auth.LogInActivity;
import com.example.intouch.models.UserSettings;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    String prevStarted = "yes";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String MY_PREFERENCE = "InTouch";


    // Checks if it the first time that the app is opened and displays the "Welcome" screen
    // if not, it redirects the user to the login page
    @Override
    protected void onResume() {
        super.onResume();

        if (!sharedpreferences.getBoolean(prevStarted, false)) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(prevStarted, Boolean.TRUE);
            editor.apply();
        } else {
            redirectToLogIn();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        editor = getSharedPreferences(MY_PREFERENCE, MODE_PRIVATE).edit();


        // Set default app settings
        UserSettings userSettings = new UserSettings();

        // Set the values
        Set<String> colorSchemeSet = new HashSet<String>(userSettings.colorScheme);
        editor.putStringSet("colorSchemeSet", colorSchemeSet);

        Set<String> emojisSet = new HashSet<String>(userSettings.emojis);
        editor.putStringSet("emojisSet", emojisSet);
        editor.putString("wallpaperSide", "right");
        editor.commit();

        if(!foregroundServiceRunning()) {
            Intent serviceIntent = new Intent(this,
                    InTouchAppService.class);
            startForegroundService(serviceIntent);
        }

        initApplication(getApplicationContext());

    }

    public void initApplication(Context c){
        SharedPreferences prefs  = c.getSharedPreferences(MY_PREFERENCE,Context.MODE_PRIVATE);
        String partner_mood =prefs.getString(WIDGET_PARTNER_MOOD_CHANGE,"nomood");
        String my_mood = prefs.getString(WIDGET_MY_MOOD_CHANGE,"nomood");
        if(partner_mood.equals("nomood") && my_mood.equals("nomood"))
            ScreenManager.getInstance().initWallpapersFirstStartup(c);
    }

    public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(InTouchAppService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    // Redirects to the login screen
    public void redirectToLogIn(){
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    // Called when user taps the "Let's  Start" button
    public void startUsingApp(View view){
        Intent intent = new Intent(this, CreateAccountActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}