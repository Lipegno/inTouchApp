package com.example.intouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.intouch.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AcceptedRequestActivity extends AppCompatActivity {

    ImageView firstUserImageView;
    ImageView secondUserImageView;
    User firstUser;
    User secondUser;

    Button buttonLetsStart;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getSharedPreferences(MainActivity.MY_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        String wallpaperSide = sharedpreferences.getString("wallpaperSide", null);

        if (wallpaperSide != null) {
            redirectToHomeActivity();
            return;
        }

        setContentView(R.layout.activity_accepted_request);

        firstUserImageView = findViewById(R.id.user1ImageView);
        secondUserImageView = findViewById(R.id.user2ImageView);
        buttonLetsStart = findViewById(R.id.buttonStartAcceptedRequest);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            firstUser = (User) bundle.getSerializable("sender");
            secondUser = (User) bundle.getSerializable("receiver");


            Glide.with(this)
                    .load(firstUser.photoURL)
                    .into(firstUserImageView);

            Glide.with(this)
                    .load(secondUser.photoURL)
                    .into(secondUserImageView);
        }

        buttonLetsStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToColorSchemePickerActivity(firstUser, secondUser);
            }
        });

    }

    private void redirectToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void redirectToColorSchemePickerActivity(User firstUser, User secondUser) {
        Intent intent = new Intent(this, ColorSchemePickerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}