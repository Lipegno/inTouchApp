package com.example.intouch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.intouch.dao.DAOUser;
import com.example.intouch.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CreateAccountActivity extends AppCompatActivity {

    private StorageReference storageReference;

    EditText inputEmail;
    EditText inputPassword;
    EditText inputConfirmPassword;
    Button buttonContinue;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Initialization
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        buttonContinue = findViewById(R.id.buttonContinue);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerformAuth();
            }
        });
    }

    // Performs the authentication
    private void PerformAuth() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();

        // Inputs validation
        if (!email.matches(emailPattern)) {
            validateInput(inputEmail, "Please, enter a correct email.");
        } else if (password.isEmpty()) {
            validateInput(inputPassword, "Please, enter a password.");
        } else if (!password.equals(confirmPassword)) {
            validateInput(inputConfirmPassword, "Passwords do not match.");
        } else {
            showProgressDialog(progressDialog);

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mUser = task.getResult().getUser();
                        String email = mUser.getEmail();
                        String uid = mUser.getUid();

                        // Set also the default profile image
                        // Reference to default image file in Cloud Storage
                        storageReference.child("images/profile_image.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                addNewUser(uid, email, uri);
                            }
                        });

                        progressDialog.dismiss();


                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(CreateAccountActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void addNewUser(String uid, String email, @NonNull Uri photoURL) {
        User user = new User(uid, email, photoURL.toString());

        DAOUser.getInstance().add(user)
                .addOnSuccessListener(suc -> {
                    Toast.makeText(CreateAccountActivity.this, "User entity added", Toast.LENGTH_SHORT).show();
                    // Update the profile image
                    updateUserProfilePicture(photoURL);
                })
                .addOnFailureListener(fail -> {
                    Toast.makeText(CreateAccountActivity.this, "Failed to add user entity", Toast.LENGTH_SHORT).show();
                });
    }

    private void showProgressDialog(@NonNull ProgressDialog progressDialog) {
        progressDialog.setMessage("Please, wait while we are creating your account.");
        progressDialog.setTitle("Register");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void validateInput(@NonNull EditText input, String error) {
        input.setError(error);
        input.requestFocus();
    }

    private void redirectToAccountCreatedActivity() {

        Intent intent = new Intent(this, AccountCreatedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
                            Toast.makeText(CreateAccountActivity.this, "Picture updated", Toast.LENGTH_SHORT).show();
                            redirectToAccountCreatedActivity();
                        }
                    }
                });
    }

}