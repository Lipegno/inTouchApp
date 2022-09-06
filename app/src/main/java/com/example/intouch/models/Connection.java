package com.example.intouch.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Connection {
    public UserSettings firstUser;
    public UserSettings secondUser;
    public int notified;

    public Connection() {
        firstUser = new UserSettings();
        secondUser = new UserSettings();
        notified = 0;
    }

    public Connection(UserSettings first, UserSettings second) {
        this.firstUser = first;
        this.secondUser = second;
        this.notified = 0;
    }

    public UserSettings getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(UserSettings firstUser) {
        this.firstUser = firstUser;
    }

    public UserSettings getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(UserSettings secondUser) {
        this.secondUser = secondUser;
    }

    public int getNotified() {
        return notified;
    }

    public void setNotified(int notified) {
        this.notified = notified;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("firstUser", firstUser);
        result.put("secondUser", secondUser);
        result.put("notified", notified);

        return result;
    }
}
