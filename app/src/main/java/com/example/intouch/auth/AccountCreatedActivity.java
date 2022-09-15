package com.example.intouch.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.intouch.MainActivity;
import com.example.intouch.R;
import com.example.intouch.dao.DAOConnection;
import com.example.intouch.dao.DAOPendingConnection;
import com.example.intouch.dao.DAOUser;
import com.example.intouch.helpers.Callback;
import com.example.intouch.models.Connection;
import com.example.intouch.models.PendingConnection;
import com.example.intouch.models.User;
import com.example.intouch.requests.SentRequestActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountCreatedActivity extends AppCompatActivity {

    // region Declarations
    EditText partnerEmail;
    TextView userEmail;
    TextView singOutTextView;
    ImageView userImageView;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    TextView popUpMessage;
    Button yesButton;
    Button cancelButton;
    Button buttonContinue;

    private Uri filePath;
    private Uri userPhotoURL;
    private final int PICK_IMAGE = 100;

    private ProfilePicture.PictureListener pictureListener;
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_with);

        // Initialize
        initialize();
    }

    // region Initialization
    private void initialize() {
        userEmail = findViewById(R.id.userEmail);
        partnerEmail = findViewById(R.id.partnerEmail);
        userImageView = findViewById(R.id.userImageViewConnectWith);
        singOutTextView = findViewById(R.id.signOut);
        buttonContinue = findViewById(R.id.buttonContinueConnectWith);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        sharedpreferences = getSharedPreferences(MainActivity.MY_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        if (mUser != null) {
            userEmail.setText(mUser.getEmail());
            userPhotoURL = mUser.getPhotoUrl();

            Glide.with(this)
                    .load(userPhotoURL)
                    .into(userImageView);
        }

        // Set click listeners
        setClickListeners();
    }

    private void setClickListeners() {
        singOutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignOutPopUp(view);
            }
        });

        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update profile picture
                openGallery();
            }
        });

        pictureListener = new ProfilePicture.PictureListener() {
            @Override
            public void onProfilePictureUpdated() {
                Uri uri = mUser.getPhotoUrl();
                Glide.with(AccountCreatedActivity.this).load(uri).into(userImageView);
            }
        };

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = partnerEmail.getText().toString();

                checkIfCanConnect(email,
                        //  Success callback
                        new Callback<User>() {
                            @Override
                            public void execute(User user) {
                                addPendingConnection(user);
                            }
                        },
                        new Callback<String>() {
                            @Override
                            public void execute(String errorMessage) {
                                Toast.makeText(AccountCreatedActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            filePath = data.getData();

            ProfilePicture profilePicture = new ProfilePicture(filePath, userPhotoURL, mUser, pictureListener, AccountCreatedActivity.this);
            profilePicture.uploadImage(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AccountCreatedActivity.this, "Picture updated", Toast.LENGTH_SHORT).show();
                        pictureListener.onProfilePictureUpdated();
                    }
                }
            });
        }
    }

    // endregion

    // region Redirects
    // Redirects to the sent request screen
    private void redirectToSentRequestActivity(User receiver) {
        Intent intent = new Intent(this, SentRequestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle bundle = new Bundle();
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

    // region Connect with
    private void checkIfCanConnect(String email, Callback<User> successCallback, Callback<String> displayErrorMessage) {
        //  check if the email exists
        DAOUser.getInstance().getUserByEmail(email,
                new Callback<User>() {
                    @Override
                    public void execute(User user) {
                        //  if email exists, check if there is already a connection that includes this user
                        DAOConnection.getInstance().getConnectionByAUserUid(user.uid,
                                //  if there is already a connection that includes this user -> display error message
                                new Callback<Connection>() {
                                    @Override
                                    public void execute(Connection object) {
                                        displayErrorMessage.execute("This user is already connected with someone else.");
                                    }
                                },
                                new Callback() {
                                    @Override
                                    public void execute(Object object) {
                                        //  if the email exists, and there is no connection that includes this user
                                        //  check if there is already a pending connection that includes this user
                                        DAOPendingConnection.getInstance().getPendingConnectionByAUserUid(user.uid,
                                                //  if there is already a pending connection that includes this user -> display message
                                                new Callback<PendingConnection>() {
                                                    @Override
                                                    public void execute(PendingConnection pc) {
                                                        displayErrorMessage.execute("This email already has a pending connection.");
                                                    }
                                                },
                                                //  if this reached, it means we can create a pending connection to this email
                                                //  execute the success callback
                                                new Callback() {
                                                    @Override
                                                    public void execute(Object object) {
                                                        successCallback.execute(user);
                                                    }
                                                });
                                    }
                                });
                    }
                },
                //  if the email does not exists -> show error message and do not continue
                new Callback() {
                    @Override
                    public void execute(Object object) {
                        displayErrorMessage.execute("This email does not exist");
                    }
                });
    }

    private void addPendingConnection(User user) {
        String senderUid = mUser.getUid();
        String receivingUid = user.uid;
        String status = "Pending";

        DAOPendingConnection.getInstance().add(new PendingConnection(senderUid, receivingUid, status)).addOnSuccessListener(suc -> {
            Toast.makeText(AccountCreatedActivity.this, "Your connection request has been made.", Toast.LENGTH_SHORT).show();
            redirectToSentRequestActivity(user);
        }).addOnFailureListener(fail -> {
            Toast.makeText(AccountCreatedActivity.this, "Failed to request a connection", Toast.LENGTH_SHORT).show();
        });
    }
    // endregion

    // region Sign Out
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