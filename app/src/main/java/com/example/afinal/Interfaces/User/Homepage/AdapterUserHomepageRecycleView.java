package com.example.afinal.Interfaces.User.Homepage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.Database.Model.Xe;
import com.example.afinal.Database.Utilites.XeUtility;
import com.example.afinal.Interfaces.User.UserViewPagerAdapter;
import com.example.afinal.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class AdapterUserHomepageRecycleView extends RecyclerView.Adapter<AdapterUserHomepageRecycleView.ViewHolder> {
    private static final String TAG = "AdapterHomepage";
    private Context context;
    private List<Xe> xeList;
    private XeUtility xeUtility;
    private Fragment parentFragment;

    public AdapterUserHomepageRecycleView(Context context, List<Xe> xeList, Fragment parentFragment) {
        this.context = context;
        this.xeList = xeList;
        this.xeUtility = new XeUtility(context);
        this.parentFragment = parentFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_homepage, parent, false);
        return new AdapterUserHomepageRecycleView.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Xe xe = xeList.get(position);

        holder.txtTenXe.setText(xe.getTenXe());
        holder.txtBienSo.setText(xe.getBienSo());
        holder.txtGiaThue.setText(XeUtility.formatGiaThue(xe.getGiaThue()));
        holder.txtHangXe.setText(xe.getLoaiXe());
        holder.txtTrangThai.setText(xeUtility.getTrangThaiText(xe.getTrangThai()));

        if (xe.getTrangThai() == 0) {
            holder.txtThueXe.setVisibility(View.VISIBLE);
        } else {
            holder.txtThueXe.setVisibility(View.GONE);
        }

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

        holder.txtThueXe.setOnClickListener(v -> {
            Log.d(TAG, "Thuê xe button clicked for vehicle: " + xe.getTenXe());
            
            // Lưu xe được chọn vào ViewPagerAdapter
            ViewPager viewPager = parentFragment.requireActivity().findViewById(R.id.view_pager);
            if (viewPager != null && viewPager.getAdapter() instanceof UserViewPagerAdapter) {
                UserViewPagerAdapter adapter = (UserViewPagerAdapter) viewPager.getAdapter();
                adapter.setSelectedXe(xe, true);
                Log.d(TAG, "Selected vehicle saved: " + xe.getTenXe());
                
                // Chuyển sang tab Thuê xe
                Log.d(TAG, "Switching to rent vehicle tab using ViewPager");
                viewPager.setCurrentItem(1, true);
                
                // Đảm bảo ViewPager cập nhật ngay lập tức
                viewPager.post(() -> {
                    adapter.notifyDataSetChanged();
                });
            }
        });
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
        }
    }
}
