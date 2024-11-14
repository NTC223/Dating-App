package com.example.datingapp.Chat;

public class ChatObject {
    public static final int TYPE_SEND = 1;
    public static final int TYPE_RECEIVE = 2;

    private String message;
    private Boolean currentUser;
    private String profileImageUrl;

    public ChatObject(String message, Boolean currentUser, String profileImageUrl){
        this.message = message;
        this.currentUser = currentUser;
        this.profileImageUrl = profileImageUrl;
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
}

