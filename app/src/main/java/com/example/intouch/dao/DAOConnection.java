package com.example.intouch.dao;

import com.example.intouch.db.Connection;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOConnection {
    private DatabaseReference databaseReference;

    public DAOConnection(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        databaseReference = db.getReference(Connection.class.getSimpleName());
    }

    public Task<Void> add(Connection connection){
        return databaseReference.push().setValue(connection);
    }
}
