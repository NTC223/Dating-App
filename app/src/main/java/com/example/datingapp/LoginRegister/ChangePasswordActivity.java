package com.example.datingapp.LoginRegister;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datingapp.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText mOldPasswordField, mNewPasswordField, mConfirmNewPasswordField;
    private Button mBack, mDone;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String mOldPassword, mNewPassword, mConfirmNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mBack = findViewById(R.id.back);
        mDone = findViewById(R.id.confirm);

        mOldPasswordField = findViewById(R.id.oldPassword);
        mNewPasswordField = findViewById(R.id.newPassword);
        mConfirmNewPasswordField = findViewById(R.id.confirmNewPassword);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOldPassword = mOldPasswordField.getText().toString();
                mNewPassword = mNewPasswordField.getText().toString();
                mConfirmNewPassword = mConfirmNewPasswordField.getText().toString();
                if (user!=null){
                    if (!TextUtils.isEmpty(mOldPassword) && !TextUtils.isEmpty(mNewPassword) && !TextUtils.isEmpty(mConfirmNewPassword)){
                        String email = user.getEmail();
                        AuthCredential credential = EmailAuthProvider.getCredential(email, mOldPassword);
                        user.reauthenticate(credential).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (mNewPassword.length()>=6){
                                    if (!mNewPassword.equals(mOldPassword)){
                                        if (mNewPassword.equals(mConfirmNewPassword)){
                                            user.updatePassword(mNewPassword).addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()){
                                                    Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                                    auth.signOut();
                                                    Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }else{
                                                    Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }else{
                                            Toast.makeText(ChangePasswordActivity.this, "Xác nhận mật khẩu chưa chính xác", Toast.LENGTH_SHORT).show();
                                        }
                                    }else {
                                        Toast.makeText(ChangePasswordActivity.this, "Mật khẩu mới phải khác mật khẩu cũ", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(ChangePasswordActivity.this, "Mật khẩu phải có tối thiểu 6 ký tự", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ChangePasswordActivity.this, "Mật khẩu chưa chính xác", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Toast.makeText(ChangePasswordActivity.this, "Hãy nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ChangePasswordActivity.this, "Người dùng chưa đăng nhập!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}