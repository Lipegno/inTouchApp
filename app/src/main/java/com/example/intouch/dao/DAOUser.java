package com.example.intouch.dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.intouch.dbmodels.User;
import com.example.intouch.helpers.Callback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class DAOUser {
    private static volatile DAOUser instance;

    private DatabaseReference databaseReference;
    private static final String TAG = "DAOUser";

    public static DAOUser getInstance() {
        if (instance == null) {
            synchronized (DAOUser.class) {
                if (instance == null) {
                    instance = new DAOUser();
                }
            }
        }
        return instance;
    }

    private DAOUser() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(User.class.getSimpleName());
    }

    public Task<Void> add(User user) {
        try {
            return databaseReference.push().setValue(user);
        } catch (Exception e) {
            Exception a = e;
            return null;
        }
    }

    public void update(User user) {

        HashMap<String, Object> result = new HashMap<>();
        result.put("deviceToken", user.getDeviceToken());
        result.put("email", user.getEmail());
        result.put("notified", user.getNotified());
        result.put("photoURL", user.getPhotoURL());
        result.put("uid", user.getUid());


        getUserByEmail(user.getEmail(), new Callback<User>() {
            @Override
            public void execute(User object) {
                Log.i(TAG,"Testinho");

                databaseReference.child(object.getKey()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i(TAG, "Updating user success");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Updating user failed");
                    }
                });
            }
        }, new Callback() {
            @Override
            public void execute(Object object) {
                Log.i(TAG,"Testinho");
            }
        });


    }

    public void getUserByEmail(String email, final Callback<User> onReceived, final Callback onFailed) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> userChildren = dataSnapshot.getChildren();

                for (DataSnapshot user : userChildren) {
                    User u = user.getValue(User.class);      //make a model User with necessary fields
                    u.setKey(user.getKey());
                    if (u.email.equalsIgnoreCase(email)) {
                        onReceived.execute(u);
                        return;
                    }
                }

                onFailed.execute(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getUserById(String UID, final Callback<User> onReceived, final Callback onFailed) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> userChildren = dataSnapshot.getChildren();

                for (DataSnapshot user : userChildren) {
                    User u = user.getValue(User.class);      //make a model User with necessary fields

                    if (u.uid.equalsIgnoreCase(UID)) {
                        onReceived.execute(u);
                        return;
                    }
                }

                onFailed.execute(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

