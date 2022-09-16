package com.example.intouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class AllSetActivity extends AppCompatActivity {

    // region Declarations
    Button buttonGotIt;
    Button buttonNeedHelp;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_set);

        // Initialization
        initialize();
    }

    // region Initialization
    private void initialize() {
        buttonGotIt = findViewById(R.id.buttonGotIt);
        buttonNeedHelp = findViewById(R.id.buttonNeedHelp);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        setClickListeners();
    }

    private void setClickListeners() {
        buttonGotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToHomeActivity();
            }
        });

        buttonNeedHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TO DO
            }
        });
    }
    // endregion

    // region Redirects
    private void redirectToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    // endregion
}