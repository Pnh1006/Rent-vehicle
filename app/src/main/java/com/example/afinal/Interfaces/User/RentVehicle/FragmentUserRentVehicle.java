package com.example.afinal.Interfaces.User.RentVehicle;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ScrollView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;

import com.example.afinal.Database.DAO.ThueXeDAO;
import com.example.afinal.Database.DAO.ChiTietThueXeDAO;
import com.example.afinal.Database.DAO.ThanhToanDAO;
import com.example.afinal.Database.DAO.XeDAO;
import com.example.afinal.Database.Model.NguoiDung;
import com.example.afinal.Database.Model.ThueXe;
import com.example.afinal.Database.Model.ChiTietThueXe;
import com.example.afinal.Database.Model.Xe;
import com.example.afinal.Database.Model.ThanhToan;
import com.example.afinal.Database.Utilites.NguoiDungUtility;
import com.example.afinal.Database.Utilites.ThueXeUtility;
import com.example.afinal.Interfaces.User.Transaction.Action.PaymentNotification;
import com.example.afinal.Interfaces.User.Transaction.Api.CreateOrder;
import com.example.afinal.Interfaces.User.Transaction.Constant.AppInfo;
import com.example.afinal.R;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Collectors;

import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

/**
 * @noinspection CallToPrintStackTrace
 */
public class FragmentUserRentVehicle extends Fragment {
    private TextView tvHoTen, tvCCCD, tvSDT, tvNgayDat, tvTenXe, tvTienCoc, tvNgayBatDauDK, tvNgayKetThucDK, tvNgayBatDauTT, tvNgayHienTai, tvThanhTien, tvThucTra, tvGhiChu, tvTrangThaiThanhToan, tvBienSo, tvLoaiXe, tvGiaThue;
    private MaterialAutoCompleteTextView spinnerPhuongThuc;
    private Button btnDungThue, btnBatDauThue, btnHuyThue;
    private ThanhToanDAO thanhToanDAO;
    private Xe selectedXe;
    private XeDAO xeDAO;
    private NguoiDungUtility nguoiDungUtility;
    private NguoiDung currentUser;
    private ThueXeDAO thueXeDAO;
    private ChiTietThueXeDAO chiTietThueXeDAO;
    private int tienCoc;
    private int totalAmount;
    private ScrollView scrollViewRentalInfo;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat inputDateFormat;
    private RecyclerView recyclerViewLichSuThue;
    private RentHistoryAdapter rentHistoryAdapter;
    private LinearLayout emptyStateLayout;
    private boolean isViewingHistory = false;
    private int currentRentalId = -1;
    private static final String TAG = "RentVehicle";
    private static final int TRANG_THAI_DANG_THUE = 0;    // "Đang thuê"
    private static final int TRANG_THAI_DA_TRA = 1;       // "Đã trả"
    private static final int TRANG_THAI_DANG_XU_LY = 2;   // "Đang xử lý"
    private static final int TRANG_THAI_HUY = 3;          // "Hủy"
    private static final int THANH_TOAN_DA_THANH_TOAN = 0;  // "Đã thanh toán"
    private static final int THANH_TOAN_CHUA_THANH_TOAN = 1; // "Chưa thanh toán"
    private static final int THANH_TOAN_DANG_XU_LY = 2;     // "Đang xử lý"
    private static final int THANH_TOAN_THAT_BAI = 3;       // "Thất bại"
    private boolean isDepositPayment = false;

    public static final class RentalStatus {
        public static final int PROCESSING = 2;
        public static final int RENTING = 0;
        public static final int COMPLETED = 1;
        public static final int CANCELLED = 3;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");

        Bundle args = getArguments();
        if (args != null) {
            boolean isFromHomepage = args.getBoolean("isFromHomepage", false);
            Xe xe = (Xe) args.getSerializable("xe");
            Log.d(TAG, "onCreate: isFromHomepage=" + isFromHomepage + ", xe=" + (xe != null ? xe.getTenXe() : "null"));
        } else {
            Log.d(TAG, "onCreate: No arguments found");
        }

        initData();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View view = inflater.inflate(R.layout.fragement_user_rent_vehicle, container, false);
        initViews(view);

        Bundle args = getArguments();
        if (args != null) {
            boolean isFromHomepage = args.getBoolean("isFromHomepage", false);
            Log.d(TAG, "onCreateView: isFromHomepage=" + isFromHomepage);

            if (isFromHomepage) {
                Xe xe = (Xe) args.getSerializable("xe");
                Log.d(TAG, "onCreateView: Got xe object: " + (xe != null ? xe.getTenXe() : "null"));

                if (xe != null) {
                    View scrollViewRentalInfo = view.findViewById(R.id.scrollViewRentalInfo);
                    View emptyView = view.findViewById(R.id.emptyView);
                    View rentVehicleContainer = view.findViewById(R.id.user_rent_vehicle_container);

                    Log.d(TAG, "Views found - scrollView: " + (scrollViewRentalInfo != null) +
                            ", emptyView: " + (emptyView != null) +
                            ", container: " + (rentVehicleContainer != null));

                    if (scrollViewRentalInfo != null) scrollViewRentalInfo.setVisibility(View.GONE);
                    if (emptyView != null) emptyView.setVisibility(View.GONE);
                    if (rentVehicleContainer != null) {
                        rentVehicleContainer.setVisibility(View.VISIBLE);
                        Log.d(TAG, "Set container visibility to VISIBLE");
                    }

                    // Clear any existing fragments in the container
                    getChildFragmentManager().popBackStackImmediate(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    UserRentVehicleStart rentVehicleStartFragment = new UserRentVehicleStart();
                    Bundle fragmentArgs = new Bundle();
                    fragmentArgs.putSerializable("xe", xe);
                    rentVehicleStartFragment.setArguments(fragmentArgs);

                    try {
                        getChildFragmentManager().beginTransaction()
                                .replace(R.id.user_rent_vehicle_container, rentVehicleStartFragment)
                                .commit();
                        Log.d(TAG, "Successfully added UserRentVehicleStart fragment");
                    } catch (Exception e) {
                        Log.e(TAG, "Error adding UserRentVehicleStart fragment", e);
                    }
                }
            }
        } else {
            Log.d(TAG, "onCreateView: No arguments found");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");

        Bundle args = getArguments();
        boolean isFromHomepage = args != null && args.getBoolean("isFromHomepage", false);
        Log.d(TAG, "onViewCreated: isFromHomepage=" + isFromHomepage);

        if (!isFromHomepage) {
            showEmptyView();
            loadLatestRentalInfo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        Bundle args = getArguments();
        boolean isFromHomepage = args != null && args.getBoolean("isFromHomepage", false);
        if (!isFromHomepage && currentUser != null) {
            loadLatestRentalInfo();
        }
    }

    private void initViews(View view) {
        try {
            tvHoTen = view.findViewById(R.id.tvHoTen);
            tvCCCD = view.findViewById(R.id.tvCCCD);
            tvSDT = view.findViewById(R.id.tvSDT);
            tvNgayDat = view.findViewById(R.id.tvNgayDat);
            tvTenXe = view.findViewById(R.id.tvTenXe);
            tvBienSo = view.findViewById(R.id.tvBienSo);
            tvLoaiXe = view.findViewById(R.id.tvLoaiXe);
            tvGiaThue = view.findViewById(R.id.tvGiaThue);
            tvTienCoc = view.findViewById(R.id.tvTienCoc);
            tvNgayBatDauDK = view.findViewById(R.id.tvNgayBatDauDK);
            tvNgayKetThucDK = view.findViewById(R.id.tvNgayKetThucDK);
            tvNgayBatDauTT = view.findViewById(R.id.tcNgayBatDauTT);
            tvNgayHienTai = view.findViewById(R.id.tvNgayHienTai);
            tvThanhTien = view.findViewById(R.id.tvThanhTien);
            tvThucTra = view.findViewById(R.id.tvThucTra);
            tvTrangThaiThanhToan = view.findViewById(R.id.tvTrangThaiThanhToan);
            spinnerPhuongThuc = view.findViewById(R.id.spinnerPhuongThuc);
            tvGhiChu = view.findViewById(R.id.tvGhiChu);
            btnDungThue = view.findViewById(R.id.btnDungThue);
            btnBatDauThue = view.findViewById(R.id.btnBatDauThue);
            btnHuyThue = view.findViewById(R.id.btnHuyThue);
            scrollViewRentalInfo = view.findViewById(R.id.scrollViewRentalInfo);
            recyclerViewLichSuThue = view.findViewById(R.id.recyclerViewLichSuThue);
            emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
            if (nguoiDungUtility != null) {
                currentUser = nguoiDungUtility.getCurrentUser();
            }

            recyclerViewLichSuThue.setLayoutManager(new LinearLayoutManager(requireContext()));
            rentHistoryAdapter = new RentHistoryAdapter(null, null, null, (thueXe, chiTiet, xe) -> {
                isViewingHistory = true;
                showRentalInfoView();
                displayRentalInfo(thueXe, chiTiet, xe, nguoiDungUtility.getCurrentUser(),
                        thanhToanDAO.getByMaThueXe(thueXe.getMaThueXe()));
            });
            recyclerViewLichSuThue.setAdapter(rentHistoryAdapter);

            btnDungThue.setOnClickListener(v -> handleStopRental());
            btnBatDauThue.setOnClickListener(v -> handleStartRental());
            btnHuyThue.setOnClickListener(v -> handleCancelRental());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        try {
            nguoiDungUtility = NguoiDungUtility.getInstance(requireContext());
            if (nguoiDungUtility == null) {
                return;
            }
            currentUser = nguoiDungUtility.getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(requireContext(), "Vui lòng đăng nhập để tiếp tục", Toast.LENGTH_SHORT).show();
                return;
            }
            thanhToanDAO = new ThanhToanDAO(requireContext());
            xeDAO = new XeDAO(requireContext());
            thueXeDAO = new ThueXeDAO(requireContext());
            chiTietThueXeDAO = new ChiTietThueXeDAO(requireContext());
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            inputDateFormat = new SimpleDateFormat("yyyy-M-d HH:mm", Locale.getDefault());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLatestRentalInfo() {
        try {
            NguoiDung currentUser = nguoiDungUtility.getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(requireContext(), "Vui lòng đăng nhập để xem thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("RentVehicle", "=== THÔNG TIN NGƯỜI DÙNG ===");
            Log.d("RentVehicle", "Mã ND: " + currentUser.getMaND());
            Log.d("RentVehicle", "Họ tên: " + currentUser.getHoTen());

            // Lấy thông tin thuê xe mới nhất của người dùng
            ThueXeDAO thueXeDAO = new ThueXeDAO(requireContext());
            List<ThueXe> userRentals = thueXeDAO.getByMaND(currentUser.getMaND());

            if (userRentals != null && !userRentals.isEmpty()) {
                Log.d("RentVehicle", "=== THÔNG TIN THUÊ XE ===");
                Log.d("RentVehicle", "Số lượng đơn thuê xe: " + userRentals.size());

                // Lọc bỏ các đơn đã hủy và sắp xếp theo ngày đặt mới nhất
                userRentals = userRentals.stream()
                    .filter(rental -> rental.getTrangThai() != TRANG_THAI_HUY)
                    .sorted((r1, r2) -> r2.getNgayDat().compareTo(r1.getNgayDat()))
                    .collect(Collectors.toList());

                if (!userRentals.isEmpty()) {
                    ThueXe latestRental = userRentals.get(0);

                    Log.d("RentVehicle", "Đơn thuê xe mới nhất:");
                    Log.d("RentVehicle", "- Mã thuê xe: " + latestRental.getMaThueXe());
                    Log.d("RentVehicle", "- Ngày đặt: " + latestRental.getNgayDat());
                    Log.d("RentVehicle", "- Trạng thái: " + latestRental.getTrangThai());

                    // Lấy thông tin chi tiết thuê xe
                    ChiTietThueXeDAO chiTietDAO = new ChiTietThueXeDAO(requireContext());
                    List<ChiTietThueXe> detailsList = chiTietDAO.getByMaThueXe(latestRental.getMaThueXe());

                    if (detailsList != null && !detailsList.isEmpty()) {
                        Log.d("RentVehicle", "=== CHI TIẾT THUÊ XE ===");
                        ChiTietThueXe details = detailsList.get(0);
                        Log.d("RentVehicle", "- Mã xe: " + details.getMaXe());
                        Log.d("RentVehicle", "- Ngày bắt đầu DK: " + details.getNgayBatDauDK());
                        Log.d("RentVehicle", "- Ngày kết thúc DK: " + details.getNgayKetThucDK());
                        Log.d("RentVehicle", "- Ngày bắt đầu TT: " + details.getNgayBatDauTT());
                        Log.d("RentVehicle", "- Ngày kết thúc TT: " + details.getNgayKetThucTT());
                        Log.d("RentVehicle", "- Tiền cọc: " + details.getTienCoc());
                        Log.d("RentVehicle", "- Thành tiền: " + details.getThanhTien());
                        Log.d("RentVehicle", "- Ghi chú: " + details.getGhiChu());

                        // Lấy thông tin xe
                        XeDAO xeDAO = new XeDAO(requireContext());
                        Xe xe = xeDAO.getById(details.getMaXe());

                        if (xe != null) {
                            Log.d("RentVehicle", "=== THÔNG TIN XE ===");
                            Log.d("RentVehicle", "- Tên xe: " + xe.getTenXe());
                            Log.d("RentVehicle", "- Biển số: " + xe.getBienSo());
                            Log.d("RentVehicle", "- Loại xe: " + xe.getLoaiXe());
                            Log.d("RentVehicle", "- Giá thuê: " + xe.getGiaThue());

                            // Lấy thông tin thanh toán
                            ThanhToanDAO thanhToanDAO = new ThanhToanDAO(requireContext());
                            ThanhToan thanhToan = thanhToanDAO.getByMaThueXe(latestRental.getMaThueXe());

                            try {
                                // Lấy ngày hiện tại và reset về 00:00:00
                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.HOUR_OF_DAY, 0);
                                cal.set(Calendar.MINUTE, 0);
                                cal.set(Calendar.SECOND, 0);
                                cal.set(Calendar.MILLISECOND, 0);
                                Date ngayHienTai = cal.getTime();

                                // Parse ngày kết thúc từ chi tiết thuê xe
                                String ngayKetThucStr = details.getNgayKetThucTT();
                                if (ngayKetThucStr == null || ngayKetThucStr.isEmpty()) {
                                    ngayKetThucStr = details.getNgayKetThucDK();
                                }

                                Date ngayKetThuc = null;
                                if (ngayKetThucStr != null && !ngayKetThucStr.isEmpty()) {
                                    try {
                                        // Thử parse với định dạng mới (không có số 0 ở đầu)
                                        ngayKetThuc = inputDateFormat.parse(ngayKetThucStr);
                                    } catch (Exception e1) {
                                        Log.e("RentVehicle", "Lỗi parse ngày với định dạng thông thường: " + e1.getMessage());
                                        try {
                                            // Nếu thất bại, thử chuyển đổi định dạng ngày
                                            String[] parts = ngayKetThucStr.split(" ")[0].split("-");
                                            if (parts.length == 3) {
                                                @SuppressLint("DefaultLocale") String reformattedDate = String.format("%s-%d-%d %s",
                                                        parts[0],
                                                        Integer.parseInt(parts[1]),
                                                        Integer.parseInt(parts[2]),
                                                        ngayKetThucStr.contains(" ") ? ngayKetThucStr.split(" ")[1] : "00:00");
                                                ngayKetThuc = inputDateFormat.parse(reformattedDate);
                                            }
                                        } catch (Exception e2) {
                                            Log.e("RentVehicle", "Lỗi parse ngày sau khi format lại: " + e2.getMessage());
                                            throw e2;
                                        }
                                    }
                                }

                                if (ngayKetThuc != null) {
                                    // Reset ngày kết thúc về 00:00:00
                                    cal.setTime(ngayKetThuc);
                                    cal.set(Calendar.HOUR_OF_DAY, 0);
                                    cal.set(Calendar.MINUTE, 0);
                                    cal.set(Calendar.SECOND, 0);
                                    cal.set(Calendar.MILLISECOND, 0);
                                    ngayKetThuc = cal.getTime();

                                    Log.d("RentVehicle", "=== SO SÁNH NGÀY ===");
                                    Log.d("RentVehicle", "Ngày kết thúc: " + inputDateFormat.format(ngayKetThuc));
                                    Log.d("RentVehicle", "Ngày hiện tại: " + inputDateFormat.format(ngayHienTai));
                                    Log.d("RentVehicle", "Kết quả so sánh: " + ngayKetThuc.compareTo(ngayHienTai));

                                    // So sánh ngày
                                    if (ngayKetThuc.compareTo(ngayHienTai) >= 0) {
                                        Log.d("RentVehicle", "Hiển thị thông tin thuê xe (ngày kết thúc >= ngày hiện tại)");
                                        showRentalInfoView();
                                        displayRentalInfo(latestRental, details, xe, currentUser, thanhToan);
                                    } else {
                                        Log.d("RentVehicle", "Hiển thị lịch sử thuê xe (ngày kết thúc < ngày hiện tại)");
                                        showEmptyView();
                                        loadRentHistory(currentUser.getMaND());
                                    }
                                } else {
                                    Log.d("RentVehicle", "Không có ngày kết thúc");
                                    showEmptyView();
                                    loadRentHistory(currentUser.getMaND());
                                }
                            } catch (Exception e) {
                                Log.e("RentVehicle", "Lỗi parse ngày: " + e.getMessage());
                                e.printStackTrace();
                                showEmptyView();
                                loadRentHistory(currentUser.getMaND());
                            }
                        } else {
                            Log.d("RentVehicle", "Không có chi tiết thuê xe");
                            showEmptyView();
                        }
                    } else {
                        Log.d("RentVehicle", "Không có thông tin thuê xe");
                        showEmptyView();
                    }
                } else {
                    Log.d("RentVehicle", "Không có đơn thuê xe đang hoạt động");
                    showEmptyView();
                    loadRentHistory(currentUser.getMaND());
                }
            } else {
                Log.d("RentVehicle", "Không có thông tin thuê xe");
                showEmptyView();
            }
        } catch (Exception e) {
            Log.e("RentVehicle", "Lỗi: " + e.getMessage());
            e.printStackTrace();
            showError("Có lỗi xảy ra khi tải thông tin thuê xe");
        }
    }

    private void showRentalInfoView() {
        if (getView() == null) return;

        // Clear any existing fragments in the container
        getChildFragmentManager().popBackStackImmediate(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // Find all views
        View scrollViewRentalInfo = getView().findViewById(R.id.scrollViewRentalInfo);
        View emptyView = getView().findViewById(R.id.emptyView);
        View rentVehicleContainer = getView().findViewById(R.id.user_rent_vehicle_container);
        View recyclerViewLichSuThue = getView().findViewById(R.id.recyclerViewLichSuThue);

        // Hide all views first
        if (scrollViewRentalInfo != null) scrollViewRentalInfo.setVisibility(View.GONE);
        if (emptyView != null) emptyView.setVisibility(View.GONE);
        if (rentVehicleContainer != null) rentVehicleContainer.setVisibility(View.GONE);
        if (recyclerViewLichSuThue != null) recyclerViewLichSuThue.setVisibility(View.GONE);

        // Remove any fragments in the container
        Fragment existingFragment = getChildFragmentManager().findFragmentById(R.id.user_rent_vehicle_container);
        if (existingFragment != null) {
            getChildFragmentManager().beginTransaction()
                    .remove(existingFragment)
                    .commitNow(); // Use commitNow to ensure immediate execution
        }

        // Show only the rental info view
        if (scrollViewRentalInfo != null) {
            scrollViewRentalInfo.setVisibility(View.VISIBLE);
        }

        Log.d(TAG, "showRentalInfoView: Updated view visibilities");
    }

    private void showEmptyView() {
        Log.d(TAG, "showEmptyView called");
        if (getView() == null) {
            Log.e(TAG, "showEmptyView: View is null");
            return;
        }

        View scrollViewRentalInfo = getView().findViewById(R.id.scrollViewRentalInfo);
        View emptyView = getView().findViewById(R.id.emptyView);
        View rentVehicleContainer = getView().findViewById(R.id.user_rent_vehicle_container);

        if (scrollViewRentalInfo != null) scrollViewRentalInfo.setVisibility(View.GONE);
        if (emptyView != null) emptyView.setVisibility(View.VISIBLE);
        if (rentVehicleContainer != null) rentVehicleContainer.setVisibility(View.GONE);

        Log.d(TAG, "showEmptyView: Updated view visibilities");
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        scrollViewRentalInfo.setVisibility(View.GONE);
    }

    private String getPaymentStatusText(int status) {
        String[] statusArray = getResources().getStringArray(R.array.trang_thai_thanh_toan);
        if (status >= 0 && status < statusArray.length) {
            return statusArray[status];
        }
        return "Không xác định";
    }

    /**
     * @noinspection deprecation
     */
    @SuppressLint({"DefaultLocale", "SetTextI18n", "UseCompatLoadingForColorStateLists"})
    private void displayRentalInfo(ThueXe rental, ChiTietThueXe details, Xe vehicle, NguoiDung user, ThanhToan thanhToan) {
        try {
            selectedXe = vehicle;
            currentRentalId = rental.getMaThueXe();
            tvHoTen.setText(user.getHoTen());
            tvCCCD.setText(user.getCccd());
            tvSDT.setText(user.getSdt());
            tvNgayDat.setText(rental.getNgayDat());
            tvTenXe.setText(vehicle.getTenXe());
            tvBienSo.setText(vehicle.getBienSo());
            tvLoaiXe.setText(vehicle.getLoaiXe());
            tvGiaThue.setText(String.format("%,d VNĐ/ngày", vehicle.getGiaThue()));
            tvTienCoc.setText(String.format("%,d VNĐ", details.getTienCoc()));
            tvNgayBatDauDK.setText(details.getNgayBatDauDK());
            tvNgayKetThucDK.setText(details.getNgayKetThucDK());
            tvNgayBatDauTT.setText(details.getNgayBatDauTT());
            tvNgayHienTai.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()));
            int thanhTien = details.getThanhTien();
            int tienCoc = details.getTienCoc();
            int thucTra = thanhTien - tienCoc;
            tvThanhTien.setText(String.format("%,d VNĐ", thanhTien));
            tvThucTra.setText(String.format("%,d VNĐ", thucTra));
            String[] paymentMethods = getResources().getStringArray(R.array.phuong_thuc_thanh_toan);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_dropdown_item_1line, paymentMethods);
            spinnerPhuongThuc.setAdapter(adapter);

            // Cập nhật trạng thái thanh toán
            if (thanhToan != null) {
                spinnerPhuongThuc.setText(thanhToan.getPhuongThuc(), false);
                // Nếu là thanh toán tiền cọc và đã thanh toán thành công
                if (thanhToan.getSoTien() == tienCoc && thanhToan.getTrangThai() == THANH_TOAN_DA_THANH_TOAN) {
                    tvTrangThaiThanhToan.setText("Đã thanh toán tiền cọc");
                } else if (thanhToan.getSoTien() == thucTra && thanhToan.getTrangThai() == THANH_TOAN_DA_THANH_TOAN) {
                    tvTrangThaiThanhToan.setText("Đã thanh toán tiền thuê");
                } else {
                    tvTrangThaiThanhToan.setText(getPaymentStatusText(thanhToan.getTrangThai()));
                }
            } else {
                spinnerPhuongThuc.setText(details.getGhiChu(), false);
                tvTrangThaiThanhToan.setText(getResources().getStringArray(R.array.trang_thai_thanh_toan)[1]); // Chưa thanh toán
            }
            tvGhiChu.setText(details.getGhiChu());

            // Disable tất cả các trường input
            tvHoTen.setEnabled(false);
            tvCCCD.setEnabled(false);
            tvSDT.setEnabled(false);
            tvNgayDat.setEnabled(false);
            tvTenXe.setEnabled(false);
            tvBienSo.setEnabled(false);
            tvLoaiXe.setEnabled(false);
            tvGiaThue.setEnabled(false);
            tvTienCoc.setEnabled(false);
            tvNgayBatDauDK.setEnabled(false);
            tvNgayKetThucDK.setEnabled(false);
            tvNgayBatDauTT.setEnabled(false);
            tvNgayHienTai.setEnabled(false);
            tvThanhTien.setEnabled(false);
            tvThucTra.setEnabled(false);
            spinnerPhuongThuc.setEnabled(false);
            tvTrangThaiThanhToan.setEnabled(false);
            tvGhiChu.setEnabled(false);

            // Cập nhật text và style của button
            if (isViewingHistory) {
                btnDungThue.setText("Quay lại");
                btnDungThue.setBackgroundTintList(getResources().getColorStateList(R.color.title_color));
            } else {
                btnDungThue.setText("Dừng thuê xe");
                btnDungThue.setBackgroundTintList(getResources().getColorStateList(R.color.title_color));
            }

            scrollViewRentalInfo.setVisibility(View.VISIBLE);

            // Cập nhật hiển thị button dựa trên trạng thái mới
            updateButtonVisibility(rental, details);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Có lỗi xảy ra khi hiển thị thông tin thuê xe");
        }
    }

    private void updateButtonVisibility(ThueXe rental, ChiTietThueXe details) {
        try {
            // Lấy ngày hiện tại
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date ngayHienTai = cal.getTime();

            // Parse ngày bắt đầu dự kiến
            Date ngayBatDauDK = inputDateFormat.parse(details.getNgayBatDauDK());
            if (ngayBatDauDK != null) {
                cal.setTime(ngayBatDauDK);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                ngayBatDauDK = cal.getTime();
            }

            // Parse ngày kết thúc dự kiến
            Date ngayKetThucDK = inputDateFormat.parse(details.getNgayKetThucDK());
            if (ngayKetThucDK != null) {
                cal.setTime(ngayKetThucDK);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                ngayKetThucDK = cal.getTime();
            }

            // Ẩn tất cả các button trước
            btnDungThue.setVisibility(View.GONE);
            btnBatDauThue.setVisibility(View.GONE);
            btnHuyThue.setVisibility(View.GONE);

            // Trường hợp 1: Đang xử lý hoặc (Đang thuê và ngày bắt đầu > ngày hiện tại)
            if (rental.getTrangThai() == TRANG_THAI_DANG_XU_LY || 
                (rental.getTrangThai() == TRANG_THAI_DANG_THUE && ngayBatDauDK.after(ngayHienTai))) {
                // Chỉ hiển thị nút Hủy thuê
                btnHuyThue.setVisibility(View.VISIBLE);
                btnBatDauThue.setVisibility(View.GONE);
                btnDungThue.setVisibility(View.GONE);
            }
            // Trường hợp 2: Đang thuê và trong thời gian thuê
            else if (rental.getTrangThai() == TRANG_THAI_DANG_THUE && 
                     !ngayBatDauDK.after(ngayHienTai) && 
                     !ngayHienTai.after(ngayKetThucDK)) {
                // Hiển thị cả 2 nút Bắt đầu thuê và Dừng thuê
                btnBatDauThue.setVisibility(View.VISIBLE);
                btnDungThue.setVisibility(View.VISIBLE);
            }

            Log.d(TAG, "Button visibility updated - Huy: " + btnHuyThue.getVisibility() + 
                       ", BatDau: " + btnBatDauThue.getVisibility() + 
                       ", Dung: " + btnDungThue.getVisibility());
        } catch (Exception e) {
            Log.e(TAG, "Error updating button visibility: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadRentHistory(int maND) {
        try {
            ThueXeDAO thueXeDAO = new ThueXeDAO(requireContext());
            ChiTietThueXeDAO chiTietDAO = new ChiTietThueXeDAO(requireContext());
            XeDAO xeDAO = new XeDAO(requireContext());

            List<ThueXe> rentHistory = thueXeDAO.getByMaND(maND);
            if (rentHistory != null && !rentHistory.isEmpty()) {
                List<ChiTietThueXe> chiTietList = new ArrayList<>();
                List<Xe> xeList = new ArrayList<>();

                for (ThueXe thueXe : rentHistory) {
                    List<ChiTietThueXe> details = chiTietDAO.getByMaThueXe(thueXe.getMaThueXe());
                    if (details != null && !details.isEmpty()) {
                        chiTietList.add(details.get(0));
                        Xe xe = xeDAO.getById(details.get(0).getMaXe());
                        if (xe != null) {
                            xeList.add(xe);
                        }
                    }
                }

                if (!chiTietList.isEmpty() && !xeList.isEmpty()) {
                    rentHistoryAdapter.updateData(rentHistory, chiTietList, xeList);
                    recyclerViewLichSuThue.setVisibility(View.VISIBLE);
                    emptyStateLayout.setVisibility(View.GONE);
                } else {
                    recyclerViewLichSuThue.setVisibility(View.GONE);
                    emptyStateLayout.setVisibility(View.VISIBLE);
                }
            } else {
                recyclerViewLichSuThue.setVisibility(View.GONE);
                emptyStateLayout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            recyclerViewLichSuThue.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        }
    }

    // Thêm phương thức helper để xử lý chuỗi tiền tệ
    private int parseCurrencyString(String currencyStr) {
        try {
            if (currencyStr == null || currencyStr.trim().isEmpty()) {
                return 0;
            }
            // Loại bỏ tất cả ký tự không phải số, dấu chấm và khoảng trắng
            String cleanStr = currencyStr.replaceAll("[^0-9.,\\s]", "").trim();

            // Loại bỏ dấu chấm phân cách hàng nghìn và khoảng trắng
            cleanStr = cleanStr.replaceAll("[\\s.]", "");

            // Thay thế dấu phẩy bằng dấu chấm (nếu có)
            cleanStr = cleanStr.replace(",", ".");

            // Nếu có dấu chấm thập phân, lấy phần nguyên
            if (cleanStr.contains(".")) {
                cleanStr = cleanStr.substring(0, cleanStr.indexOf("."));
            }

            return Integer.parseInt(cleanStr);
        } catch (Exception e) {
            Log.e("Currency", "Error parsing currency string: " + currencyStr, e);
            return 0;
        }
    }

    private void setupButtonListeners() {
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập để tiếp tục", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra xem có đang xem lịch sử không
        if (isViewingHistory) {
            showEmptyView();
            loadRentHistory(currentUser.getMaND());
            return;
        }

        String buttonText = btnDungThue.getText().toString();
        if (buttonText.equals("Dừng thuê xe")) {
            handleStopRental();
        } else {
            handlePayment();
        }
    }

    private void handleStopRental() {
        try {
            if (currentRentalId == -1) {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin thuê xe", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật ngày kết thúc thực tế
            ChiTietThueXe chiTiet = chiTietThueXeDAO.getSingleByMaThueXe(currentRentalId);
            if (chiTiet != null) {
                String ngayKetThucTT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                chiTiet.setNgayKetThucTT(ngayKetThucTT);
                chiTietThueXeDAO.update(chiTiet);
                
                // Cập nhật UI
                Toast.makeText(requireContext(), "Đã cập nhật thời gian kết thúc thuê", Toast.LENGTH_SHORT).show();
                
                // Tính toán và hiển thị thanh toán
                calculateAndShowPayment(chiTiet);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error stopping rental: " + e.getMessage());
            Toast.makeText(requireContext(), "Có lỗi xảy ra khi dừng thuê xe", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCashStopRental() {
        try {
            if (currentRentalId == -1) {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin thuê xe", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy thông tin thuê xe hiện tại
            ThueXe currentRental = thueXeDAO.getById(currentRentalId);
            if (currentRental == null) {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin thuê xe", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật trạng thái thuê xe thành "Đang xử lý"
            currentRental.setTrangThai(TRANG_THAI_DANG_XU_LY);
            int updateResult = thueXeDAO.update(currentRental);
            Log.d("StopRental", "Updated rental status result: " + updateResult);

            // Tính toán số tiền cần thanh toán
            int thanhTien = parseCurrencyString(tvThanhTien.getText().toString());
            int tienCoc = parseCurrencyString(tvTienCoc.getText().toString());
            int finalAmount = thanhTien - tienCoc;

            if (finalAmount < 0) {
                Toast.makeText(requireContext(), "Số tiền thanh toán không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đơn thanh toán mới
            ThanhToan thanhToan = new ThanhToan();
            thanhToan.setMaND(currentUser.getMaND());
            thanhToan.setMaThueXe(currentRental.getMaThueXe());
            thanhToan.setSoTien(finalAmount);
            thanhToan.setNoiDung("Thanh toán tiền thuê xe - Dừng thuê");
            thanhToan.setNgayThucHien(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            thanhToan.setPhuongThuc("Tiền mặt");
            thanhToan.setTrangThai(TRANG_THAI_DA_TRA);
            thanhToan.setGhiChu("Thanh toán khi dừng thuê xe");

            long thanhToanId = thanhToanDAO.insert(thanhToan);
            Log.d("StopRental", "Created payment record with ID: " + thanhToanId);

            if (thanhToanId > 0) {
                try {
                    // Ẩn giao diện chi tiết thuê xe
                    if (scrollViewRentalInfo != null) {
                        scrollViewRentalInfo.setVisibility(View.GONE);
                    }
                    if (recyclerViewLichSuThue != null) {
                        recyclerViewLichSuThue.setVisibility(View.GONE);
                    }
                    if (emptyStateLayout != null) {
                        emptyStateLayout.setVisibility(View.GONE);
                    }

                    // Hiển thị container
                    View rentVehicleContainer = requireView().findViewById(R.id.user_rent_vehicle_container);
                    if (rentVehicleContainer != null) {
                        rentVehicleContainer.setVisibility(View.VISIBLE);
                    }

                    // Hiển thị thông báo thanh toán
                    PaymentNotification paymentNotiFragment = new PaymentNotification();
                    Bundle bundle = new Bundle();
                    bundle.putString("result", "Đã thanh toán");
                    bundle.putString("subMessage", "Vui lòng đợi xác nhận từ nhân viên");
                    bundle.putBoolean("isStopRental", true);
                    bundle.putInt("maThueXe", currentRental.getMaThueXe());
                    bundle.putInt("maXe", selectedXe.getMaXe());
                    paymentNotiFragment.setArguments(bundle);

                    Log.d("Navigation", "Bundle created with maThueXe: " + currentRental.getMaThueXe() + ", maXe: " + selectedXe.getMaXe());

                    // Thực hiện chuyển fragment
                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.user_rent_vehicle_container, paymentNotiFragment)
                            .addToBackStack(null)
                            .commit();

                    Toast.makeText(requireContext(),
                            "Đã tạo yêu cầu dừng thuê xe thành công",
                            Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Log.e("StopRental", "Error in navigation: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.e("StopRental", "Error in handleCashStopRental: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(requireContext(), "Có lỗi xảy ra khi xử lý dừng thuê xe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void hideMainViews() {
        try {
            View mainContainer = requireActivity().findViewById(R.id.user_rent_vehicle_container);
            if (mainContainer != null) {
                TextView tvWelcome = mainContainer.findViewById(R.id.tv_welcome);
                AutoCompleteTextView tvSearch = mainContainer.findViewById(R.id.tv_search);
                AutoCompleteTextView spinnerVehicleType = mainContainer.findViewById(R.id.spinner_vehicle_type);
                AutoCompleteTextView spinnerStatus = mainContainer.findViewById(R.id.spinner_status);
                Button btnClearFilter = mainContainer.findViewById(R.id.btn_clear_filter);
                RecyclerView recyclerView = mainContainer.findViewById(R.id.vehicle_list);

                if (tvWelcome != null) tvWelcome.setVisibility(View.GONE);
                if (tvSearch != null) tvSearch.setVisibility(View.GONE);
                if (spinnerVehicleType != null) spinnerVehicleType.setVisibility(View.GONE);
                if (spinnerStatus != null) spinnerStatus.setVisibility(View.GONE);
                if (btnClearFilter != null) btnClearFilter.setVisibility(View.GONE);
                if (recyclerView != null) recyclerView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handlePayment() {
        String paymentMethod = spinnerPhuongThuc.getText().toString();
        if (paymentMethod.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (paymentMethod.equals("Tiền mặt")) {
                handleCashPayment();
            } else {
                handleZaloPayment();
            }
        } catch (Exception e) {
            Log.e("Payment", "Error in handlePayment: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(requireContext(), "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCashPayment() {
        try {
            // Parse các giá trị tiền tệ
            int thanhTien = parseCurrencyString(tvThanhTien.getText().toString());
            
            // Xác định loại thanh toán dựa vào bundle arguments
            Bundle args = getArguments();
            isDepositPayment = args != null && args.getBoolean("isFromHomepage", false);
            
            // Tính toán số tiền cần thanh toán
            int tienCoc = isDepositPayment ? (int)(thanhTien * 0.3) : parseCurrencyString(tvTienCoc.getText().toString());
            int amountToPay = isDepositPayment ? tienCoc : (thanhTien - tienCoc);

            if (amountToPay <= 0) {
                Toast.makeText(requireContext(), "Số tiền thanh toán không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save payment transaction
            long appTransId = System.currentTimeMillis();
            
            // Tạo đơn thuê xe với trạng thái tương ứng
            ThueXe thueXe = new ThueXe();
            thueXe.setMaND(currentUser.getMaND());
            thueXe.setNgayDat(ThueXeUtility.getCurrentDate());
            // Nếu là thanh toán tiền cọc -> trạng thái "Đang xử lý"
            // Nếu là thanh toán tiền thuê -> trạng thái "Đang thuê"
            thueXe.setTrangThai(isDepositPayment ? TRANG_THAI_DANG_XU_LY : TRANG_THAI_DANG_THUE);

            Log.d("Cash", "Creating rental record for user: " + currentUser.getMaND());
            long maThueXe = thueXeDAO.insert(thueXe);
            Log.d("Cash", "Rental record created with maThueXe: " + maThueXe);

            if (maThueXe > 0) {
                ChiTietThueXe chiTiet = new ChiTietThueXe();
                chiTiet.setMaThueXe((int) maThueXe);
                chiTiet.setMaXe(selectedXe.getMaXe());
                chiTiet.setNgayBatDauDK(tvNgayBatDauDK.getText().toString());
                chiTiet.setNgayKetThucDK(tvNgayKetThucDK.getText().toString());
                chiTiet.setNgayBatDauTT(tvNgayBatDauTT.getText().toString());
                chiTiet.setNgayKetThucTT(tvNgayHienTai.getText().toString());
                chiTiet.setTienCoc(tienCoc);
                chiTiet.setThanhTien(thanhTien);
                chiTiet.setGhiChu("Thanh toán " + (isDepositPayment ? "tiền cọc" : "tiền thuê") + " qua tiền mặt");

                Log.d("Cash", "Creating rental details for maThueXe: " + maThueXe);
                long result = chiTietThueXeDAO.insert(chiTiet);
                Log.d("Cash", "Rental details created with result: " + result);

                if (result > 0) {
                    // Cập nhật trạng thái xe thành "Đang thuê" trong cả 2 trường hợp
                    Log.d("Cash", "Updating vehicle status for maXe: " + selectedXe.getMaXe());
                    xeDAO.updateStatus(selectedXe.getMaXe(), 1); // 1 = Đang thuê

                    // Lưu thông tin thanh toán với trạng thái "Đang xử lý"
                    savePaymentTransaction(
                        String.valueOf(amountToPay), 
                        "Tiền mặt",
                        "Đã thanh toán",
                        String.valueOf(appTransId),
                        String.valueOf(appTransId)
                    );

                    Log.d("Cash", "Updating payment record with maThueXe: " + maThueXe);
                    ThanhToan payment = thanhToanDAO.getByMaGiaoDich(String.valueOf(appTransId));
                    if (payment != null) {
                        payment.setMaThueXe((int) maThueXe);
                        payment.setTrangThai(THANH_TOAN_DA_THANH_TOAN); // Cập nhật trạng thái thành "Đã thanh toán"
                        payment.setNgayThanhCong(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                        int updateResult = thanhToanDAO.update(payment);
                        Log.d("Cash", "Payment record updated with result: " + updateResult);
                    } else {
                        Log.e("Cash", "Payment record not found for appTransId: " + appTransId);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Cash", "Error in handleCashPayment: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(requireContext(), "Có lỗi xảy ra khi xử lý thanh toán: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleZaloPayment() {
        try {
            // Parse các giá trị tiền tệ
            int thanhTien = parseCurrencyString(tvThanhTien.getText().toString());
            
            // Xác định loại thanh toán
            Bundle args = getArguments();
            isDepositPayment = args != null && args.getBoolean("isFromHomepage", false);
            
            int tienCoc = isDepositPayment ? (int)(thanhTien * 0.3) : parseCurrencyString(tvTienCoc.getText().toString());
            int finalAmount = isDepositPayment ? tienCoc : (thanhTien - tienCoc);

            Log.d("ZaloPay", "Parsed thanhTien: " + thanhTien);
            Log.d("ZaloPay", "Parsed tienCoc: " + tienCoc);
            Log.d("ZaloPay", "Final amount: " + finalAmount);

            if (thanhTien <= 0) {
                Toast.makeText(requireContext(), "Số tiền thanh toán không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (tienCoc < 0) {
                Toast.makeText(requireContext(), "Số tiền cọc không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (finalAmount < 0) {
                Toast.makeText(requireContext(), "Số tiền thanh toán không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            CreateOrder orderApi = new CreateOrder();

            Log.d("ZaloPay", "Amount to pay: " + finalAmount);
            Log.d("ZaloPay", "APP_ID: " + AppInfo.APP_ID);
            Log.d("ZaloPay", "MAC_KEY: " + AppInfo.MAC_KEY);

            JSONObject data = orderApi.createOrder(String.valueOf(finalAmount));
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
                                String appTransId = data.getString("apptransid");
                                Log.d("ZaloPay", "Token: " + token);
                                
                                // Lưu payment record với trạng thái "Processing" trước khi gọi ZaloPay
                                savePaymentTransaction(
                                    String.valueOf(finalAmount),
                                    "ZaloPay",
                                    "Processing",
                                    null,
                                    appTransId
                                );

                                ZaloPaySDK.getInstance().payOrder(requireActivity(), token, "demozpdk://app", new PayOrderListener() {
                                    @Override
                                    public void onPaymentSucceeded(String transactionId, String transToken, String appTransId) {
                                        Log.d("ZaloPay", "Payment succeeded - transactionId: " + transactionId + ", appTransId: " + appTransId);

                                        // Cập nhật payment record với trạng thái "Đã thanh toán"
                                        ThanhToan payment = thanhToanDAO.getByMaGiaoDich(appTransId);
                                        if (payment != null) {
                                            payment.setTrangThai(THANH_TOAN_DA_THANH_TOAN);
                                            payment.setMaGiaoDich(transactionId);
                                            payment.setNgayThanhCong(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                                            int updateResult = thanhToanDAO.update(payment);
                                            Log.d("ZaloPay", "Payment status updated with result: " + updateResult);
                                        } else {
                                            Log.e("ZaloPay", "Payment record not found for appTransId: " + appTransId);
                                            // Nếu không tìm thấy payment record, tạo mới
                                            savePaymentTransaction(
                                                String.valueOf(finalAmount),
                                                "ZaloPay",
                                                String.valueOf(THANH_TOAN_DA_THANH_TOAN),
                                                transactionId,
                                                appTransId
                                            );
                                        }

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
                                            chiTiet.setNgayBatDauDK(tvNgayBatDauDK.getText().toString());
                                            chiTiet.setNgayKetThucDK(tvNgayKetThucDK.getText().toString());
                                            chiTiet.setNgayBatDauTT(tvNgayBatDauDK.getText().toString());
                                            chiTiet.setNgayKetThucTT(tvNgayKetThucDK.getText().toString());
                                            chiTiet.setTienCoc(tienCoc);
                                            chiTiet.setThanhTien(thanhTien);
                                            chiTiet.setGhiChu("Thanh toán " + (isDepositPayment ? "tiền cọc" : "tiền thuê") + " qua ZaloPay");

                                            long result = chiTietThueXeDAO.insert(chiTiet);
                                            Log.d("ZaloPay", "Rental details created with result: " + result);

                                            if (result > 0) {
                                                Log.d("ZaloPay", "Updating vehicle status for maXe: " + selectedXe.getMaXe());
                                                xeDAO.updateStatus(selectedXe.getMaXe(), 1);

                                                // Cập nhật mã thuê xe cho payment record
                                                payment = thanhToanDAO.getByMaGiaoDich(appTransId);
                                                if (payment != null) {
                                                    payment.setMaThueXe((int) maThueXe);
                                                    payment.setTrangThai(THANH_TOAN_DA_THANH_TOAN); // Cập nhật trạng thái thành "Đã thanh toán"
                                                    payment.setNgayThanhCong(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                                                    int updateResult = thanhToanDAO.update(payment);
                                                    Log.d("ZaloPay", "Payment record updated with result: " + updateResult);
                                                }

                                                // Hiển thị thông báo thành công
                                                requireActivity().runOnUiThread(() -> {
                                                    PaymentNotification paymentNotiFragment = new PaymentNotification();
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("result", "Đã thanh toán");
                                                    bundle.putString("transactionId", transactionId);
                                                    bundle.putString("appTransId", appTransId);
                                                    bundle.putString("subMessage", isDepositPayment ? 
                                                        "Thanh toán tiền cọc thành công" : 
                                                        "Thanh toán tiền thuê thành công");
                                                    paymentNotiFragment.setArguments(bundle);

                                                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                                                    transaction.replace(R.id.user_rent_vehicle_container, paymentNotiFragment);
                                                    transaction.commit();
                                                });
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

                                        // Cập nhật payment record với trạng thái "Chưa thanh toán"
                                        ThanhToan payment = thanhToanDAO.getByMaGiaoDich(appTransId);
                                        if (payment != null) {
                                            payment.setTrangThai(THANH_TOAN_CHUA_THANH_TOAN);
                                            thanhToanDAO.update(payment);
                                        }

                                        PaymentNotification paymentNotiFragment = new PaymentNotification();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("result", "Chưa thanh toán");
                                        paymentNotiFragment.setArguments(bundle);

                                        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                                        transaction.replace(R.id.user_rent_vehicle_container, paymentNotiFragment);
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                    }

                                    @Override
                                    public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransId) {
                                        Log.d("ZaloPay", "Payment error - " + zaloPayError.toString());

                                        // Cập nhật payment record với trạng thái "Thất bại"
                                        ThanhToan payment = thanhToanDAO.getByMaGiaoDich(appTransId);
                                        if (payment != null) {
                                            payment.setTrangThai(THANH_TOAN_THAT_BAI);
                                            thanhToanDAO.update(payment);
                                        }

                                        PaymentNotification paymentNotiFragment = new PaymentNotification();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("result", "Thất bại");
                                        paymentNotiFragment.setArguments(bundle);

                                        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
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
            thanhToan.setGhiChu(tvGhiChu.getText().toString().trim());

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

    // Thêm phương thức để cập nhật trạng thái khi thanh toán hoàn tất
    public void updateRentalStatusAfterPayment(int maThueXe, int maXe) {
        try {
            // Cập nhật trạng thái thuê xe thành "Đã trả"
            ThueXe thueXe = thueXeDAO.getById(maThueXe);
            if (thueXe != null) {
                thueXe.setTrangThai(TRANG_THAI_DA_TRA);
                int updateRentalResult = thueXeDAO.update(thueXe);
                Log.d("UpdateStatus", "Updated rental status result: " + updateRentalResult);
            }

            // Cập nhật trạng thái xe thành "Hiện còn"
            int updateVehicleResult = xeDAO.updateStatus(maXe, 0); // 0 = Hiện còn
            Log.d("UpdateStatus", "Updated vehicle status result: " + updateVehicleResult);

            Toast.makeText(requireContext(), "Đã cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("UpdateStatus", "Error updating status: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(requireContext(), "Có lỗi xảy ra khi cập nhật trạng thái: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void showRentalInfo() {
        // Ẩn recyclerView lịch sử thuê xe nếu đang hiển thị
        if (recyclerViewLichSuThue != null) {
            recyclerViewLichSuThue.setVisibility(View.GONE);
        }

        // Ẩn layout trạng thái trống nếu đang hiển thị
        if (emptyStateLayout != null) {
            emptyStateLayout.setVisibility(View.GONE);
        }

        // Hiển thị thông tin thuê xe
        if (scrollViewRentalInfo != null) {
            scrollViewRentalInfo.setVisibility(View.VISIBLE);
        }

        // Tải lại thông tin thuê xe mới nhất
        loadCurrentRental();
    }

    private void loadCurrentRental() {
        try {
            // Lấy thông tin thuê xe mới nhất của người dùng hiện tại
            NguoiDung currentUser = nguoiDungUtility.getCurrentUser();
            if (currentUser != null) {
                ThueXe latestRental = thueXeDAO.getLatestRentalByUser(currentUser.getMaND());
                if (latestRental != null) {
                    // Lấy chi tiết thuê xe
                    ChiTietThueXe chiTiet = chiTietThueXeDAO.getSingleByMaThueXe(latestRental.getMaThueXe());
                    if (chiTiet != null) {
                        // Lấy thông tin xe
                        selectedXe = xeDAO.getById(chiTiet.getMaXe());
                        if (selectedXe != null) {
                            // Cập nhật UI với thông tin mới
                            updateRentalInfoUI(latestRental, chiTiet, selectedXe);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("RentVehicle", "Error loading current rental: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressLint("DefaultLocale")
    private void updateRentalInfoUI(ThueXe thueXe, ChiTietThueXe chiTiet, Xe xe) {
        try {
            // Cập nhật thông tin xe
            if (tvTenXe != null) tvTenXe.setText(xe.getTenXe());
            if (tvBienSo != null) tvBienSo.setText(xe.getBienSo());
            if (tvLoaiXe != null) tvLoaiXe.setText(xe.getLoaiXe());

            // Cập nhật thông tin thuê xe
            if (tvNgayDat != null) tvNgayDat.setText(thueXe.getNgayDat());
            if (tvNgayBatDauDK != null) tvNgayBatDauDK.setText(chiTiet.getNgayBatDauDK());
            if (tvNgayKetThucDK != null) tvNgayKetThucDK.setText(chiTiet.getNgayKetThucDK());

            // Cập nhật thông tin thanh toán
            if (tvTienCoc != null)
                tvTienCoc.setText(String.format("%,d VNĐ", chiTiet.getTienCoc()));
            if (tvThanhTien != null)
                tvThanhTien.setText(String.format("%,d VNĐ", chiTiet.getThanhTien()));

            // Cập nhật trạng thái
            if (tvTrangThaiThanhToan != null) {
                String[] trangThaiArray = getResources().getStringArray(R.array.trang_thai_thue_xe);
                String trangThai = trangThaiArray[thueXe.getTrangThai()];
                tvTrangThaiThanhToan.setText(trangThai);
            }
        } catch (Exception e) {
            Log.e("RentVehicle", "Error updating rental info UI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void refreshRentalInfo() {
        try {
            // Clear current data
            if (currentUser != null) {
                // Refresh rental info
                loadLatestRentalInfo();
                
                // Refresh history if showing
                if (recyclerViewLichSuThue != null && recyclerViewLichSuThue.getVisibility() == View.VISIBLE) {
                    loadRentHistory(currentUser.getMaND());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing rental info: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleCancelRental() {
        try {
            if (currentRentalId == -1 || selectedXe == null) {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin thuê xe", Toast.LENGTH_SHORT).show();
                return;
            }

            // Hiển thị dialog xác nhận
            new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận hủy")
                .setMessage("Bạn có chắc chắn muốn hủy đơn thuê xe này không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    try {
                        // 1. Cập nhật trạng thái đơn thuê xe thành Hủy
                        ThueXe currentRental = thueXeDAO.getById(currentRentalId);
                        if (currentRental != null) {
                            Log.d(TAG, "Updating rental status to CANCELLED for rental ID: " + currentRentalId);
                            currentRental.setTrangThai(TRANG_THAI_HUY);
                            int rentalUpdateResult = thueXeDAO.update(currentRental);
                            Log.d(TAG, "Rental status update result: " + rentalUpdateResult);
                            
                            if (rentalUpdateResult <= 0) {
                                Log.e(TAG, "Failed to update rental status");
                                Toast.makeText(requireContext(), "Không thể cập nhật trạng thái đơn thuê xe", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        // 2. Cập nhật trạng thái xe thành Hiện còn
                        Log.d(TAG, "Updating vehicle status to AVAILABLE for vehicle ID: " + selectedXe.getMaXe());
                        int vehicleUpdateResult = xeDAO.updateStatus(selectedXe.getMaXe(), 0); // 0 = Hiện còn
                        Log.d(TAG, "Vehicle status update result: " + vehicleUpdateResult);
                        
                        if (vehicleUpdateResult <= 0) {
                            Log.e(TAG, "Failed to update vehicle status");
                            Toast.makeText(requireContext(), "Không thể cập nhật trạng thái xe", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 3. Cập nhật trạng thái đơn thanh toán thành Thất bại
                        ThanhToan thanhToan = thanhToanDAO.getByMaThueXe(currentRentalId);
                        if (thanhToan != null) {
                            Log.d(TAG, "Updating payment status to FAILED for rental ID: " + currentRentalId);
                            thanhToan.setTrangThai(THANH_TOAN_THAT_BAI);
                            int paymentUpdateResult = thanhToanDAO.update(thanhToan);
                            Log.d(TAG, "Payment status update result: " + paymentUpdateResult);
                        }

                        // 4. Hiển thị view không có đơn thuê xe và refresh dữ liệu
                        showEmptyView();
                        loadRentHistory(currentUser.getMaND());
                        Toast.makeText(requireContext(), "Đã hủy đơn thuê xe thành công", Toast.LENGTH_SHORT).show();

                        // 5. Refresh lại view
                        showEmptyView();
                        loadRentHistory(currentUser.getMaND());
                        refreshRentalInfo();
                    } catch (Exception e) {
                        Log.e(TAG, "Error in cancel rental process: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Có lỗi xảy ra khi hủy đơn thuê xe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Không", null)
                .show();

        } catch (Exception e) {
            Log.e(TAG, "Error showing cancel dialog: " + e.getMessage());
            Toast.makeText(requireContext(), "Có lỗi xảy ra khi hủy đơn thuê xe", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleStartRental() {
        try {
            if (currentRentalId == -1) {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin thuê xe", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật ngày bắt đầu thực tế
            ChiTietThueXe chiTiet = chiTietThueXeDAO.getSingleByMaThueXe(currentRentalId);
            if (chiTiet != null) {
                String ngayBatDauTT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                chiTiet.setNgayBatDauTT(ngayBatDauTT);
                chiTietThueXeDAO.update(chiTiet);
                
                // Cập nhật UI
                tvNgayBatDauTT.setText(ngayBatDauTT);
                Toast.makeText(requireContext(), "Đã cập nhật thời gian bắt đầu thuê", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error starting rental: " + e.getMessage());
            Toast.makeText(requireContext(), "Có lỗi xảy ra khi bắt đầu thuê xe", Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateAndShowPayment(ChiTietThueXe chiTiet) {
        try {
            // Parse ngày bắt đầu và kết thúc thực tế
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date startDate = sdf.parse(chiTiet.getNgayBatDauTT());
            Date endDate = sdf.parse(chiTiet.getNgayKetThucTT());

            if (startDate != null && endDate != null) {
                // Tính số ngày thuê (làm tròn lên)
                long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
                int days = (int) Math.ceil(diffInMillies / (1000.0 * 60 * 60 * 24));
                if (days == 0) days = 1; // Minimum 1 day

                // Tính tổng tiền
                int giaThueNgay = selectedXe.getGiaThue();
                totalAmount = giaThueNgay * days;

                // Cập nhật thành tiền trong chi tiết thuê xe
                chiTiet.setThanhTien(totalAmount);
                chiTietThueXeDAO.update(chiTiet);

                // Cập nhật UI
                tvThanhTien.setText(String.format(Locale.getDefault(), "%,d VNĐ", totalAmount));
                int tienCoc = chiTiet.getTienCoc();
                int thucTra = totalAmount - tienCoc;
                tvThucTra.setText(String.format(Locale.getDefault(), "%,d VNĐ", thucTra));

                // Hiển thị dialog xác nhận thanh toán
                showPaymentConfirmationDialog(thucTra);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error calculating payment: " + e.getMessage());
            Toast.makeText(requireContext(), "Có lỗi xảy ra khi tính tiền thuê xe", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPaymentConfirmationDialog(int amount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Xác nhận thanh toán")
               .setMessage(String.format(Locale.getDefault(), "Số tiền cần thanh toán: %,d VNĐ\nBạn có muốn thanh toán ngay không?", amount))
               .setPositiveButton("Thanh toán ngay", (dialog, which) -> handleCashStopRental())
               .setNegativeButton("Để sau", (dialog, which) -> dialog.dismiss())
               .show();
    }

    private boolean validateRentalData(ThueXe rental, ChiTietThueXe details) {
        return rental != null && details != null;
        // Thêm các điều kiện kiểm tra khác
    }


    private void handleError(String operation, Exception e) {
        Log.e(TAG, "Error in " + operation + ": " + e.getMessage());
        Toast.makeText(requireContext(),
                "Có lỗi xảy ra khi " + operation + ": " + e.getMessage(),
                Toast.LENGTH_SHORT).show();
    }
}
