package com.example.intouch.dbmodels;

public class ActivityLog {
    public String activity;
    public String senderEmail;
    public String receiverEmail;
    public String dateTime;

    public ActivityLog(){}

    public ActivityLog(String activity, String senderEmail, String receiverEmail, String dateTime){
        this.activity = activity;
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.dateTime = dateTime;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
