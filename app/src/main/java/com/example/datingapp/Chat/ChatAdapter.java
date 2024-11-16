package com.example.datingapp.Chat;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.datingapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolders> {
    private List<ChatObject> chatList;
    private Context context;

    public ChatAdapter(List<ChatObject> matchesList, Context context){
        this.chatList = matchesList;
        this.context = context;
    }
    @NonNull
    @Override
    public ChatViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView;

        if (viewType == ChatObject.TYPE_SEND) {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_send_message, parent, false);
        } else {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_received_message, parent, false);
        }

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        return new ChatViewHolders(layoutView);
    }

    @Override
    public int getItemViewType(int position) {
        ChatObject chat = chatList.get(position);
        if (chat.getCurrentUser()) {
            return ChatObject.TYPE_SEND;
        } else {
            return ChatObject.TYPE_RECEIVE;
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ChatViewHolders holder, int position) {
        holder.mMessage.setText(chatList.get(position).getMessage());
        holder.mDateView.setText(chatList.get(position).getDateTime());
        ChatObject chat = chatList.get(position);
        switch (chat.getProfileImageUrl()){
            case "default":
                Glide.with(context).load(R.mipmap.ic_launcher).into(holder.mImageView);
                break;
            default:
                Glide.with(context).clear(holder.mImageView);
                Glide.with(context).load(chat.getProfileImageUrl()).into(holder.mImageView);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}
