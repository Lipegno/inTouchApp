package com.example.intouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ColorSchemePickerActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    RadioGroup radioGroupColorSchemePicker;
    RadioButton radioColorScheme1;
    RadioButton radioColorScheme2;
    RadioButton radioCustomizeColorScheme;

    Button buttonOK;

    ArrayList<String> colorScheme1;
    ArrayList<String> colorScheme2;

    ArrayList<String> selectedColorScheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_scheme);

        radioGroupColorSchemePicker = findViewById(R.id.radioGroupColorPicker);
        radioColorScheme1 = findViewById(R.id.radioColorS1);
        radioColorScheme2 = findViewById(R.id.radioColorS2);
        radioCustomizeColorScheme = findViewById(R.id.radioColorCustomize);
        buttonOK = findViewById(R.id.buttonOKColorSchemePicker);

        // Disable second scheme and the customization for now
        radioColorScheme2.setEnabled(false);
        radioCustomizeColorScheme.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        initializeColorSchemes();

        radioGroupColorSchemePicker.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // Check which radio button was clicked
                switch (checkedId) {
                    case R.id.radioColorS1:
                        Toast.makeText(ColorSchemePickerActivity.this, "Color Scheme 1", Toast.LENGTH_SHORT).show();
                        selectedColorScheme = colorScheme1;
                        break;
                    case R.id.radioColorS2:
                        Toast.makeText(ColorSchemePickerActivity.this, "Color Scheme 2", Toast.LENGTH_SHORT).show();
                        selectedColorScheme = colorScheme2;
                        break;
                    case R.id.radioColorCustomize:
                        Toast.makeText(ColorSchemePickerActivity.this, "Color Scheme Customize", Toast.LENGTH_SHORT).show();
                        //customizeColorScheme();
                        break;
                    default:
                        Toast.makeText(ColorSchemePickerActivity.this, "Default Scheme", Toast.LENGTH_SHORT).show();
                        selectedColorScheme = colorScheme1;
                        break;
                }
            }
        });

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToWallpaperSideActivity();
            }
        });
    }

    private void initializeColorSchemes() {
        colorScheme1 = new ArrayList<String>();
        colorScheme1.add("#cefad0");
        colorScheme1.add("#fff380");
        colorScheme1.add("#ff2c2c");

        colorScheme2 = new ArrayList<String>();
        colorScheme1.add("#bfe6ff");
        colorScheme1.add("#e88504");
        colorScheme1.add("#555555");

        selectedColorScheme = colorScheme1;

        // TO DO
        // Set circles background colors
    }

    private void redirectToWallpaperSideActivity() {
        Intent intent = new Intent(this, WallpaperSideActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("selectedColorScheme", selectedColorScheme);
        intent.putExtras(bundle);

        startActivity(intent);
    }

}