package com.example.datingapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.datingapp.Matches.MatchesActivity;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private EditText mNameField, mPhoneField, mAgeField, mEducationField, mPetField, mBioField, mLookingforField, mDrinkingSmokingField, mPersonalTypeField, mZodiacField;

    private Button mConfirm, mBack;

    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private String userId, name, phone, profileImageUrl, userSex, age, education, pet, bio, lookingfor, zodiac, drinkingSmoking, personalType;

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.phone);
        mAgeField = (EditText) findViewById(R.id.age);
        mEducationField = (EditText) findViewById(R.id.education);
        mPetField = (EditText) findViewById(R.id.pet);
        mBioField = (EditText) findViewById(R.id.bio);
        mLookingforField = (EditText) findViewById(R.id.lookingfor);
        mZodiacField = (EditText) findViewById(R.id.zodiac);
        mDrinkingSmokingField = (EditText) findViewById(R.id.drinkingSmoking);
        mPersonalTypeField = (EditText) findViewById(R.id.personalType);

        mProfileImage = (ImageView) findViewById(R.id.profileImage);

        mBack = (Button) findViewById(R.id.back);
        mConfirm = (Button) findViewById(R.id.confirm);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });
    }

    private void getUserInfo(){
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if(map.get("name")!=null){
                        name = map.get("name").toString();
                        mNameField.setText(name);
                    }
                    if(map.get("phone")!=null){
                        phone = map.get("phone").toString();
                        mPhoneField.setText(phone);
                    }
                    if(map.get("age")!=null){
                        age = map.get("age").toString();
                        mAgeField.setText(age);
                    }
                    if(map.get("education")!=null){
                        education = map.get("education").toString();
                        mEducationField.setText(education);
                    }
                    if(map.get("pet")!=null){
                        pet = map.get("pet").toString();
                        mPetField.setText(pet);
                    }
                    if(map.get("bio")!=null){
                        bio = map.get("bio").toString();
                        mBioField.setText(bio);
                    }
                    if(map.get("lookingfor")!=null){
                        lookingfor = map.get("lookingfor").toString();
                        mLookingforField.setText(lookingfor);
                    }
                    if(map.get("personalType")!=null){
                        personalType = map.get("personalType").toString();
                        mPersonalTypeField.setText(personalType);
                    }
                    if(map.get("zodiac")!=null){
                        zodiac = map.get("zodiac").toString();
                        mZodiacField.setText(zodiac);
                    }
                    if(map.get("drinkingSmoking")!=null){
                        drinkingSmoking = map.get("drinkingSmoking").toString();
                        mDrinkingSmokingField.setText(drinkingSmoking);
                    }
                    if(map.get("sex")!=null){
                        userSex = map.get("sex").toString();
                    }
                    Glide.with(getApplication()).clear(mProfileImage);
                    if(map.get("profileImageUrl") != null){
                        profileImageUrl = map.get("profileImageUrl").toString();
                        switch (profileImageUrl){
                            case "default":
                                Glide.with(getApplication()).load(R.mipmap.ic_launcher).into(mProfileImage);
                                break;
                            default:
                                Glide.with(getApplication()).load(profileImageUrl).into(mProfileImage);
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

    private void saveUserInformation(){
        name = mNameField.getText().toString();
        phone = mPhoneField.getText().toString();
        age = mAgeField.getText().toString();
        education = mEducationField.getText().toString();
        pet = mPetField.getText().toString();
        bio = mBioField.getText().toString();
        lookingfor = mLookingforField.getText().toString();
        personalType = mPersonalTypeField.getText().toString();
        zodiac = mZodiacField.getText().toString();
        drinkingSmoking = mDrinkingSmokingField.getText().toString();

        Map userInfo = new HashMap<>();
        userInfo.put("name",name);
        userInfo.put("phone",phone);
        userInfo.put("age",age);
        userInfo.put("education", education);
        userInfo.put("pet", pet);
        userInfo.put("bio",bio);
        userInfo.put("lookingfor",lookingfor);
        userInfo.put("zodiac",zodiac);
        userInfo.put("personalType", personalType);
        userInfo.put("drinkingSmoking", drinkingSmoking);

        mUserDatabase.updateChildren(userInfo);
        if (resultUri != null){
            StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filepath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUrl) {
                            Map userInfo = new HashMap<>();
                            userInfo.put("profileImageUrl",downloadUrl.toString());
                            mUserDatabase.updateChildren(userInfo);
                            finish();
                        }
                    });
                }
            });

        }else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }

    public void logoutUser(View view){
        mAuth.signOut();
        Intent intent = new Intent(SettingsActivity.this, ChooseLoginOrRegistationActivity.class);
        startActivity(intent);
        finish();
        return;
    }
}