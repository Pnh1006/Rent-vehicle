package com.example.afinal.Interfaces.User.RentVehicle;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.Database.DAO.ThanhToanDAO;
import com.example.afinal.Database.DAO.ThueXeDAO;
import com.example.afinal.Database.DAO.ChiTietThueXeDAO;
import com.example.afinal.Database.DAO.XeDAO;
import com.example.afinal.Database.Model.NguoiDung;
import com.example.afinal.Database.Model.ThanhToan;
import com.example.afinal.Database.Model.Xe;
import com.example.afinal.Database.Model.ThueXe;
import com.example.afinal.Database.Model.ChiTietThueXe;
import com.example.afinal.Database.Utilites.NguoiDungUtility;
import com.example.afinal.Database.Utilites.ThueXeUtility;
import com.example.afinal.Interfaces.User.Transaction.Action.PaymentNotification;
import com.example.afinal.Interfaces.User.Transaction.Api.CreateOrder;
import com.example.afinal.Interfaces.User.Transaction.Constant.AppInfo;
import com.example.afinal.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

/**
 * @noinspection CallToPrintStackTrace
 */
public class UserRentVehicleStart extends Fragment {
    private TextView tvTenXe, tvTienCoc, tvThanhTien, tvGiaThue;
    private EditText edNgayBatDauDK, edNgayKetThucDK, edGhiChu;
    private AutoCompleteTextView spinnerPhuongThuc;
    private Button btnThanhToan, btnQuayVe;
    private Xe selectedXe;
    private NguoiDung currentUser;
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private NguoiDungUtility nguoiDungUtility;
    private ThanhToanDAO thanhToanDAO;
    private ThueXeDAO thueXeDAO;
    private ChiTietThueXeDAO chiTietThueXeDAO;
    private XeDAO xeDAO;
    private int tienCoc;
    private int totalAmount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.form_user_rent_vehicle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nguoiDungUtility = NguoiDungUtility.getInstance(requireContext());

        thanhToanDAO = new ThanhToanDAO(requireContext());
        thueXeDAO = new ThueXeDAO(requireContext());
        chiTietThueXeDAO = new ChiTietThueXeDAO(requireContext());
        xeDAO = new XeDAO(requireContext());

        initViews(view);

        if (getArguments() != null && getArguments().containsKey("xe")) {
            selectedXe = (Xe) getArguments().getSerializable("xe");
            if (selectedXe != null) {
                displayVehicleInfo();
            }
        }

        setupDatePickers();

        setupPaymentMethodSpinner();

        setupButtonListeners();
    }

    private void initViews(View view) {
        TextView tvHoTen = view.findViewById(R.id.tvHoTen);
        TextView tvCCCD = view.findViewById(R.id.tvCCCD);
        TextView tvSDT = view.findViewById(R.id.tvSDT);
        TextView tvNgayDat = view.findViewById(R.id.tvNgayDat);
        tvTenXe = view.findViewById(R.id.tvTenXe);
        tvTienCoc = view.findViewById(R.id.tvTienCoc);
        tvThanhTien = view.findViewById(R.id.tvThanhTien);
        tvGiaThue = view.findViewById(R.id.tvGiaThue);
        edNgayBatDauDK = view.findViewById(R.id.edNgayBatDauDK);
        edNgayKetThucDK = view.findViewById(R.id.edNgayKetThucDK);
        edGhiChu = view.findViewById(R.id.edGhiChu);
        spinnerPhuongThuc = view.findViewById(R.id.spinnerPhuongThuc);
        btnThanhToan = view.findViewById(R.id.btnThanhToan);
        btnQuayVe = view.findViewById(R.id.btnQuayVe);

        currentUser = nguoiDungUtility.getCurrentUser();
        if (currentUser != null) {
            tvHoTen.setText(currentUser.getHoTen());
            tvCCCD.setText(currentUser.getCccd());
            tvSDT.setText(currentUser.getSdt());
        } else {
            Toast.makeText(requireContext(), "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
        }

        tvNgayDat.setText(dateFormat.format(new Date()));
    }

    @SuppressLint("DefaultLocale")
    private void displayVehicleInfo() {
        tvTenXe.setText(selectedXe.getTenXe());
        tvGiaThue.setText(String.format("%,d VNĐ/ngày", selectedXe.getGiaThue()));
        tienCoc = selectedXe.getGiaThue();
        tvTienCoc.setText(String.format("%,d VNĐ", tienCoc));
    }

    private void setupDatePickers() {
        edNgayBatDauDK.setOnClickListener(v -> showDatePicker(edNgayBatDauDK));
        edNgayKetThucDK.setOnClickListener(v -> showDatePicker(edNgayKetThucDK));
    }

    private void showDatePicker(final EditText editText) {
        Calendar minDate = Calendar.getInstance();
        minDate.set(Calendar.HOUR_OF_DAY, 0);
        minDate.set(Calendar.MINUTE, 0);
        minDate.set(Calendar.SECOND, 0);
        minDate.set(Calendar.MILLISECOND, 0);

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (calendar.getTimeInMillis() < minDate.getTimeInMillis()) {
                Toast.makeText(requireContext(), "Ngày không được nhỏ hơn ngày hiện tại",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (editText == edNgayKetThucDK && !edNgayBatDauDK.getText().toString().isEmpty()) {
                try {
                    Date startDate = dateFormat.parse(edNgayBatDauDK.getText().toString());
                    if (calendar.getTime().before(startDate)) {
                        Toast.makeText(requireContext(), "Ngày kết thúc phải sau ngày bắt đầu",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            showTimePicker(editText);
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showTimePicker(final EditText editText) {
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            Calendar now = Calendar.getInstance();
            if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                    calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                    calendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)) {

                if (calendar.getTimeInMillis() <= now.getTimeInMillis()) {
                    Toast.makeText(requireContext(), "Thời gian phải sau thời điểm hiện tại",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (editText == edNgayKetThucDK && !edNgayBatDauDK.getText().toString().isEmpty()) {
                try {
                    String startDateTime = edNgayBatDauDK.getText().toString();
                    Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                            .parse(startDateTime);
                    if (calendar.getTime().before(startDate)) {
                        Toast.makeText(requireContext(), "Thời gian kết thúc phải sau thời gian bắt đầu",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Set the date and time in the EditText
            String dateTime = dateFormat.format(calendar.getTime()) + " " +
                    timeFormat.format(calendar.getTime());
            editText.setText(dateTime);

            // If end date is set, calculate total amount
            if (editText == edNgayKetThucDK && !edNgayBatDauDK.getText().toString().isEmpty()) {
                calculateTotalAmount();
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // 24-hour format
        );
        timePickerDialog.show();
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void calculateTotalAmount() {
        try {
            String startDate = edNgayBatDauDK.getText().toString();
            String endDate = edNgayKetThucDK.getText().toString();

            if (!startDate.isEmpty() && !endDate.isEmpty()) {
                Date start = dateFormat.parse(startDate);
                Date end = dateFormat.parse(endDate);

                long diffInMillis = 0;
                if (start != null) {
                    if (end != null) {
                        diffInMillis = end.getTime() - start.getTime();
                    }
                }
                int days = (int) (diffInMillis / (24 * 60 * 60 * 1000));

                if (days > 0) {
                    totalAmount = selectedXe.getGiaThue() * days;
                    tvThanhTien.setText(String.format("%,d VNĐ", totalAmount));
                } else {
                    tvThanhTien.setText("0 VNĐ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            tvThanhTien.setText("0 VNĐ");
        }
    }

    private void setupPaymentMethodSpinner() {
        String[] paymentMethods = getResources().getStringArray(R.array.phuong_thuc_thanh_toan);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                paymentMethods
        );
        spinnerPhuongThuc.setAdapter(adapter);
    }

    private void savePaymentTransaction(String amount, String paymentMethod, String status, String transactionId, String appTransId) {
        try {
            ThanhToan thanhToan = new ThanhToan();
            thanhToan.setMaND(currentUser.getMaND());
            thanhToan.setMaThueXe(0);
            thanhToan.setSoTien(Integer.parseInt(amount.replaceAll("[^0-9]", "")));
            thanhToan.setNoiDung("Thanh toán tiền thuê xe");
            thanhToan.setNgayThucHien(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            thanhToan.setNgayThanhCong(status.equals("Đã thanh toán") ?
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()) : null);
            thanhToan.setPhuongThuc(paymentMethod);
            thanhToan.setMaGiaoDich(transactionId != null ? transactionId : appTransId);
            thanhToan.setGhiChu(edGhiChu.getText().toString().trim());

            int trangThai;
            switch (status) {
                case "Đã thanh toán":
                    trangThai = 0;
                    break;
                case "Chưa thanh toán":
                    trangThai = 1;
                    break;
                case "Thất bại":
                    trangThai = 3;
                    break;
                default:
                    trangThai = 2; // "Đang xử lý"
                    break;
            }
            thanhToan.setTrangThai(trangThai);

            long result = thanhToanDAO.insert(thanhToan);
            if (result == -1) {
                Log.e("Payment", "Failed to save payment transaction");
            }
        } catch (Exception e) {
            Log.e("Payment", "Error saving payment transaction: " + e.getMessage());
        }
    }

    private void setupButtonListeners() {
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(553, Environment.SANDBOX);

        btnThanhToan.setOnClickListener(v -> {
            if (edNgayBatDauDK.getText().toString().isEmpty() || edNgayKetThucDK.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng chọn ngày bắt đầu và kết thúc",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String amountText = tvThanhTien.getText().toString().replaceAll("[^0-9]", "");
            if (amountText.isEmpty()) {
                Toast.makeText(requireContext(), "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount = Integer.parseInt(amountText);
            if (amount < 1000) {
                Toast.makeText(requireContext(), "Số tiền tối thiểu là 1,000 VNĐ", Toast.LENGTH_SHORT).show();
                return;
            }

            String paymentMethod = spinnerPhuongThuc.getText().toString();
            if (paymentMethod.equals("Tiền mặt")) {
                handleCashPayment();
            } else {
                handleZaloPayment();
            }
        });

        // Handle "Quay về trang chủ" button click
        btnQuayVe.setOnClickListener(v -> {
            // Show homepage elements again
            View container = requireActivity().findViewById(R.id.user_rent_vehicle_container);
            if (container != null) {
                container.setVisibility(View.GONE);
            }

            // Show homepage elements
            TextView tvWelcome = requireActivity().findViewById(R.id.tv_welcome);
            AutoCompleteTextView tvSearch = requireActivity().findViewById(R.id.tv_search);
            AutoCompleteTextView spinnerVehicleType = requireActivity().findViewById(R.id.spinner_vehicle_type);
            AutoCompleteTextView spinnerStatus = requireActivity().findViewById(R.id.spinner_status);
            Button btnClearFilter = requireActivity().findViewById(R.id.btn_clear_filter);
            RecyclerView recyclerView = requireActivity().findViewById(R.id.vehicle_list);

            if (tvWelcome != null) tvWelcome.setVisibility(View.VISIBLE);
            if (tvSearch != null) tvSearch.setVisibility(View.VISIBLE);
            if (spinnerVehicleType != null) spinnerVehicleType.setVisibility(View.VISIBLE);
            if (spinnerStatus != null) spinnerStatus.setVisibility(View.VISIBLE);
            if (btnClearFilter != null) btnClearFilter.setVisibility(View.VISIBLE);
            if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
        });
    }

    private void handleCashPayment() {
        try {
            // Save payment transaction
            long appTransId = System.currentTimeMillis();
            Log.d("Cash", "Saving payment transaction - amount: " + totalAmount + ", method: Tiền mặt");
            savePaymentTransaction(String.valueOf(totalAmount), "Tiền mặt", "Đang xử lý", String.valueOf(appTransId), String.valueOf(appTransId));

            ThueXe thueXe = new ThueXe();
            thueXe.setMaND(currentUser.getMaND());
            thueXe.setNgayDat(ThueXeUtility.getCurrentDate());
            thueXe.setTrangThai(0); // Đang thuê

            Log.d("Cash", "Creating rental record for user: " + currentUser.getMaND());
            long maThueXe = thueXeDAO.insert(thueXe);
            Log.d("Cash", "Rental record created with maThueXe: " + maThueXe);

            if (maThueXe > 0) {
                ChiTietThueXe chiTiet = new ChiTietThueXe();
                chiTiet.setMaThueXe((int) maThueXe);
                chiTiet.setMaXe(selectedXe.getMaXe());
                chiTiet.setNgayBatDauDK(edNgayBatDauDK.getText().toString());
                chiTiet.setNgayKetThucDK(edNgayKetThucDK.getText().toString());
                chiTiet.setNgayBatDauTT(edNgayKetThucDK.getText().toString());
                chiTiet.setNgayKetThucTT("");
                chiTiet.setTienCoc(tienCoc);
                chiTiet.setThanhTien(totalAmount);
                chiTiet.setGhiChu("Thanh toán qua tiền mặt");

                Log.d("Cash", "Creating rental details for maThueXe: " + maThueXe);
                long result = chiTietThueXeDAO.insert(chiTiet);
                Log.d("Cash", "Rental details created with result: " + result);

                if (result > 0) {
                    Log.d("Cash", "Updating vehicle status for maXe: " + selectedXe.getMaXe());
                    xeDAO.updateStatus(selectedXe.getMaXe(), 1);

                    Log.d("Cash", "Updating payment record with maThueXe: " + maThueXe);
                    ThanhToan payment = thanhToanDAO.getByMaGiaoDich(String.valueOf(appTransId));
                    if (payment != null) {
                        payment.setMaThueXe((int) maThueXe);
                        int updateResult = thanhToanDAO.update(payment);
                        Log.d("Cash", "Payment record updated with result: " + updateResult);
                    } else {
                        Log.e("Cash", "Payment record not found for appTransId: " + appTransId);
                    }

                    PaymentNotification paymentNotiFragment = new PaymentNotification();
                    Bundle bundle = new Bundle();
                    bundle.putString("result", "Đang xử lý");
                    bundle.putString("subMessage", "Cảm ơn bạn đã sử dụng dịch vụ");
                    bundle.putBoolean("isCashPayment", true);
                    paymentNotiFragment.setArguments(bundle);

                    // Sử dụng getParentFragment() để lấy FragmentUserRentVehicle
                    Fragment parentFragment = getParentFragment();
                    if (parentFragment != null && parentFragment.isAdded()) {
                        FragmentTransaction transaction = parentFragment.getChildFragmentManager().beginTransaction();
                        transaction.replace(R.id.user_rent_vehicle_container, paymentNotiFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                } else {
                    Log.e("Cash", "Failed to create rental details");
                    Toast.makeText(requireContext(), "Có lỗi xảy ra khi tạo chi tiết thuê xe", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("Cash", "Failed to create rental record");
                Toast.makeText(requireContext(), "Có lỗi xảy ra khi tạo đơn thuê xe", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("Cash", "Error in handleCashPayment: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(requireContext(), "Có lỗi xảy ra khi xử lý thanh toán: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleZaloPayment() {
        try {
            CreateOrder orderApi = new CreateOrder();
            Log.d("ZaloPay", "Amount to pay: " + totalAmount);
            Log.d("ZaloPay", "APP_ID: " + AppInfo.APP_ID);
            Log.d("ZaloPay", "MAC_KEY: " + AppInfo.MAC_KEY);
            JSONObject data = orderApi.createOrder(String.valueOf(totalAmount));
            Log.d("ZaloPay", "Response data: " + (data != null ? data.toString() : "null"));

            if (data != null) {
                if (data.has("returncode")) {
                    int code = data.getInt("returncode");
                    Log.d("ZaloPay", "Return code: " + code);

                    String errorMessage = "Lỗi không xác định";
                    switch (code) {
                        case 1:
                            if (data.has("zptranstoken")) {
                                String token = data.getString("zptranstoken");
                                Log.d("ZaloPay", "Token: " + token);
                                ZaloPaySDK.getInstance().payOrder(requireActivity(), token, "demozpdk://app", new PayOrderListener() {
                                    @Override
                                    public void onPaymentSucceeded(String transactionId, String transToken, String appTransId) {
                                        Log.d("ZaloPay", "Payment succeeded - transactionId: " + transactionId + ", appTransId: " + appTransId);

                                        // Save payment transaction
                                        savePaymentTransaction(String.valueOf(totalAmount), "ZaloPay", "Đã thanh toán", transactionId, appTransId);

                                        ThueXe thueXe = new ThueXe();
                                        thueXe.setMaND(currentUser.getMaND());
                                        thueXe.setNgayDat(ThueXeUtility.getCurrentDate());
                                        thueXe.setTrangThai(0); // Đang thuê

                                        Log.d("ZaloPay", "Creating rental record for user: " + currentUser.getMaND());
                                        long maThueXe = thueXeDAO.insert(thueXe);
                                        Log.d("ZaloPay", "Rental record created with maThueXe: " + maThueXe);

                                        if (maThueXe > 0) {
                                            ChiTietThueXe chiTiet = new ChiTietThueXe();
                                            chiTiet.setMaThueXe((int) maThueXe);
                                            chiTiet.setMaXe(selectedXe.getMaXe());
                                            chiTiet.setNgayBatDauDK(edNgayBatDauDK.getText().toString());
                                            chiTiet.setNgayKetThucDK(edNgayKetThucDK.getText().toString());
                                            chiTiet.setNgayBatDauTT(edNgayBatDauDK.getText().toString());
                                            chiTiet.setNgayKetThucTT(edNgayKetThucDK.getText().toString());
                                            chiTiet.setTienCoc(tienCoc);
                                            chiTiet.setThanhTien(totalAmount);
                                            chiTiet.setGhiChu("Thanh toán qua ZaloPay");

                                            Log.d("ZaloPay", "Creating rental details for maThueXe: " + maThueXe);
                                            long result = chiTietThueXeDAO.insert(chiTiet);
                                            Log.d("ZaloPay", "Rental details created with result: " + result);

                                            if (result > 0) {
                                                Log.d("ZaloPay", "Updating vehicle status for maXe: " + selectedXe.getMaXe());
                                                xeDAO.updateStatus(selectedXe.getMaXe(), 1);

                                                Log.d("ZaloPay", "Updating payment record with maThueXe: " + maThueXe);
                                                ThanhToan payment = thanhToanDAO.getByMaGiaoDich(appTransId);
                                                if (payment != null) {
                                                    payment.setMaThueXe((int) maThueXe);
                                                    int updateResult = thanhToanDAO.update(payment);
                                                    Log.d("ZaloPay", "Payment record updated with result: " + updateResult);
                                                } else {
                                                    Log.e("ZaloPay", "Payment record not found for appTransId: " + appTransId);
                                                }

                                                PaymentNotification paymentNotiFragment = new PaymentNotification();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("result", "Đã thanh toán");
                                                bundle.putString("transactionId", transactionId);
                                                bundle.putString("appTransId", appTransId);
                                                paymentNotiFragment.setArguments(bundle);

                                                // Sử dụng getParentFragment() để lấy FragmentUserRentVehicle
                                                Fragment parentFragment = getParentFragment();
                                                if (parentFragment != null && parentFragment.isAdded()) {
                                                    FragmentTransaction transaction = parentFragment.getChildFragmentManager().beginTransaction();
                                                    transaction.replace(R.id.user_rent_vehicle_container, paymentNotiFragment);
                                                    transaction.addToBackStack(null);
                                                    transaction.commit();
                                                }
                                            } else {
                                                Log.e("ZaloPay", "Failed to create rental details");
                                                Toast.makeText(requireContext(), "Có lỗi xảy ra khi tạo chi tiết thuê xe", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Log.e("ZaloPay", "Failed to create rental record");
                                            Toast.makeText(requireContext(), "Có lỗi xảy ra khi tạo đơn thuê xe", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onPaymentCanceled(String zpTransToken, String appTransId) {
                                        Log.d("ZaloPay", "Payment canceled - zpTransToken: " + zpTransToken);

                                        String amount = tvThanhTien.getText().toString();
                                        String paymentMethod = spinnerPhuongThuc.getText().toString();
                                        savePaymentTransaction(amount, paymentMethod, "Chưa thanh toán", null, null);

                                        PaymentNotification paymentNotiFragment = new PaymentNotification();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("result", "Chưa thanh toán");
                                        paymentNotiFragment.setArguments(bundle);

                                        // Sử dụng getParentFragment() để lấy FragmentUserRentVehicle
                                        Fragment parentFragment = getParentFragment();
                                        if (parentFragment != null && parentFragment.isAdded()) {
                                            FragmentTransaction transaction = parentFragment.getChildFragmentManager().beginTransaction();
                                            transaction.replace(R.id.user_rent_vehicle_container, paymentNotiFragment);
                                            transaction.addToBackStack(null);
                                            transaction.commit();
                                        }
                                    }

                                    @Override
                                    public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransId) {
                                        Log.d("ZaloPay", "Payment error - " + zaloPayError.toString());

                                        String amount = tvThanhTien.getText().toString();
                                        String paymentMethod = spinnerPhuongThuc.getText().toString();
                                        savePaymentTransaction(amount, paymentMethod, "Thất bại", null, null);

                                        PaymentNotification paymentNotiFragment = new PaymentNotification();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("result", "Thất bại");
                                        paymentNotiFragment.setArguments(bundle);

                                        // Sử dụng getParentFragment() để lấy FragmentUserRentVehicle
                                        Fragment parentFragment = getParentFragment();
                                        if (parentFragment != null && parentFragment.isAdded()) {
                                            FragmentTransaction transaction = parentFragment.getChildFragmentManager().beginTransaction();
                                            transaction.replace(R.id.user_rent_vehicle_container, paymentNotiFragment);
                                            transaction.addToBackStack(null);
                                            transaction.commit();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(requireContext(), "Không nhận được token thanh toán", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case -2:
                            errorMessage = "Lỗi xác thực. Vui lòng kiểm tra lại thông tin thanh toán.";
                            break;
                        case -3:
                            errorMessage = "Lỗi kết nối. Vui lòng thử lại sau.";
                            break;
                        case -4:
                            errorMessage = "Lỗi dữ liệu. Vui lòng kiểm tra lại thông tin.";
                            break;
                        default:
                            errorMessage = data.has("returnmessage") && !data.getString("returnmessage").isEmpty()
                                    ? data.getString("returnmessage")
                                    : "Lỗi không xác định. Mã lỗi: " + code;
                    }

                    if (code != 1) {
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Không nhận được mã phản hồi từ ZaloPay", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Không thể kết nối với ZaloPay", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("ZaloPay", "Error in handleZaloPayment: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(requireContext(), "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            ZaloPaySDK.getInstance().onResult(data);

            // Get payment result from ZaloPay
            String zpTransToken = data.getStringExtra("zptranstoken");
            String appTransId = data.getStringExtra("apptransid");
            String status = data.getStringExtra("status");

            if (status != null) {
                // Create and show PaymentNoti fragment
                PaymentNotification paymentNotiFragment = new PaymentNotification();
                Bundle bundle = new Bundle();

                switch (status) {
                    case "success":
                        bundle.putString("result", "Đã thanh toán");
                        bundle.putString("transactionId", zpTransToken);
                        bundle.putString("appTransId", appTransId);
                        break;
                    case "cancel":
                        bundle.putString("result", "Chưa thanh toán");
                        break;
                    default:
                        bundle.putString("result", "Thất bại");
                        break;
                }

                paymentNotiFragment.setArguments(bundle);

                // Add fragment on top of current screen
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.user_rent_vehicle_container, paymentNotiFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
    }
}
