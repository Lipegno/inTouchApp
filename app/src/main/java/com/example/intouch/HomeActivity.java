package com.example.intouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class HomeActivity extends AppCompatActivity {

    TextView userEmailTextView;
    TextView singOutTextView;
    Bundle bundle;
    String userEmail;
    ImageView userImageView;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    TextView popUpMessage;
    Button yesButton;
    Button cancelButton;

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
                showSignOutPopUp(view);
            }
        });
    }

    private void showSignOutPopUp(View view) {
        // Inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

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

        popUpMessage.setText("Are you sure you want to sign out?");


        yesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                popupWindow.dismiss();
                signOut();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        // Show the popup window
        // Which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(findViewById(R.id.userImage), Gravity.CENTER, 0, 0);

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