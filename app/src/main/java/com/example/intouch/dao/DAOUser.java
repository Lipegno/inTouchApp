package com.example.intouch.dao;

import com.example.intouch.db.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOUser {
    private DatabaseReference databaseReference;

    public DAOUser(){
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
}
