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
    private ImageView profileImage;
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
        profileImage = (ImageView) findViewById(R.id.profileImage);

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
                    Glide.with(getApplication()).clear(profileImage);
                    if(map.get("profileImageUrl") != null){
                        profileImageUrl = map.get("profileImageUrl").toString();
                        switch (profileImageUrl){
                            case "default":
                                Glide.with(getApplication()).load(R.mipmap.ic_launcher).into(profileImage);
                                break;
                            default:
                                Glide.with(getApplication()).load(profileImageUrl).into(profileImage);
                                break;
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