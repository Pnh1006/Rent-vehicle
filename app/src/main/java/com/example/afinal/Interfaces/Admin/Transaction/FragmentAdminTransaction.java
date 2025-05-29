package com.example.afinal.Interfaces.Admin.Transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.Database.DAO.ThanhToanDAO;
import com.example.afinal.Database.Model.ThanhToan;
import com.example.afinal.R;

import java.util.ArrayList;

public class FragmentAdminTransaction extends Fragment implements AdapterAdminTransactionRecycleView.OnTransactionClickListener {
    private RecyclerView recyclerView;
    private FrameLayout transactionContainer;
    private AdapterAdminTransactionRecycleView adapter;
    private ThanhToanDAO thanhToanDAO;
    private ArrayList<ThanhToan> listThanhToan;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        setupRecyclerView();
    }

    private void bindView(View view) {
        recyclerView = view.findViewById(R.id.admin_transaction_recycle);
        transactionContainer = view.findViewById(R.id.admin_transaction_container);
    }

    private void setupRecyclerView() {
        thanhToanDAO = new ThanhToanDAO(requireContext());
        listThanhToan = thanhToanDAO.getAll();
        
        adapter = new AdapterAdminTransactionRecycleView(requireContext(), listThanhToan, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void showFormFragment(ThanhToan thanhToan) {
        recyclerView.setVisibility(View.GONE);
        transactionContainer.setVisibility(View.VISIBLE);

        FragmentFormTransaction formFragment = new FragmentFormTransaction();
        if (thanhToan != null) {
            Bundle args = new Bundle();
            args.putSerializable("thanhToan", thanhToan);
            formFragment.setArguments(args);
        }

        getChildFragmentManager().beginTransaction()
                .replace(R.id.admin_transaction_container, formFragment)
                .addToBackStack(null)
                .commit();
    }

    public void showListFragment() {
        transactionContainer.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        if (adapter != null) {
            listThanhToan = thanhToanDAO.getAll();
            adapter.updateData(listThanhToan);
        }
    }

    @Override
    public void onEditClick(ThanhToan thanhToan) {
        showFormFragment(thanhToan);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (thanhToanDAO != null) {
            thanhToanDAO.close();
        }
    }
}
