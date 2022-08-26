package com.example.intouch.dao;

import androidx.annotation.NonNull;

import com.example.intouch.db.Connection;
import com.example.intouch.helpers.Callback;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DAOConnection {
    private static volatile DAOConnection instance;

    private DatabaseReference databaseReference;

    public static DAOConnection getInstance() {
        if (instance == null) {
            synchronized (DAOConnection.class) {
                if (instance == null) {
                    instance = new DAOConnection();
                }
            }
        }
        return instance;
    }

    private DAOConnection(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        databaseReference = db.getReference(Connection.class.getSimpleName());
    }

    public Task<Void> add(Connection connection){
        return databaseReference.push().setValue(connection);
    }

    public void getConnectionByAUserUid(String userUid, final Callback<Connection> onReceived, final Callback onFailed){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> connectionChildren = dataSnapshot.getChildren();

                for (DataSnapshot connection: connectionChildren) {
                    Connection c = connection.getValue(Connection.class);

                    if(c.firstUser.userUID.equalsIgnoreCase(userUid) || c.secondUser.userUID.equalsIgnoreCase(userUid)){
                        onReceived.execute(c);
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
