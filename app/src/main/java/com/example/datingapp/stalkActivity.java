package com.example.datingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class stalkActivity extends AppCompatActivity {
    private TextView nameField, phoneField, ageField, educationField, petField, bioField, lookingforField, zodiacField, drinkingSmokingField, personalTypeField;
    private String  userId, name, phone, profileImageUrl, age, education, pet, bio, lookingfor, zodiac, drinkingSmoking, personalType;
    private ImageView[] imageViews;
    private ImageView ProfileImage, Image1, Image2, Image3, Image4, Image5, Image6;
    private String[] mapKeys = {"profileImageUrl", "image1", "image2", "image3", "image4", "image5", "image6"};
    private int[] defaultImages = {R.mipmap.ic_launcher, R.drawable.image_placeholder, R.drawable.image_placeholder, R.drawable.image_placeholder, R.drawable.image_placeholder, R.drawable.image_placeholder, R.drawable.image_placeholder};
    private Button mBack;
    private DatabaseReference userDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stalk);

        userId = getIntent().getExtras().getString("userId");

        mBack = (Button) findViewById(R.id.back);
        nameField = (TextView) findViewById(R.id.name);
        phoneField = (TextView) findViewById(R.id.phone);
        ageField = (TextView) findViewById(R.id.age);
        educationField = (TextView) findViewById(R.id.education);
        petField = (TextView) findViewById(R.id.pet);
        bioField = (TextView) findViewById(R.id.bio);
        lookingforField = (TextView) findViewById(R.id.lookingfor);
        zodiacField = (TextView) findViewById(R.id.zodiac);
        drinkingSmokingField = (TextView) findViewById(R.id.drinkingSmoking);
        personalTypeField = (TextView) findViewById(R.id.personalType);
        ProfileImage = (ImageView) findViewById(R.id.profileImage);
        Image1 = (ImageView) findViewById(R.id.image1);
        Image2 = (ImageView) findViewById(R.id.image2);
        Image3 = (ImageView) findViewById(R.id.image3);
        Image4 = (ImageView) findViewById(R.id.image4);
        Image5 = (ImageView) findViewById(R.id.image5);
        Image6 = (ImageView) findViewById(R.id.image6);
        imageViews = new ImageView[]{
                ProfileImage, Image1, Image2, Image3, Image4, Image5, Image6
        };

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getUserInfo();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });
    }
    private void getUserInfo(){
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if(map.get("name")!=null){
                        name = map.get("name").toString();
                        nameField.setText(name);
                    }
                    if(map.get("phone")!=null){
                        phone = map.get("phone").toString();
                        phoneField.setText(phone);
                    }
                    if(map.get("age")!=null){
                        age = map.get("age").toString();
                        ageField.setText(age);
                    }
                    if(map.get("education")!=null){
                        education = map.get("education").toString();
                        educationField.setText(education);
                    }
                    if(map.get("pet")!=null){
                        pet = map.get("pet").toString();
                        petField.setText(pet);
                    }
                    if(map.get("bio")!=null){
                        bio = map.get("bio").toString();
                        bioField.setText(bio);
                    }
                    if(map.get("lookingfor")!=null){
                        lookingfor = map.get("lookingfor").toString();
                        lookingforField.setText(lookingfor);
                    }
                    if(map.get("personalType")!=null){
                        personalType = map.get("personalType").toString();
                        personalTypeField.setText(personalType);
                    }
                    if(map.get("zodiac")!=null){
                        zodiac = map.get("zodiac").toString();
                        zodiacField.setText(zodiac);
                    }
                    if(map.get("drinkingSmoking")!=null){
                        drinkingSmoking = map.get("drinkingSmoking").toString();
                        drinkingSmokingField.setText(drinkingSmoking);
                    }
                    for (int i=0; i < imageViews.length; i++){
                        ImageView imageView = imageViews[i];
                        String key = mapKeys[i];
                        int defaultImage = defaultImages[i];

                        Glide.with(getApplication()).clear(imageView);
                        if(map.get(key) != null){
                            String imageUrl = map.get(key).toString();
                            switch (imageUrl){
                                case "default":
                                    Glide.with(getApplication()).load(defaultImage).into(imageView);
                                    break;
                                default:
                                    Glide.with(getApplication()).load(imageUrl).into(imageView);
                                    break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}