package com.example.intouch.models;

public class FCMNotification {
    public String deviceToken;
    public String title;
    public String message;

    public FCMNotification(){}

    public FCMNotification(String deviceToken, String title, String message) {
        this.deviceToken = deviceToken;
        this.title = title;
        this.message = message;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
