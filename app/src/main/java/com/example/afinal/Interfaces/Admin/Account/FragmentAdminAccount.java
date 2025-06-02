package com.example.afinal.Interfaces.Admin.Account;

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

import com.example.afinal.Database.Model.NguoiDung;
import com.example.afinal.Database.Utilites.NguoiDungUtility;
import com.example.afinal.R;

import java.util.List;

public class FragmentAdminAccount extends Fragment implements AdapterAdminAccountRecycleView.OnAccountActionListener {
    private RecyclerView recyclerView;
    private FrameLayout accountContainer;
    private Button btnThem;
    private AdapterAdminAccountRecycleView adapter;
    private NguoiDungUtility nguoiDungUtility;
    private LinearLayout list_account;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        setupRecyclerView();
        setupListeners();
    }

    private void bindView(View view) {
        recyclerView = view.findViewById(R.id.admin_account_recycle);
        accountContainer = view.findViewById(R.id.admin_account_container);
        btnThem = view.findViewById(R.id.btnThem);
        list_account = view.findViewById(R.id.list_account);
    }

    private void setupRecyclerView() {
        nguoiDungUtility = NguoiDungUtility.getInstance(requireContext());
        List<NguoiDung> nguoiDungList = nguoiDungUtility.getAllNguoiDung();
        adapter = new AdapterAdminAccountRecycleView(requireContext(), nguoiDungList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        btnThem.setOnClickListener(v -> showFormFragment(null));
    }

    private void showFormFragment(NguoiDung nguoiDung) {
        btnThem.setVisibility(View.GONE);
        list_account.setVisibility(View.GONE);
        accountContainer.setVisibility(View.VISIBLE);

        FragmentFormAccount formFragment = new FragmentFormAccount();
        if (nguoiDung != null) {
            Bundle args = new Bundle();
            args.putSerializable("nguoiDung", nguoiDung);
            formFragment.setArguments(args);
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.admin_account_container, formFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showListFragment() {
        accountContainer.setVisibility(View.GONE);
        btnThem.setVisibility(View.VISIBLE);
        list_account.setVisibility(View.VISIBLE);

        if (adapter != null) {
            adapter.updateData(nguoiDungUtility.getAllNguoiDung());
        }
    }

    @Override
    public void onUpdateClick(NguoiDung nguoiDung) {
        showFormFragment(nguoiDung);
    }

    @Override
    public void onDeleteClick(NguoiDung nguoiDung) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản " + nguoiDung.getHoTen() + "?")
                .setPositiveButton("Có", (dialog, which) -> {
                    int result = nguoiDungUtility.deleteNguoiDung(nguoiDung.getMaND());
                    if (result > 0) {
                        Toast.makeText(requireContext(), "Xóa tài khoản thành công", Toast.LENGTH_SHORT).show();
                        adapter.updateData(nguoiDungUtility.getAllNguoiDung());
                    } else {
                        Toast.makeText(requireContext(), "Xóa tài khoản thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.updateData(nguoiDungUtility.getAllNguoiDung());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
