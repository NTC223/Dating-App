package com.example.datingapp.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingapp.MainActivity;
import com.example.datingapp.Matches.MatchesActivity;
import com.example.datingapp.Matches.MatchesAdapter;
import com.example.datingapp.Matches.MatchesObject;
import com.example.datingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;

    private EditText mSendEditText;

    private ImageView mBack, mSendButton;
    private ProgressBar progressBar;

    private String currentUserId, matchId, chatId, matchName;

    private TextView mMatchName;

    DatabaseReference mDatabaseUser, mDatabaseChat, mDatabaseProfileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        matchId = getIntent().getExtras().getString("matchId");
        matchName = getIntent().getExtras().getString("matchName");


        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("connections").child("matches").child(matchId).child("chatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        getChatId();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);

        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetMatches(), ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);

        mSendEditText = findViewById(R.id.message);
        mSendButton = findViewById(R.id.send);

        mBack = findViewById(R.id.imageBack);
        mMatchName = findViewById(R.id.textName);

        progressBar = findViewById(R.id.progressBar);

        mMatchName.setText(matchName);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, MatchesActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void sendMessage() {
        String sendMessageText = mSendEditText.getText().toString();

        if(!sendMessageText.isEmpty()){
            DatabaseReference newMessageDb = mDatabaseChat.push();
            Map newMessage = new HashMap();
            newMessage.put("createdByUser", currentUserId);
            newMessage.put("text", sendMessageText);
            newMessage.put("date", getReadableDateTime(new Date()));
            newMessageDb.setValue(newMessage);
        }
        mSendEditText.setText(null);
    }

    private void getChatId(){
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    chatId = snapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessage();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void getChatMessage() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    String message = null;
                    String createdByUser = null;
                    String date = null;
                    if(snapshot.child("text").getValue()!=null){
                        message = snapshot.child("text").getValue().toString();
                    }
                    if(snapshot.child("createdByUser").getValue()!=null){
                        createdByUser = snapshot.child("createdByUser").getValue().toString();
                    }
                    if(snapshot.child("date").getValue()!=null){
                        date = snapshot.child("date").getValue().toString();
                    }
                    getProfileImage(message, createdByUser, date);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getProfileImage(String message, String createdByUser, String date) {
        mDatabaseProfileImageUrl = FirebaseDatabase.getInstance().getReference().child("Users").child(createdByUser).child("profileImageUrl");
        mDatabaseProfileImageUrl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String profileImageUrl = snapshot.getValue().toString();
                    if(message!=null && createdByUser!=null && profileImageUrl!=null){
                        Boolean currentUserBoolean = false;
                        if(createdByUser.equals(currentUserId)){
                            currentUserBoolean = true;
                        }

                        ChatObject newMessage =  new ChatObject(message, currentUserBoolean, profileImageUrl, date);
                        resultsChat.add(newMessage);

                        int count = resultsChat.size();
                        if(count == 0) mChatAdapter.notifyDataSetChanged();
                        else {
                            mChatAdapter.notifyItemRangeInserted(count - 1, 1);
                            mRecyclerView.post(() -> mRecyclerView.smoothScrollToPosition(count - 1));
                            System.out.println("Cuon xuong r");
                        }
                        mRecyclerView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private ArrayList<ChatObject> resultsChat = new ArrayList<ChatObject>();
    private List<ChatObject> getDataSetMatches() {
        return resultsChat;
    }
}