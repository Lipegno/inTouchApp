package com.example.intouch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;

public class WallpaperSideActivity extends AppCompatActivity {

    ArrayList<String> selectedColorScheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_side);

        Bundle bundle = getIntent().getExtras();
        selectedColorScheme = bundle.getStringArrayList("selectedColorScheme");


    }
}