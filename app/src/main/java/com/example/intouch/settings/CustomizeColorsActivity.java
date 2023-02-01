package com.example.intouch.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.intouch.R;
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog;
import com.github.dhaval2404.colorpicker.listener.ColorListener;
import com.github.dhaval2404.colorpicker.model.ColorShape;
import com.github.dhaval2404.colorpicker.model.ColorSwatch;
import com.google.firebase.database.annotations.NotNull;


public class CustomizeColorsActivity extends AppCompatActivity implements View.OnClickListener {

    private View goodMoodColorBtn;
    private View neutralMoodColorBtn;
    private View badMoodColorBtn;

    private int currentBackgroundColor;

    private static final String TAG = "CustomizeColorsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_colors);
        goodMoodColorBtn = findViewById(R.id.goodMoodButton);
        neutralMoodColorBtn = findViewById(R.id.okayMoodButton);
        badMoodColorBtn = findViewById(R.id.badMoodButton);
        goodMoodColorBtn.setOnClickListener(this);
        neutralMoodColorBtn.setOnClickListener(this);
        badMoodColorBtn.setOnClickListener(this);

    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View view) {
        Log.i(TAG, "Changing colors " + view.getId());

        new MaterialColorPickerDialog
                .Builder(this)
                .setTitle("Pick a Color")
                .setColorShape(ColorShape.SQAURE)
                .setColorSwatch(ColorSwatch._300)
                .setDefaultColor(Color.parseColor("#FFFFFFFF"))
                .setColorListener(new ColorListener() {
                    @Override
                    public void onColorSelected(int color, @NotNull String colorHex) {
                        if(view.getId()==R.id.goodMoodButton){
                            Log.i(TAG,"Setting good mood color");
                        }else if(view.getId()==R.id.okayMoodButton){
                            Log.i(TAG,"Setting neutral mood color");

                        }else if(view.getId()==R.id.badMoodButton){
                            Log.i(TAG,"Setting bad mood color");

                        }
                    }
                })
                .show();

    }
}