package com.example.afinal.Interfaces.Admin.Vehicle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.Database.Model.Xe;
import com.example.afinal.Database.Utilites.XeUtility;
import com.example.afinal.R;

import java.util.List;

public class AdapterAdminVehicleRecycleView extends RecyclerView.Adapter<AdapterAdminVehicleRecycleView.ViewHolder> {
    private final Context context;
    private List<Xe> xeList;
    private final XeUtility xeUtility;
    private final OnVehicleActionListener listener;

    public interface OnVehicleActionListener {
        void onUpdateClick(Xe xe);
        void onDeleteClick(Xe xe);
    }

    public AdapterAdminVehicleRecycleView(Context context, List<Xe> xeList, OnVehicleActionListener listener) {
        this.context = context;
        this.xeList = xeList;
        this.xeUtility = new XeUtility(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vehicle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Xe xe = xeList.get(position);
        holder.txtTenXe.setText(xe.getTenXe());
        holder.txtBienSo.setText(xe.getBienSo());
        holder.txtGiaThue.setText(XeUtility.formatGiaThue(xe.getGiaThue()));
        holder.txtNamSX.setText(String.valueOf(xe.getNamSX()));
        holder.txtMauSac.setText(xe.getMauSac());
        holder.txtLoaiXe.setText(xe.getLoaiXe());
        holder.txtTrangThai.setText(xeUtility.getTrangThaiText(xe.getTrangThai()));

        if (xe.getHinhAnh() != null && !xe.getHinhAnh().isEmpty()) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(xe.getHinhAnh());
                if (bitmap != null) {
                    holder.imgPreview.setImageBitmap(bitmap);
                } else {
                    holder.imgPreview.setImageResource(R.drawable.directions_bike);
                }
            } catch (Exception e) {
                holder.imgPreview.setImageResource(R.drawable.directions_bike);
            }
        } else {
            holder.imgPreview.setImageResource(R.drawable.directions_bike);
        }

        // Set click listeners for update and delete buttons
        holder.btnCapNhat.setOnClickListener(v -> listener.onUpdateClick(xe));
        holder.btnXoa.setOnClickListener(v -> listener.onDeleteClick(xe));
    }

    @Override
    public int getItemCount() {
        return xeList.size();
    }

    public void updateData(List<Xe> newXeList) {
        this.xeList = newXeList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenXe, txtBienSo, txtGiaThue, txtNamSX, txtMauSac, txtLoaiXe, txtTrangThai;
        ImageView imgPreview;
        Button btnCapNhat, btnXoa;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTenXe = itemView.findViewById(R.id.txtTenXe);
            txtBienSo = itemView.findViewById(R.id.txtBienSo);
            txtGiaThue = itemView.findViewById(R.id.txtGiaThue);
            txtNamSX = itemView.findViewById(R.id.txtNamSX);
            txtMauSac = itemView.findViewById(R.id.txtMauSac);
            txtLoaiXe = itemView.findViewById(R.id.txtLoaiXe);
            txtTrangThai = itemView.findViewById(R.id.txtTrangThai);
            imgPreview = itemView.findViewById(R.id.imgPreview);
            btnCapNhat = itemView.findViewById(R.id.btnCapNhat);
            btnXoa = itemView.findViewById(R.id.btnXoa);
        }
    }
}
