package com.example.intouch.dao;

import androidx.annotation.NonNull;

import com.example.intouch.models.ColorScheme;
import com.example.intouch.models.User;
import com.example.intouch.helpers.Callback;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DAOColorScheme {
    private static volatile DAOColorScheme instance;

    private DatabaseReference databaseReference;

    public static DAOColorScheme getInstance() {
        if (instance == null) {
            synchronized (DAOColorScheme.class) {
                if (instance == null) {
                    instance = new DAOColorScheme();
                }
            }
        }
        return instance;
    }

    private DAOColorScheme(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(ColorScheme.class.getSimpleName());
    }

    public Task<Void> add(ColorScheme colorScheme){
        try{
            return databaseReference.push().setValue(colorScheme);
        }
        catch(Exception e){
            Exception a = e;
            return null;
        }
    }

    public void getColorSchemeById(String colorSchemeID, final Callback<ColorScheme> onReceived, final Callback onFailed){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> colorSchemeChildren = dataSnapshot.getChildren();

                for (DataSnapshot colorScheme: colorSchemeChildren) {
                    ColorScheme scheme = colorScheme.getValue(ColorScheme.class);      //make a model ColorScheme with necessary fields

                    if(scheme.getColorSchemeID().equals(colorSchemeID)){
                        onReceived.execute(scheme);
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
