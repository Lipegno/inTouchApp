package com.example.intouch.dao;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.intouch.db.PendingConnection;
import com.example.intouch.helpers.Callback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DAOPendingConnection {
    private static volatile DAOPendingConnection instance;

    private DatabaseReference databaseReference;

    public static DAOPendingConnection getInstance() {
        if (instance == null) {
            synchronized (DAOPendingConnection.class) {
                if (instance == null) {
                    instance = new DAOPendingConnection();
                }
            }
        }
        return instance;
    }

    public DAOPendingConnection() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        databaseReference = db.getReference(PendingConnection.class.getSimpleName());
    }

    public Task<Void> add(PendingConnection pendingConnection) {
        return databaseReference.push().setValue(pendingConnection);
    }

    public void getPendingConnectionByAUserUid(String userUid, final Callback<PendingConnection> onReceived, final Callback onFailed) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> pendingConnectionChildren = dataSnapshot.getChildren();

                for (DataSnapshot connection : pendingConnectionChildren) {
                    PendingConnection pc = connection.getValue(PendingConnection.class);

                    if (pc.receiverUID.equalsIgnoreCase(userUid) || pc.senderUID.equalsIgnoreCase(userUid)) {
                        onReceived.execute(pc);
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

    public void deletePendingConnectionByAUsersUid(String senderUid, String receiverUid, final Callback onDeleted, final Callback onFailed) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> pendingConnectionChildren = dataSnapshot.getChildren();

                for (DataSnapshot connection : pendingConnectionChildren) {
                    PendingConnection pc = connection.getValue(PendingConnection.class);

                    if (pc.receiverUID.equalsIgnoreCase(receiverUid) || pc.senderUID.equalsIgnoreCase(senderUid)) {
                        connection.getRef().removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        onDeleted.execute(null);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        onFailed.execute(null);
                                    }
                                });

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
