package com.example.intouch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.intouch.dao.DAOConnection;
import com.example.intouch.dao.DAOPendingConnection;
import com.example.intouch.dao.DAOUser;
import com.example.intouch.db.Connection;
import com.example.intouch.db.PendingConnection;
import com.example.intouch.db.User;
import com.example.intouch.helpers.Callback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountCreatedActivity extends AppCompatActivity {

    Button buttonContinue;
    EditText inputEmail;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_created);

        buttonContinue = findViewById(R.id.buttonContinue);
        inputEmail = findViewById(R.id.emailField);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputEmail.getText().toString();

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
                                    //TODO: Display ConnectionRequestSent activity
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
}