package com.example.intouch.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class UserSettings {
    public String userUID;
    public ArrayList<String> colorScheme;
    public String wallpaperSide;
    public ArrayList<String> emojis;


    public UserSettings(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserSettings(String userUID) {
        this.userUID = userUID;
        this.wallpaperSide = "right";

        this.colorScheme = new ArrayList<String>();
        this.colorScheme.add("#cefad0");
        this.colorScheme.add("#fff380");
        this.colorScheme.add("#ff2c2c");

        this.emojis = new ArrayList<>();
        this.emojis.add("0x1F970");
        this.emojis.add("0x1F917");
        this.emojis.add("0x1F622");
        this.emojis.add("0x1F4AA");
    }

    public UserSettings(String userUID, ArrayList<String> colorScheme) {
        this.userUID = userUID;
        this.wallpaperSide = "right";
        this.colorScheme = colorScheme;
        this.emojis = new ArrayList<>();
        this.emojis.add("0x1F970");
        this.emojis.add("0x1F917");
        this.emojis.add("0x1F622");
        this.emojis.add("0x1F4AA");
    }

    public UserSettings(String userUID, ArrayList<String> colorScheme, String wallpaperSide) {
        this.userUID = userUID;
        this.wallpaperSide = wallpaperSide;
        this.colorScheme = colorScheme;
        this.emojis = new ArrayList<>();
        this.emojis.add("0x1F970");
        this.emojis.add("0x1F917");
        this.emojis.add("0x1F622");
        this.emojis.add("0x1F4AA");
    }

    public UserSettings(String userUID, ArrayList<String> colorScheme, String wallpaperSide, ArrayList<String> emojis) {
        this.userUID = userUID;
        this.wallpaperSide = wallpaperSide;
        this.colorScheme = colorScheme;
        this.emojis = emojis;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public List<String> getColorScheme() {
        return colorScheme;
    }

    public void setColorScheme(ArrayList<String> colorScheme) {
        this.colorScheme = colorScheme;
    }

    public ArrayList<String> getEmojis() {
        return emojis;
    }

    public void setEmojis(ArrayList<String> emojis) {
        this.emojis = emojis;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userUID", userUID);
        result.put("colorScheme", colorScheme);
        result.put("wallpaperSide", wallpaperSide);
        result.put("emojis", emojis);
        return result;
    }
}
