package com.example.afinal.Interfaces.User.Transaction.Action;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.Interfaces.User.RentVehicle.FragmentUserRentVehicle;
import com.example.afinal.Interfaces.User.RentalHistory.FragmentUserRentalHistory;
import com.example.afinal.R;


public class PaymentNoti extends Fragment {
    private View rootView;
    private TextView tvThanhToanThanhCong;
    private Button btnTrangChu, btnThongTinThueXe, btnThongTinGiaoDich;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.noti_payment_success, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        tvThanhToanThanhCong = view.findViewById(R.id.tvThanhToanThanhCong);
        btnTrangChu = view.findViewById(R.id.btnTrangChu);
        btnThongTinThueXe = view.findViewById(R.id.btnThongTinThueXe);
        btnThongTinGiaoDich = view.findViewById(R.id.btnThongTinGiaoDich);

        // Get payment result from arguments
        if (getArguments() != null) {
            String result = getArguments().getString("result", "");
            tvThanhToanThanhCong.setText(result);

            // Show/hide buttons based on payment result
            if (result.contains("thành công")) {
                btnThongTinThueXe.setVisibility(View.VISIBLE);
                btnThongTinGiaoDich.setVisibility(View.VISIBLE);
            } else {
                btnThongTinThueXe.setVisibility(View.GONE);
                btnThongTinGiaoDich.setVisibility(View.GONE);
            }
        }

        // Set up button click listeners
        btnTrangChu.setOnClickListener(v -> {
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

            // Pop all fragments from back stack to return to homepage
            requireActivity().getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });

        btnThongTinThueXe.setOnClickListener(v -> {
            // Navigate to rental details
            FragmentUserRentVehicle fragmentUserRentVehicle = new FragmentUserRentVehicle();
            
            // Get current rental information from arguments
            if (getArguments() != null) {
                String appTransId = getArguments().getString("appTransId");
                if (appTransId != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("appTransId", appTransId);
                    fragmentUserRentVehicle.setArguments(bundle);
                }
            }
            
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.user_rent_vehicle_container, fragmentUserRentVehicle);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        btnThongTinGiaoDich.setOnClickListener(v -> {
            // Navigate to transaction history
            FragmentUserRentalHistory fragmentUserRentalHistory = new FragmentUserRentalHistory();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.user_rent_vehicle_container, fragmentUserRentalHistory);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }
}
