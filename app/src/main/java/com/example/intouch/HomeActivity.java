package com.example.intouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    TextView userEmailTextView;
    TextView singOutTextView;
    Bundle bundle;
    String userEmail;
    ImageView userImageView;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Getting the bundle
        bundle = getIntent().getExtras();
        // Extracting the data…
        userEmail = bundle.getString("email");

        userEmailTextView = findViewById(R.id.userEmail);
        userImageView = findViewById(R.id.userImage);
        singOutTextView = findViewById(R.id.signOut);

        userEmailTextView.setText(userEmail);
        userImageView.setBackgroundResource(R.drawable.ic_user_icon_black);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        singOutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }

    private void signOut() {
        mAuth.signOut();
        redirectToLogIn();
    }

    // Redirects to the login screen
    public void redirectToLogIn(){
        Intent intent = new Intent(this,LogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}