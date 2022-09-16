package com.example.intouch.auth;

import androidx.annotation.NonNull;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.example.intouch.R;
import com.example.intouch.dao.DAOUser;
import com.example.intouch.dbmodels.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

public class GoogleSignInActivity extends LogInActivity {

    // region Declarations
    private static final int RC_SIGN_IN = 101;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressDialog progressDialog;
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialization
        initialize();

        // Configure and start sign in with Google
        signInWithGoogle();
    }

    // region Initialization
    private void initialize() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Google Sign In...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    private void signInWithGoogle() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    // endregion

    // region Redirects
    private void redirectToAccountCreatedActivity() {
        Intent intent = new Intent(GoogleSignInActivity.this, AccountCreatedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    // endregion

    // region Activity Results
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch(ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
    }
    // endregion

    // region SignIn with Google
    // Performs the authentication with Google
    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            addNewUser();
                        } else {
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(GoogleSignInActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }

    // Adds the user in the Firebase Real-time database
    private void addNewUser() {
        FirebaseUser fbuser = mAuth.getCurrentUser();
        String email = fbuser.getEmail();
        String uid = fbuser.getUid();
        Uri photoURL = fbuser.getPhotoUrl();

        // region Code snippet to take the device registration token on account creation
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(GoogleSignInActivity.this, "Fetching FCM registration token failed: " + task.getException() , Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Get new FCM registration token
                        String deviceToken = task.getResult();

                        User user = new User(uid, email, photoURL.toString(), 0, deviceToken);

                        DAOUser.getInstance().add(user)
                                .addOnSuccessListener(suc -> {
                                    Toast.makeText(GoogleSignInActivity.this, "User entity added", Toast.LENGTH_SHORT).show();
                                    redirectToAccountCreatedActivity();
                                })
                                .addOnFailureListener(fail -> {
                                    Toast.makeText(GoogleSignInActivity.this, "Failed to add user entity", Toast.LENGTH_SHORT).show();
                                });
                    }
                });

        // endregion

    }
    // endregion
}