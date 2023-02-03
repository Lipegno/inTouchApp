package com.example.intouch.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.intouch.R;

public class EmojiSelectionActivity extends AppCompatActivity {

    public static final String MY_PREFERENCE = "InTouch";
    public static final String FIRST_EMOJI_SELECTION = "first_emoji";
    public static final String SECOND_EMOJI_SELECTION = "second_emoji";
    public static final String THIRD_EMOJI_SELECTION = "third_emoji";
    public static final String FOURTH_EMOJI_SELECTION = "fourth_emoji";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji_selection2);

        TextView first_emoji = findViewById(R.id.editTextTextPersonName8);
        TextView second_emoji = findViewById(R.id.editTextTextPersonName7);
        TextView third_emoji = findViewById(R.id.editTextTextPersonName6);
        TextView fourth_emoji = findViewById(R.id.editTextTextPersonName5);
        SharedPreferences prefs  = getApplicationContext().getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        String first_emoji_string = prefs.getString(EmojiSelectionActivity.FIRST_EMOJI_SELECTION,"noemoji");
        String second_emoji_string = prefs.getString(EmojiSelectionActivity.SECOND_EMOJI_SELECTION,"noemoji");
        String third_emoji_string = prefs.getString(EmojiSelectionActivity.THIRD_EMOJI_SELECTION,"noemoji");
        String fourth_emoji_string = prefs.getString(EmojiSelectionActivity.FOURTH_EMOJI_SELECTION,"noemoji");
        first_emoji.setText(first_emoji_string);
        second_emoji.setText(second_emoji_string);
        third_emoji.setText(third_emoji_string);
        fourth_emoji.setText(fourth_emoji_string);

        Button save_changes =  findViewById(R.id.save_emoji_button);

        save_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs  = getApplicationContext().getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
                prefs.edit().putString(FIRST_EMOJI_SELECTION, first_emoji.getText().toString()).apply();
                prefs.edit().putString(SECOND_EMOJI_SELECTION, second_emoji.getText().toString()).apply();
                prefs.edit().putString(THIRD_EMOJI_SELECTION, third_emoji.getText().toString()).apply();
                prefs.edit().putString(FOURTH_EMOJI_SELECTION, fourth_emoji.getText().toString()).apply();

                Toast.makeText(getApplicationContext(),"Changes Saved",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
