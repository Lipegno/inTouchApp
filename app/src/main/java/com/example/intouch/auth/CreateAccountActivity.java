package com.example.intouch.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.intouch.MainActivity;
import com.example.intouch.R;
import com.example.intouch.dao.DAOUser;
import com.example.intouch.dbmodels.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CreateAccountActivity extends AppCompatActivity {

    // region Declarations
    EditText inputEmail;
    EditText inputPassword;
    EditText inputConfirmPassword;
    Button buttonContinue;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    private StorageReference storageReference;
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Initialization
        initialize();
    }

    // region Initialization
    private void initialize() {
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        buttonContinue = findViewById(R.id.buttonContinue);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        sharedpreferences = getSharedPreferences(MainActivity.MY_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        // Set click listeners
        setClickListeners();
    }

    private void setClickListeners() {
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerformAuth();
            }
        });
    }
    // endregion

    // region Redirects
    private void redirectToAccountCreatedActivity() {
        Intent intent = new Intent(this, AccountCreatedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    // endregion

    // region Sign Up
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

                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.apply();
                        mUser = task.getResult().getUser();
                        Log.i("CreateAccountActivity",task.getResult().getUser().getUid());
                        // region Code snippet to take the device registration token on account creation
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(CreateAccountActivity.this, "Fetching FCM registration token failed: " + task.getException() , Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        String email = mUser.getEmail();
                                        String uid = mUser.getUid();

                                        // Get new FCM registration token
                                        String deviceToken = task.getResult();

                                        // Log and toast
                                        Toast.makeText(CreateAccountActivity.this, "Your device registration token"+deviceToken, Toast.LENGTH_SHORT).show();
                                        System.out.println("Your device registration token"+deviceToken);


                                        // Set also the default profile image
                                        // Reference to default image file in Cloud Storage
                                        storageReference.child("images/profile_image.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                addNewUser(uid, email, uri, deviceToken);
                                            }
                                        });

                                    }
                                });
                        // endregion
                        progressDialog.dismiss();

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(CreateAccountActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void addNewUser(String uid, String email, @NonNull Uri photoURL, String deviceToken) {
        User user = new User(uid, email, photoURL.toString(), 0, deviceToken);

        DAOUser.getInstance().add(user)
                .addOnSuccessListener(suc -> {
                    Toast.makeText(CreateAccountActivity.this, "User entity added", Toast.LENGTH_SHORT).show();
                    // Update the profile image

                    ProfilePicture profilePicture = new ProfilePicture(mUser,CreateAccountActivity.this);
                    profilePicture.updateUserProfilePicture(photoURL, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(CreateAccountActivity.this, "Picture updated", Toast.LENGTH_SHORT).show();
                                redirectToAccountCreatedActivity();
                            }
                        }
                    });

                })
                .addOnFailureListener(fail -> {
                    Toast.makeText(CreateAccountActivity.this, "Failed to add user entity", Toast.LENGTH_SHORT).show();
                });
    }

    private void validateInput(@NonNull EditText input, String error) {
        input.setError(error);
        input.requestFocus();
    }
    // endregion

    private void showProgressDialog(@NonNull ProgressDialog progressDialog) {
        progressDialog.setMessage("Por favor aguarde enquanto a sua conta é criada.");
        progressDialog.setTitle("Registo");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }
}