package com.example.afinal.Interfaces.User.Homepage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.Database.Model.Xe;
import com.example.afinal.Database.Utilites.XeUtility;
import com.example.afinal.Interfaces.User.RentVehicle.UserRentVehicleStart;
import com.example.afinal.R;

import java.util.List;

public class AdapterUserHomepageRecycleView extends RecyclerView.Adapter<AdapterUserHomepageRecycleView.ViewHolder> {
    private Context context;
    private List<Xe> xeList;
    private XeUtility xeUtility;
    private Fragment parentFragment;
    private View container;

    public AdapterUserHomepageRecycleView(Context context, List<Xe> xeList, Fragment parentFragment) {
        this.context = context;
        this.xeList = xeList;
        this.xeUtility = new XeUtility(context);
        this.parentFragment = parentFragment;
        this.container = parentFragment.getView();
    }

    private void showHomepageElements() {
        if (container != null) {
            TextView tvWelcome = container.findViewById(R.id.tv_welcome);
            AutoCompleteTextView tvSearch = container.findViewById(R.id.tv_search);
            Spinner spinnerVehicleType = container.findViewById(R.id.spinner_vehicle_type);
            Spinner spinnerStatus = container.findViewById(R.id.spinner_status);
            Button btnClearFilter = container.findViewById(R.id.btn_clear_filter);
            RecyclerView recyclerView = container.findViewById(R.id.vehicle_list);
            View fragmentContainer = container.findViewById(R.id.user_rent_vehicle_container);

            if (tvWelcome != null) tvWelcome.setVisibility(View.VISIBLE);
            if (tvSearch != null) tvSearch.setVisibility(View.VISIBLE);
            if (spinnerVehicleType != null) spinnerVehicleType.setVisibility(View.VISIBLE);
            if (spinnerStatus != null) spinnerStatus.setVisibility(View.VISIBLE);
            if (btnClearFilter != null) btnClearFilter.setVisibility(View.VISIBLE);
            if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
            if (fragmentContainer != null) fragmentContainer.setVisibility(View.GONE);
        }
    }

    private void hideHomepageElements() {
        if (container != null) {
            TextView tvWelcome = container.findViewById(R.id.tv_welcome);
            AutoCompleteTextView tvSearch = container.findViewById(R.id.tv_search);
            Spinner spinnerVehicleType = container.findViewById(R.id.spinner_vehicle_type);
            Spinner spinnerStatus = container.findViewById(R.id.spinner_status);
            Button btnClearFilter = container.findViewById(R.id.btn_clear_filter);
            RecyclerView recyclerView = container.findViewById(R.id.vehicle_list);
            View fragmentContainer = container.findViewById(R.id.user_rent_vehicle_container);

            if (tvWelcome != null) tvWelcome.setVisibility(View.GONE);
            if (tvSearch != null) tvSearch.setVisibility(View.GONE);
            if (spinnerVehicleType != null) spinnerVehicleType.setVisibility(View.GONE);
            if (spinnerStatus != null) spinnerStatus.setVisibility(View.GONE);
            if (btnClearFilter != null) btnClearFilter.setVisibility(View.GONE);
            if (recyclerView != null) recyclerView.setVisibility(View.GONE);
            if (fragmentContainer != null) fragmentContainer.setVisibility(View.VISIBLE);
        }
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
            Bundle bundle = new Bundle();
            bundle.putSerializable("xe", xe);

            UserRentVehicleStart rentVehicleStartFragment = new UserRentVehicleStart();
            rentVehicleStartFragment.setArguments(bundle);

            hideHomepageElements();

            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    showHomepageElements();
                    this.remove();
                }
            };
            parentFragment.requireActivity().getOnBackPressedDispatcher().addCallback(parentFragment, callback);
            
            FragmentTransaction transaction = parentFragment.getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.user_rent_vehicle_container, rentVehicleStartFragment);
            transaction.addToBackStack(null);
            transaction.commit();
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
