package com.example.intouch.db;

import android.net.Uri;

public class User {
    public String email;
    public String uid;
    public Uri photoURL;

    public User(){

    }

    public User(String email, String uid, Uri photoURL) {
        this.email = email;
        this.uid = uid;
        this.photoURL = photoURL;
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

    public Uri getPhotoURL() { return photoURL; }

    public void setPhotoURL(Uri photoURL) { this.photoURL = photoURL; }

}
