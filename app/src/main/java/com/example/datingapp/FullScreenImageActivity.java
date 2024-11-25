package com.example.datingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FullScreenImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_size_image);

        ImageView fullImageView = findViewById(R.id.fullImageView);

        Glide.with(getApplication()).clear(fullImageView);
            String imageUrl = getIntent().getExtras().getString("imageUrl");
            switch (imageUrl){
                case "default":
                    Glide.with(getApplication()).load(R.drawable.image_placeholder).into(fullImageView);
                    break;
                default:
                    Glide.with(getApplication()).load(imageUrl).into(fullImageView);
                    break;
            }
    }
    public void goBack(View view){
        Intent intent = new Intent(FullScreenImageActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
    public void deleteImage(View view){
        String userId = getIntent().getExtras().getString("Uid");
        String imageName = getIntent().getExtras().getString("imageName");
        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId).child(imageName);
        filepath.delete().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(FullScreenImageActivity.this, "Đã xoá", Toast.LENGTH_SHORT).show();
                mUserDatabase.child(imageName).setValue("default");
                Intent intent = new Intent(FullScreenImageActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }else {
                Toast.makeText(FullScreenImageActivity.this, "Xoá thất bại", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
