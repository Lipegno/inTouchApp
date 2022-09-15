package com.example.intouch.dao;

import androidx.annotation.NonNull;

import com.example.intouch.models.Connection;
import com.example.intouch.helpers.Callback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

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

    private DAOConnection() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        databaseReference = db.getReference(Connection.class.getSimpleName());
    }

    public Task<Void> add(Connection connection) {
        return databaseReference.push().setValue(connection);
    }

    public void getConnectionByAUserUid(String userUid, final Callback<Connection> onReceived, final Callback onFailed) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> connectionChildren = dataSnapshot.getChildren();

                for (DataSnapshot connection : connectionChildren) {
                    Connection c = connection.getValue(Connection.class);

                    if (c.firstUser.uid.equalsIgnoreCase(userUid) || c.secondUser.uid.equalsIgnoreCase(userUid)) {
                        c.uID = connection.getKey();
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

    public void updateConnection(Connection connection, final Callback onUpdated, final Callback onFailed) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Map<String, Object> postValues = connection.toMap();
                databaseReference.child(connection.uID).updateChildren(postValues)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                onUpdated.execute(null);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                onUpdated.execute(null);
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public Task<Void> deleteConnection(Connection connection) {
        return databaseReference.child(connection.uID).removeValue();
    }
}
