package com.example.intouch.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.intouch.requests.AcceptCancelRequestActivity;
import com.example.intouch.requests.AcceptedRequestActivity;
import com.example.intouch.HomeActivity;
import com.example.intouch.MainActivity;
import com.example.intouch.R;
import com.example.intouch.requests.WaitRequestActivity;
import com.example.intouch.dao.DAOConnection;
import com.example.intouch.dao.DAOPendingConnection;
import com.example.intouch.dao.DAOUser;
import com.example.intouch.models.Connection;
import com.example.intouch.models.PendingConnection;
import com.example.intouch.models.User;
import com.example.intouch.helpers.Callback;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    // region Declarations
    EditText inputEmail;
    EditText inputPassword;
    Button buttonContinueLogIn;
    SignInButton signInButton;
    TextView signUpTextView;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialization
        initialize();
    }

    // region Initialization
    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        sharedpreferences = getSharedPreferences(MainActivity.MY_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        progressDialog = new ProgressDialog(this);

        String savedEmail = sharedpreferences.getString("email", null);
        String savedPassword = sharedpreferences.getString("password", null);

        // If user was previously logged in
        if (savedEmail != null && savedPassword != null) {
            PerformLogIn(savedEmail, savedPassword);
            return;
        }

        setContentView(R.layout.activity_log_in);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        buttonContinueLogIn = findViewById(R.id.buttonContinueLogIn);
        signUpTextView = findViewById(R.id.signUp);

        // Set the dimensions of the sign-in button.
        signInButton = findViewById(R.id.google_sign_in);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        setGoogleSignInButtonText(signInButton, "Continue with Google");

        // Set click listeners
        setClickListeners();
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

    private void setClickListeners() {
        buttonContinueLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                PerformLogIn(email, password);
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
    //endregion

    // region Redirects
    private void redirectToCreateAccountActivity() {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void redirectToWaitRequestActivity(User receiver) {
        Intent intent = new Intent(this, WaitRequestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle bundle = new Bundle();
        bundle.putSerializable("receiver", receiver);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    private void redirectToAcceptCancelRequest(String senderUID) {
        Intent intent = new Intent(this, AcceptCancelRequestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle bundle = new Bundle();
        bundle.putString("senderUID", senderUID);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    private void redirectToAcceptedRequestActivity(String firstUID, String secondUID) {
        Intent intent = new Intent(this, AcceptedRequestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        User sender = getUserById(firstUID);
        User receiver = getUserById(secondUID);

        Bundle bundle = new Bundle();
        bundle.putSerializable("sender", sender);
        bundle.putSerializable("receiver", receiver);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    private void redirectToAccountCreatedActivity() {
        Intent intent = new Intent(this, AccountCreatedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void redirectToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    // endregion

    // region Log In
    private void PerformLogIn(String email, String password) {

        if (!email.matches(emailPattern)) {
            validateInput(inputEmail, "Please, enter a correct email.");
        } else if (password.isEmpty()) {
            validateInput(inputPassword, "Please, enter a password.");
        } else {
            showProgressDialog(progressDialog);

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.apply();

                        FirebaseUser user = task.getResult().getUser();
                        String userUID = user.getUid();

                        // Check if user is in a connection

                        DAOConnection.getInstance().getConnectionByAUserUid(userUID,
                                //  If there is already a connection that includes this user ->
                                hasConnection(userUID),
                                //  If there is no connection that includes this user ->
                                hasNoConnection(userUID));


                        progressDialog.dismiss();

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LogInActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private Callback hasNoConnection(String userUID) {
        return new Callback() {
            @Override
            public void execute(Object object) {
                DAOUser daoUser = DAOUser.getInstance();
                //  If there is no connection that includes this user
                //  check if there is already a pending connection that includes this user
                DAOPendingConnection.getInstance().getPendingConnectionByAUserUid(userUID,
                        //  if there is already a pending connection that includes this user -> display message
                        new Callback<PendingConnection>() {
                            @Override
                            public void execute(PendingConnection pc) {
                                // 2) Check if user is receiver
                                if (userUID.equals(pc.receiverUID)) {
                                    // if yes redirect him to AcceptCancelRequest view
                                    redirectToAcceptCancelRequest(pc.senderUID);
                                } else if (userUID.equals(pc.senderUID)) {
                                    // else
                                    // redirect him to WaitRequestActivity
                                    daoUser.getUserById(pc.receiverUID,
                                            new Callback<User>() {
                                                @Override
                                                public void execute(User user) {
                                                    redirectToWaitRequestActivity(user);
                                                }
                                            }, new Callback() {
                                                @Override
                                                public void execute(Object object) {
                                                    Toast.makeText(LogInActivity.this, "Could not get the receiver email.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(LogInActivity.this, "User id does not match to receiver nor sender.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        },
                        new Callback() {
                            @Override
                            public void execute(Object object) {
                                redirectToAccountCreatedActivity();
                            }
                        });
            }
        };
    }

    private Callback<Connection> hasConnection(String userUID) {
        //  If there is already a connection that includes this user ->
        return new Callback<Connection>() {
            @Override
            public void execute(Connection connection) {
                //  check if it is first time after request was accepted
                User currentUser = connection.firstUser.uid.equals((userUID)) ? connection.firstUser : connection.secondUser;
                if (currentUser.notified == 0) {
                    currentUser.notified = 1;
                    DAOConnection.getInstance().updateConnection(connection, new Callback() {
                        @Override
                        public void execute(Object object) {
                            redirectToAcceptedRequestActivity(connection.firstUser.uid, connection.secondUser.uid);
                        }
                    }, new Callback() {
                        @Override
                        public void execute(Object object) {
                            Toast.makeText(LogInActivity.this, "The user could not be notified.", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    redirectToHomeActivity();
                }
            }
        };
    }

    private User getUserById(String firstUID) {
        User user = new User();

        DAOUser.getInstance().getUserById(firstUID, new Callback<User>() {
            @Override
            public void execute(User user) {
                user = user;
            }
        }, new Callback() {
            @Override
            public void execute(Object object) {

            }
        });

        return user;
    }

    private void validateInput(@NonNull EditText input, String error) {
        input.setError(error);
        input.requestFocus();
    }
    // endregion

    private void showProgressDialog(@NonNull ProgressDialog progressDialog) {
        progressDialog.setMessage("Please, wait while we are logging you in.");
        progressDialog.setTitle("Log In");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }
}