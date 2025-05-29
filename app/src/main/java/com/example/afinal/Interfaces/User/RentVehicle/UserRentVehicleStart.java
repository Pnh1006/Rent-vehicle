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
import android.widget.Spinner;
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
import com.example.afinal.Interfaces.User.Transaction.Action.PaymentNoti;
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

/** @noinspection CallToPrintStackTrace*/
public class UserRentVehicleStart extends Fragment {
    private TextView tvHoTen, tvCCCD, tvSDT, tvNgayDat, tvTenXe, tvTienCoc, tvThanhTien, tvGiaThue;
    private EditText edNgayBatDauDK, edNgayKetThucDK, edGhiChu;
    private Spinner spinnerPhuongThuc;
    private Button btnThanhToan, btnQuayVe;
    private Xe selectedXe;
    private NguoiDung currentUser;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
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
        return inflater.inflate(R.layout.fragement_user_rent_vehicle_start, container, false);
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

        // Setup payment method spinner
        setupPaymentMethodSpinner();

        // Setup button click listeners
        setupButtonListeners();
    }

    private void initViews(View view) {
        // Initialize all views
        tvHoTen = view.findViewById(R.id.tvHoTen);
        tvCCCD = view.findViewById(R.id.tvCCCD);
        tvSDT = view.findViewById(R.id.tvSDT);
        tvNgayDat = view.findViewById(R.id.tvNgayDat);
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

        // Get and display current user info
        currentUser = nguoiDungUtility.getCurrentUser();
        if (currentUser != null) {
            // Display user information
            tvHoTen.setText(currentUser.getHoTen());
            tvCCCD.setText(currentUser.getCccd());
            tvSDT.setText(currentUser.getSdt());
        } else {
            Toast.makeText(requireContext(), "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
        }

        // Set current date
        tvNgayDat.setText(dateFormat.format(new Date()));
    }

    @SuppressLint("DefaultLocale")
    private void displayVehicleInfo() {
        tvTenXe.setText(selectedXe.getTenXe());
        // Display rental price
        tvGiaThue.setText(String.format("%,d VNĐ/ngày", selectedXe.getGiaThue()));
        // Calculate deposit (30% of daily rate)
        tienCoc = (int) (selectedXe.getGiaThue() * 0.3);
        tvTienCoc.setText(String.format("%,d VNĐ", tienCoc));
    }

    private void setupDatePickers() {
        edNgayBatDauDK.setOnClickListener(v -> showDatePicker(edNgayBatDauDK));
        edNgayKetThucDK.setOnClickListener(v -> showDatePicker(edNgayKetThucDK));
    }

    private void showDatePicker(final EditText editText) {
        // Set minimum date to today
        Calendar minDate = Calendar.getInstance();
        minDate.set(Calendar.HOUR_OF_DAY, 0);
        minDate.set(Calendar.MINUTE, 0);
        minDate.set(Calendar.SECOND, 0);
        minDate.set(Calendar.MILLISECOND, 0);

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Check if selected date is before today
            if (calendar.getTimeInMillis() < minDate.getTimeInMillis()) {
                Toast.makeText(requireContext(), "Ngày không được nhỏ hơn ngày hiện tại",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // If this is end date, check if it's after start date
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

            // Show time picker after date is selected
            showTimePicker(editText);
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showTimePicker(final EditText editText) {
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            // If this is today, check if selected time is in the future
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

            // If this is end date, check if it's after start date and time
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
                
                long diffInMillis = end.getTime() - start.getTime();
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
        // Get payment methods from resources
        String[] paymentMethods = getResources().getStringArray(R.array.phuong_thuc_thanh_toan);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                paymentMethods
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPhuongThuc.setAdapter(adapter);
    }

    private void savePaymentTransaction(String amount, String paymentMethod, String status, String transactionId, String appTransId) {
        try {
            ThanhToan thanhToan = new ThanhToan();
            thanhToan.setMaND(currentUser.getMaND());
            thanhToan.setMaThueXe(0); // Will be updated when rental is created
            thanhToan.setSoTien(Integer.parseInt(amount.replaceAll("[^0-9]", "")));
            thanhToan.setNoiDung("Thanh toán tiền thuê xe");
            thanhToan.setNgayThucHien(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            thanhToan.setNgayThanhCong(status.equals("Thanh toán thành công") ? 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()) : null);
            thanhToan.setPhuongThuc(paymentMethod);
            thanhToan.setMaGiaoDich(transactionId != null ? transactionId : appTransId);
            thanhToan.setGhiChu(edGhiChu.getText().toString().trim());
            
            // Map status to match string array values
            int trangThai;
            switch (status) {
                case "Thanh toán thành công":
                    trangThai = 0; // "Đã thanh toán"

                    break;
                case "Hủy thanh toán":
                    trangThai = 1; // "Chưa thanh toán"

                    break;
                case "Lỗi thanh toán":
                    trangThai = 3; // "Thất bại"

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

        // Handle "Thanh toán tiền cọc" button click
        btnThanhToan.setOnClickListener(v -> {
            if (edNgayBatDauDK.getText().toString().isEmpty() || edNgayKetThucDK.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng chọn ngày bắt đầu và kết thúc",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String paymentMethod = spinnerPhuongThuc.getSelectedItem().toString();
            if (paymentMethod.equals("Tiền mặt")) {
                // Navigate to payment screen
                UserPayStart payStartFragment = new UserPayStart();

                // Pass necessary data to payment fragment
                Bundle bundle = new Bundle();
                bundle.putSerializable("xe", selectedXe);
                bundle.putString("ngayBatDau", edNgayBatDauDK.getText().toString());
                bundle.putString("ngayKetThuc", edNgayKetThucDK.getText().toString());
                bundle.putString("ghiChu", edGhiChu.getText().toString().trim());
                payStartFragment.setArguments(bundle);

                // Navigate to payment fragment
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.user_rent_vehicle_container, payStartFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                CreateOrder orderApi = new CreateOrder();
                try {
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
                    
                    Log.d("ZaloPay", "Amount to pay: " + amount);
                    Log.d("ZaloPay", "APP_ID: " + AppInfo.APP_ID);
                    Log.d("ZaloPay", "MAC_KEY: " + AppInfo.MAC_KEY);
                    JSONObject data = orderApi.createOrder(String.valueOf(amount));
                    Log.d("ZaloPay", "Response data: " + (data != null ? data.toString() : "null"));
                    
                    if (data != null) {
                        if (data.has("returncode")) {
                            int code = data.getInt("returncode");
                            Log.d("ZaloPay", "Return code: " + code);
                            
                            String errorMessage = "Lỗi không xác định"; // Default error message
                            switch (code) {
                                case 1:
                                    if (data.has("zptranstoken")) {
                                        String token = data.getString("zptranstoken");
                                        Log.d("ZaloPay", "Token: " + token);
                                        ZaloPaySDK.getInstance().payOrder(requireActivity(), token, "demozpdk://app", new PayOrderListener() {
                                            @Override
                                            public void onPaymentSucceeded(String transactionId, String transToken, String appTransId) {
                                                Log.d("ZaloPay", "Payment succeeded - transactionId: " + transactionId);
                                                
                                                // Save successful payment transaction
                                                String amount = tvThanhTien.getText().toString();
                                                String paymentMethod = spinnerPhuongThuc.getSelectedItem().toString();
                                                savePaymentTransaction(amount, paymentMethod, "Thanh toán thành công", transactionId, appTransId);
                                                
                                                // Tạo đơn thuê xe
                                                ThueXe thueXe = new ThueXe();
                                                thueXe.setMaND(currentUser.getMaND());
                                                thueXe.setNgayDat(ThueXeUtility.getCurrentDate());
                                                thueXe.setTrangThai(0); // Đang thuê

                                                long maThueXe = thueXeDAO.insert(thueXe);
                                                if (maThueXe > 0) {
                                                    // Tạo chi tiết thuê xe
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

                                                    long result = chiTietThueXeDAO.insert(chiTiet);
                                                    if (result > 0) {
                                                        // Cập nhật trạng thái xe
                                                        xeDAO.updateStatus(selectedXe.getMaXe(), 1); // Đang được thuê
                                                    }
                                                }
                                                
                                                // Create and show PaymentNoti fragment
                                                PaymentNoti paymentNotiFragment = new PaymentNoti();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("result", "Thanh toán thành công");
                                                bundle.putString("transactionId", transactionId);
                                                bundle.putString("appTransId", appTransId);
                                                paymentNotiFragment.setArguments(bundle);

                                                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                                transaction.replace(R.id.user_rent_vehicle_container, paymentNotiFragment);
                                                transaction.addToBackStack(null);
                                                transaction.commit();
                                            }

                                            @Override
                                            public void onPaymentCanceled(String zpTransToken, String appTransId) {
                                                Log.d("ZaloPay", "Payment canceled - zpTransToken: " + zpTransToken);
                                                
                                                // Save cancelled payment transaction
                                                String amount = tvThanhTien.getText().toString();
                                                String paymentMethod = spinnerPhuongThuc.getSelectedItem().toString();
                                                savePaymentTransaction(amount, paymentMethod, "Hủy thanh toán", null, null);
                                                
                                                // Create and show PaymentNoti fragment with error
                                                PaymentNoti paymentNotiFragment = new PaymentNoti();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("result", "Hủy thanh toán");
                                                paymentNotiFragment.setArguments(bundle);

                                                // Add fragment on top of current screen
                                                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                                transaction.replace(R.id.user_rent_vehicle_container, paymentNotiFragment);
                                                transaction.addToBackStack(null);
                                                transaction.commit();
                                            }

                                            @Override
                                            public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransId) {
                                                Log.d("ZaloPay", "Payment error - " + zaloPayError.toString());
                                                
                                                // Save failed payment transaction
                                                String amount = tvThanhTien.getText().toString();
                                                String paymentMethod = spinnerPhuongThuc.getSelectedItem().toString();
                                                savePaymentTransaction(amount, paymentMethod, "Lỗi thanh toán", null, null);
                                                
                                                // Create and show PaymentNoti fragment with error
                                                PaymentNoti paymentNotiFragment = new PaymentNoti();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("result", "Lỗi thanh toán: " + zaloPayError);
                                                paymentNotiFragment.setArguments(bundle);

                                                // Add fragment on top of current screen
                                                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                                transaction.replace(R.id.user_rent_vehicle_container, paymentNotiFragment);
                                                transaction.addToBackStack(null);
                                                transaction.commit();
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
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
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
            Spinner spinnerVehicleType = requireActivity().findViewById(R.id.spinner_vehicle_type);
            Spinner spinnerStatus = requireActivity().findViewById(R.id.spinner_status);
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
                PaymentNoti paymentNotiFragment = new PaymentNoti();
                Bundle bundle = new Bundle();
                
                switch (status) {
                    case "success":
                        bundle.putString("result", "Thanh toán thành công");
                        bundle.putString("transactionId", zpTransToken);
                        bundle.putString("appTransId", appTransId);
                        break;
                    case "cancel":
                        bundle.putString("result", "Hủy thanh toán");
                        break;
                    default:
                        bundle.putString("result", "Lỗi thanh toán");
                        break;
                }
                
                paymentNotiFragment.setArguments(bundle);

                // Add fragment on top of current screen
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.user_rent_vehicle_container, paymentNotiFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                // Hide the rent vehicle container
                View container = requireActivity().findViewById(R.id.user_rent_vehicle_container);
                if (container != null) {
                    container.setVisibility(View.GONE);
                }

                // Show homepage elements
                TextView tvWelcome = requireActivity().findViewById(R.id.tv_welcome);
                AutoCompleteTextView tvSearch = requireActivity().findViewById(R.id.tv_search);
                Spinner spinnerVehicleType = requireActivity().findViewById(R.id.spinner_vehicle_type);
                Spinner spinnerStatus = requireActivity().findViewById(R.id.spinner_status);
                Button btnClearFilter = requireActivity().findViewById(R.id.btn_clear_filter);
                RecyclerView recyclerView = requireActivity().findViewById(R.id.vehicle_list);

                if (tvWelcome != null) tvWelcome.setVisibility(View.VISIBLE);
                if (tvSearch != null) tvSearch.setVisibility(View.VISIBLE);
                if (spinnerVehicleType != null) spinnerVehicleType.setVisibility(View.VISIBLE);
                if (spinnerStatus != null) spinnerStatus.setVisibility(View.VISIBLE);
                if (btnClearFilter != null) btnClearFilter.setVisibility(View.VISIBLE);
                if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }
}
