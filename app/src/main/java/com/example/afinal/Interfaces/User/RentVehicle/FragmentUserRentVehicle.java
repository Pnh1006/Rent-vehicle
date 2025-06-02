package com.example.afinal.Interfaces.User.RentVehicle;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
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

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

/**
 * @noinspection CallToPrintStackTrace
 */
public class FragmentUserRentVehicle extends Fragment {
    private TextView tvHoTen, tvCCCD, tvSDT, tvNgayDat, tvTenXe, tvTienCoc, tvNgayBatDauDK, tvNgayKetThucDK, tvNgayBatDauTT, tvNgayHienTai, tvThanhTien, tvThucTra,
            tvGhiChu, tvTrangThaiThanhToan, tvBienSo, tvLoaiXe, tvGiaThue;
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
        Xe xe = args != null ? (Xe) args.getSerializable("xe") : null;

        // Reset isFromHomepage flag to prevent showing start screen on subsequent resumes
        if (args != null) {
            args.putBoolean("isFromHomepage", false);
        }

        if (isFromHomepage && xe != null) {
            // Show the start screen with the selected vehicle
            showRentVehicleStart(xe);
        } else if (currentUser != null) {
            // Show the normal rental view or history
            showEmptyView();
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

            btnDungThue.setOnClickListener(v -> {
                if (isViewingHistory) {
                    showEmptyView();
                    loadRentHistory(nguoiDungUtility.getCurrentUser().getMaND());
                } else {
                    handleStopRental();
                }
            });

            btnBatDauThue.setOnClickListener(v -> {
                try {
                    if (currentRentalId == -1) {
                        Toast.makeText(requireContext(), "Không tìm thấy thông tin thuê xe", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Lấy thông tin thuê xe và chi tiết
                    ThueXe currentRental = thueXeDAO.getById(currentRentalId);
                    ChiTietThueXe chiTiet = chiTietThueXeDAO.getSingleByMaThueXe(currentRentalId);

                    if (currentRental == null || chiTiet == null) {
                        Toast.makeText(requireContext(), "Không tìm thấy thông tin thuê xe", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Kiểm tra trạng thái thanh toán
                    ThanhToan thanhToan = thanhToanDAO.getByMaThueXe(currentRentalId);
                    if (thanhToan == null || thanhToan.getTrangThai() != 0) { // 0 = Đã thanh toán
                        Toast.makeText(requireContext(), "Vui lòng thanh toán tiền cọc trước khi bắt đầu thuê", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Cập nhật ngày bắt đầu thực tế
                    String currentDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
                    chiTiet.setNgayBatDauTT(currentDateTime);
                    long updateResult = chiTietThueXeDAO.update(chiTiet);

                    if (updateResult > 0) {
                        // Cập nhật trạng thái thuê xe thành "Đang thuê"
                        currentRental.setTrangThai(1); // 1 = Đang thuê
                        thueXeDAO.update(currentRental);

                        Toast.makeText(requireContext(), "Bắt đầu thuê xe thành công", Toast.LENGTH_SHORT).show();
                        loadLatestRentalInfo();
                    } else {
                        Toast.makeText(requireContext(), "Có lỗi xảy ra khi bắt đầu thuê xe", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("RentVehicle", "Error in btnBatDauThue click: " + e.getMessage());
                    Toast.makeText(requireContext(), "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            btnHuyThue.setOnClickListener(v -> {
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

                    // Kiểm tra xem có thể hủy không
                    if (!ThueXeUtility.canCancel(currentRental.getTrangThai())) {
                        Toast.makeText(requireContext(), "Không thể hủy đơn thuê xe ở trạng thái này", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Cập nhật trạng thái thuê xe thành "Đã hủy"
                    currentRental.setTrangThai(3); // 3 = Đã hủy
                    int updateResult = thueXeDAO.update(currentRental);

                    if (updateResult > 0) {
                        // Cập nhật trạng thái xe thành "Hiện còn"
                        if (selectedXe != null) {
                            xeDAO.updateStatus(selectedXe.getMaXe(), 0);
                        }

                        Toast.makeText(requireContext(), "Đã hủy thuê xe thành công", Toast.LENGTH_SHORT).show();
                        showEmptyView();
                    } else {
                        Toast.makeText(requireContext(), "Có lỗi xảy ra khi hủy thuê xe", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("RentVehicle", "Error in btnHuyThue click: " + e.getMessage());
                    Toast.makeText(requireContext(), "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
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
            dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
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

            ThueXe latestRental = thueXeDAO.getLatestRentalByUser(currentUser.getMaND());
            Log.d("RentVehicle", "Latest rental: " + (latestRental != null ? "Found" : "Not found"));

            if (latestRental != null && (latestRental.getTrangThai() == 0 || latestRental.getTrangThai() == 1)) {
                // Chỉ hiển thị thông tin chi tiết nếu đơn đang thuê (0) hoặc đang xử lý (1)
                Log.d("RentVehicle", "=== THÔNG TIN THUÊ XE ===");
                Log.d("RentVehicle", "Mã thuê xe: " + latestRental.getMaThueXe());
                Log.d("RentVehicle", "Ngày đặt: " + latestRental.getNgayDat());
                Log.d("RentVehicle", "Trạng thái: " + latestRental.getTrangThai());

                ChiTietThueXe chiTiet = chiTietThueXeDAO.getSingleByMaThueXe(latestRental.getMaThueXe());
                if (chiTiet != null) {
                    Xe xe = xeDAO.getById(chiTiet.getMaXe());
                    if (xe != null) {
                        Log.d("RentVehicle", "=== THÔNG TIN XE ===");
                        Log.d("RentVehicle", "- Tên xe: " + xe.getTenXe());
                        Log.d("RentVehicle", "- Biển số: " + xe.getBienSo());
                        Log.d("RentVehicle", "- Loại xe: " + xe.getLoaiXe());
                        Log.d("RentVehicle", "- Giá thuê: " + xe.getGiaThue());

                        ThanhToan thanhToan = thanhToanDAO.getByMaThueXe(latestRental.getMaThueXe());
                        Log.d("RentVehicle", "Payment info: " + (thanhToan != null ? "Found" : "Not found"));

                        displayRentalInfo(latestRental, chiTiet, xe, currentUser, thanhToan);
                        showRentalInfoView();
                        return;
                    }
                }
            }

            // Nếu không có đơn đang thuê hoặc đang xử lý, hiển thị lịch sử
            showEmptyView();
            loadRentHistory(currentUser.getMaND());

        } catch (Exception e) {
            Log.e("RentVehicle", "Lỗi: " + e.getMessage());
            e.printStackTrace();
            showEmptyView();
            loadRentHistory(currentUser.getMaND());
        }
    }

    private void showRentalInfoView() {
        if (getView() == null) return;

        getChildFragmentManager().popBackStackImmediate(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);

        View scrollViewRentalInfo = getView().findViewById(R.id.scrollViewRentalInfo);
        View emptyView = getView().findViewById(R.id.emptyView);
        View rentVehicleContainer = getView().findViewById(R.id.user_rent_vehicle_container);
        View recyclerViewLichSuThue = getView().findViewById(R.id.recyclerViewLichSuThue);

        if (scrollViewRentalInfo != null) scrollViewRentalInfo.setVisibility(View.GONE);
        if (emptyView != null) emptyView.setVisibility(View.GONE);
        if (rentVehicleContainer != null) rentVehicleContainer.setVisibility(View.GONE);
        if (recyclerViewLichSuThue != null) recyclerViewLichSuThue.setVisibility(View.GONE);

        Fragment existingFragment = getChildFragmentManager().findFragmentById(R.id.user_rent_vehicle_container);
        if (existingFragment != null) {
            getChildFragmentManager().beginTransaction()
                    .remove(existingFragment)
                    .commitNow(); // Use commitNow to ensure immediate execution
        }

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
        RecyclerView recyclerViewLichSuThue = getView().findViewById(R.id.recyclerViewLichSuThue);

        if (scrollViewRentalInfo != null) scrollViewRentalInfo.setVisibility(View.GONE);
        if (emptyView != null) emptyView.setVisibility(View.VISIBLE);
        if (rentVehicleContainer != null) rentVehicleContainer.setVisibility(View.GONE);
        if (recyclerViewLichSuThue != null) recyclerViewLichSuThue.setVisibility(View.VISIBLE);

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
            tvNgayHienTai.setText(dateFormat.format(new Date()));
            int thanhTien = details.getThanhTien();
            tienCoc = details.getTienCoc();
            int thucTra = thanhTien - tienCoc;
            totalAmount = thanhTien;
            tvThanhTien.setText(String.format("%,d VNĐ", thanhTien));
            tvThucTra.setText(String.format("%,d VNĐ", thucTra));
            String[] paymentMethods = getResources().getStringArray(R.array.phuong_thuc_thanh_toan);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_dropdown_item_1line, paymentMethods);
            spinnerPhuongThuc.setAdapter(adapter);

            // Ẩn tất cả các button trước
            btnBatDauThue.setVisibility(View.GONE);
            btnDungThue.setVisibility(View.GONE);
            btnHuyThue.setVisibility(View.GONE);

            // Hiển thị các button dựa vào trạng thái thuê xe và thanh toán
            if (rental.getTrangThai() == 3 || rental.getTrangThai() == 2) {
                // Đã hủy hoặc đã trả - không hiển thị button nào
                return;
            }

            if (thanhToan != null) {
                spinnerPhuongThuc.setText(thanhToan.getPhuongThuc(), false);
                tvTrangThaiThanhToan.setText(getPaymentStatusText(thanhToan.getTrangThai()));
                tvGhiChu.setText(thanhToan.getGhiChu());

                // Kiểm tra trạng thái thanh toán và ngày thuê
                if (details.getNgayBatDauTT().isEmpty()) {
                    // Chưa bắt đầu thuê
                    if (thanhToan.getTrangThai() == 0) { // Đã thanh toán tiền cọc
                        btnBatDauThue.setVisibility(View.VISIBLE);
                        btnHuyThue.setVisibility(View.VISIBLE);
                    } else if (thanhToan.getTrangThai() == 2) { // Đang xử lý thanh toán
                        btnHuyThue.setVisibility(View.VISIBLE);
                    }
                } else if (details.getNgayKetThucTT().isEmpty()) {
                    // Đang thuê
                    btnDungThue.setVisibility(View.VISIBLE);
                }
            } else {
                // Chưa có thông tin thanh toán
                spinnerPhuongThuc.setText("", false);
                tvTrangThaiThanhToan.setText("Chưa có thông tin");
                tvGhiChu.setText("");
                
                // Cho phép hủy nếu chưa có thông tin thanh toán
                btnHuyThue.setVisibility(View.VISIBLE);
            }

            // Cập nhật text cho các button
            btnBatDauThue.setText("Bắt đầu thuê");
            btnDungThue.setText("Dừng thuê xe");
            btnHuyThue.setText("Hủy thuê xe");

        } catch (Exception e) {
            Log.e("RentVehicle", "Error displaying rental info: " + e.getMessage());
            e.printStackTrace();
            showError("Có lỗi xảy ra khi hiển thị thông tin thuê xe");
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
                List<ThueXe> filteredRentHistory = new ArrayList<>();

                for (ThueXe thueXe : rentHistory) {
                    // Lấy tất cả đơn thuê xe, không lọc theo trạng thái
                    List<ChiTietThueXe> details = chiTietDAO.getByMaThueXe(thueXe.getMaThueXe());
                    if (details != null && !details.isEmpty()) {
                        ChiTietThueXe chiTiet = details.get(0);
                        Xe xe = xeDAO.getById(chiTiet.getMaXe());
                        if (xe != null) {
                            filteredRentHistory.add(thueXe);
                            chiTietList.add(chiTiet);
                            xeList.add(xe);
                        }
                    }
                }

                if (!filteredRentHistory.isEmpty()) {
                    rentHistoryAdapter.updateData(filteredRentHistory, chiTietList, xeList);
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

    private int parseCurrencyString(String currencyStr) {
        try {
            if (currencyStr == null || currencyStr.trim().isEmpty()) {
                return 0;
            }
            String cleanStr = currencyStr.replaceAll("[^0-9.,\\s]", "").trim();

            cleanStr = cleanStr.replaceAll("[\\s.]", "");

            cleanStr = cleanStr.replace(",", ".");

            if (cleanStr.contains(".")) {
                cleanStr = cleanStr.substring(0, cleanStr.indexOf("."));
            }

            return Integer.parseInt(cleanStr);
        } catch (Exception e) {
            Log.e("Currency", "Error parsing currency string: " + currencyStr, e);
            return 0;
        }
    }

    private void handleStopRental() {
        try {
            if (currentRentalId == -1) {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin thuê xe", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy thông tin thuê xe hiện tại
            ThueXe currentRental = thueXeDAO.getById(currentRentalId);
            ChiTietThueXe chiTiet = chiTietThueXeDAO.getSingleByMaThueXe(currentRentalId);
            if (currentRental == null || chiTiet == null || selectedXe == null) {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin thuê xe", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật ngày kết thúc thực tế
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String currentDateTime = dateTimeFormat.format(new Date());
            chiTiet.setNgayKetThucTT(currentDateTime);

            // Tính số ngày thuê thực tế
            try {
                Date startDate = dateTimeFormat.parse(chiTiet.getNgayBatDauTT());
                Date endDate = dateTimeFormat.parse(currentDateTime);
                if (startDate != null && endDate != null) {
                    long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
                    long diffInDays = diffInMillies / (24 * 60 * 60 * 1000);
                    // Nếu có phần dư, tính thêm 1 ngày
                    if (diffInMillies % (24 * 60 * 60 * 1000) > 0) {
                        diffInDays++;
                    }
                    
                    // Tính thành tiền mới
                    int thanhTien = (int) (diffInDays * selectedXe.getGiaThue());
                    int tienCoc = chiTiet.getTienCoc();
                    int thucTra = thanhTien - tienCoc;

                    if (thucTra <= 0) {
                        // Hiển thị dialog xác nhận hủy đơn
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
                        builder.setTitle("Xác nhận");
                        builder.setMessage("Số tiền thực trả nhỏ hơn số tiền bạn đã cọc. Nếu hủy thuê xe bạn sẽ mất cọc. Bạn có muốn hủy thuê xe không?");
                        builder.setPositiveButton("Có", (dialog, which) -> {
                            // Cập nhật trạng thái đơn thuê xe thành "Đã trả"
                            currentRental.setTrangThai(2);
                            thueXeDAO.update(currentRental);

                            // Cập nhật trạng thái xe thành "Hiện còn"
                            selectedXe.setTrangThai(0);
                            xeDAO.updateStatus(selectedXe.getMaXe(), 0);

                            // Cập nhật chi tiết thuê xe
                            chiTiet.setThanhTien(0);
                            chiTietThueXeDAO.update(chiTiet);

                            Toast.makeText(requireContext(), "Đã hủy đơn thuê xe thành công", Toast.LENGTH_SHORT).show();
                            showEmptyView();
                            loadRentHistory(currentUser.getMaND());
                        });
                        builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());
                        builder.show();
                    } else {
                        // Cập nhật thành tiền mới
                        chiTiet.setThanhTien(thanhTien);
                        chiTietThueXeDAO.update(chiTiet);

                        // Hiển thị thông tin thanh toán mới
                        tvThanhTien.setText(String.format("%,d VNĐ", thanhTien));
                        tvThucTra.setText(String.format("%,d VNĐ", thucTra));

                        // Cập nhật trạng thái đơn thuê xe thành "Đang xử lý"
                        currentRental.setTrangThai(1);
                        thueXeDAO.update(currentRental);

                        // Xử lý thanh toán dựa trên phương thức đã chọn
                        String paymentMethod = spinnerPhuongThuc.getText().toString();
                        if (paymentMethod.equals("Tiền mặt")) {
                            String appTransId = String.valueOf(System.currentTimeMillis());
                            savePaymentTransaction(String.valueOf(thucTra), "Tiền mặt", "Đang xử lý", appTransId, appTransId);
                            showPaymentNotification("Đang xử lý", "Vui lòng đợi xác nhận từ nhân viên", true);
                        } else if (paymentMethod.equals("ZaloPay")) {
                            handleZaloPayment(thucTra);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("StopRental", "Error calculating rental duration: " + e.getMessage());
                Toast.makeText(requireContext(), "Có lỗi xảy ra khi tính thời gian thuê", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("StopRental", "Error in handleStopRental: " + e.getMessage());
            Toast.makeText(requireContext(), "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleZaloPayment(int amount) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            ZaloPaySDK.init(553, Environment.SANDBOX);

            CreateOrder orderApi = new CreateOrder();
            JSONObject data = orderApi.createOrder(String.valueOf(amount));
            
            if (data != null && data.has("returncode") && data.getInt("returncode") == 1) {
                String token = data.getString("zptranstoken");
                ZaloPaySDK.getInstance().payOrder(requireActivity(), token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String transactionId, String transToken, String appTransId) {
                        savePaymentTransaction(String.valueOf(amount), "ZaloPay", "Đã thanh toán", transactionId, appTransId);
                        updateRentalStatusAfterPayment(currentRentalId, selectedXe.getMaXe());
                        showPaymentNotification("Đã thanh toán", "Thanh toán thành công qua ZaloPay", true);
                    }

                    @Override
                    public void onPaymentCanceled(String zpTransToken, String appTransId) {
                        savePaymentTransaction(String.valueOf(amount), "ZaloPay", "Đã hủy", null, appTransId);
                        showPaymentNotification("Đã hủy", "Giao dịch đã bị hủy", false);
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransId) {
                        savePaymentTransaction(String.valueOf(amount), "ZaloPay", "Thất bại", null, appTransId);
                        showPaymentNotification("Thất bại", "Thanh toán thất bại: " + zaloPayError.toString(), false);
                    }
                });
            } else {
                Toast.makeText(requireContext(), "Không thể tạo giao dịch ZaloPay", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("ZaloPay", "Error in handleZaloPayment: " + e.getMessage());
            Toast.makeText(requireContext(), "Có lỗi xảy ra khi thanh toán: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            int thanhTien = parseCurrencyString(tvThanhTien.getText().toString());
            int tienCoc = parseCurrencyString(tvTienCoc.getText().toString());

            Log.d("Payment", "Parsed thanhTien: " + thanhTien);
            Log.d("Payment", "Parsed tienCoc: " + tienCoc);

            if (thanhTien <= 0) {
                Toast.makeText(requireContext(), "Số tiền thanh toán không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (tienCoc < 0) {
                Toast.makeText(requireContext(), "Số tiền cọc không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            int finalAmount = thanhTien - tienCoc;
            if (finalAmount < 0) {
                Toast.makeText(requireContext(), "Số tiền thanh toán không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            long appTransId = System.currentTimeMillis();
            savePaymentTransaction(String.valueOf(finalAmount), "Tiền mặt", "Đang xử lý", String.valueOf(appTransId), String.valueOf(appTransId));

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
                chiTiet.setNgayBatDauDK(tvNgayBatDauDK.getText().toString());
                chiTiet.setNgayKetThucDK(tvNgayKetThucDK.getText().toString());
                chiTiet.setNgayBatDauTT(tvNgayBatDauTT.getText().toString());
                chiTiet.setNgayKetThucTT(tvNgayHienTai.getText().toString());
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

                    // Chuyển sang màn hình thông báo
                    Log.d("Navigation", "Preparing to show payment notification");
                    PaymentNotification paymentNotiFragment = new PaymentNotification();
                    Bundle bundle = new Bundle();
                    bundle.putString("result", "Đã thanh toán");
                    bundle.putString("transactionId", String.valueOf(appTransId));
                    bundle.putString("appTransId", String.valueOf(appTransId));
                    paymentNotiFragment.setArguments(bundle);

                    View container = requireActivity().findViewById(R.id.user_rent_vehicle_container);
                    if (container != null) {
                        container.setVisibility(View.VISIBLE);
                    }

                    getChildFragmentManager().popBackStackImmediate(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    Log.d("Navigation", "Executing fragment transaction");
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.replace(R.id.user_rent_vehicle_container, paymentNotiFragment);
                    transaction.commit();
                    Log.d("Navigation", "Fragment transaction completed");
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
            int thanhTien = parseCurrencyString(tvThanhTien.getText().toString());
            int tienCoc = parseCurrencyString(tvTienCoc.getText().toString());
            int finalAmount = thanhTien - tienCoc;

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
                                Log.d("ZaloPay", "Token: " + token);
                                ZaloPaySDK.getInstance().payOrder(requireActivity(), token, "demozpdk://app", new PayOrderListener() {
                                    @Override
                                    public void onPaymentSucceeded(String transactionId, String transToken, String appTransId) {
                                        Log.d("ZaloPay", "Payment succeeded - transactionId: " + transactionId + ", appTransId: " + appTransId);

                                        savePaymentTransaction(String.valueOf(finalAmount), "ZaloPay", "Đã thanh toán", transactionId, appTransId);

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
                                            chiTiet.setNgayBatDauTT(tvNgayBatDauTT.getText().toString());
                                            chiTiet.setNgayKetThucTT(tvNgayHienTai.getText().toString());
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
                                            } else {
                                                Log.e("ZaloPay", "Failed to create rental details");
                                            }
                                        } else {
                                            Log.e("ZaloPay", "Failed to create rental record");
                                        }

                                        PaymentNotification paymentNotiFragment = new PaymentNotification();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("result", "Đã thanh toán");
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

                                        String amount = tvThanhTien.getText().toString();
                                        String paymentMethod = spinnerPhuongThuc.getText().toString();
                                        savePaymentTransaction(amount, paymentMethod, "Chưa thanh toán", null, null);

                                        PaymentNotification paymentNotiFragment = new PaymentNotification();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("result", "Chưa thanh toán");
                                        paymentNotiFragment.setArguments(bundle);

                                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                        transaction.replace(R.id.user_rent_vehicle_container, paymentNotiFragment);
                                        transaction.addToBackStack(null);
                                        transaction.commit();

                                        View container = requireActivity().findViewById(R.id.user_rent_vehicle_container);
                                        if (container != null) {
                                            container.setVisibility(View.GONE);
                                        }

                                        TextView tvWelcome = requireActivity().findViewById(R.id.tv_welcome);
                                        AutoCompleteTextView tvSearch = requireActivity().findViewById(R.id.tv_search);
                                        AutoCompleteTextView spinnerVehicleType = requireActivity().findViewById(R.id.spinner_vehicle_type);
                                        AutoCompleteTextView spinnerStatus = requireActivity().findViewById(R.id.spinner_status);
                                        Button btnClearFilter = requireActivity().findViewById(R.id.btn_clear_filter);
                                        RecyclerView recyclerView = requireActivity().findViewById(R.id.vehicle_list);

                                        if (tvWelcome != null)
                                            tvWelcome.setVisibility(View.VISIBLE);
                                        if (tvSearch != null)
                                            tvSearch.setVisibility(View.VISIBLE);
                                        if (spinnerVehicleType != null)
                                            spinnerVehicleType.setVisibility(View.VISIBLE);
                                        if (spinnerStatus != null)
                                            spinnerStatus.setVisibility(View.VISIBLE);
                                        if (btnClearFilter != null)
                                            btnClearFilter.setVisibility(View.VISIBLE);
                                        if (recyclerView != null)
                                            recyclerView.setVisibility(View.VISIBLE);
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

    private void savePaymentTransaction(String amount, String paymentMethod, String status, String transactionId, String appTransId) {
        try {
            ThanhToan thanhToan = new ThanhToan();
            thanhToan.setMaND(currentUser.getMaND());
            thanhToan.setMaThueXe(currentRentalId);
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
            } else {
                Log.d("Payment", "Successfully saved payment transaction with ID: " + result);
            }
        } catch (Exception e) {
            Log.e("Payment", "Error saving payment transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateRentalStatusAfterPayment(int maThueXe, int maXe) {
        try {
            // Get the rental record
            ThueXe thueXe = thueXeDAO.getById(maThueXe);
            if (thueXe != null) {
                // Get the payment record
                ThanhToan thanhToan = thanhToanDAO.getByMaThueXe(maThueXe);
                if (thanhToan != null) {
                    // Get rental details to check if this is deposit or final payment
                    ChiTietThueXe chiTiet = chiTietThueXeDAO.getSingleByMaThueXe(maThueXe);
                    if (chiTiet != null) {
                        boolean isDepositPayment = thanhToan.getSoTien() == chiTiet.getTienCoc();

                        if (isDepositPayment) {
                            // For deposit payment, set status to "Đang thuê"
                            thueXe.setTrangThai(0);
                            int updateRentalResult = thueXeDAO.update(thueXe);
                            Log.d("UpdateStatus", "Updated rental status to ĐANG THUÊ, result: " + updateRentalResult);

                            // Update vehicle status to "Đã đặt"
                            int updateVehicleResult = xeDAO.updateStatus(maXe, 1);
                            Log.d("UpdateStatus", "Updated vehicle status to ĐÃ ĐẶT, result: " + updateVehicleResult);
                        } else {
                            // For final payment, set status to "Đã trả"
                            thueXe.setTrangThai(2);
                            int updateRentalResult = thueXeDAO.update(thueXe);
                            Log.d("UpdateStatus", "Updated rental status to ĐÃ TRẢ, result: " + updateRentalResult);

                            // Update vehicle status to "Hiện còn"
                            int updateVehicleResult = xeDAO.updateStatus(maXe, 0);
                            Log.d("UpdateStatus", "Updated vehicle status to HIỆN CÒN, result: " + updateVehicleResult);
                        }

                        Toast.makeText(requireContext(), "Đã cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("UpdateStatus", "Error updating status: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(requireContext(), "Có lỗi xảy ra khi cập nhật trạng thái: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void showRentalInfo() {
        if (recyclerViewLichSuThue != null) {
            recyclerViewLichSuThue.setVisibility(View.GONE);
        }

        if (emptyStateLayout != null) {
            emptyStateLayout.setVisibility(View.GONE);
        }

        if (scrollViewRentalInfo != null) {
            scrollViewRentalInfo.setVisibility(View.VISIBLE);
        }

        loadCurrentRental();
    }

    private void loadCurrentRental() {
        try {
            NguoiDung currentUser = nguoiDungUtility.getCurrentUser();
            if (currentUser != null) {
                ThueXe latestRental = thueXeDAO.getLatestRentalByUser(currentUser.getMaND());
                if (latestRental != null) {
                    ChiTietThueXe chiTiet = chiTietThueXeDAO.getSingleByMaThueXe(latestRental.getMaThueXe());
                    if (chiTiet != null) {
                        selectedXe = xeDAO.getById(chiTiet.getMaXe());
                        if (selectedXe != null) {
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
            if (tvTenXe != null) tvTenXe.setText(xe.getTenXe());
            if (tvBienSo != null) tvBienSo.setText(xe.getBienSo());
            if (tvLoaiXe != null) tvLoaiXe.setText(xe.getLoaiXe());

            if (tvNgayDat != null) tvNgayDat.setText(thueXe.getNgayDat());
            if (tvNgayBatDauDK != null) tvNgayBatDauDK.setText(chiTiet.getNgayBatDauDK());
            if (tvNgayKetThucDK != null) tvNgayKetThucDK.setText(chiTiet.getNgayKetThucDK());

            if (tvTienCoc != null)
                tvTienCoc.setText(String.format("%,d VNĐ", chiTiet.getTienCoc()));
            if (tvThanhTien != null)
                tvThanhTien.setText(String.format("%,d VNĐ", chiTiet.getThanhTien()));

            if (tvTrangThaiThanhToan != null) {
                String trangThai;
                switch (thueXe.getTrangThai()) {
                    case 0:
                        trangThai = "Đang thuê";
                        break;
                    case 1:
                        trangThai = "Đã trả";
                        break;
                    case 2:
                        trangThai = "Đã hủy";
                        break;
                    default:
                        trangThai = "Không xác định";
                }
                tvTrangThaiThanhToan.setText(trangThai);
            }
        } catch (Exception e) {
            Log.e("RentVehicle", "Error updating rental info UI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void refreshRentalInfo() {
        loadLatestRentalInfo();
    }

    private void showPaymentNotification(String result, String subMessage, boolean isStopRental) {
        try {
            if (scrollViewRentalInfo != null) {
                scrollViewRentalInfo.setVisibility(View.GONE);
            }
            if (recyclerViewLichSuThue != null) {
                recyclerViewLichSuThue.setVisibility(View.GONE);
            }
            if (emptyStateLayout != null) {
                emptyStateLayout.setVisibility(View.GONE);
            }

            View rentVehicleContainer = requireView().findViewById(R.id.user_rent_vehicle_container);
            if (rentVehicleContainer != null) {
                rentVehicleContainer.setVisibility(View.VISIBLE);
            }

                        PaymentNotification paymentNotiFragment = new PaymentNotification();
                        Bundle bundle = new Bundle();
            bundle.putString("result", result);
            bundle.putString("subMessage", subMessage);
            bundle.putBoolean("isStopRental", isStopRental);
            if (currentRentalId != -1) {
                bundle.putInt("maThueXe", currentRentalId);
            }
            if (selectedXe != null) {
                bundle.putInt("maXe", selectedXe.getMaXe());
            }
                        paymentNotiFragment.setArguments(bundle);

                        getChildFragmentManager()
                                .beginTransaction()
                                .replace(R.id.user_rent_vehicle_container, paymentNotiFragment)
                                .addToBackStack(null)
                                .commit();
            } catch (Exception e) {
            Log.e("RentVehicle", "Error showing payment notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showRentVehicleStart(Xe xe) {
        if (getView() == null) return;

        View scrollViewRentalInfo = getView().findViewById(R.id.scrollViewRentalInfo);
        View emptyView = getView().findViewById(R.id.emptyView);
        View rentVehicleContainer = getView().findViewById(R.id.user_rent_vehicle_container);

        if (scrollViewRentalInfo != null) scrollViewRentalInfo.setVisibility(View.GONE);
        if (emptyView != null) emptyView.setVisibility(View.GONE);
        if (rentVehicleContainer != null) {
            rentVehicleContainer.setVisibility(View.VISIBLE);
            
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
}
