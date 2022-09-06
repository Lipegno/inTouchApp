package com.example.intouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.intouch.models.ColorScheme;
import com.example.intouch.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ColorSchemePickerActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    RadioGroup radioGroupColorSchemePicker;
    RadioButton radioColorScheme1;
    RadioButton radioColorScheme2;
    RadioButton radioCustomizeColorScheme;

    Button buttonOK;

    ColorScheme colorScheme1;
    ColorScheme colorScheme2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_scheme);

        radioGroupColorSchemePicker = findViewById(R.id.radioGroupColorPicker);
        radioColorScheme1 = findViewById(R.id.radioColorS1);
        radioColorScheme2 = findViewById(R.id.radioColorS2);
        radioCustomizeColorScheme = findViewById(R.id.radioColorCustomize);
        buttonOK =  findViewById(R.id.buttonOKColorSchemePicker);

        buttonOK.setEnabled(false);

        // Disable the customization for now
        radioCustomizeColorScheme.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        colorScheme1 = new ColorScheme("#cefad0", "#fff380", "#ff2c2c");
        colorScheme2 = new ColorScheme("#bfe6ff", "#e88504", "#555555");

        // TO DO:
        // Set the colors as background to the circles

        radioGroupColorSchemePicker.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // Check which radio button was clicked
                switch(checkedId) {
                    case R.id.radioColorS1:
                        Toast.makeText(ColorSchemePickerActivity.this, "Color Scheme 1", Toast.LENGTH_SHORT).show();
                        setColorSchemeSettings(colorScheme1);
                    case R.id.radioColorS2:
                        Toast.makeText(ColorSchemePickerActivity.this, "Color Scheme 2", Toast.LENGTH_SHORT).show();
                        setColorSchemeSettings(colorScheme2);
                    case R.id.radioColorCustomize:
                        Toast.makeText(ColorSchemePickerActivity.this, "Color Scheme Customize", Toast.LENGTH_SHORT).show();
                        //customizeColorScheme();
                    default:
                        Toast.makeText(ColorSchemePickerActivity.this, "Default Scheme", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveColorSchemeSettings();
            }
        });
    }

    private void setColorSchemeSettings(ColorScheme colorScheme) {
        // set the color to user settings
        buttonOK.setEnabled(true);
    }

    private void saveColorSchemeSettings() {
        // save settings to DB
        // on success
        redirectToWallpaperSideActivity();
    }

    private void redirectToWallpaperSideActivity() {
        Intent intent = new Intent(this, WallpaperSideActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}