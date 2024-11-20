package com.example.datingapp.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.datingapp.R;

public class ChatViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMessage;
    public ImageView mImageView;
    public TextView mDateView;

    public ChatViewHolders(View itemView){
        super(itemView);
        itemView.setOnClickListener(this);

        mMessage = itemView.findViewById(R.id.message);
        mImageView = itemView.findViewById(R.id.imageProfile);
        mDateView = itemView.findViewById(R.id.textDateTime);
        mDateView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view){

    }
}