package com.example.intouch.db;

import android.net.Uri;

import java.io.Serializable;

public class User implements Serializable {
    public String uid;
    public String email;
    public String photoURL;

    public User(){}

    public User(String uid, String email, String photoURL) {
        this.uid = uid;
        this.email = email;
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

    public String getPhotoURL() { return photoURL; }

    public void setPhotoURL(String photoURL) { this.photoURL = photoURL; }

}
