package com.example.datingapp.Cards;

public class cards {
    private String userId, name, profileImageUrl, age, bio, lookingfor;
    public cards(String userId, String name, String profileImageUrl, String age, String bio, String lookingfor){
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.age = age;
        this.bio = bio;
        this.lookingfor = lookingfor;
    }

    public String getUserId(){
        return this.userId;
    }

    public String getName(){
        return this.name;
    }

    public String getAge(){
        return this.age;
    }

    public String getBio(){
        return this.bio;
    }

    public String getLookingfor(){
        return this.lookingfor;
    }

    public String getProfileImageUrl(){
        return this.profileImageUrl;
    }
}
