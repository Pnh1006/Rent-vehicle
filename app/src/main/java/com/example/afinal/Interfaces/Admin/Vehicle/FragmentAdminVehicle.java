package com.example.afinal.Interfaces.Admin.Vehicle;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.Database.Model.Xe;
import com.example.afinal.Database.Utilites.XeUtility;
import com.example.afinal.R;

import java.util.List;

public class FragmentAdminVehicle extends Fragment implements AdapterAdminVehicleRecycleView.OnVehicleActionListener {
    private RecyclerView recyclerView;
    private FrameLayout vehicleContainer;
    private Button btnThem;
    private AdapterAdminVehicleRecycleView adapter;
    private XeUtility xeUtility;
    private LinearLayout vehicle_list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_vehicle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        setupRecyclerView();
        setupListeners();
        setupFragmentResultListener();
    }

    private void bindView(View view) {
        recyclerView = view.findViewById(R.id.admin_vehicle_recycle);
        vehicleContainer = view.findViewById(R.id.admin_vehicle_container);
        btnThem = view.findViewById(R.id.btnThem);
        vehicle_list = view.findViewById(R.id.vehicle_list);
    }

    private void setupRecyclerView() {
        xeUtility = new XeUtility(requireContext());
        List<Xe> xeList = xeUtility.getAllXe();
        adapter = new AdapterAdminVehicleRecycleView(requireContext(), xeList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        btnThem.setOnClickListener(v -> showFormFragment(null));
    }

    private void setupFragmentResultListener() {
        getParentFragmentManager().setFragmentResultListener("refresh_vehicle_list", this, (requestKey, bundle) -> {
            if (adapter != null) {
                adapter.updateData(xeUtility.getAllXe());
            }
        });
    }

    private void showFormFragment(Xe xe) {
        vehicle_list.setVisibility(View.GONE);
        vehicleContainer.setVisibility(View.VISIBLE);
        btnThem.setVisibility(View.GONE);

        FragmentFormVehicle formFragment = new FragmentFormVehicle();
        if (xe != null) {
            Bundle args = new Bundle();
            args.putSerializable("xe", xe);
            formFragment.setArguments(args);
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.admin_vehicle_container, formFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showListFragment() {
        vehicleContainer.setVisibility(View.GONE);
        vehicle_list.setVisibility(View.VISIBLE);
        btnThem.setVisibility(View.VISIBLE);

        if (adapter != null) {
            adapter.updateData(xeUtility.getAllXe());
        }
    }

    @Override
    public void onUpdateClick(Xe xe) {
        showFormFragment(xe);
    }

    @Override
    public void onDeleteClick(Xe xe) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa xe " + xe.getTenXe() + "?")
                .setPositiveButton("Có", (dialog, which) -> {
                    int result = xeUtility.deleteXe(xe.getMaXe());
                    if (result > 0) {
                        Toast.makeText(requireContext(), "Xóa xe thành công", Toast.LENGTH_SHORT).show();
                        adapter.updateData(xeUtility.getAllXe());
                    } else {
                        Toast.makeText(requireContext(), "Xóa xe thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Không", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.updateData(xeUtility.getAllXe());
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
