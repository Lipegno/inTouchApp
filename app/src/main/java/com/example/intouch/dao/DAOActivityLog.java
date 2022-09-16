package com.example.intouch.dao;

import com.example.intouch.dbmodels.ActivityLog;
import com.example.intouch.dbmodels.Connection;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOActivityLog {
    private static volatile DAOActivityLog instance;

    private DatabaseReference databaseReference;

    public static DAOActivityLog getInstance() {
        if (instance == null) {
            synchronized (DAOActivityLog.class) {
                if (instance == null) {
                    instance = new DAOActivityLog();
                }
            }
        }
        return instance;
    }

    private DAOActivityLog() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        databaseReference = db.getReference(ActivityLog.class.getSimpleName());
    }

    public Task<Void> add(ActivityLog log) {
        return databaseReference.push().setValue(log);
    }
}
