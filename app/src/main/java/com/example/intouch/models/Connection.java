package com.example.intouch.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Connection {
    public User firstUser;
    public User secondUser;
    public int notified;

    public Connection() {
        firstUser = new User();
        secondUser = new User();
        notified = 0;
    }

    public Connection(User first, User second) {
        this.firstUser = first;
        this.secondUser = second;
        this.notified = 0;
    }

    public User getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(User firstUser) {
        this.firstUser = firstUser;
    }

    public User getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(User secondUser) {
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
