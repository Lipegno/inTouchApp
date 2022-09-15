package com.example.intouch.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.intouch.AllSetActivity;
import com.example.intouch.MainActivity;
import com.example.intouch.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WallpaperSideActivity extends AppCompatActivity {

    RadioGroup radioGroupWallpaperPicker;
    Button buttonOK;

    ArrayList<String> selectedColorScheme;
    String selectedWallpaperSide;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_side);

        Bundle bundle = getIntent().getExtras();
        selectedColorScheme = bundle.getStringArrayList("selectedColorScheme");

        radioGroupWallpaperPicker = findViewById(R.id.radioGroupWallpaperPicker);
        buttonOK = findViewById(R.id.buttonOKWallpaperPicker);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        radioGroupWallpaperPicker.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // Check which radio button was clicked
                switch (checkedId) {
                    case R.id.radioWallpaperLeft:
                        Toast.makeText(WallpaperSideActivity.this, "LeftWallpaper", Toast.LENGTH_SHORT).show();
                        selectedWallpaperSide = "left";
                        break;
                    case R.id.radioWallpaperRight:
                        Toast.makeText(WallpaperSideActivity.this, "Color Scheme 2", Toast.LENGTH_SHORT).show();
                        selectedWallpaperSide = "right";
                        break;
                    default:
                        Toast.makeText(WallpaperSideActivity.this, "Default Scheme", Toast.LENGTH_SHORT).show();
                        selectedWallpaperSide = "right";
                        break;
                }
            }
        });

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserSettings();
            }
        });
    }


    private void updateUserSettings() {
        SharedPreferences.Editor editor = getSharedPreferences(MainActivity.MY_PREFERENCE, MODE_PRIVATE).edit();

        //Set the selected values
        Set<String> colorSchemeSet = new HashSet<String>(selectedColorScheme);
        editor.putStringSet("colorSchemeSet", colorSchemeSet);
        editor.putString("wallpaperSide", selectedWallpaperSide);

        editor.apply();

        redirectToAllSetActivity();
    }

    private void redirectToAllSetActivity() {
        Intent intent = new Intent(this, AllSetActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}