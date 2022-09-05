package com.example.intouch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.intouch.dao.DAOConnection;
import com.example.intouch.dao.DAOPendingConnection;
import com.example.intouch.dao.DAOUser;
import com.example.intouch.db.Connection;
import com.example.intouch.db.PendingConnection;
import com.example.intouch.db.User;
import com.example.intouch.helpers.Callback;
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

public class AccountCreatedActivity extends AppCompatActivity {

    private interface PictureListener {
        void onProfilePictureUpdated();
    }

    EditText partnerEmail;
    TextView userEmail;
    TextView singOutTextView;
    ImageView userImageView;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    TextView popUpMessage;
    Button yesButton;
    Button cancelButton;
    Button buttonContinue;

    private Uri filePath;
    private Uri userPhotoUrl;
    private final int PICK_IMAGE = 100;

    private AccountCreatedActivity.PictureListener pictureListener;

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_with);

        userEmail = findViewById(R.id.userEmail);
        partnerEmail = findViewById(R.id.partnerEmail);
        userImageView = findViewById(R.id.userImageViewConnectWith);
        singOutTextView = findViewById(R.id.signOut);
        buttonContinue = findViewById(R.id.buttonContinueConnectWith);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference();


        if (mUser != null) {
            userEmail.setText(mUser.getEmail());

            userPhotoUrl = mUser.getPhotoUrl();

            Glide.with(this)
                    .load(userPhotoUrl)
                    .into(userImageView);
        }

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

        pictureListener = new AccountCreatedActivity.PictureListener() {
            @Override
            public void onProfilePictureUpdated() {
                Uri uri = mUser.getPhotoUrl();
                //Toast.makeText(HomeActivity.this, ""+uri.toString(), Toast.LENGTH_SHORT).show();
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
                                String senderUid = mUser.getUid();
                                String receivingUid = user.uid;
                                String status = "Pending";
                                DAOPendingConnection.getInstance().add(new PendingConnection(senderUid, receivingUid, status)).addOnSuccessListener(suc -> {
                                    Toast.makeText(AccountCreatedActivity.this, "Your connection request has been made.", Toast.LENGTH_SHORT).show();

                                    redirectToSentRequestActivity(user.email);

                                }).addOnFailureListener(fail -> {
                                    Toast.makeText(AccountCreatedActivity.this, "Failed to request a connection", Toast.LENGTH_SHORT).show();
                                });
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

    private void redirectToSentRequestActivity(String receiverEmail) {
        Intent intent = new Intent(this, SentRequestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle bundle = new Bundle();
        bundle.putString("receiverEmail", receiverEmail);
        intent.putExtras(bundle);

        startActivity(intent);
    }

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

    private void signOut() {
        mAuth.signOut();
        redirectToLogIn();
    }

    // Redirects to the login screen
    public void redirectToLogIn() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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

            uploadImage();
        }
    }

    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();


            String uid = mUser.getUid();
            StorageReference ref = storageReference.child("users/" + uid + "/profile_image");

            if (ref != null) {
                Toast.makeText(this, "" + ref, Toast.LENGTH_SHORT).show();
            }

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Reference to default image file in Cloud Storage
                            // refactor this
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    updateUserProfilePicture(uri);
                                    progressDialog.dismiss();
                                }
                            });
                            Toast.makeText(AccountCreatedActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AccountCreatedActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });

        }
    }

    private void updateUserProfilePicture(final Uri uri) {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        mUser.updateProfile(profileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
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