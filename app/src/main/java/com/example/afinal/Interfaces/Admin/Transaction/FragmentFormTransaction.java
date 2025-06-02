package com.example.afinal.Interfaces.Admin.Transaction;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.afinal.Database.DAO.NguoiDungDAO;
import com.example.afinal.Database.DAO.ThanhToanDAO;
import com.example.afinal.Database.DAO.ThueXeDAO;
import com.example.afinal.Database.DAO.XeDAO;
import com.example.afinal.Database.Model.NguoiDung;
import com.example.afinal.Database.Model.ThanhToan;
import com.example.afinal.Database.Model.ThueXe;
import com.example.afinal.Database.Model.Xe;
import com.example.afinal.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FragmentFormTransaction extends Fragment {
    private TextInputLayout  tilTrangThai;
    private EditText edHoTen, edSDT, edCCCD, edTenXe, edTienCoc;
    private EditText edSoTien, edNoiDung, edNgayThucHien, edNgayThanhCong, edMaGiaoDich, edGhiChu;
    private MaterialAutoCompleteTextView edPhuongThucThanhToan, edTrangThai;
    private Button btnLuu, btnHuy;
    private ThanhToanDAO thanhToanDAO;
    private ThueXeDAO thueXeDAO;
    private ThanhToan thanhToan;
    private boolean isEditMode = false;
    private SimpleDateFormat dateFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.form_create_update_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        setupSpinners();
        setupListeners();
        loadDataIfEditing();
    }

    private void bindView(View view) {
        edHoTen = view.findViewById(R.id.edHoTen);
        edSDT = view.findViewById(R.id.edSDT);
        edCCCD = view.findViewById(R.id.edCCCD);
        edTenXe = view.findViewById(R.id.edTenXe);
        edTienCoc = view.findViewById(R.id.edTienCoc);

        tilTrangThai = view.findViewById(R.id.tilTrangThai);

        edSoTien = view.findViewById(R.id.edSoTien);
        edNoiDung = view.findViewById(R.id.edNoiDung);
        edNgayThucHien = view.findViewById(R.id.edNgayThucHien);
        edNgayThanhCong = view.findViewById(R.id.edNgayThanhCong);
        edMaGiaoDich = view.findViewById(R.id.edMaGiaoDich);
        edGhiChu = view.findViewById(R.id.edGhiChu);

        edPhuongThucThanhToan = view.findViewById(R.id.edPhuongThucThanhToan);
        edTrangThai = view.findViewById(R.id.edTrangThai);

        btnLuu = view.findViewById(R.id.btnSave);
        btnHuy = view.findViewById(R.id.btnCancle);

        thanhToanDAO = new ThanhToanDAO(requireContext());
        thueXeDAO = new ThueXeDAO(requireContext());

        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        disableAllFields();
    }

    private void disableAllFields() {
        edSoTien.setEnabled(false);
        edNoiDung.setEnabled(false);
        edNgayThucHien.setEnabled(false);
        edNgayThanhCong.setEnabled(false);
        edPhuongThucThanhToan.setEnabled(false);
        edMaGiaoDich.setEnabled(false);
        edGhiChu.setEnabled(false);
        edHoTen.setEnabled(false);
        edSDT.setEnabled(false);
        edCCCD.setEnabled(false);
        edTenXe.setEnabled(false);
        edTienCoc.setEnabled(false);
        edTrangThai.setEnabled(false);

        edNgayThucHien.setFocusable(false);
        edNgayThucHien.setClickable(false);
        edNgayThanhCong.setFocusable(false);
        edNgayThanhCong.setClickable(false);

        edPhuongThucThanhToan.setFocusable(false);
        edPhuongThucThanhToan.setClickable(false);
        edTrangThai.setFocusable(false);
        edTrangThai.setClickable(false);
    }

    private void setupSpinners() {
        setupStatusDropdown();
    }

    private void setupStatusDropdown() {
        String[] statuses = getResources().getStringArray(R.array.trang_thai_thanh_toan);
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                statuses
        );
        edTrangThai.setAdapter(statusAdapter);
        edTrangThai.setInputType(InputType.TYPE_NULL);
        edTrangThai.setKeyListener(null);
        edTrangThai.setThreshold(0);
    }

    private void enableStatusField() {
        edTrangThai.setEnabled(true);
        edTrangThai.setFocusable(true);
        edTrangThai.setClickable(true);

        if (edTrangThai.getAdapter() == null) {
            setupStatusDropdown();
        }

        edTrangThai.setOnClickListener(v -> edTrangThai.showDropDown());
    }

    private void loadDataIfEditing() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("thanhToan")) {
            isEditMode = true;
            thanhToan = (ThanhToan) args.getSerializable("thanhToan");
            if (thanhToan != null) {
                loadCustomerAndVehicleInfo(thanhToan.getMaND(), thanhToan.getMaThueXe());

                edSoTien.setText(String.valueOf(thanhToan.getSoTien()));
                edNoiDung.setText(thanhToan.getNoiDung());
                edNgayThucHien.setText(thanhToan.getNgayThucHien());
                
                String statusText = getStatusText(thanhToan.getTrangThai());
                edTrangThai.setText(statusText, false);
                
                if (statusText.equals("Đã thanh toán")) {
                    edNgayThanhCong.setText(thanhToan.getNgayThanhCong());
                } else {
                    edNgayThanhCong.setText("");
                }
                
                edPhuongThucThanhToan.setText(thanhToan.getPhuongThuc());
                edMaGiaoDich.setText(thanhToan.getMaGiaoDich());
                edGhiChu.setText(thanhToan.getGhiChu());

                enableStatusField();

                edTrangThai.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedStatus = parent.getItemAtPosition(position).toString();
                    if (selectedStatus.equals("Đã thanh toán")) {
                        edNgayThanhCong.setText(dateFormat.format(Calendar.getInstance().getTime()));
                    } else {
                        edNgayThanhCong.setText("");
                    }
                });
            }
        }
    }

    private void saveTransaction() {
        if (!validateInput()) {
            return;
        }

        try {
            if (isEditMode && thanhToan != null) {
                String newStatus = edTrangThai.getText().toString();
                String newCompletionDate = edNgayThanhCong.getText().toString();
                
                thanhToan.setTrangThai(getStatusValue(newStatus));
                thanhToan.setNgayThanhCong(newCompletionDate);

                thanhToanDAO.update(thanhToan);
                Toast.makeText(requireContext(), "Cập nhật trạng thái giao dịch thành công", Toast.LENGTH_SHORT).show();
            }

            goBack();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput() {
        boolean isValid = true;

        if (edTrangThai.getText().toString().isEmpty()) {
            tilTrangThai.setError("Vui lòng chọn trạng thái");
            isValid = false;
        } else {
            tilTrangThai.setError(null);
        }

        return isValid;
    }

    private void goBack() {
        if (getParentFragment() instanceof FragmentAdminTransaction) {
            FragmentAdminTransaction fragment = (FragmentAdminTransaction) getParentFragment();
            fragment.showListFragment();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (thanhToanDAO != null) {
            thanhToanDAO.close();
        }
        if (thueXeDAO != null) {
            thueXeDAO.close();
        }
    }

    private void loadCustomerAndVehicleInfo(int maND, int maThueXe) {
        NguoiDungDAO nguoiDungDAO = new NguoiDungDAO(requireContext());
        NguoiDung nguoiDung = nguoiDungDAO.getById(maND);
        if (nguoiDung != null) {
            edHoTen.setText(nguoiDung.getHoTen());
            edSDT.setText(nguoiDung.getSdt());
            edCCCD.setText(nguoiDung.getCccd());
        }

        ThueXe thueXe = thueXeDAO.getById(maThueXe);
        if (thueXe != null) {
            XeDAO xeDAO = new XeDAO(requireContext());
            Xe xe = xeDAO.getById(thueXe.getMaThueXe());
            if (xe != null) {
                edTenXe.setText(xe.getTenXe());
                edTienCoc.setText(String.valueOf(xe.getGiaThue()));
                edSoTien.setText(String.valueOf(xe.getGiaThue()));
            }
        }
    }

    private String getStatusText(int status) {
        String[] statuses = getResources().getStringArray(R.array.trang_thai_thanh_toan);
        if (status >= 0 && status < statuses.length) {
            return statuses[status];
        }
        return statuses[0];
    }

    private int getStatusValue(String status) {
        String[] statuses = getResources().getStringArray(R.array.trang_thai_thanh_toan);
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(status)) {
                return i;
            }
        }
        return 0;
    }

    private void setupListeners() {
        btnLuu.setOnClickListener(v -> saveTransaction());
        btnHuy.setOnClickListener(v -> goBack());
    }
}