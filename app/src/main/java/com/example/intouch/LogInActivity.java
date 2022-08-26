package com.example.intouch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    EditText inputEmail;
    EditText inputPassword;
    Button buttonContinueLogIn;
    SignInButton signInButton;
    TextView signUpTextView;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        buttonContinueLogIn = findViewById(R.id.buttonContinueLogIn);
        signUpTextView = findViewById(R.id.signUp);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // Set the dimensions of the sign-in button.
        signInButton = findViewById(R.id.google_sign_in);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        setGoogleSignInButtonText(signInButton, "Continue with Google");


        buttonContinueLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerformLogIn();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this, GoogleSignInActivity.class);
                startActivity(intent);
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToCreateAccountActivity();
            }
        });

    }

    private void redirectToCreateAccountActivity() {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setGoogleSignInButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    private void PerformLogIn() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (!email.matches(emailPattern)) {
            validateInput(inputEmail, "Please, enter a correct email.");
        } else if (password.isEmpty()) {
            validateInput(inputPassword, "Please, enter a password.");
        } else {
            showProgressDialog(progressDialog);

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        redirectToHomeActivity(email);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LogInActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showProgressDialog(@NonNull ProgressDialog progressDialog) {
        progressDialog.setMessage("Please, wait while we are logging you in.");
        progressDialog.setTitle("Log In");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void validateInput(@NonNull EditText input, String error){
        input.setError(error);
        input.requestFocus();
    }

    private void redirectToHomeActivity(String email) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

        // Creating the bundle
        Bundle bundle = new Bundle();
        // Adding the data to bundle
        bundle.putString("email", email);
        // Adding the bundle to the intent
        intent.putExtras(bundle);

        startActivity(intent);
    }
}