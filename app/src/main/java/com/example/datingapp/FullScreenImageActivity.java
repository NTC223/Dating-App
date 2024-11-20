package com.example.datingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

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
        finish();
    }
}
