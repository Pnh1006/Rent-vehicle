package com.example.afinal.Interfaces.Admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.afinal.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/** @noinspection deprecation*/
public class AdminHomepage extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_homepage);
        bottomNavigationView = findViewById(R.id.admin_bottom_nav);

        viewPager = findViewById(R.id.view_pager);
        setUpViewPager();

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_admin_homepage) {
                viewPager.setCurrentItem(0);
            } else if (item.getItemId() == R.id.nav_admin_account) {
                viewPager.setCurrentItem(1);
            } else if (item.getItemId() == R.id.nav_admin_transaction) {
                viewPager.setCurrentItem(2);
            } else if (item.getItemId() == R.id.nav_admin_vehicle) {
                viewPager.setCurrentItem(3);
            }
            return true;
        });
    }

    private void setUpViewPager() {
        AdminViewPagerAdapter adminViewPagerAdapter = new AdminViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adminViewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.nav_admin_homepage).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.nav_admin_account).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.nav_admin_transaction).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.nav_admin_vehicle).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}