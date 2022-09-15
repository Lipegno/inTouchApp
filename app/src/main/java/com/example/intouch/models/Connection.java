package com.example.intouch.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Connection {

    @Exclude
    public String uID;

    public User firstUser;
    public User secondUser;

    public Connection() {
        firstUser = new User();
        secondUser = new User();
    }

    public Connection(User first, User second) {
        this.firstUser = first;
        this.secondUser = second;
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

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("firstUser", firstUser);
        result.put("secondUser", secondUser);

        return result;
    }
}
