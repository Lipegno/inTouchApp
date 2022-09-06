package com.example.intouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.intouch.db.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WaitRequestActivity extends AppCompatActivity {

    ImageView userImageView;
    TextView userEmailTextView;
    TextView waitingRequestTextView;
    TextView signOutTextView;
    Button buttonCancelRequest;


    TextView popUpMessage;
    Button yesButton;
    Button cancelButton;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    private Uri userPhotoUrl;
    User receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_request);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        userImageView = findViewById(R.id.userImageWaitRequest);
        userEmailTextView = findViewById(R.id.userEmailWaitRequest);
        waitingRequestTextView = findViewById(R.id.waitingRequest);
        buttonCancelRequest = findViewById(R.id.buttonCancelRequest);
        signOutTextView = findViewById(R.id.signOutWaitRequest);

        userPhotoUrl = mUser.getPhotoUrl();
        Glide.with(this).load(userPhotoUrl.toString()).into(userImageView);

        Bundle bundle = getIntent().getExtras();

        if (mUser != null) {
            userEmailTextView.setText(mUser.getEmail());

            if (bundle != null) {
                receiver = (User) bundle.getSerializable("receiver");
                waitingRequestTextView.setText("Waiting for your request to be accepted by " + receiver.email);
            }

        }

        buttonCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUp(view, "cancelRequest");
                // TO DO:
                // cancelRequest();

                //if cancelRequestSuccess
                //redirectToConnectWithActivity();
            }
        });

        signOutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUp(view, "signout");
            }
        });

    }

    private void showPopUp(View view, String action) {
        // Inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_popup_window, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = false;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        popUpMessage = popupView.findViewById(R.id.popUpMessage);
        yesButton = popupView.findViewById(R.id.yesButton);
        cancelButton = popupView.findViewById(R.id.cancelButton);

        // change the email
        if(action == "cancelRequest"){
            popUpMessage.setText("Are you sure you want to cancel your request?");
            yesButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    popupWindow.dismiss();


                }
            });
        }

        if(action == "signout"){
            popUpMessage.setText("Are you sure you want to sign out?");

            yesButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    popupWindow.dismiss();
                    signOut();
                }
            });
        }



        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        // Show the popup window
        // Which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(findViewById(R.id.userEmailWaitRequest), Gravity.CENTER, 0, 0);
    }

    private void redirectToConnectWithActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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