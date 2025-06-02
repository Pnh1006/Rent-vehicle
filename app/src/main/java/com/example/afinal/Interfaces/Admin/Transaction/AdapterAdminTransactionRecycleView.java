package com.example.afinal.Interfaces.Admin.Transaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.Database.Model.ThanhToan;
import com.example.afinal.R;

import java.util.ArrayList;

public class AdapterAdminTransactionRecycleView extends RecyclerView.Adapter<AdapterAdminTransactionRecycleView.ViewHolder> {
    private Context context;
    private ArrayList<ThanhToan> list;
    private OnTransactionClickListener listener;

    public interface OnTransactionClickListener {
        void onEditClick(ThanhToan thanhToan);
    }

    public AdapterAdminTransactionRecycleView(Context context, ArrayList<ThanhToan> list, OnTransactionClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ThanhToan thanhToan = list.get(position);

        holder.txtSoTien.setText(String.format("%,d VNĐ", thanhToan.getSoTien()));
        holder.txtNoiDung.setText(thanhToan.getNoiDung());
        holder.txtNgayThucHien.setText(thanhToan.getNgayThucHien());
        
        String status = getStatusText(thanhToan.getTrangThai());
        if (status.equals("Đã thanh toán")) {
            holder.txtNgayThanhCong.setText(thanhToan.getNgayThanhCong());
        } else {
            holder.txtNgayThanhCong.setText("");
        }
        
        holder.txtPhuongThuc.setText(getPaymentMethodText(thanhToan.getPhuongThuc()));
        holder.txtMaGiaoDich.setText(thanhToan.getMaGiaoDich());
        holder.txtTrangThai.setText(status);
        holder.txtGhiChu.setText(thanhToan.getGhiChu());

        holder.btnCapNhat.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(thanhToan);
            }
        });
    }

    private String getStatusText(int status) {
        String[] statuses = context.getResources().getStringArray(R.array.trang_thai_thanh_toan);
        if (status >= 0 && status < statuses.length) {
            return statuses[status];
        }
        return statuses[0];
    }

    private String getPaymentMethodText(String method) {
        String[] methods = context.getResources().getStringArray(R.array.phuong_thuc_thanh_toan);
        for (String m : methods) {
            if (m.equals(method)) {
                return m;
            }
        }
        return methods[0];
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateData(ArrayList<ThanhToan> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtSoTien, txtNoiDung, txtNgayThucHien, txtNgayThanhCong, txtPhuongThuc, txtMaGiaoDich, txtTrangThai, txtGhiChu;
        Button btnCapNhat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSoTien = itemView.findViewById(R.id.txtSoTien);
            txtNoiDung = itemView.findViewById(R.id.txtNoiDung);
            txtNgayThucHien = itemView.findViewById(R.id.txtNgayThucHien);
            txtNgayThanhCong = itemView.findViewById(R.id.txtNgayThanhCong);
            txtPhuongThuc = itemView.findViewById(R.id.txtPhuongThuc);
            txtMaGiaoDich = itemView.findViewById(R.id.txtMaGiaoDich);
            txtTrangThai = itemView.findViewById(R.id.txtTrangThai);
            txtGhiChu = itemView.findViewById(R.id.txtGhiChu);
            btnCapNhat = itemView.findViewById(R.id.btnCapNhat);
        }
    }
}
