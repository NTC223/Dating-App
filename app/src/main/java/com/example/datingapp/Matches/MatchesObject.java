package com.example.datingapp.Matches;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MatchesObject {
    private String userId;
    private String name;
    private String profileImageUrl;
    private String date;
    private String displayDate;
    private String text;

    public MatchesObject(String userId, String name, String profileImageUrl, String text, String date, String displayDate){
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.text = text;
        this.date = date;
        this.displayDate = displayDate;
    }
    public String getUserId(){
        return userId;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getProfileImageUrl(){
        return profileImageUrl;
    }

    public String getDate(){
        return this.date;
    }

    public String getText(){
        return this.text;
    }

    public String getDisplayDate() {
        return displayDate;
    }
}
