package com.example.datingapp.LoginRegister;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datingapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText mEmailField;
    private Button mOk, mBack;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    protected String mEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mEmailField = findViewById(R.id.email);
        mOk = findViewById(R.id.ok);
        mBack = findViewById(R.id.back);
        auth = FirebaseAuth.getInstance();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmail = mEmailField.getText().toString();
                if(!TextUtils.isEmpty(mEmail)){
                    auth.sendPasswordResetEmail(mEmail).addOnCompleteListener(task -> {
                       if(task.isSuccessful()){
                           Toast.makeText(ForgotPasswordActivity.this, "Password reset email has been sent.", Toast.LENGTH_SHORT).show();
                            finish();
                       }else {
                           Toast.makeText(ForgotPasswordActivity.this, "Unable to send email. Please try again.", Toast.LENGTH_SHORT).show();
                       }
                    });
                }else {
                    Toast.makeText(ForgotPasswordActivity.this, "HPlease enter your email.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}