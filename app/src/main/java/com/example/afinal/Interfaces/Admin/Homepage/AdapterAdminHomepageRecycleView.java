package com.example.afinal.Interfaces.Admin.Homepage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.Database.Model.Xe;
import com.example.afinal.Database.Utilites.XeUtility;
import com.example.afinal.R;

import java.util.List;

public class AdapterAdminHomepageRecycleView extends RecyclerView.Adapter<AdapterAdminHomepageRecycleView.ViewHolder> {
    private Context context;
    private List<Xe> xeList;
    private XeUtility xeUtility;

    public AdapterAdminHomepageRecycleView(Context context, List<Xe> xeList) {
        this.context = context;
        this.xeList = xeList;
        this.xeUtility = new XeUtility(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_homepage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Xe xe = xeList.get(position);
        
        holder.txtTenXe.setText(xe.getTenXe());
        holder.txtBienSo.setText(xe.getBienSo());
        holder.txtGiaThue.setText(XeUtility.formatGiaThue(xe.getGiaThue()));
        holder.txtHangXe.setText(xe.getLoaiXe());
        holder.txtTrangThai.setText(xeUtility.getTrangThaiText(xe.getTrangThai()));
        
        if (xe.getHinhAnh() != null && !xe.getHinhAnh().isEmpty()) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(xe.getHinhAnh());
                if (bitmap != null) {
                    holder.ivImageVehicle.setImageBitmap(bitmap);
                } else {
                    holder.ivImageVehicle.setImageResource(R.drawable.directions_bike);
                }
            } catch (Exception e) {
                holder.ivImageVehicle.setImageResource(R.drawable.directions_bike);
            }
        } else {
            holder.ivImageVehicle.setImageResource(R.drawable.directions_bike);
        }
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
        ImageView ivImageVehicle;
        TextView txtTenXe, txtBienSo, txtGiaThue, txtHangXe, txtTrangThai, txtThueXe;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImageVehicle = itemView.findViewById(R.id.ivImageVehicle);
            txtTenXe = itemView.findViewById(R.id.txtTenXe);
            txtBienSo = itemView.findViewById(R.id.txtBienSo);
            txtGiaThue = itemView.findViewById(R.id.txtGiaThue);
            txtHangXe = itemView.findViewById(R.id.txtHangXe);
            txtTrangThai = itemView.findViewById(R.id.txtTrangThai);
            txtThueXe = itemView.findViewById(R.id.txtThueXe);
            txtThueXe.setVisibility(View.GONE);

        }
    }
}
