package com.example.afinal.Interfaces.Commons;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.afinal.Database.Model.NguoiDung;
import com.example.afinal.Database.Utilites.NguoiDungUtility;
import com.example.afinal.Interfaces.Admin.AdminHomepage;
import com.example.afinal.Interfaces.User.UserHomepage;
import com.example.afinal.R;
import com.google.android.material.textfield.TextInputLayout;

public class SignIn extends AppCompatActivity {

    private EditText inputPhone, inputPassword;
    private TextInputLayout tilPhone, tilPassword;
    private Button btnSignIn;
    private TextView tvSignUp;
    private ProgressBar progressBar;
    private NguoiDungUtility nguoiDungUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        bindView();
        nguoiDungUtility = NguoiDungUtility.getInstance(this);

        setupInputValidation();
        inputPassword.setText("123456");
        inputPhone.setText("0945678901");
        btnSignIn.setOnClickListener(view -> handleSignIn());

        tvSignUp.setOnClickListener(v -> startActivity(new Intent(this, SignUp.class)));
    }

    private void bindView() {
        inputPhone = findViewById(R.id.etPhone);
        inputPassword = findViewById(R.id.etPassword);
        tilPhone = findViewById(R.id.tilPhone);
        tilPassword = findViewById(R.id.tilPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvSignUp = findViewById(R.id.tvSignUp);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupInputValidation() {
        inputPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validatePhone();
            }
        });

        inputPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validatePassword();
            }
        });
    }

    private boolean validatePhone() {
        String phone = inputPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            tilPhone.setError("Vui lòng nhập số điện thoại");
            return false;
        }
        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            tilPhone.setError("Số điện thoại không hợp lệ");
            return false;
        }
        tilPhone.setError(null);
        return true;
    }

    private boolean validatePassword() {
        String password = inputPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Vui lòng nhập mật khẩu");
            return false;
        }
        if (password.length() < 6) {
            tilPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }
        tilPassword.setError(null);
        return true;
    }

    private void handleSignIn() {
        if (!validatePhone() || !validatePassword()) {
            return;
        }

        String phone = inputPhone.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        setLoading(true);

        if (nguoiDungUtility.dangNhap(phone, password)) {
            NguoiDung user = nguoiDungUtility.getCurrentUser();
            if (user != null) {
                Intent intent;
                if (user.getVaiTro() == 0) {
                    intent = new Intent(SignIn.this, UserHomepage.class);
                    intent.putExtra("UserLogin", user);
                } else {
                    intent = new Intent(SignIn.this, AdminHomepage.class);
                    intent.putExtra("AdminLogin", user);
                }
                startActivity(intent);
                finish();
            }
        }

        // Ẩn loading
        setLoading(false);
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSignIn.setEnabled(!isLoading);
        inputPhone.setEnabled(!isLoading);
        inputPassword.setEnabled(!isLoading);
        tvSignUp.setEnabled(!isLoading);
    }
}