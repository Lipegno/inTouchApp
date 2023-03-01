package com.example.intouch.requests;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.intouch.MainActivity;
import com.example.intouch.R;
import com.example.intouch.auth.AccountCreatedActivity;
import com.example.intouch.auth.LogInActivity;
import com.example.intouch.dao.DAOPendingConnection;
import com.example.intouch.helpers.Callback;
import com.example.intouch.dbmodels.PendingConnection;
import com.example.intouch.dbmodels.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WaitRequestActivity extends AppCompatActivity {

    // region Declarations
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
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    private Uri userPhotoUrl;
    User receiver;
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_request);

        // Initialization
        initialize();
    }

    // region Initialize
    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        sharedpreferences = getSharedPreferences(MainActivity.MY_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

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
                waitingRequestTextView.setText("Por favor aguarde que o pedido seja aceite para começar a utilizar a aplicação " + receiver.email);
            }

        }

        setClickListeners();
    }

    private void setClickListeners() {
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
    // endregion

    //region Redirects
    private void redirectToAccountCreatedActivity() {
        Intent intent = new Intent(this, AccountCreatedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // Redirects to the login screen
    public void redirectToLogIn() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    // endregion

    // region Sign out
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
        if (action == "cancelRequest") {
            popUpMessage.setText("Are you sure you want to cancel your request?");
            yesButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    popupWindow.dismiss();

                    DAOPendingConnection.getInstance().getPendingConnectionByAUserUid(receiver.uid, new Callback<PendingConnection>() {
                        @Override
                        public void execute(PendingConnection pc) {
                            DAOPendingConnection.getInstance().deletePendingConnection(pc).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    redirectToAccountCreatedActivity();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(WaitRequestActivity.this, "Failed to delete pending connection", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }, new Callback() {
                        @Override
                        public void execute(Object object) {
                            Toast.makeText(WaitRequestActivity.this, "Failed to get the pending connection.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        if (action == "signout") {
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

    private void signOut() {
        editor.putString("email", null);
        editor.putString("password", null);
        editor.apply();

        mAuth.signOut();
        redirectToLogIn();
    }
    // endregion
}