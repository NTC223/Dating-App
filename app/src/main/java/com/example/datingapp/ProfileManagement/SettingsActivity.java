package com.example.datingapp.ProfileManagement;

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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.datingapp.MainActivity;
import com.example.datingapp.UIHelper.FullScreenImageActivity;
import com.example.datingapp.LoginRegister.ChangePasswordActivity;
import com.example.datingapp.LoginRegister.ChooseLoginOrRegistationActivity;
import com.example.datingapp.R;
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
    private LinearLayout imageContainer;
    private Button mConfirm, mBack, mExpandButton;
    private RadioGroup mRadioGroup;
    private ImageView[] imageViews;
    private ImageView mProfileImage, mImage1, mImage2, mImage3, mImage4, mImage5, mImage6;
    private SeekBar seekBarMax, seekBarMin;
    private TextView bubbleMax, bubbleMin;
    private String minAge, maxAge;

    private String[] mapKeys = {"profileImageUrl", "image1", "image2", "image3", "image4", "image5", "image6"};
    private int[] defaultImages = {R.mipmap.ic_launcher, R.drawable.image_placeholder, R.drawable.image_placeholder, R.drawable.image_placeholder, R.drawable.image_placeholder, R.drawable.image_placeholder, R.drawable.image_placeholder};
    private List<Uri> imageUris = new ArrayList<>();
    private Map<String, String> uploadedImageUrls = new HashMap<>();
    private List<String> imageurls = new ArrayList<>();

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private String userId, show, name, phone, profileImageUrl, userSex, age, education, pet, bio, lookingfor, zodiac, drinkingSmoking, personalType;

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

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        seekBarMax = (SeekBar) findViewById(R.id.seekBarMax);
        bubbleMax = (TextView) findViewById(R.id.bubbleMax);
        seekBarMin = (SeekBar) findViewById(R.id.seekBarMin);
        bubbleMin = (TextView) findViewById(R.id.bubbleMin);

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
        mExpandButton = (Button) findViewById(R.id.expand_button);
        imageContainer = (LinearLayout) findViewById(R.id.image_container);

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
        mExpandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageContainer.getVisibility() == View.GONE) {
                    imageContainer.setVisibility(View.VISIBLE);
                    mExpandButton.setText("Hide Images");
                } else {
                    imageContainer.setVisibility(View.GONE);
                    mExpandButton.setText("Add Images");
                }
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

        seekBarMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                bubbleMax.setVisibility(View.VISIBLE);
                bubbleMax.setText(String.valueOf(i));
                maxAge = String.valueOf(i);

                float thumbOffset = seekBar.getThumb().getBounds().exactCenterX();
                float position = seekBar.getX() + thumbOffset;
                bubbleMax.setX(position - (bubbleMax.getWidth() / 2));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                bubbleMax.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                bubbleMax.setVisibility(View.GONE);
            }
        });

        seekBarMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                bubbleMin.setVisibility(View.VISIBLE);
                bubbleMin.setText(String.valueOf(i));
                minAge = String.valueOf(i);

                float thumbOffset = seekBar.getThumb().getBounds().exactCenterX();
                float position = seekBar.getX() + thumbOffset;
                bubbleMin.setX(position - (bubbleMin.getWidth() / 2));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                bubbleMin.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                bubbleMin.setVisibility(View.GONE);
            }
        });
    }

    private void showFullScreenImage(int index) {
        if (imageurls.get(index).equals("default")){
            return;
        }
        Intent intent = new Intent(SettingsActivity.this, FullScreenImageActivity.class);
        intent.putExtra("imageUrl", imageurls.get(index));
        intent.putExtra("Uid", userId);
        intent.putExtra("imageName", mapKeys[index]);
        startActivity(intent);
        finish();
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
                    if(map.get("show")!=null){
                        show = map.get("show").toString();
                        switch (show){
                            case "Male":
                                mRadioGroup.check(R.id.male);
                                break;
                            case "Female":
                                mRadioGroup.check(R.id.female);
                                break;
                            default:
                                mRadioGroup.check(R.id.everyone);
                        }
                    }
                    if(map.get("maxAge")!=null){
                        maxAge = map.get("maxAge").toString();
                        seekBarMax.setProgress(Integer.parseInt(maxAge));
                    }
                    if(map.get("minAge")!=null){
                        minAge = map.get("minAge").toString();
                        seekBarMin.setProgress(Integer.parseInt(minAge));
                    }
                    for (int i=0; i < imageViews.length; i++){
                        ImageView imageView = imageViews[i];
                        String key = mapKeys[i];
                        int defaultImage = defaultImages[i];

                        Glide.with(getApplication()).clear(imageView);
                        if(map.get(key) != null){
                            String imageUrl = map.get(key).toString();
                            if(!imageUrl.equals("default")){
                                Glide.with(getApplication()).load(imageUrl).into(imageView);
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
        int selectId = mRadioGroup.getCheckedRadioButtonId();
        if (selectId == -1){
            show = "Everyone";
        }else {
            final RadioButton radioButton = (RadioButton) findViewById(selectId);
            show = radioButton.getText().toString();
        }
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
        userInfo.put("show", show);
        userInfo.put("minAge", minAge);
        userInfo.put("maxAge", maxAge);
        mUserDatabase.updateChildren(userInfo);

        uploadAllImages();
        Toast.makeText(SettingsActivity.this,"Saved", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        return;
    }
}