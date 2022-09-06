package com.example.intouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.intouch.db.User;

public class SentRequestActivity extends AppCompatActivity {

    Button buttonOK;

    User receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_request);

        buttonOK = findViewById(R.id.buttonOKSentRequest);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            receiver = (User) bundle.getSerializable("receiver");
        }

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToWaitRequestActivity();
            }
        });
    }

    private void redirectToWaitRequestActivity() {
        Intent intent = new Intent(this, WaitRequestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle bundle = new Bundle();
        bundle.putSerializable("receiver", receiver);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}