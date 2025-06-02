package com.example.afinal.Interfaces.User.RentalHistory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.Database.DAO.ThanhToanDAO;
import com.example.afinal.Database.Model.ThanhToan;
import com.example.afinal.Database.Utilites.NguoiDungUtility;
import com.example.afinal.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentUserRentalHistory extends Fragment {
    private RecyclerView recyclerView;
    private AdapterUserRentalHistoryRecycleView adapter;
    private ThanhToanDAO thanhToanDAO;
    private NguoiDungUtility nguoiDungUtility;
    private List<ThanhToan> thanhToanList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragement_user_rental_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        thanhToanDAO = new ThanhToanDAO(requireContext());
        nguoiDungUtility = NguoiDungUtility.getInstance(requireContext());

        recyclerView = view.findViewById(R.id.rental_history_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        thanhToanList = new ArrayList<>();

        adapter = new AdapterUserRentalHistoryRecycleView(requireContext(), thanhToanList);
        recyclerView.setAdapter(adapter);

        loadTransactionHistory();
    }

    private void loadTransactionHistory() {
        if (nguoiDungUtility.getCurrentUser() == null) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập để xem lịch sử giao dịch", Toast.LENGTH_SHORT).show();
            return;
        }

        List<ThanhToan> transactions = thanhToanDAO.getByMaND(nguoiDungUtility.getCurrentUser().getMaND());

        if (transactions != null && !transactions.isEmpty()) {
            // Sort transactions by date in descending order (newest first)
            transactions.sort((t1, t2) -> t2.getNgayThucHien().compareTo(t1.getNgayThucHien()));
            
            thanhToanList.clear();
            thanhToanList.addAll(transactions);
            adapter.updateData(thanhToanList);

            // If we have a specific transaction to show (from appTransId)
            if (getArguments() != null) {
                String appTransId = getArguments().getString("appTransId");
                if (appTransId != null) {
                    // Find the transaction and scroll to it
                    for (int i = 0; i < thanhToanList.size(); i++) {
                        if (thanhToanList.get(i).getMaGiaoDich().equals(appTransId)) {
                            recyclerView.scrollToPosition(i);
                            break;
                        }
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "Không có giao dịch nào", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (thanhToanDAO != null) {
            thanhToanDAO.close();
        }
    }
}
