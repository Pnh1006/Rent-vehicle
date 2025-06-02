package com.example.afinal.Interfaces.Admin.Homepage;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.Database.Model.Xe;
import com.example.afinal.Database.Utilites.NguoiDungUtility;
import com.example.afinal.Database.Utilites.XeUtility;
import com.example.afinal.Interfaces.Commons.SignIn;
import com.example.afinal.R;

import java.util.List;

public class FragmentAdminHomepage extends Fragment {
    private AutoCompleteTextView tvSearch;
    private AutoCompleteTextView spinnerVehicleType;
    private AutoCompleteTextView spinnerStatus;
    private Button btnClearFilter;
    private RecyclerView recyclerView;
    private AdapterAdminHomepageRecycleView adapter;
    private XeUtility xeUtility;
    ImageView iv_logout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_homepage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindView(view);
        setupSpinners();
        setupRecyclerView();
        setupSearch();
        setupClearFilter();
        logout();
    }

    private void logout() {
        iv_logout.setOnClickListener(v -> {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
            builder.setTitle("Xác nhận đăng xuất");
            builder.setMessage("Bạn có chắc chắn muốn đăng xuất không?");

            builder.setPositiveButton("Có", (dialog, which) -> {
                NguoiDungUtility nguoiDungUtility = NguoiDungUtility.getInstance(requireContext());
                nguoiDungUtility.dangXuat();

                Intent intent = new Intent(requireActivity(), SignIn.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            });

            builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());

            builder.show();
        });
    }

    private void bindView(View view) {
        tvSearch = view.findViewById(R.id.tv_search);
        spinnerVehicleType = view.findViewById(R.id.spinner_vehicle_type);
        spinnerStatus = view.findViewById(R.id.spinner_status);
        btnClearFilter = view.findViewById(R.id.btn_clear_filter);
        recyclerView = view.findViewById(R.id.vehicle_list);
        iv_logout = view.findViewById(R.id.iv_logout);
    }

    private void setupSpinners() {
        String[] vehicleTypes = getResources().getStringArray(R.array.loai_xe);
        String[] vehicleTypesWithAll = new String[vehicleTypes.length + 1];
        vehicleTypesWithAll[0] = "Tất cả";
        System.arraycopy(vehicleTypes, 0, vehicleTypesWithAll, 1, vehicleTypes.length);

        ArrayAdapter<String> vehicleTypeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                vehicleTypesWithAll
        );
        vehicleTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehicleType.setAdapter(vehicleTypeAdapter);
        spinnerVehicleType.setText("Tất cả", false);

        String[] statusTypes = getResources().getStringArray(R.array.trang_thai_xe);
        String[] statusTypesWithAll = new String[statusTypes.length + 1];
        statusTypesWithAll[0] = "Tất cả";
        System.arraycopy(statusTypes, 0, statusTypesWithAll, 1, statusTypes.length);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                statusTypesWithAll
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
        spinnerStatus.setText("Tất cả", false);

        spinnerVehicleType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                performSearch();
            }
        });

        spinnerStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                performSearch();
            }
        });
    }

    private void setupRecyclerView() {
        xeUtility = new XeUtility(requireContext());
        adapter = new AdapterAdminHomepageRecycleView(requireContext(), xeUtility.getAllXe());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        tvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                performSearch();
            }
        });
    }

    private void performSearch() {
        String searchText = tvSearch.getText().toString();
        String selectedVehicleType = spinnerVehicleType.getText().toString();
        String selectedStatus = spinnerStatus.getText().toString();

        if (selectedVehicleType.equals("Tất cả")) {
            selectedVehicleType = null;
        }
        if (selectedStatus.equals("Tất cả")) {
            selectedStatus = null;
        }

        Integer trangThai = null;
        if (selectedStatus != null) {
            trangThai = xeUtility.getTrangThaiIndex(selectedStatus);
        }

        List<Xe> searchResults = xeUtility.searchXe(searchText, selectedVehicleType, trangThai);
        adapter.updateData(searchResults);
    }

    private void setupClearFilter() {
        btnClearFilter.setOnClickListener(v -> {
            tvSearch.setText("");

            spinnerVehicleType.setText("Tất cả", false);
            spinnerStatus.setText("Tất cả", false);

            ((ArrayAdapter<?>) spinnerVehicleType.getAdapter()).notifyDataSetChanged();
            ((ArrayAdapter<?>) spinnerStatus.getAdapter()).notifyDataSetChanged();

            adapter.updateData(xeUtility.getAllXe());
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (xeUtility != null) {
            xeUtility.close();
        }
    }
}
