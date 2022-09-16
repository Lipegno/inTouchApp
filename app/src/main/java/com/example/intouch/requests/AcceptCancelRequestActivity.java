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
import com.example.intouch.auth.LogInActivity;
import com.example.intouch.dao.DAOConnection;
import com.example.intouch.dao.DAOPendingConnection;
import com.example.intouch.dao.DAOUser;
import com.example.intouch.dbmodels.Connection;
import com.example.intouch.dbmodels.PendingConnection;
import com.example.intouch.dbmodels.User;
import com.example.intouch.helpers.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class AcceptCancelRequestActivity extends AppCompatActivity {

    // region Declarations
    Button buttonCancelRequest;
    Button buttonAcceptRequest;

    TextView userEmail;
    TextView receivingText;
    TextView singOutTextView;
    User sender;
    String senderUID;

    ImageView userImageView;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String deviceToken;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    Uri userPhotoUrl;

    TextView popUpMessage;
    Button yesButton;
    Button cancelButton;
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_cancel_request);

        // Initialization
        initialize();

    }

    // region Initialization
    private void initialize() {
        Bundle bundle = getIntent().getExtras();
        senderUID = bundle.getString("senderUID");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        sharedpreferences = getSharedPreferences(MainActivity.MY_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        userEmail = findViewById(R.id.userEmailAcceptCancelRequest);
        receivingText = findViewById(R.id.receivingRequest);
        singOutTextView = findViewById(R.id.signOutAcceptCancelRequest);
        buttonAcceptRequest = findViewById(R.id.buttonAcceptRequest);
        buttonCancelRequest = findViewById(R.id.buttonCancelRequest);
        userImageView = findViewById(R.id.userImageAcceptCancelRequest);

        if (mUser != null) {
            userEmail.setText(mUser.getEmail());
            userPhotoUrl = mUser.getPhotoUrl();

            Glide.with(this)
                    .load(userPhotoUrl)
                    .into(userImageView);
        }

        // region Code snippet to take the device registration token on account creation
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(AcceptCancelRequestActivity.this, "Fetching FCM registration token failed: " + task.getException() , Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Get new FCM registration token
                        deviceToken = task.getResult();
                    }
                });

        DAOUser.getInstance().getUserById(senderUID, new Callback<User>() {
            @Override
            public void execute(User user) {
                sender = user;
                receivingText.setText("You received a connection request from " + sender.email);
            }
        }, new Callback() {
            @Override
            public void execute(Object object) {
                Toast.makeText(AcceptCancelRequestActivity.this, "Could not retrieve the sender.", Toast.LENGTH_SHORT).show();
            }
        });

        setClickListeners();
    }

    private void setClickListeners() {
        singOutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignOutPopUp(view);
            }
        });

        buttonAcceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptRequest();
            }
        });
    }
    // endregion

    // region Redirects
    private void redirectToAcceptedRequestActivity(User sender, User receiver) {
        Intent intent = new Intent(this, AcceptedRequestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle bundle = new Bundle();
        bundle.putSerializable("sender", sender);
        bundle.putSerializable("receiver", receiver);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    // Redirects to the login screen
    public void redirectToLogIn() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    // endregion

    // region Accept request
    // Creates a new connection and deletes the entry from pending connections
    private void acceptRequest() {

        DAOPendingConnection daoPendingConnection = DAOPendingConnection.getInstance();

        daoPendingConnection.getPendingConnectionByAUserUid(mUser.getUid(),
                //  if there is already a pending connection that includes this user ->
                new Callback<PendingConnection>() {
                    @Override
                    public void execute(PendingConnection pc) {
                        // Create a connection
                        User receiver = new User(mUser.getUid(), mUser.getEmail(), mUser.getPhotoUrl().toString(), 1, deviceToken);
                        String receiverUID = receiver.uid;

                        Connection newConnection = new Connection(receiver, sender);

                        // Add the connection to the database
                        DAOConnection.getInstance().add(newConnection).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // If connection successfully added
                                // delete the pending connection entry
                                // and redirect the user to the Accepted Request screen
                                daoPendingConnection.deletePendingConnection(pc).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        redirectToAcceptedRequestActivity(sender, receiver);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AcceptCancelRequestActivity.this, "Could not delete the pending connection.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                    }
                },
                //  execute the success callback
                new Callback() {
                    @Override
                    public void execute(Object object) {
                        Toast.makeText(AcceptCancelRequestActivity.this, "There is no pending connection.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // endregion

    // region Sign out
    private void showSignOutPopUp(View view) {
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
        popupWindow.showAtLocation(findViewById(R.id.userImageViewConnectWith), Gravity.CENTER, 0, 0);

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