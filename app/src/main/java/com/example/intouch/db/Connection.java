package com.example.intouch.db;

public class Connection {
    public UserInfo firstUser;
    public UserInfo secondUser;

    public Connection(){
        firstUser = new UserInfo();
        secondUser = new UserInfo();
    }

    public Connection(UserInfo first, UserInfo second){
        this.firstUser = first;
        this.secondUser = second;
    }

    public UserInfo getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(UserInfo firstUser) {
        this.firstUser = firstUser;
    }

    public UserInfo getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(UserInfo secondUser) {
        this.secondUser = secondUser;
    }
}
