package com.example.datingapp.Matches;

public class MatchesObject {
    private String userId;
    private String name;
    private String profileImageUrl;
    private String date;
    private String text;

    public MatchesObject(String userId, String name, String profileImageUrl, String text, String date){
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.text = text;
        this.date = date;
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
}
