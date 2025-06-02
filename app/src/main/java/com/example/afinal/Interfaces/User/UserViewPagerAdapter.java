package com.example.afinal.Interfaces.User;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.afinal.Database.Model.Xe;
import com.example.afinal.Interfaces.User.Homepage.FragmentUserHomepage;
import com.example.afinal.Interfaces.User.RentVehicle.FragmentUserRentVehicle;
import com.example.afinal.Interfaces.User.RentalHistory.FragmentUserRentalHistory;
import com.example.afinal.Interfaces.User.Setting.FragmentUserSetting;

public class UserViewPagerAdapter extends FragmentStatePagerAdapter {
    private Xe selectedXe;
    private boolean isFromHomepage;
    private FragmentUserRentVehicle rentFragment;

    public UserViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public void setSelectedXe(Xe xe, boolean fromHomepage) {
        this.selectedXe = xe;
        this.isFromHomepage = fromHomepage;
        if (rentFragment != null) {
            Bundle args = new Bundle();
            args.putSerializable("xe", selectedXe);
            args.putBoolean("isFromHomepage", true);
            rentFragment.setArguments(args);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (object instanceof FragmentUserRentVehicle && isFromHomepage) {
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                rentFragment = new FragmentUserRentVehicle();
                if (selectedXe != null && isFromHomepage) {
                    Bundle args = new Bundle();
                    args.putSerializable("xe", selectedXe);
                    args.putBoolean("isFromHomepage", true);
                    rentFragment.setArguments(args);
                }
                return rentFragment;
            case 2:
                return new FragmentUserRentalHistory();
            case 3:
                return new FragmentUserSetting();
            case 0:
            default:
                return new FragmentUserHomepage();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
