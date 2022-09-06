package com.example.intouch.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Connection {
    public UserInfo firstUser;
    public UserInfo secondUser;
    public int notified;

    public Connection() {
        firstUser = new UserInfo();
        secondUser = new UserInfo();
        notified = 0;
    }

    public Connection(UserInfo first, UserInfo second) {
        this.firstUser = first;
        this.secondUser = second;
        this.notified = 0;
    }

    public UserInfo getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(UserInfo firstUser) {
        this.firstUser = firstUser;
    }

    public UserInfo getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(UserInfo secondUser) {
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
