package com.example.intouch.dao;

import androidx.annotation.NonNull;

import com.example.intouch.models.User;
import com.example.intouch.helpers.Callback;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DAOUser {
    private static volatile DAOUser instance;

    private DatabaseReference databaseReference;

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

    private DAOUser(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(User.class.getSimpleName());
    }

    public Task<Void> add(User user){
        try{
            return databaseReference.push().setValue(user);
        }
        catch(Exception e){
            Exception a = e;
            return null;
        }
    }

    public void getUserByEmail(String email, final Callback<User> onReceived, final Callback onFailed){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> userChildren = dataSnapshot.getChildren();

                for (DataSnapshot user: userChildren) {
                    User u = user.getValue(User.class);      //make a model User with necessary fields

                    if(u.email.equalsIgnoreCase(email)){
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

    public void getUserById(String UID, final Callback<User> onReceived, final Callback onFailed){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> userChildren = dataSnapshot.getChildren();

                for (DataSnapshot user: userChildren) {
                    User u = user.getValue(User.class);      //make a model User with necessary fields

                    if(u.uid.equalsIgnoreCase(UID)){
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
