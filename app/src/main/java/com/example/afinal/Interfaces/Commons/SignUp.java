package com.example.afinal.Interfaces.Commons;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.afinal.Database.Utilites.NguoiDungUtility;
import com.example.afinal.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class SignUp extends AppCompatActivity {

    private EditText etFullName, etPhone, etCCCD, etPassword, etConfirmPassword;
    private TextInputLayout tilFullName, tilPhone, tilCCCD, tilPassword, tilConfirmPassword;
    private TextView tvBirthDate;
    private RadioGroup rgGender;
    private Button btnRegister;
    private ProgressBar progressBar;
    private NguoiDungUtility nguoiDungUtility;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        bindViews();
        nguoiDungUtility = NguoiDungUtility.getInstance(this);
        calendar = Calendar.getInstance();

        setupInputValidation();
        setupDatePicker();

        btnRegister.setOnClickListener(v -> handleRegistration());
    }

    private void bindViews() {
        etFullName = findViewById(R.id.et_full_name);
        etPhone = findViewById(R.id.et_phone);
        etCCCD = findViewById(R.id.et_cccd);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        tilFullName = findViewById(R.id.til_full_name);
        tilPhone = findViewById(R.id.til_phone);
        tilCCCD = findViewById(R.id.til_cccd);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);

        tvBirthDate = findViewById(R.id.tv_birth_date);
        rgGender = findViewById(R.id.rg_gender);
        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupInputValidation() {
        etFullName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validateFullName();
        });

        etPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validatePhone();
        });

        etCCCD.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validateCCCD();
        });

        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validatePassword();
        });

        etConfirmPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validateConfirmPassword();
        });
    }

    private void setupDatePicker() {
        tvBirthDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, yearD, monthD, dayD) -> {
                        @SuppressLint("DefaultLocale") String date = String.format("%d-%02d-%02d", yearD, monthD + 1, dayD);
                        tvBirthDate.setText(date);
                        tvBirthDate.setError(null);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            Calendar minAge = Calendar.getInstance();
            minAge.add(Calendar.YEAR, -18);
            datePickerDialog.getDatePicker().setMaxDate(minAge.getTimeInMillis());

            datePickerDialog.show();
        });
    }

    private boolean validateFullName() {
        String fullName = etFullName.getText().toString().trim();
        if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError("Vui lòng nhập họ tên");
            return false;
        }
        if (fullName.length() < 2) {
            tilFullName.setError("Họ tên phải có ít nhất 2 ký tự");
            return false;
        }
        tilFullName.setError(null);
        return true;
    }

    private boolean validatePhone() {
        String phone = etPhone.getText().toString().trim();
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

    private boolean validateCCCD() {
        String cccd = etCCCD.getText().toString().trim();
        if (TextUtils.isEmpty(cccd)) {
            tilCCCD.setError("Vui lòng nhập CCCD");
            return false;
        }
        if (cccd.length() != 12 || !cccd.matches("\\d+")) {
            tilCCCD.setError("CCCD không hợp lệ");
            return false;
        }
        tilCCCD.setError(null);
        return true;
    }

    private boolean validatePassword() {
        String password = etPassword.getText().toString().trim();
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

    private boolean validateConfirmPassword() {
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Mật khẩu không khớp");
            return false;
        }
        tilConfirmPassword.setError(null);
        return true;
    }

    private boolean validateBirthDate() {
        String birthDate = tvBirthDate.getText().toString().trim();
        if (TextUtils.isEmpty(birthDate) || birthDate.equals("Chọn ngày sinh")) {
            tvBirthDate.setError("Vui lòng chọn ngày sinh");
            return false;
        }
        tvBirthDate.setError(null);
        return true;
    }

    private void handleRegistration() {
        if (!validateFullName() || !validatePhone() || !validateCCCD() ||
                !validatePassword() || !validateConfirmPassword() || !validateBirthDate()) {
            return;
        }

        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String cccd = etCCCD.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String birthDate = tvBirthDate.getText().toString().trim();

        String gender = "Nam";
        int selectedGender = rgGender.getCheckedRadioButtonId();
        if (selectedGender == R.id.rb_female) gender = "Nữ";
        else if (selectedGender == R.id.rb_other) gender = "Khác";

        setLoading(true);

        if (nguoiDungUtility.dangKyNguoiDung(fullName, phone, password, cccd, birthDate, gender)) {
            finish();
        }

        setLoading(false);
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!isLoading);
        etFullName.setEnabled(!isLoading);
        etPhone.setEnabled(!isLoading);
        etCCCD.setEnabled(!isLoading);
        etPassword.setEnabled(!isLoading);
        etConfirmPassword.setEnabled(!isLoading);
        tvBirthDate.setEnabled(!isLoading);
        rgGender.setEnabled(!isLoading);
    }
}