package com.example.datingapp.Matches;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.datingapp.Chat.ChatActivity;
import com.example.datingapp.R;

public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMatchId, mMatchName, mMatchDate, mMatchText;
    public ImageView mMatchImage;
    public MatchesViewHolders (View itemView){
        super(itemView);
        itemView.setOnClickListener(this);

        mMatchId = (TextView) itemView.findViewById(R.id.MatchId);
        mMatchName = (TextView) itemView.findViewById(R.id.MatchName);
        mMatchImage = (ImageView) itemView.findViewById(R.id.MatchImage);
        mMatchDate = (TextView) itemView.findViewById(R.id.date);
        mMatchText = (TextView) itemView.findViewById(R.id.text);
    }
    @Override
    public void onClick(View view){
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        Bundle b = new Bundle();
        b.putString("matchId", mMatchId.getText().toString());
        b.putString("matchName", mMatchName.getText().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}