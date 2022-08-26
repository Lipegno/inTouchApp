package com.example.intouch.db;

public class PendingConnection {
    public String senderUID;
    public String receiverUID;
    public String status;

    public PendingConnection(){

    }

    public PendingConnection(String senderUID, String receiverUID, String status){
        this.senderUID = senderUID;
        this.receiverUID = receiverUID;
        this.status = status;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
    }

    public String getReceiverUID() {
        return receiverUID;
    }

    public void setReceiverUID(String receiverUID) {
        this.receiverUID = receiverUID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
