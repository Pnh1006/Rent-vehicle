package com.example.afinal.Interfaces.User;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.afinal.Interfaces.User.Homepage.FragmentUserHomepage;
import com.example.afinal.Interfaces.User.RentVehicle.FragmentUserRentVehicle;
import com.example.afinal.Interfaces.User.RentalHistory.FragmentUserRentalHistory;
import com.example.afinal.Interfaces.User.Setting.FragmentUserSetting;

public class UserViewPagerAdapter extends FragmentStatePagerAdapter {
    public UserViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new FragmentUserRentVehicle();
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
