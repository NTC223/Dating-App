package com.example.datingapp.Matches;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.datingapp.R;

import java.util.List;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolders> {
    private List<MatchesObject> matchesList;
    private Context context;

    public MatchesAdapter(List<MatchesObject> matchesList, Context context){
        this.matchesList = matchesList;
        this.context = context;
    }
    @NonNull
    @Override
    public MatchesViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matches, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchesViewHolders rcv = new MatchesViewHolders((layoutView));

        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchesViewHolders holder, int position) {
        holder.mMatchId.setText(matchesList.get(position).getUserId());
        holder.mMatchName.setText(matchesList.get(position).getName());
        holder.mMatchDate.setText(matchesList.get(position).getDisplayDate());
        holder.mMatchText.setText(matchesList.get(position).getText());
        switch (matchesList.get(position).getProfileImageUrl()){
            case "default":
                Glide.with(context).load(R.mipmap.ic_launcher).into(holder.mMatchImage);
                break;
            default:
                Glide.with(context).clear(holder.mMatchImage);
                Glide.with(context).load(matchesList.get(position).getProfileImageUrl()).into(holder.mMatchImage);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }
}
