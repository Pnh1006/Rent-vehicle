package com.example.afinal.Interfaces.Admin.Account;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.afinal.Database.Model.NguoiDung;
import com.example.afinal.Database.Utilites.NguoiDungUtility;
import com.example.afinal.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FragmentFormAccount extends Fragment {
    private EditText edtHoTen, edtSDT, edtMatKhau, edtCCCD, edtNgaySinh;
    private Spinner spnGioiTinh, spnVaiTro, spnTrangThai;
    private Button btnLuu, btnHuy;
    private NguoiDungUtility nguoiDungUtility;
    private NguoiDung nguoiDung;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.form_create_update_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        bindView(view);
        setupSpinners();
        setupListeners();
        loadData();
    }

    private void bindView(View view) {
        edtHoTen = view.findViewById(R.id.edHoTen);
        edtSDT = view.findViewById(R.id.edSDT);
        edtMatKhau = view.findViewById(R.id.edMatKhau);
        edtCCCD = view.findViewById(R.id.edCCCD);
        edtNgaySinh = view.findViewById(R.id.edNgaySinh);
        spnGioiTinh = view.findViewById(R.id.spinnerGioiTinh);
        spnVaiTro = view.findViewById(R.id.spinnerVaiTro);
        spnTrangThai = view.findViewById(R.id.spinnerTrangThai);
        btnLuu = view.findViewById(R.id.btnSave);
        btnHuy = view.findViewById(R.id.btnCancle);

        nguoiDungUtility = NguoiDungUtility.getInstance(requireContext());

        edtNgaySinh.setOnClickListener(v -> showDatePicker());
        edtNgaySinh.setFocusable(false);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> gioiTinhAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.gioi_tinh, android.R.layout.simple_spinner_item);
        gioiTinhAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGioiTinh.setAdapter(gioiTinhAdapter);

        ArrayAdapter<CharSequence> vaiTroAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.vai_tro_tai_khoan, android.R.layout.simple_spinner_item);
        vaiTroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnVaiTro.setAdapter(vaiTroAdapter);

        ArrayAdapter<CharSequence> trangThaiAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.trang_thai_tai_khoan, android.R.layout.simple_spinner_item);
        trangThaiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTrangThai.setAdapter(trangThaiAdapter);
    }

    private void setupListeners() {
        btnLuu.setOnClickListener(v -> saveAccount());
        btnHuy.setOnClickListener(v -> {
            if (getParentFragment() instanceof FragmentAdminAccount) {
                ((FragmentAdminAccount) getParentFragment()).showListFragment();
            }
        });
    }

    private void loadData() {
        if (getArguments() != null) {
            nguoiDung = (NguoiDung) getArguments().getSerializable("nguoiDung");
            if (nguoiDung != null) {
                edtHoTen.setText(nguoiDung.getHoTen());
                edtSDT.setText(nguoiDung.getSdt());
                edtMatKhau.setText(nguoiDung.getMatKhau());
                edtCCCD.setText(nguoiDung.getCccd());
                edtNgaySinh.setText(nguoiDung.getNgaySinh());

                setSpinnerPosition(spnGioiTinh, nguoiDung.getGioiTinh());

                spnVaiTro.setSelection(nguoiDung.getVaiTro());
                spnTrangThai.setSelection(nguoiDung.getTrangThai());

                TextView tvNgayDangKy = requireView().findViewById(R.id.tvNgayDangKy);
                if (tvNgayDangKy != null) {
                    tvNgayDangKy.setText(nguoiDung.getNgayDangKy());
                }
            }
        }
    }

    private void setSpinnerPosition(Spinner spinner, String value) {
        try {
            int position = Integer.parseInt(value);
            if (position >= 0 && position < spinner.getCount()) {
                spinner.setSelection(position);
            }
        } catch (NumberFormatException e) {
            for (int i = 0; i < spinner.getCount(); i++) {
                if (spinner.getItemAtPosition(i).toString().equals(value)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateInEditText();
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateDateInEditText() {
        edtNgaySinh.setText(dateFormat.format(calendar.getTime()));
    }

    private void saveAccount() {
        if (!validateInput()) {
            return;
        }

        NguoiDung newNguoiDung = new NguoiDung();
        if (nguoiDung != null) {
            newNguoiDung.setMaND(nguoiDung.getMaND());
            newNguoiDung.setNgayDangKy(nguoiDung.getNgayDangKy());
        } else {
            newNguoiDung.setNgayDangKy(dateFormat.format(Calendar.getInstance().getTime()));
        }

        newNguoiDung.setHoTen(edtHoTen.getText().toString());
        newNguoiDung.setSdt(edtSDT.getText().toString());
        newNguoiDung.setMatKhau(edtMatKhau.getText().toString());
        newNguoiDung.setCccd(edtCCCD.getText().toString());
        newNguoiDung.setNgaySinh(edtNgaySinh.getText().toString());
        newNguoiDung.setGioiTinh(spnGioiTinh.getSelectedItem().toString());
        newNguoiDung.setVaiTro(spnVaiTro.getSelectedItemPosition());
        newNguoiDung.setTrangThai(spnTrangThai.getSelectedItemPosition());

        long result;
        if (nguoiDung == null) {
            result = nguoiDungUtility.insertNguoiDung(newNguoiDung);
            if (result <= 0) {
                Toast.makeText(requireContext(), "Thêm tài khoản thất bại", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            result = nguoiDungUtility.updateNguoiDung(newNguoiDung);
            if (result <= 0) {
                Toast.makeText(requireContext(), "Cập nhật tài khoản thất bại", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (getParentFragment() instanceof FragmentAdminAccount) {
            ((FragmentAdminAccount) getParentFragment()).showListFragment();
        }
    }

    private boolean validateInput() {
        String hoTen = edtHoTen.getText().toString().trim();
        String sdt = edtSDT.getText().toString().trim();
        String matKhau = edtMatKhau.getText().toString().trim();
        String cccd = edtCCCD.getText().toString().trim();
        String ngaySinh = edtNgaySinh.getText().toString().trim();

        if (hoTen.isEmpty()) {
            edtHoTen.setError("Vui lòng nhập họ tên");
            return false;
        }

        if (sdt.isEmpty()) {
            edtSDT.setError("Vui lòng nhập số điện thoại");
            return false;
        }
        if (!sdt.matches("\\d{10}")) {
            edtSDT.setError("Số điện thoại phải có 10 chữ số");
            return false;
        }

        if (matKhau.isEmpty()) {
            edtMatKhau.setError("Vui lòng nhập mật khẩu");
            return false;
        }
        if (matKhau.length() < 6) {
            edtMatKhau.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }

        if (cccd.isEmpty()) {
            edtCCCD.setError("Vui lòng nhập CCCD");
            return false;
        }
        if (!cccd.matches("\\d{12}")) {
            edtCCCD.setError("CCCD phải có 12 chữ số");
            return false;
        }

        if (ngaySinh.isEmpty()) {
            edtNgaySinh.setError("Vui lòng chọn ngày sinh");
            return false;
        }

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
} 