package com.example.afinal.Interfaces.User.Setting;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.afinal.Database.Model.NguoiDung;
import com.example.afinal.Database.Utilites.NguoiDungUtility;
import com.example.afinal.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @noinspection CallToPrintStackTrace
 */
public class FragmentUserSetting extends Fragment {
    private EditText etHoTen, etCCCD, etNgaySinh;
    private TextView tvSDT;
    private RadioGroup rgGioiTinh;
    private RadioButton rbNam, rbNu, rbKhac;
    private Button btnLuu, btnHuy, btnDoiMatKhau;
    private NguoiDungUtility nguoiDungUtility;
    private NguoiDung currentUser;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private View rootView;
    private View inf_button1;
    private LinearLayout inf_button2;
    private MaterialCardView inf_title, inf_content;
    private FrameLayout changePassContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragement_user_setting, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        
        nguoiDungUtility = NguoiDungUtility.getInstance(requireContext());
        
        if (!initViews(view)) {
            Toast.makeText(requireContext(), "Không thể khởi tạo giao diện", Toast.LENGTH_SHORT).show();
            return;
        }
        
        displayUserInfo();
        
        setupButtonListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreViews();
        displayUserInfo();
    }

    private void restoreViews() {
        if (rootView != null) {
            // Show all user info views
            if (inf_content != null) inf_content.setVisibility(View.VISIBLE);
            if (inf_title != null) inf_title.setVisibility(View.VISIBLE);
            if (inf_button2 != null) inf_button2.setVisibility(View.VISIBLE);
            if (inf_button1 != null) inf_button1.setVisibility(View.VISIBLE);
            if (changePassContainer != null) changePassContainer.setVisibility(View.GONE);

            // Show all other views in the root view
            for (int i = 0; i < ((ViewGroup) rootView).getChildCount(); i++) {
                View child = ((ViewGroup) rootView).getChildAt(i);
                if (child.getId() != R.id.user_setting_container) {
                    child.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void showListFragment() {
        if (inf_content != null) inf_content.setVisibility(View.VISIBLE);
        if (inf_title != null) inf_title.setVisibility(View.VISIBLE);
        if (inf_button2 != null) inf_button2.setVisibility(View.VISIBLE);
        if (inf_button1 != null) inf_button1.setVisibility(View.VISIBLE);
        if (changePassContainer != null) changePassContainer.setVisibility(View.GONE);
    }

    private boolean initViews(View view) {
        try {
            etHoTen = view.findViewById(R.id.et_full_name);
            etCCCD = view.findViewById(R.id.et_cccd);
            tvSDT = view.findViewById(R.id.tv_phone);
            etNgaySinh = view.findViewById(R.id.edNgaySinh);
            
            rgGioiTinh = view.findViewById(R.id.rg_gender);
            rbNam = view.findViewById(R.id.rb_male);
            rbNu = view.findViewById(R.id.rb_female);
            rbKhac = view.findViewById(R.id.rb_other);

            btnLuu = view.findViewById(R.id.btnSave);
            btnHuy = view.findViewById(R.id.btnCancle);
            btnDoiMatKhau = view.findViewById(R.id.btnDoiMatKhau);
            changePassContainer = view.findViewById(R.id.user_setting_container);

            inf_content = view.findViewById(R.id.inf_content);
            inf_title = view.findViewById(R.id.inf_title);
            inf_button2 = view.findViewById(R.id.inf_button2);
            inf_button1 = view.findViewById(R.id.inf_button1);

            etNgaySinh.setOnClickListener(v -> showDatePicker());
            etNgaySinh.setFocusable(false);
            etNgaySinh.setClickable(true);

            return etHoTen != null && etCCCD != null && tvSDT != null &&
                   etNgaySinh != null && rgGioiTinh != null && 
                   rbNam != null && rbNu != null && rbKhac != null &&
                   btnLuu != null && btnHuy != null && btnDoiMatKhau != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showDatePicker() {
        String currentDate = etNgaySinh.getText().toString();
        if (!currentDate.isEmpty()) {
            try {
                Date date = dateFormat.parse(currentDate);
                if (date != null) {
                    calendar.setTime(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    etNgaySinh.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void displayUserInfo() {
        currentUser = nguoiDungUtility.getCurrentUser();
        
        if (currentUser != null) {
            try {
                if (etHoTen != null) etHoTen.setText(currentUser.getHoTen());
                if (etCCCD != null) etCCCD.setText(currentUser.getCccd());
                if (tvSDT != null) tvSDT.setText(currentUser.getSdt());
                if (etNgaySinh != null) etNgaySinh.setText(currentUser.getNgaySinh());
                
                String gioiTinh = currentUser.getGioiTinh();
                if (gioiTinh != null && rbNam != null && rbNu != null && rbKhac != null) {
                    switch (gioiTinh.toLowerCase()) {
                        case "nam":
                            rbNam.setChecked(true);
                            break;
                        case "nữ":
                            rbNu.setChecked(true);
                            break;
                        default:
                            rbKhac.setChecked(true);
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupButtonListeners() {
        btnLuu.setOnClickListener(v -> {
            if (currentUser != null) {
                try {
                    currentUser.setHoTen(etHoTen.getText().toString());
                    currentUser.setCccd(etCCCD.getText().toString());
                    currentUser.setNgaySinh(etNgaySinh.getText().toString());
                    
                    String gioiTinh = "nam";
                    int selectedId = rgGioiTinh.getCheckedRadioButtonId();
                    if (selectedId == R.id.rb_male) {
                        gioiTinh = "nam";
                    } else if (selectedId == R.id.rb_female) {
                        gioiTinh = "nữ";
                    } else if (selectedId == R.id.rb_other) {
                        gioiTinh = "khác";
                    }
                    currentUser.setGioiTinh(gioiTinh);

                    if (nguoiDungUtility.capNhatThongTin(currentUser)) {
                        Toast.makeText(requireContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Lỗi khi cập nhật thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnHuy.setOnClickListener(v -> {
            displayUserInfo();
            Toast.makeText(requireContext(), "Đã khôi phục thông tin ban đầu", Toast.LENGTH_SHORT).show();
        });

        btnDoiMatKhau.setOnClickListener(v -> {
            // Hide user info views and show password change container
            if (inf_content != null) inf_content.setVisibility(View.GONE);
            if (inf_title != null) inf_title.setVisibility(View.GONE);
            if (inf_button2 != null) inf_button2.setVisibility(View.GONE);
            if (inf_button1 != null) inf_button1.setVisibility(View.GONE);
            if (changePassContainer != null) changePassContainer.setVisibility(View.VISIBLE);

            // Add the password change fragment
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.user_setting_container, new UserChangePass());
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }
}
