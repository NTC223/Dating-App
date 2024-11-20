package com.example.datingapp.Chat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatObject {
    public static final int TYPE_SEND = 1;
    public static final int TYPE_RECEIVE = 2;

    private String message;
    private Boolean currentUser;
    private String profileImageUrl, dateTime;

    public ChatObject(String message, Boolean currentUser, String profileImageUrl, String dateTime){
        this.message = message;
        this.currentUser = currentUser;
        this.profileImageUrl = profileImageUrl;
        this.dateTime = dateTime;
    }

    public String getMessage(){
        return this.message;
    }

    public Boolean getCurrentUser(){
        return this.currentUser;
    }

    public String getProfileImageUrl(){
        return this.profileImageUrl;
    }

    public String getDateTime() {
        return dateTime;
    }


}

