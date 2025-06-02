package com.example.afinal.Interfaces.User.Setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.afinal.Database.Utilites.NguoiDungUtility;
import com.example.afinal.Interfaces.Admin.Vehicle.FragmentAdminVehicle;
import com.example.afinal.R;

public class UserChangePass extends Fragment {
    private EditText etNhapMKCu, etNhapMKMoi, etNhapLaiMKMoi;
    private Button btnLuu, btnHuy;
    private NguoiDungUtility nguoiDungUtility;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.form_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nguoiDungUtility = NguoiDungUtility.getInstance(requireContext());

        initViews(view);
        setupButtonListeners();
    }

    private void initViews(View view) {
        etNhapMKCu = view.findViewById(R.id.etOldPass);
        etNhapMKMoi = view.findViewById(R.id.etNewPass);
        etNhapLaiMKMoi = view.findViewById(R.id.etReNewPass);
        btnLuu = view.findViewById(R.id.btnSave);
        btnHuy = view.findViewById(R.id.btnCancle);

        etNhapMKCu.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etNhapMKMoi.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etNhapLaiMKMoi.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    private void setupButtonListeners() {
        btnLuu.setOnClickListener(v -> {
            String matKhauCu = etNhapMKCu.getText().toString().trim();
            String matKhauMoi = etNhapMKMoi.getText().toString().trim();
            String nhapLaiMatKhauMoi = etNhapLaiMKMoi.getText().toString().trim();

            // Validate input
            if (TextUtils.isEmpty(matKhauCu)) {
                Toast.makeText(requireContext(), "Vui lòng nhập mật khẩu cũ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(matKhauMoi)) {
                Toast.makeText(requireContext(), "Vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(nhapLaiMatKhauMoi)) {
                Toast.makeText(requireContext(), "Vui lòng nhập lại mật khẩu mới", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!matKhauMoi.equals(nhapLaiMatKhauMoi)) {
                Toast.makeText(requireContext(), "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // Change password
            if (nguoiDungUtility.doiMatKhau(matKhauCu, matKhauMoi)) {
                Toast.makeText(requireContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                // Clear all password fields
                etNhapMKCu.setText("");
                etNhapMKMoi.setText("");
                etNhapLaiMKMoi.setText("");
                // Return to settings screen
                if (getParentFragment() instanceof FragmentUserSetting) {
                    ((FragmentUserSetting) getParentFragment()).showListFragment();
                }
            } else {
                Toast.makeText(requireContext(), "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
            }
        });

        btnHuy.setOnClickListener(v -> {
            // Clear all password fields
            etNhapMKCu.setText("");
            etNhapMKMoi.setText("");
            etNhapLaiMKMoi.setText("");
            // Return to settings screen
            if (getParentFragment() instanceof FragmentUserSetting) {
                ((FragmentUserSetting) getParentFragment()).showListFragment();
            }
        });
    }
}
