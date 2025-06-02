package com.example.afinal.Interfaces.User.Transaction.Action;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.afinal.Database.DAO.ThanhToanDAO;
import com.example.afinal.Database.DAO.XeDAO;
import com.example.afinal.Database.Model.ThanhToan;
import com.example.afinal.Database.Model.Xe;
import com.example.afinal.Interfaces.User.RentVehicle.FragmentUserRentVehicle;
import com.example.afinal.R;
import com.example.afinal.Interfaces.User.RentVehicle.UserRentVehicleStart;

public class PaymentNotification extends Fragment {
    private TextView tvResult, tvSubMessage;
    private Button btnTrangChu, btnThongTinThueXe, btnThongTinGiaoDich;
    private boolean isStopRental = false;
    private int maThueXe = -1;
    private int maXe = -1;
    private ThanhToanDAO thanhToanDAO;
    private XeDAO xeDAO;
    private Xe selectedXe;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thanhToanDAO = new ThanhToanDAO(requireContext());
        xeDAO = new XeDAO(requireContext());
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
        view.setVisibility(View.VISIBLE);
        if (view.getParent() instanceof View) {
            ((View) view.getParent()).setVisibility(View.VISIBLE);
        }

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
            tvResult = view.findViewById(R.id.tvThanhToanThanhCong);
            tvSubMessage = view.findViewById(R.id.tvSubMessage);
            btnTrangChu = view.findViewById(R.id.btnTrangChu);
            btnThongTinThueXe = view.findViewById(R.id.btnThongTinThueXe);
            btnThongTinGiaoDich = view.findViewById(R.id.btnThongTinGiaoDich);

            Log.d("PaymentNotification", "Finding views - tvResult ID: " + R.id.tvThanhToanThanhCong);

            if (tvResult == null) Log.e("PaymentNotification", "tvResult not found");
            if (tvSubMessage == null) Log.e("PaymentNotification", "tvSubMessage not found");
            if (btnTrangChu == null) Log.e("PaymentNotification", "btnTrangChu not found");
            if (btnThongTinThueXe == null) Log.e("PaymentNotification", "btnThongTinThueXe not found");
            if (btnThongTinGiaoDich == null) Log.e("PaymentNotification", "btnThongTinGiaoDich not found");

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

            Log.d("PaymentNotification", "View visibility - " +
                "tvResult: " + (tvResult != null ? tvResult.getVisibility() : "null") + ", " +
                "tvSubMessage: " + (tvSubMessage != null ? tvSubMessage.getVisibility() : "null") + ", " +
                "btnTrangChu: " + (btnTrangChu != null ? btnTrangChu.getVisibility() : "null") + ", " +
                "btnThongTinThueXe: " + (btnThongTinThueXe != null ? btnThongTinThueXe.getVisibility() : "null") + ", " +
                "btnThongTinGiaoDich: " + (btnThongTinGiaoDich != null ? btnThongTinGiaoDich.getVisibility() : "null"));

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

                // Load vehicle information
                if (maXe != -1) {
                    selectedXe = xeDAO.getById(maXe);
                }

                Log.d("PaymentNotification", "Setting up data - result: " + result +
                    ", subMessage: " + subMessage +
                    ", isStopRental: " + isStopRental +
                    ", maThueXe: " + maThueXe +
                    ", maXe: " + maXe +
                    ", selectedXe: " + (selectedXe != null ? selectedXe.getTenXe() : "null"));

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
                Fragment parentFragment = getParentFragment();
                if (parentFragment instanceof FragmentUserRentVehicle) {
                    ((FragmentUserRentVehicle) parentFragment).updateRentalStatusAfterPayment(maThueXe, maXe);
                }
                
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
            if (getActivity() != null) {
                ViewPager viewPager = getActivity().findViewById(R.id.view_pager);
                if (viewPager != null) {
                    viewPager.setCurrentItem(0);
                }
            }
        });

        btnThongTinThueXe.setOnClickListener(v -> {
            try {
                FragmentActivity activity = getActivity();
                if (activity == null) {
                    Log.e("PaymentNotification", "Activity is null");
                    return;
                }

                FragmentManager parentFragmentManager = getParentFragmentManager();
                if (parentFragmentManager == null) {
                    Log.e("PaymentNotification", "Parent FragmentManager is null");
                    return;
                }

                Fragment parentFragment = getParentFragment();
                if (!(parentFragment instanceof FragmentUserRentVehicle)) {
                    Log.e("PaymentNotification", "Parent fragment is not FragmentUserRentVehicle");
                    return;
                }

                FragmentUserRentVehicle rentVehicleFragment = (FragmentUserRentVehicle) parentFragment;

                // Xóa fragment notification hiện tại
                parentFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                if (getView() != null) {
                    getView().setVisibility(View.GONE);
                }

                // Xóa các fragment con trong FragmentUserRentVehicle
                FragmentManager childFragmentManager = rentVehicleFragment.getChildFragmentManager();
                if (childFragmentManager == null) {
                    Log.e("PaymentNotification", "Child FragmentManager is null");
                    return;
                }
                childFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                View rentVehicleView = rentVehicleFragment.getView();
                if (rentVehicleView != null) {
                    View scrollViewRentalInfo = rentVehicleView.findViewById(R.id.scrollViewRentalInfo);
                    View emptyView = rentVehicleView.findViewById(R.id.emptyView);
                    View rentVehicleContainer = rentVehicleView.findViewById(R.id.user_rent_vehicle_container);
                    View recyclerViewLichSuThue = rentVehicleView.findViewById(R.id.recyclerViewLichSuThue);

                    // Ẩn các view không cần thiết
                    if (emptyView != null) emptyView.setVisibility(View.GONE);
                    if (rentVehicleContainer != null) rentVehicleContainer.setVisibility(View.GONE);
                    if (recyclerViewLichSuThue != null) recyclerViewLichSuThue.setVisibility(View.GONE);
                    
                    // Hiển thị thông tin chi tiết thuê xe
                    if (scrollViewRentalInfo != null) {
                        scrollViewRentalInfo.setVisibility(View.VISIBLE);
                    }

                    // Load thông tin thuê xe mới nhất
                    rentVehicleFragment.refreshRentalInfo();
                }

            } catch (Exception e) {
                Log.e("PaymentNotification", "Error in btnThongTinThueXe click: " + e.getMessage());
                e.printStackTrace();
            }
        });

        btnThongTinGiaoDich.setOnClickListener(v -> {
            if (getActivity() != null) {
                ViewPager viewPager = getActivity().findViewById(R.id.view_pager);
                if (viewPager != null) {
                    viewPager.setCurrentItem(2); // Chuyển đến tab lịch sử giao dịch
                }

                // Xóa fragment notification hiện tại
                FragmentManager parentFragmentManager = getParentFragmentManager();
                if (parentFragmentManager != null) {
                    parentFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                if (getView() != null) {
                    getView().setVisibility(View.GONE);
                }
            }
        });
    }
}
