package com.example.afinal.Interfaces.User.RentVehicle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.Database.Model.ThueXe;
import com.example.afinal.Database.Model.ChiTietThueXe;
import com.example.afinal.Database.Model.Xe;
import com.example.afinal.R;

import java.util.List;

public class RentHistoryAdapter extends RecyclerView.Adapter<RentHistoryAdapter.RentHistoryViewHolder> {
    private List<ThueXe> rentHistoryList;
    private List<ChiTietThueXe> chiTietList;
    private List<Xe> xeList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ThueXe thueXe, ChiTietThueXe chiTiet, Xe xe);
    }

    public RentHistoryAdapter(List<ThueXe> rentHistoryList, List<ChiTietThueXe> chiTietList, List<Xe> xeList, OnItemClickListener listener) {
        this.rentHistoryList = rentHistoryList;
        this.chiTietList = chiTietList;
        this.xeList = xeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RentHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rent_vehicle_information, parent, false);
        return new RentHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RentHistoryViewHolder holder, int position) {
        ThueXe thueXe = rentHistoryList.get(position);
        ChiTietThueXe chiTiet = chiTietList.get(position);
        Xe xe = xeList.get(position);

        holder.txtNgayBatDauThucTe.setText(chiTiet.getNgayBatDauTT());
        holder.txtNgayKetThucThucTe.setText(chiTiet.getNgayKetThucDK());
        holder.txtThanhTien.setText(String.format("%,d VNĐ", chiTiet.getThanhTien()));
        holder.txtTrangThai.setText(getTrangThaiText(thueXe.getTrangThai()));

        if (chiTiet.getGhiChu() != null && !chiTiet.getGhiChu().isEmpty()) {
            holder.layoutGhiChu.setVisibility(View.VISIBLE);
            holder.txtGhiChu.setText(chiTiet.getGhiChu());
        } else {
            holder.layoutGhiChu.setVisibility(View.GONE);
        }

        holder.btnChiTiet.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(thueXe, chiTiet, xe);
            }
        });
    }

    private String getTrangThaiText(int trangThai) {
        switch (trangThai) {
            case 0:
                return "Đang thuê";
            case 1:
                return "Đang xử lý";
            case 2:
                return "Đã trả";
            case 3:
                return "Đã hủy";
            default:
                return "Không xác định";
        }
    }

    @Override
    public int getItemCount() {
        return rentHistoryList != null ? rentHistoryList.size() : 0;
    }

    public void updateData(List<ThueXe> newRentHistoryList, List<ChiTietThueXe> newChiTietList, List<Xe> newXeList) {
        this.rentHistoryList = newRentHistoryList;
        this.chiTietList = newChiTietList;
        this.xeList = newXeList;
        notifyDataSetChanged();
    }

    static class RentHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView txtNgayBatDauThucTe, txtNgayKetThucThucTe, txtThanhTien, txtTrangThai, txtGhiChu;
        View layoutGhiChu;
        Button btnChiTiet;

        public RentHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNgayBatDauThucTe = itemView.findViewById(R.id.txtNgayBatDauThucTe);
            txtNgayKetThucThucTe = itemView.findViewById(R.id.txtNgayKetThucThucTe);
            txtThanhTien = itemView.findViewById(R.id.txtThanhTien);
            txtTrangThai = itemView.findViewById(R.id.txtTrangThai);
            txtGhiChu = itemView.findViewById(R.id.txtGhiChu);
            layoutGhiChu = itemView.findViewById(R.id.layoutGhiChu);
            btnChiTiet = itemView.findViewById(R.id.btnChiTiet);
        }
    }
} 