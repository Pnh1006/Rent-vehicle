package com.example.afinal.Interfaces.Admin.Vehicle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.afinal.Database.Model.Xe;
import com.example.afinal.Database.Utilites.XeUtility;
import com.example.afinal.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/** @noinspection CallToPrintStackTrace*/
public class FragmentFormVehicle extends Fragment {
    private EditText edTenXe, edBienSo, edGiaThue;
    private AutoCompleteTextView spinnerNamSX, spinnerMauSac, spinnerLoaiXe, spinnerTrangThai;
    private ImageView imgPreview;
    private Button btnChonAnh, btnLuu, btnHuy;
    private XeUtility xeUtility;
    private Xe xe;
    private String selectedImagePath;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    try {
                        String imagePath = saveImageToInternalStorage(selectedImageUri);
                        selectedImagePath = imagePath;
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                        imgPreview.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Lỗi khi xử lý ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    /** @noinspection ResultOfMethodCallIgnored*/
    private String saveImageToInternalStorage(Uri imageUri) throws IOException {
        File imagesDir = new File(requireContext().getFilesDir(), "images");
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }

        String fileName = "vehicle_" + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(imagesDir, fileName);

        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        }

        return imageFile.getAbsolutePath();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.form_create_update_vehicle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        xeUtility = new XeUtility(requireContext());
        bindView(view);
        setupSpinners();
        setupListeners();
        loadData();
    }

    private void bindView(View view) {
        edTenXe = view.findViewById(R.id.edDiaTenXe);
        edBienSo = view.findViewById(R.id.edDiaBienSo);
        edGiaThue = view.findViewById(R.id.edDiaGiaThue);
        spinnerNamSX = view.findViewById(R.id.spinnerNamSX);
        spinnerMauSac = view.findViewById(R.id.spinnerDiaMauSac);
        spinnerLoaiXe = view.findViewById(R.id.spinnerLoaiXe);
        spinnerTrangThai = view.findViewById(R.id.spinnerTrangThai);
        imgPreview = view.findViewById(R.id.imgPreview);
        btnChonAnh = view.findViewById(R.id.btnChonAnh);
        btnLuu = view.findViewById(R.id.btnSave);
        btnHuy = view.findViewById(R.id.btnCancle);
    }

    private void setupSpinners() {
        String[] namSX = getResources().getStringArray(R.array.nam_san_xuat);
        ArrayAdapter<String> namSXAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, namSX);
        namSXAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNamSX.setAdapter(namSXAdapter);

        String[] mauSac = getResources().getStringArray(R.array.mau_sac);
        ArrayAdapter<String> mauSacAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, mauSac);
        mauSacAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMauSac.setAdapter(mauSacAdapter);

        String[] loaiXe = getResources().getStringArray(R.array.loai_xe);
        ArrayAdapter<String> loaiXeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, loaiXe);
        loaiXeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoaiXe.setAdapter(loaiXeAdapter);

        String[] trangThai = getResources().getStringArray(R.array.trang_thai_xe);
        ArrayAdapter<String> trangThaiAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, trangThai);
        trangThaiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTrangThai.setAdapter(trangThaiAdapter);
    }

    private void setupListeners() {
        btnChonAnh.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        btnLuu.setOnClickListener(v -> saveVehicle());

        btnHuy.setOnClickListener(v -> {
            if (getParentFragment() instanceof FragmentAdminVehicle) {
                ((FragmentAdminVehicle) getParentFragment()).showListFragment();
            }
        });
    }

    private void loadData() {
        if (getArguments() != null) {
            xe = (Xe) getArguments().getSerializable("xe");
            if (xe != null) {
                edTenXe.setText(xe.getTenXe());
                edBienSo.setText(xe.getBienSo());
                edGiaThue.setText(String.valueOf(xe.getGiaThue()));
                
                String[] namSX = getResources().getStringArray(R.array.nam_san_xuat);
                String[] mauSac = getResources().getStringArray(R.array.mau_sac);
                String[] loaiXe = getResources().getStringArray(R.array.loai_xe);
                String[] trangThai = getResources().getStringArray(R.array.trang_thai_xe);

                setSpinnerSelection(spinnerNamSX, namSX, xe.getNamSX());
                setSpinnerSelection(spinnerMauSac, mauSac, xe.getMauSac());
                setSpinnerSelection(spinnerLoaiXe, loaiXe, xe.getLoaiXe());
                setSpinnerSelection(spinnerTrangThai, trangThai, xe.getTrangThai());

                if (xe.getHinhAnh() != null && !xe.getHinhAnh().isEmpty()) {
                    selectedImagePath = xe.getHinhAnh();
                    Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);
                    if (bitmap != null) {
                        imgPreview.setImageBitmap(bitmap);
                    }
                }
            }
        }
    }

    private void setSpinnerSelection(AutoCompleteTextView spinner, String[] array, Object value) {
        if (value instanceof String) {
            String strValue = (String) value;
            for (String s : array) {
                if (s.equals(strValue)) {
                    spinner.setText(strValue, false);
                    break;
                }
            }
        } else if (value instanceof Integer) {
            int intValue = (Integer) value;
            if (intValue >= 0 && intValue < array.length) {
                spinner.setText(array[intValue], false);
            } else {
                String yearStr = String.valueOf(intValue);
                for (String year : array) {
                    if (year.equals(yearStr)) {
                        spinner.setText(year, false);
                        break;
                    }
                }
            }
        }
    }

    private void saveVehicle() {
        // Validate input
        String tenXe = edTenXe.getText().toString().trim();
        String bienSo = edBienSo.getText().toString().trim();
        String giaThueStr = edGiaThue.getText().toString().trim();

        if (tenXe.isEmpty() || bienSo.isEmpty() || giaThueStr.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int giaThue = Integer.parseInt(giaThueStr);
            
            if (xe == null) {
                xe = new Xe();
            }
            
            xe.setTenXe(tenXe);
            xe.setBienSo(bienSo);
            xe.setGiaThue(giaThue);
            xe.setNamSX(Integer.parseInt(spinnerNamSX.getText().toString()));
            xe.setMauSac(spinnerMauSac.getText().toString());
            xe.setLoaiXe(spinnerLoaiXe.getText().toString());
            
            String[] trangThai = getResources().getStringArray(R.array.trang_thai_xe);
            String selectedStatus = spinnerTrangThai.getText().toString();
            int statusIndex = -1;
            for (int i = 0; i < trangThai.length; i++) {
                if (trangThai[i].equals(selectedStatus)) {
                    statusIndex = i;
                    break;
                }
            }
            xe.setTrangThai(statusIndex);
            
            if (selectedImagePath != null) {
                xe.setHinhAnh(selectedImagePath);
            }

            if (xe.getMaXe() == 0) {
                // Thêm mới xe
                long result = xeUtility.insertXe(xe);
                if (result != -1) {
                    Toast.makeText(requireContext(), "Thêm xe thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Thêm xe thất bại", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                int result = xeUtility.updateXe(xe);
                if (result > 0) {
                    Toast.makeText(requireContext(), "Cập nhật xe thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Cập nhật xe thất bại", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (getParentFragment() instanceof FragmentAdminVehicle) {
                ((FragmentAdminVehicle) getParentFragment()).showListFragment();
            }
            
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Giá thuê hoặc năm sản xuất không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (xeUtility != null) {
            xeUtility.close();
        }
    }
} 