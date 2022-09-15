package com.example.intouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.intouch.auth.CreateAccountActivity;
import com.example.intouch.auth.LogInActivity;
import com.example.intouch.models.UserSettings;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        editor = getSharedPreferences(MY_PREFERENCE, MODE_PRIVATE).edit();


        // Set default app settings
        UserSettings userSettings = new UserSettings();

        //Set the values
        Set<String> colorSchemeSet = new HashSet<String>(userSettings.colorScheme);
        editor.putStringSet("colorSchemeSet", colorSchemeSet);

        Set<String> emojisSet = new HashSet<String>(userSettings.emojis);
        editor.putStringSet("emojisSet", emojisSet);
        editor.putString("wallpaperSide", "right");
        editor.commit();
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