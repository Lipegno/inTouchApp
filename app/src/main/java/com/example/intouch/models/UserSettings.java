package com.example.intouch.models;

public class UserSettings {
    public String userUID;
    public ColorScheme colorScheme;
    public String emoji;

    public UserSettings() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserSettings(String userUID, ColorScheme colorScheme, String emoji) {
        this.userUID = userUID;
        this.colorScheme = colorScheme;
        this.emoji = emoji;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public ColorScheme getColor() {
        return colorScheme;
    }

    public void setColor(ColorScheme color) {
        this.colorScheme = color;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
}
