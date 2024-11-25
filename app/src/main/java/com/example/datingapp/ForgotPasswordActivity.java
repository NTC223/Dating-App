package com.example.datingapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
                           Toast.makeText(ForgotPasswordActivity.this, "Email đặt lại mật khẩu đã được gửi.", Toast.LENGTH_SHORT).show();
                            finish();
                       }else {
                           Toast.makeText(ForgotPasswordActivity.this, "Không thể gửi email. Hãy thử lại.", Toast.LENGTH_SHORT).show();
                       }
                    });
                }else {
                    Toast.makeText(ForgotPasswordActivity.this, "Hãy nhập email của bạn.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}