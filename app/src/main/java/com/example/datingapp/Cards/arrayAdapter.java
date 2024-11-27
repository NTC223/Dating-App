package com.example.datingapp.Cards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.datingapp.R;
import com.example.datingapp.ProfileManagement.stalkActivity;

import java.util.List;

public class arrayAdapter extends ArrayAdapter<cards> {

    public arrayAdapter(Context context, int resourceId, List<cards> items){
        super(context, resourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        cards card_item = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView info = (TextView) convertView.findViewById(R.id.info);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        ImageButton stalkButton = (ImageButton) convertView.findViewById(R.id.stalk);

        stalkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), stalkActivity.class);
                Bundle b = new Bundle();
                b.putString("userId", card_item.getUserId().toString());
                intent.putExtras(b);
                view.getContext().startActivity(intent);
            }
        });
        name.setText(card_item.getName()+ " " + card_item.getAge());
        info.setText(card_item.getBio() + "\n" + card_item.getLookingfor());
        switch (card_item.getProfileImageUrl()){
            case "default":
                Glide.with(convertView.getContext()).load(R.mipmap.ic_launcher).into(image);
                break;
            default:
                Glide.with(convertView.getContext()).clear(image);
                Glide.with(convertView.getContext()).load(card_item.getProfileImageUrl()).into(image);
                break;
        }
        return convertView;
    }
}
