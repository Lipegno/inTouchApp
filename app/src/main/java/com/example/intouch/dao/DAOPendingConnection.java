package com.example.intouch.dao;

import com.example.intouch.db.Connection;
import com.example.intouch.db.PendingConnection;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOPendingConnection {
    private DatabaseReference databaseReference;

    public DAOPendingConnection(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        databaseReference = db.getReference(PendingConnection.class.getSimpleName());
    }

    public Task<Void> add(PendingConnection pendingConnection){
        return databaseReference.push().setValue(pendingConnection);
    }
}
