package com.example.datingapp.LoginRegister;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.datingapp.MainActivity.MainActivity;
import com.example.datingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistationActivity extends AppCompatActivity {

    private Button mRegister, mBack;
    private EditText mEmail, mPassword ,mName, mAge;

    private RadioGroup mRadioGroup;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registation);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    Intent intent = new Intent(RegistationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };


        mRegister = (Button) findViewById(R.id.register);
        mBack = (Button) findViewById(R.id.back);

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mName = (EditText) findViewById(R.id.name);
        mAge = (EditText) findViewById(R.id.age);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectId = mRadioGroup.getCheckedRadioButtonId();
                final String email = mEmail.getText().toString().trim();
                final String password = mPassword.getText().toString().trim();
                final String name = mName.getText().toString().trim();
                final String age = mAge.getText().toString().trim();

                if (selectId == -1 || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name) || TextUtils.isEmpty(age)){
                    Toast.makeText(RegistationActivity.this,"Information must not be left blank.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!age.isEmpty() && TextUtils.isDigitsOnly(age)) {
                    int ageValue = Integer.parseInt(age);
                    if (ageValue >= 18 && ageValue <= 100) {

                    } else {
                        Toast.makeText(RegistationActivity.this, "Please enter an age between 18 and 100.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(RegistationActivity.this, "Please enter a valid numeric age.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6){
                    Toast.makeText(RegistationActivity.this,"The password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
                    return;
                }

                final RadioButton radioButton = (RadioButton) findViewById(selectId);
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(RegistationActivity.this, "Email already exists. Please use a different email.", Toast.LENGTH_SHORT).show();
                        }else{
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                            Map userInfo = new HashMap<>();
                            userInfo.put("name", name);
                            userInfo.put("sex",radioButton.getText().toString());
                            userInfo.put("profileImageUrl","default");
                            userInfo.put("image1","default");
                            userInfo.put("image2","default");
                            userInfo.put("image3","default");
                            userInfo.put("image4","default");
                            userInfo.put("image5","default");
                            userInfo.put("image6","default");
                            userInfo.put("age",age);
                            userInfo.put("bio","");
                            userInfo.put("education","");
                            userInfo.put("lookingfor","");
                            userInfo.put("pet","");
                            userInfo.put("zodiac", "");
                            userInfo.put("personalType", "");
                            userInfo.put("drinkingSmoking", "");
                            userInfo.put("show", "Everyone");
                            userInfo.put("minAge", "18");
                            userInfo.put("maxAge", "23");
                            currentUserDb.updateChildren(userInfo);
                        }
                    }
                });
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistationActivity.this, ChooseLoginOrRegistationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}