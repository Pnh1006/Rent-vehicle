package com.example.afinal.Interfaces.Admin.Account;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.Database.Model.NguoiDung;
import com.example.afinal.Database.Utilites.NguoiDungUtility;
import com.example.afinal.R;

import java.util.List;

public class AdapterAdminAccountRecycleView extends RecyclerView.Adapter<AdapterAdminAccountRecycleView.ViewHolder> {
    private final Context context;
    private List<NguoiDung> nguoiDungList;
    private final OnAccountActionListener listener;

    public interface OnAccountActionListener {
        void onUpdateClick(NguoiDung nguoiDung);

        void onDeleteClick(NguoiDung nguoiDung);
    }

    public AdapterAdminAccountRecycleView(Context context, List<NguoiDung> nguoiDungList, OnAccountActionListener listener) {
        this.context = context;
        this.nguoiDungList = nguoiDungList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NguoiDung nguoiDung = nguoiDungList.get(position);
        holder.txtHoTen.setText(nguoiDung.getHoTen());
        holder.txtSDT.setText(nguoiDung.getSdt());
        holder.txtMatKhau.setText(nguoiDung.getMatKhau());
        holder.txtCCCD.setText(nguoiDung.getCccd());
        holder.txtNgaySinh.setText(nguoiDung.getNgaySinh());
        holder.txtGioiTinh.setText(nguoiDung.getGioiTinh());
        holder.txtNgayDangKy.setText(nguoiDung.getNgayDangKy());
        holder.txtTrangThai.setText(NguoiDungUtility.getTrangThaiText(context, nguoiDung.getTrangThai()));
        holder.txtVaiTro.setText(NguoiDungUtility.getVaiTroText(context, nguoiDung.getVaiTro()));

        holder.btnCapNhat.setOnClickListener(v -> listener.onUpdateClick(nguoiDung));
        holder.btnXoa.setOnClickListener(v -> listener.onDeleteClick(nguoiDung));
    }

    @Override
    public int getItemCount() {
        return nguoiDungList.size();
    }

    public void updateData(List<NguoiDung> newList) {
        this.nguoiDungList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtHoTen, txtSDT, txtMatKhau, txtCCCD, txtNgaySinh, txtGioiTinh, txtNgayDangKy, txtTrangThai, txtVaiTro;
        Button btnCapNhat, btnXoa;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtHoTen = itemView.findViewById(R.id.txtHoTen);
            txtSDT = itemView.findViewById(R.id.txtSDT);
            txtMatKhau = itemView.findViewById(R.id.txtMatKhau);
            txtCCCD = itemView.findViewById(R.id.txtCCCD);
            txtNgaySinh = itemView.findViewById(R.id.txtNgaySinh);
            txtGioiTinh = itemView.findViewById(R.id.txtGioiTinh);
            txtNgayDangKy = itemView.findViewById(R.id.txtNgayDangKy);
            txtTrangThai = itemView.findViewById(R.id.txtTrangThai);
            txtVaiTro = itemView.findViewById(R.id.txtVaiTro);
            btnCapNhat = itemView.findViewById(R.id.btnCapNhat);
            btnXoa = itemView.findViewById(R.id.btnXoa);
        }
    }
}
