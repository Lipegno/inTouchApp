package com.example.intouch.db;

public class User {
    public String email;
    public String uid;

    public User(){

    }

    public User(String email, String uid){
        this.email = email;
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
