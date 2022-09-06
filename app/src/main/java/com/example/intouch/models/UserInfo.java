package com.example.intouch.models;

public class UserInfo {
    public String userUID;
    public String color;
    public String emoji;

    public UserInfo() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserInfo(String userUID, String color, String emoji) {
        this.userUID = userUID;
        this.color = color;
        this.emoji = emoji;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
}
