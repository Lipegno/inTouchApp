package com.example.intouch.dao;

import androidx.annotation.NonNull;

import com.example.intouch.helpers.Callback;
import com.example.intouch.models.Connection;
import com.example.intouch.models.User;
import com.example.intouch.models.UserSettings;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class DAOUserSettings {
    private static volatile DAOUserSettings instance;

    private DatabaseReference databaseReference;

    public static DAOUserSettings getInstance() {
        if (instance == null) {
            synchronized (DAOUserSettings.class) {
                if (instance == null) {
                    instance = new DAOUserSettings();
                }
            }
        }
        return instance;
    }

    private DAOUserSettings(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(UserSettings.class.getSimpleName());
    }

    public Task<Void> add(UserSettings userSettings){
        try{
            return databaseReference.push().setValue(userSettings);
        }
        catch(Exception e){
            Exception a = e;
            return null;
        }
    }

    public void getUserSettingsByUserId(String UID, final Callback<UserSettings> onReceived, final Callback onFailed){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> userSettingsChildren = dataSnapshot.getChildren();

                for (DataSnapshot settings: userSettingsChildren) {
                    UserSettings s = settings.getValue(UserSettings.class);      //make a model UserSettings with necessary fields

                    if(s.userUID.equalsIgnoreCase(UID)){
                        onReceived.execute(s);
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

    public void updateUserSettingsByUserId(UserSettings userSettings, final Callback onUpdated, final Callback onFailed) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> userSettingsValue = userSettings.toMap();
                databaseReference.updateChildren(userSettingsValue)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                onUpdated.execute(null);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                onFailed.execute(null);
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
