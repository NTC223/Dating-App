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
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private EditText mNameField, mPhoneField, mAgeField, mEducationField, mPetField, mBioField, mLookingforField, mDrinkingSmokingField, mPersonalTypeField, mZodiacField;

    private Button mConfirm, mBack;

    private ImageView[] imageViews;
    private ImageView mProfileImage, mImage1, mImage2, mImage3, mImage4, mImage5, mImage6;
    private String[] mapKeys = {"profileImageUrl", "image1", "image2", "image3", "image4", "image5", "image6"};
    private int[] defaultImages = {R.mipmap.ic_launcher, R.drawable.image_placeholder, R.drawable.image_placeholder, R.drawable.image_placeholder, R.drawable.image_placeholder, R.drawable.image_placeholder, R.drawable.image_placeholder};
    private List<Uri> imageUris = new ArrayList<>();
    private Map<String, String> uploadedImageUrls = new HashMap<>();
    private List<String> imageurls = new ArrayList<>();

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
        mImage1 = (ImageView) findViewById(R.id.image1);
        mImage2 = (ImageView) findViewById(R.id.image2);
        mImage3 = (ImageView) findViewById(R.id.image3);
        mImage4 = (ImageView) findViewById(R.id.image4);
        mImage5 = (ImageView) findViewById(R.id.image5);
        mImage6 = (ImageView) findViewById(R.id.image6);
        imageViews = new ImageView[]{
                mProfileImage, mImage1, mImage2, mImage3, mImage4, mImage5, mImage6
        };

        mBack = (Button) findViewById(R.id.back);
        mConfirm = (Button) findViewById(R.id.confirm);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getUserInfo();

        for (int i=0;i< imageViews.length; i++){
            final int index = i;
            imageViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, index);
                }
            });
        }

        for (int i=0;i< imageViews.length; i++){
            final int index = i;
            imageViews[i].setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view) {
                    showFullScreenImage(index);
                    return true;
                }
            });
        }

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

    private void showFullScreenImage(int index) {
        Intent intent = new Intent(SettingsActivity.this, FullScreenImageActivity.class);
        intent.putExtra("imageUrl", imageurls.get(index));
        startActivity(intent);
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
                            imageurls.add(imageUrl);
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

        uploadAllImages();
        finish();
    }

    private void uploadAllImages() {
        for (int i = 0; i < imageUris.size(); i++) {
            Uri imageUri = imageUris.get(i);
            if (imageUri != null) {
                final int index = i; // Biến tạm để truyền vào callback
                uploadImage(imageUri, index);
            }
        }
    }

    private void saveImageUrlsToDatabase() {
        Map<String, Object> userInfo = new HashMap<>();
        for (Map.Entry<String, String> entry : uploadedImageUrls.entrySet()) {
            userInfo.put(entry.getKey(), entry.getValue());
        }
        mUserDatabase.updateChildren(userInfo);
    }

    private void uploadImage(Uri imageUri, int index) {
        String imageName = mapKeys[index];

        StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId).child(imageName);
        Bitmap bitmap = null;

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), imageUri);
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
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        Map userInfo = new HashMap<>();
                        userInfo.put(imageName, downloadUrl.toString());
                        mUserDatabase.updateChildren(userInfo);
                    }
                });
            }
        });
    }

    private void checkUploadCompletion() {
        if (uploadedImageUrls.size() == imageUris.size()) {
            saveImageUrlsToDatabase();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && data != null){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            if (requestCode >= 0 && requestCode < imageViews.length){
                imageViews[requestCode].setImageURI(imageUri);
                addImage(imageUri,requestCode);
            }
        }
    }

    private void addImage(Uri imageUri, int imageIndex) {
        while(imageUris.size() <= imageIndex){
            imageUris.add(null);
        }
        imageUris.set(imageIndex, imageUri);
    }

    public void changePassword(View view){
        Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
        startActivity(intent);
        return;
    }

    public void logoutUser(View view){
        mAuth.signOut();
        Intent intent = new Intent(SettingsActivity.this, ChooseLoginOrRegistationActivity.class);
        startActivity(intent);
        finish();
        return;
    }
}