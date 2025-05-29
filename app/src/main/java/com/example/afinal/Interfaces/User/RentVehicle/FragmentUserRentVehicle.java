package com.example.afinal.Interfaces.User.RentVehicle;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.afinal.Database.DAO.ThueXeDAO;
import com.example.afinal.Database.DAO.ChiTietThueXeDAO;
import com.example.afinal.Database.DAO.ThanhToanDAO;
import com.example.afinal.Database.DAO.XeDAO;
import com.example.afinal.Database.DAO.NguoiDungDAO;
import com.example.afinal.Database.Model.NguoiDung;
import com.example.afinal.Database.Model.ThueXe;
import com.example.afinal.Database.Model.ChiTietThueXe;
import com.example.afinal.Database.Model.ThanhToan;
import com.example.afinal.Database.Model.Xe;
import com.example.afinal.Database.Utilites.NguoiDungUtility;
import com.example.afinal.R;

import java.util.List;


public class FragmentUserRentVehicle extends Fragment {
    private static final String TAG = "FragmentUserRentVehicle";
    private static final int ZALOPAY_REQUEST_CODE = 1;

    private TextView tvHoTen, tvCCCD, tvSDT, tvNgayDat, tvTenXe, tvTienCoc, tvNgayBatDauDK, tvNgayKetThucDK, tvNgayBatDauTT, tvNgayHienTai, tvThanhTien, tvThucTra, tvGhiChu;
    private Spinner spinnerPhuongThuc;
    private Button btnDungThue;
    private ThueXeDAO thueXeDAO;
    private ChiTietThueXeDAO chiTietThueXeDAO;
    private ThanhToanDAO thanhToanDAO;
    private XeDAO xeDAO;
    private NguoiDungDAO nguoiDungDAO;
    private ThanhToan currentPayment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_user_rent_vehicle, container, false);

        initViews(view);

        thueXeDAO = new ThueXeDAO(requireContext());
        chiTietThueXeDAO = new ChiTietThueXeDAO(requireContext());
        thanhToanDAO = new ThanhToanDAO(requireContext());
        xeDAO = new XeDAO(requireContext());
        nguoiDungDAO = new NguoiDungDAO(requireContext());

        NguoiDungUtility nguoiDungUtility = NguoiDungUtility.getInstance(requireContext());
        NguoiDung currentUser = nguoiDungUtility.getCurrentUser();

        if (currentUser != null) {
            String appTransId = null;
            if (getArguments() != null) {
                appTransId = getArguments().getString("appTransId");
            }

            if (appTransId != null) {
                Log.d("RentalInfo", "Looking for payment with appTransId: " + appTransId);
                ThanhToan payment = thanhToanDAO.getByMaGiaoDich(appTransId);
                if (payment != null) {
                    Log.d("RentalInfo", "Found payment record with maThueXe: " + payment.getMaThueXe());
                    // Get rental record
                    ThueXe rental = thueXeDAO.getById(payment.getMaThueXe());
                    if (rental != null) {
                        List<ChiTietThueXe> rentalDetailsList = chiTietThueXeDAO.getByMaThueXe(rental.getMaThueXe());
                        if (rentalDetailsList != null && !rentalDetailsList.isEmpty()) {
                            ChiTietThueXe rentalDetails = rentalDetailsList.get(0);
                            displayRentalInfo(rental, rentalDetails, currentUser);
                            
                        } else {
                            Log.e("RentalInfo", "No rental details found for maThueXe: " + rental.getMaThueXe());
                        }
                    } else {
                        Log.e("RentalInfo", "No rental record found for maThueXe: " + payment.getMaThueXe());
                    }
                } else {
                    Log.e("RentalInfo", "No payment record found for appTransId: " + appTransId);
                }
            } else {
                // If no appTransId, show latest rental
                List<ThueXe> userRentals = thueXeDAO.getByMaND(currentUser.getMaND());
                if (!userRentals.isEmpty()) {
                    ThueXe latestRental = userRentals.get(userRentals.size() - 1);
                    List<ChiTietThueXe> rentalDetailsList = chiTietThueXeDAO.getByMaThueXe(latestRental.getMaThueXe());
                    if (rentalDetailsList != null && !rentalDetailsList.isEmpty()) {
                        ChiTietThueXe rentalDetails = rentalDetailsList.get(0);
                        displayRentalInfo(latestRental, rentalDetails, currentUser);
                        
                        ThanhToan payment = thanhToanDAO.getThanhToanById(latestRental.getMaThueXe());
                        if (payment != null) {
                            // Setup stop rental button click listener
                        }
                    }
                }
            }
        }

        return view;
    }

    private void initViews(View view) {
        tvHoTen = view.findViewById(R.id.tvHoTen);
        tvCCCD = view.findViewById(R.id.tvCCCD);
        tvSDT = view.findViewById(R.id.tvSDT);
        tvNgayDat = view.findViewById(R.id.tvNgayDat);
        tvTenXe = view.findViewById(R.id.tvTenXe);
        tvTienCoc = view.findViewById(R.id.tvTienCoc);
        tvNgayBatDauDK = view.findViewById(R.id.tvNgayBatDauDK);
        tvNgayKetThucDK = view.findViewById(R.id.tvNgayKetThucDK);
        tvNgayBatDauTT = view.findViewById(R.id.tcNgayBatDauTT);
        tvNgayHienTai = view.findViewById(R.id.tvNgayHienTai);
        tvThanhTien = view.findViewById(R.id.tvThanhTien);
        tvThucTra = view.findViewById(R.id.tvThucTra);
        tvGhiChu = view.findViewById(R.id.tvGhiChu);
        spinnerPhuongThuc = view.findViewById(R.id.spinnerPhuongThuc);
        btnDungThue = view.findViewById(R.id.btnDungThue);
    }

    @SuppressLint("DefaultLocale")
    private void displayRentalInfo(ThueXe rental, ChiTietThueXe details, NguoiDung currentUser) {
        // Display user information
        tvHoTen.setText(currentUser.getHoTen());
        tvCCCD.setText(currentUser.getCccd());
        tvSDT.setText(currentUser.getSdt());

        tvNgayDat.setText(rental.getNgayDat());

        Xe xe = xeDAO.getById(details.getMaXe());
        if (xe != null) {
            tvTenXe.setText(xe.getTenXe());
        }

        tvTienCoc.setText(String.format("%,d VNĐ", details.getTienCoc()));
        tvNgayBatDauDK.setText(details.getNgayBatDauDK());
        tvNgayKetThucDK.setText(details.getNgayKetThucDK());
        tvNgayBatDauTT.setText(details.getNgayBatDauTT());
        tvNgayHienTai.setText(java.time.LocalDate.now().toString());
        tvThanhTien.setText(String.format("%,d VNĐ", details.getThanhTien()));
        tvThucTra.setText(String.format("%,d VNĐ", details.getThanhTien() - details.getTienCoc()));
        tvGhiChu.setText(details.getGhiChu());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.phuong_thuc_thanh_toan, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPhuongThuc.setAdapter(adapter);
    }
}
