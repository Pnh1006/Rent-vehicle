package com.example.afinal.Interfaces.User.Transaction.Action;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.afinal.Database.DAO.ThanhToanDAO;
import com.example.afinal.Database.Model.ThanhToan;
import com.example.afinal.Interfaces.User.RentVehicle.FragmentUserRentVehicle;
import com.example.afinal.Interfaces.User.RentalHistory.FragmentUserRentalHistory;
import com.example.afinal.R;

public class PaymentNotification extends Fragment {
    private TextView tvResult, tvSubMessage;
    private Button btnTrangChu, btnThongTinThueXe, btnThongTinGiaoDich;
    private boolean isStopRental = false;
    private int maThueXe = -1;
    private int maXe = -1;
    private ThanhToanDAO thanhToanDAO;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thanhToanDAO = new ThanhToanDAO(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("PaymentNotification", "onCreateView started");
        if (container != null) {
            Log.d("PaymentNotification", "Container dimensions - width: " + container.getWidth() + ", height: " + container.getHeight());
            Log.d("PaymentNotification", "Container visibility: " + container.getVisibility());
        }
        
        View view = inflater.inflate(R.layout.notification_payment, container, false);
        Log.d("PaymentNotification", "Layout inflated");
        
        initViews(view);
        setupData();
        setupButtonListeners();
        
        Log.d("PaymentNotification", "View setup completed - width: " + view.getWidth() + ", height: " + view.getHeight());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Đảm bảo view và parent container đều visible
        view.setVisibility(View.VISIBLE);
        if (view.getParent() instanceof View) {
            ((View) view.getParent()).setVisibility(View.VISIBLE);
        }

        // Sử dụng ViewTreeObserver để theo dõi layout
        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Log.d("PaymentNotification", "OnGlobalLayout - width: " + view.getWidth() + 
                ", height: " + view.getHeight() + 
                ", visibility: " + view.getVisibility() +
                ", parent visibility: " + (view.getParent() instanceof View ? ((View) view.getParent()).getVisibility() : "unknown"));
            
            if (view.getWidth() > 0 && view.getHeight() > 0) {
                // Đảm bảo tất cả child views đều visible
                if (view instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) view;
                    for (int i = 0; i < viewGroup.getChildCount(); i++) {
                        View child = viewGroup.getChildAt(i);
                        child.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        view.post(() -> {
            Log.d("PaymentNotification", "Post layout - width: " + view.getWidth() + ", height: " + view.getHeight());
            if (view.getWidth() == 0 || view.getHeight() == 0) {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                if (params == null) {
                    params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    );
                    view.setLayoutParams(params);
                }
                view.requestLayout();
            }
        });
    }

    private void initViews(View view) {
        try {
            // Tìm các view theo ID chính xác từ layout
            tvResult = view.findViewById(R.id.tvThanhToanThanhCong);
            tvSubMessage = view.findViewById(R.id.tvSubMessage);
            btnTrangChu = view.findViewById(R.id.btnTrangChu);
            btnThongTinThueXe = view.findViewById(R.id.btnThongTinThueXe);
            btnThongTinGiaoDich = view.findViewById(R.id.btnThongTinGiaoDich);

            Log.d("PaymentNotification", "Finding views - tvResult ID: " + R.id.tvThanhToanThanhCong);

            // Kiểm tra và log kết quả tìm view
            if (tvResult == null) Log.e("PaymentNotification", "tvResult not found");
            if (tvSubMessage == null) Log.e("PaymentNotification", "tvSubMessage not found");
            if (btnTrangChu == null) Log.e("PaymentNotification", "btnTrangChu not found");
            if (btnThongTinThueXe == null) Log.e("PaymentNotification", "btnThongTinThueXe not found");
            if (btnThongTinGiaoDich == null) Log.e("PaymentNotification", "btnThongTinGiaoDich not found");

            // Đảm bảo các view đều visible và có nội dung mặc định
            if (tvResult != null) {
                tvResult.setVisibility(View.VISIBLE);
                tvResult.setText("Đang xử lý"); // Giá trị mặc định
            }
            if (tvSubMessage != null) {
                tvSubMessage.setVisibility(View.VISIBLE);
                tvSubMessage.setText("Vui lòng đợi xác nhận từ nhân viên"); // Giá trị mặc định
            }
            if (btnTrangChu != null) {
                btnTrangChu.setVisibility(View.VISIBLE);
            }
            if (btnThongTinThueXe != null) {
                btnThongTinThueXe.setVisibility(View.VISIBLE);
            }
            if (btnThongTinGiaoDich != null) {
                btnThongTinGiaoDich.setVisibility(View.VISIBLE);
            }

            // Log trạng thái visibility của các view
            Log.d("PaymentNotification", "View visibility - " +
                "tvResult: " + (tvResult != null ? tvResult.getVisibility() : "null") + ", " +
                "tvSubMessage: " + (tvSubMessage != null ? tvSubMessage.getVisibility() : "null") + ", " +
                "btnTrangChu: " + (btnTrangChu != null ? btnTrangChu.getVisibility() : "null") + ", " +
                "btnThongTinThueXe: " + (btnThongTinThueXe != null ? btnThongTinThueXe.getVisibility() : "null") + ", " +
                "btnThongTinGiaoDich: " + (btnThongTinGiaoDich != null ? btnThongTinGiaoDich.getVisibility() : "null"));

            // Log layout parameters
            View rootView = view.getRootView();
            Log.d("PaymentNotification", "Root view dimensions - " +
                "width: " + rootView.getWidth() + ", " +
                "height: " + rootView.getHeight() + ", " +
                "visibility: " + rootView.getVisibility());

        } catch (Exception e) {
            Log.e("PaymentNotification", "Error initializing views: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupData() {
        try {
            Bundle args = getArguments();
            if (args != null) {
                String result = args.getString("result", "");
                String subMessage = args.getString("subMessage", "");
                isStopRental = args.getBoolean("isStopRental", false);
                maThueXe = args.getInt("maThueXe", -1);
                maXe = args.getInt("maXe", -1);

                Log.d("PaymentNotification", "Setting up data - result: " + result +
                    ", subMessage: " + subMessage +
                    ", isStopRental: " + isStopRental +
                    ", maThueXe: " + maThueXe +
                    ", maXe: " + maXe);

                if (tvResult != null) {
                    tvResult.setText(result);
                    tvResult.setVisibility(View.VISIBLE);
                    Log.d("PaymentNotification", "Set tvResult text: " + result);
                }

                if (tvSubMessage != null) {
                    if (!subMessage.isEmpty()) {
                        tvSubMessage.setText(subMessage);
                        tvSubMessage.setVisibility(View.VISIBLE);
                        Log.d("PaymentNotification", "Set tvSubMessage text: " + subMessage);
                    } else {
                        tvSubMessage.setVisibility(View.GONE);
                    }
                }

                if (isStopRental && maThueXe != -1) {
                    checkPaymentStatus();
                }
            }
        } catch (Exception e) {
            Log.e("PaymentNotification", "Error setting up data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkPaymentStatus() {
        try {
            ThanhToan thanhToan = thanhToanDAO.getByMaThueXe(maThueXe);
            if (thanhToan != null && thanhToan.getTrangThai() == 0) { // 0 = Đã thanh toán
                // Cập nhật trạng thái thuê xe và xe
                Fragment parentFragment = getParentFragment();
                if (parentFragment instanceof FragmentUserRentVehicle) {
                    ((FragmentUserRentVehicle) parentFragment).updateRentalStatusAfterPayment(maThueXe, maXe);
                }
                
                // Cập nhật giao diện
                tvResult.setText("Đã thanh toán");
                tvSubMessage.setText("Cảm ơn bạn đã sử dụng dịch vụ");
                tvSubMessage.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupButtonListeners() {
        btnTrangChu.setOnClickListener(v -> {
            // Quay về trang chủ
            if (getActivity() != null) {
                ViewPager viewPager = getActivity().findViewById(R.id.view_pager);
                if (viewPager != null) {
                    viewPager.setCurrentItem(0); // Chuyển về tab trang chủ
                }
            }
        });

        btnThongTinThueXe.setOnClickListener(v -> {
            // Hiển thị thông tin thuê xe trong tab thuê xe
            if (getActivity() != null) {
                // Chuyển về tab thuê xe
                ViewPager viewPager = getActivity().findViewById(R.id.view_pager);
                if (viewPager != null) {
                    viewPager.setCurrentItem(1); // Chuyển về tab thuê xe
                }

                // Tìm FragmentUserRentVehicle và cập nhật UI
                Fragment parentFragment = getParentFragment();
                if (parentFragment instanceof FragmentUserRentVehicle) {
                    FragmentUserRentVehicle rentVehicleFragment = (FragmentUserRentVehicle) parentFragment;
                    
                    // Xóa PaymentNotification khỏi back stack và fragment manager
                    if (getFragmentManager() != null) {
                        getFragmentManager().popBackStack();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.remove(this);
                        ft.commit();
                    }
                    
                    // Ẩn container và các view không cần thiết
                    View container = getActivity().findViewById(R.id.user_rent_vehicle_container);
                    if (container != null) {
                        container.setVisibility(View.GONE);
                    }
                    
                    // Cập nhật UI để hiển thị thông tin thuê xe
                    rentVehicleFragment.showRentalInfo();
                }
            }
        });

        btnThongTinGiaoDich.setOnClickListener(v -> {
            // Chuyển sang tab lịch sử và hiển thị thông tin giao dịch
            if (getActivity() != null) {
                ViewPager viewPager = getActivity().findViewById(R.id.view_pager);
                if (viewPager != null) {
                    viewPager.setCurrentItem(2); // Chuyển sang tab lịch sử
                }
            }
        });
    }
}
