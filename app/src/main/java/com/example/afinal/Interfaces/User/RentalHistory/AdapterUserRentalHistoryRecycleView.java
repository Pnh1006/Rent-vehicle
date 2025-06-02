package com.example.afinal.Interfaces.User.RentalHistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.Database.Model.ThanhToan;
import com.example.afinal.Database.Utilites.ThanhToanUtility;
import com.example.afinal.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterUserRentalHistoryRecycleView extends RecyclerView.Adapter<AdapterUserRentalHistoryRecycleView.ViewHolder> {
    private final Context context;
    private List<ThanhToan> thanhToanList;


    public AdapterUserRentalHistoryRecycleView(Context context, List<ThanhToan> thanhToanList) {
        this.context = context;
        this.thanhToanList = thanhToanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    public void updateData(ArrayList<ThanhToan> newList) {
        this.thanhToanList = newList;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ThanhToan thanhToan = thanhToanList.get(position);

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

    }

    @Override
    public int getItemCount() {
        return thanhToanList.size();
    }

    public void updateData(List<ThanhToan> newThanhToanList) {
        this.thanhToanList = newThanhToanList;
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
            btnCapNhat.setVisibility(View.GONE);
        }
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
}
