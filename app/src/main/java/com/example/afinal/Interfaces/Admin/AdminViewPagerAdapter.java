package com.example.afinal.Interfaces.Admin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.afinal.Interfaces.Admin.Account.FragmentAdminAccount;
import com.example.afinal.Interfaces.Admin.Homepage.FragmentAdminHomepage;
import com.example.afinal.Interfaces.Admin.Transaction.FragmentAdminTransaction;
import com.example.afinal.Interfaces.Admin.Vehicle.FragmentAdminVehicle;

public class AdminViewPagerAdapter extends FragmentStatePagerAdapter {
    public AdminViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new FragmentAdminAccount();
            case 2:
                return new FragmentAdminTransaction();
            case 3:
                return new FragmentAdminVehicle();
            case 0:
            default:
                return new FragmentAdminHomepage();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }


}
