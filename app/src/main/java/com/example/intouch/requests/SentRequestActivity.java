package com.example.intouch.requests;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.intouch.R;
import com.example.intouch.dbmodels.User;

public class SentRequestActivity extends AppCompatActivity {
    // region Declarations
    Button buttonOK;
    User receiver;
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_request);

        // Initialization
        initialize();
    }

    // region Initialize
    private void initialize() {
        buttonOK = findViewById(R.id.buttonOKSentRequest);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            receiver = (User) bundle.getSerializable("receiver");
        }

        setClickListeners();
    }

    private void setClickListeners() {
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToWaitRequestActivity();
            }
        });
    }
    // endregion

    // region Redirects
    private void redirectToWaitRequestActivity() {
        Intent intent = new Intent(this, WaitRequestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle bundle = new Bundle();
        bundle.putSerializable("receiver", receiver);
        intent.putExtras(bundle);

        startActivity(intent);
    }
    // endregion
}