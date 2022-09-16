package com.example.intouch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.intouch.auth.AccountCreatedActivity;
import com.example.intouch.auth.LogInActivity;
import com.example.intouch.auth.ProfilePicture;
import com.example.intouch.dao.DAOConnection;
import com.example.intouch.helpers.Callback;
import com.example.intouch.models.Connection;
import com.example.intouch.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    // region Declarations
    ProfilePicture.PictureListener pictureListener;
    private StorageReference storageReference;

    TextView userEmailTextView;
    TextView partnerEmailTextView;
    TextView singOutTextView;
    TextView colorSchemeTextView;
    TextView wallpaperSidesTextView;
    TextView circlesTextView;
    TextView emojisTextView;
    TextView notificationsTextView;

    ImageView userImageView;
    ImageView partnerImageView;

    Button buttonDisconnect;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    User partner;
    Connection usersConnection;

    TextView popUpMessage;
    Button yesButton;
    Button cancelButton;
    Button buttonContinue;

    private Uri filePath;
    private Uri userPhotoUrl;
    private final int PICK_IMAGE = 100;
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_and_settings);

        // Initialization
        initialize();
    }

    // region Initialize
    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        sharedpreferences = getSharedPreferences(MainActivity.MY_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        storageReference = FirebaseStorage.getInstance().getReference();

        userImageView = findViewById(R.id.userImageHome);
        userEmailTextView = findViewById(R.id.userEmailHome);
        partnerImageView = findViewById(R.id.partnerImageViewHome);
        partnerEmailTextView = findViewById(R.id.partnerEmailHome);
        buttonDisconnect = findViewById(R.id.buttonDisconnect);

        colorSchemeTextView = findViewById(R.id.arrowColorScheme);
        wallpaperSidesTextView = findViewById(R.id.arrowWallpaperSides);
        circlesTextView = findViewById(R.id.arrowCircles);
        emojisTextView = findViewById(R.id.arrowEmojis);
        notificationsTextView = findViewById(R.id.arrowNotifications);

        singOutTextView = findViewById(R.id.signOutHome);


        if (mUser != null) {
            userEmailTextView.setText(mUser.getEmail());
            userPhotoUrl = mUser.getPhotoUrl();
            Glide.with(HomeActivity.this).load(userPhotoUrl.toString()).into(userImageView);
            getUsersConnection(mUser.getUid());
        }

        SharedPreferences prefs = getSharedPreferences(MainActivity.MY_PREFERENCE, MODE_PRIVATE);
        Set<String> colorSchemeSet = prefs.getStringSet("colorScheme", null);
        Set<String> emojisSet = prefs.getStringSet("emojisSet", null);
        String wallpaperSide = prefs.getString("wallpaperSide", "right");

        Toast.makeText(this, ""+wallpaperSide, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, ""+colorSchemeSet.toString(), Toast.LENGTH_SHORT).show();

        setOnClickListeners();

        pictureListener = new ProfilePicture.PictureListener(){
            @Override
            public void onProfilePictureUpdated() {
                Uri uri = mUser.getPhotoUrl();
                //Toast.makeText(HomeActivity.this, ""+uri.toString(), Toast.LENGTH_SHORT).show();
                Glide.with(HomeActivity.this).load(uri.toString()).into(userImageView);
            }
        };
    }

    private void setOnClickListeners() {
        buttonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "Disconnect", Toast.LENGTH_SHORT).show();
                DAOConnection.getInstance().deleteConnection(usersConnection).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Redirect to account created activity
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, "Failed to disconnect from user", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        colorSchemeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "Color scheme", Toast.LENGTH_SHORT).show();
                // TO DO
                // redirect to the activity where the color scheme is set
            }
        });

        wallpaperSidesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "Wallpaper", Toast.LENGTH_SHORT).show();
                // TO DO
                // redirect to the activity where the wallpaper is set
            }
        });

        circlesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "Circles", Toast.LENGTH_SHORT).show();
                // TO DO
                // redirect to the activity where the circles are set
            }
        });

        emojisTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "Emojis", Toast.LENGTH_SHORT).show();
                // TO DO
                // redirect to the activity where the emojis are set
            }
        });

        notificationsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "Notifications", Toast.LENGTH_SHORT).show();
                // TO DO
                // redirect to the activity where the notifications are set
            }
        });

        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update profile picture
                openGallery();
            }
        });

        singOutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignOutPopUp(view);
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

            ProfilePicture profilePicture = new ProfilePicture(filePath, userPhotoUrl, mUser, pictureListener, HomeActivity.this);
            profilePicture.uploadImage(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(HomeActivity.this, "Picture updated", Toast.LENGTH_SHORT).show();
                        pictureListener.onProfilePictureUpdated();
                    }
                }
            });
        }
    }
    // endregion

    // region Redirects
    // Redirects to the login screen
    public void redirectToLogIn() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    // endregion

    // region Connection
    private void getUsersConnection(String uid) {
        DAOConnection.getInstance().getConnectionByAUserUid(mUser.getUid(), new Callback<Connection>() {
            @Override
            public void execute(Connection connection) {
                usersConnection = connection;
                if (connection.firstUser.uid.equals(mUser.getUid())) {
                    partner = usersConnection.secondUser;
                } else {
                    partner = usersConnection.firstUser;
                }

                setPartnersContent(partner);
            }
        }, new Callback() {
            @Override
            public void execute(Object object) {

            }
        });
    }

    private void setPartnersContent(User partner) {
        partnerEmailTextView.setText(partner.email);
        Glide.with(HomeActivity.this).load(partner.photoURL).into(partnerImageView);
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
        popupWindow.showAtLocation(findViewById(R.id.userImageHome), Gravity.CENTER, 0, 0);

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