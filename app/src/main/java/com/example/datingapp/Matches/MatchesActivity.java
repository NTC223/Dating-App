package com.example.datingapp.Matches;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingapp.ChooseLoginOrRegistationActivity;
import com.example.datingapp.LoginActivity;
import com.example.datingapp.MainActivity;
import com.example.datingapp.R;
import com.example.datingapp.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class   MatchesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;
    private String currentUserId;
    private Button mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        mMatchesLayoutManager = new LinearLayoutManager(MatchesActivity.this);
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new MatchesAdapter(getDataSetMatches(), MatchesActivity.this);
        mRecyclerView.setAdapter(mMatchesAdapter);
        mBack = findViewById(R.id.back);

        getUserMatchId();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MatchesActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getUserMatchId() {

        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("connections").child("matches");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot match: snapshot.getChildren()){
                        FetchMatchInformation(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void FetchMatchInformation(String key) {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String userId = snapshot.getKey();
                    String name = "";
                    String profileImageUrl = "";
                    String chatId = "";
                    if(snapshot.child("name").getValue()!=null){
                        name = snapshot.child("name").getValue().toString();
                    }
                    if(snapshot.child("profileImageUrl").getValue()!=null){
                        profileImageUrl = snapshot.child("profileImageUrl").getValue().toString();
                    }
                    if(snapshot.child("connections").child("matches").child(currentUserId).child("chatId").getValue().toString()!=null){
                        chatId = snapshot.child("connections").child("matches").child(currentUserId).child("chatId").getValue().toString();
                        System.out.println(chatId);
                    }
                    FetchLastMessageAndDate(userId, name, profileImageUrl, chatId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void FetchLastMessageAndDate(String userId, String name, String profileImageUrl, String chatId){
        DatabaseReference chatDb = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId);
        chatDb.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String date = "";
                String text = "";
                String createdByUser = "";
                if (snapshot.exists() && snapshot.getChildren().iterator().hasNext()) {
                    DataSnapshot lastMessageSnapShot = snapshot.getChildren().iterator().next();

                    try {
                        date = getDayOrDateString(lastMessageSnapShot.child("date").getValue().toString());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    date = " . " + date;
                    text = lastMessageSnapShot.child("text").getValue().toString();
                    createdByUser = lastMessageSnapShot.child("createdByUser").getKey().toString();
                    if (!createdByUser.equals(currentUserId)){
                        text = "Bạn: " + text;
                    }
                    int remainingDistance = 33 - date.length();
                    if(text.length() > remainingDistance)
                        text = text.substring(0, remainingDistance - 3) + "... ";
                }
                MatchesObject obj = new MatchesObject(userId, name, profileImageUrl, text, date);
                resultsMatches.add(obj);
                mMatchesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static String getDayOrDateString(String dateString) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault());
        Date date = inputFormat.parse(dateString);
        
        Date currentDate = new Date();

        // Tính số ngày chênh lệch giữa ngày hiện tại và ngày đầu vào
        long diffInMillis = currentDate.getTime() - date.getTime();
        long daysDifference = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

        // Kiểm tra nếu thời gian thuộc ngày hôm nay
        SimpleDateFormat sameDayFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        if (sameDayFormat.format(date).equals(sameDayFormat.format(currentDate))) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return timeFormat.format(date);
        }

        // Kiểm tra nếu năm khác với năm hiện tại
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        if (!yearFormat.format(date).equals(yearFormat.format(currentDate))) {
            SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd 'thg' MM, yyyy", Locale.getDefault());
            return fullDateFormat.format(date);
        }

        // Nếu ngày đã quá 1 tuần, hiển thị theo định dạng "7 thg 11"
        if (daysDifference > 6) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'thg' MM", Locale.getDefault());
            return dateFormat.format(date);
        }

        // Lấy thứ trong tuần dưới dạng viết tắt (Mon, Tue, ...)
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        String dayOfWeek = dayFormat.format(date);

        // Chuyển đổi thành định dạng tiếng Việt
        switch (dayOfWeek) {
            case "Mon":
                return "T2";
            case "Tue":
                return "T3";
            case "Wed":
                return "T4";
            case "Thu":
                return "T5";
            case "Fri":
                return "T6";
            case "Sat":
                return "T7";
            case "Sun":
                return "CN";
            default:
                return "";
        }
    }

    private ArrayList<MatchesObject> resultsMatches = new ArrayList<MatchesObject>();
    private List<MatchesObject> getDataSetMatches() {
        return resultsMatches;
    }
    public void goHome(View view){
        Intent intent = new Intent(MatchesActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        return;
    }
    public void goToSettings(View view){
        Intent intent = new Intent(MatchesActivity.this, SettingsActivity.class);
        startActivity(intent);
        return;
    }
}