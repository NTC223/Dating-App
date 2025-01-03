package com.example.datingapp.Matches;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingapp.MainActivity.MainActivity;
import com.example.datingapp.R;
import com.example.datingapp.ProfileManagement.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

        chatDb.orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String date = "";
                String text = "";
                String displayDate = "";
                String createdByUser = "";

                if (snapshot.exists() && snapshot.getChildren().iterator().hasNext()) {
                    DataSnapshot lastMessageSnapShot = snapshot.getChildren().iterator().next();
                    Object dateValue = lastMessageSnapShot.child("date").getValue();
                    if (dateValue != null && !dateValue.toString().isEmpty()) {
                        date = dateValue.toString();
                        try {
                            displayDate = getDayOrDateString(date);
                        } catch (ParseException e) {
                            System.out.println(e);
                            throw new RuntimeException(e);
                        }
                        displayDate = " . " + displayDate;
                    } else {
                        date = "";
                        displayDate = date;
                    }

                    createdByUser = lastMessageSnapShot.child("createdByUser").getValue().toString();
                    Object textValue = lastMessageSnapShot.child("text").getValue();
                    if (textValue != null && !textValue.toString().isEmpty()) {
                        text = textValue.toString();
                        System.out.println(createdByUser);
                        System.out.println(currentUserId);
                        if (createdByUser.equals(currentUserId)){
                            text = "You: " + text;
                        }
                        int remainingDistance = 31 - displayDate.length();
                        if(text.length() > remainingDistance)
                            text = text.substring(0, remainingDistance - 3) + "... ";
                    } else {
                        text = "";
                    }
                }

                // Cập nhật MatchesObject
                MatchesObject obj = new MatchesObject(userId, name, profileImageUrl, text, date, displayDate);

                // Cập nhật danh sách
                updateMatchList(obj);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void updateMatchList(MatchesObject updatedMatch) {
        boolean found = false;

        // Tìm và cập nhật đối tượng trong danh sách
        for (int i = 0; i < resultsMatches.size(); i++) {
            if (resultsMatches.get(i).getUserId().equals(updatedMatch.getUserId())) {
                resultsMatches.set(i, updatedMatch);
                found = true;
                break;
            }
        }

        // Nếu không tìm thấy, thêm mới vào danh sách
        if (!found) {
            resultsMatches.add(updatedMatch);
        }

        // Sắp xếp danh sách theo ngày
        sortMatchesByDate(resultsMatches);

        // Cập nhật giao diện
        mMatchesAdapter.notifyDataSetChanged();
    }

    public static String getDayOrDateString(String dateString) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM dd, yyyy - hh:mm:ss a", Locale.getDefault());
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
            SimpleDateFormat fullDateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
            return fullDateFormat.format(date);
        }

        // Nếu ngày đã quá 1 tuần, hiển thị theo định dạng "7 thg 11"
        if (daysDifference > 6) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d", Locale.ENGLISH);
            return dateFormat.format(date);
        }

        // Lấy thứ trong tuần dưới dạng viết tắt (Mon, Tue, ...)
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        String dayOfWeek = dayFormat.format(date);

        return dayOfWeek;
    }

    public static void sortMatchesByDate(List<MatchesObject> resultsMatches) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy - hh:mm:ss a");

        // Sort list
        Collections.sort(resultsMatches, new Comparator<MatchesObject>() {
            @Override
            public int compare(MatchesObject m1, MatchesObject m2) {
                try {
                    // Kiểm tra xem đối tượng m1 có ngày hợp lệ không (không phải chuỗi rỗng)
                    Date date1 = null;
                    if (m1.getDate() != null && !m1.getDate().isEmpty()) { // Kiểm tra nếu ngày không phải chuỗi rỗng
                        date1 = dateFormat.parse(m1.getDate());
                    }

                    // Kiểm tra xem đối tượng m2 có ngày hợp lệ không (không phải chuỗi rỗng)
                    Date date2 = null;
                    if (m2.getDate() != null && !m2.getDate().isEmpty()) { // Kiểm tra nếu ngày không phải chuỗi rỗng
                        date2 = dateFormat.parse(m2.getDate());
                    }

                    // Nếu m1 không có ngày hợp lệ (chuỗi rỗng), đặt m1 sau m2
                    if (date1 == null && date2 != null) {
                        return 1; // m1 ở sau m2
                    }
                    // Nếu m2 không có ngày hợp lệ (chuỗi rỗng), đặt m2 sau m1
                    if (date2 == null && date1 != null) {
                        return -1; // m2 ở sau m1
                    }
                    // Nếu cả hai có ngày hợp lệ, sắp xếp theo thứ tự giảm dần
                    if (date1 != null && date2 != null) {
                        return date2.compareTo(date1); // Descending order
                    }

                    return 0; // Nếu cả hai không có ngày hợp lệ (chuỗi rỗng), không thay đổi thứ tự

                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }



    private ArrayList<MatchesObject> resultsMatches = new ArrayList<MatchesObject>();
    private List<MatchesObject> getDataSetMatches() {
        return resultsMatches;
    }
    public void goHome(View view){
        finish();
        return;
    }
    public void goToSettings(View view){
        Intent intent = new Intent(MatchesActivity.this, SettingsActivity.class);
        startActivity(intent);
        return;
    }
}