package com.example.datingapp.Chat;

public class ChatObject {
    private String message;
    private Boolean currentUser;
    public ChatObject(String message, Boolean currentUser){
        this.message = message;
        this.currentUser = currentUser;
    }

    public String getMessage(){
        return this.message;
    }

    public Boolean getCurrentUser(){
        return this.currentUser;
    }
}

