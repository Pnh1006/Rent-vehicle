package com.example.afinal.Interfaces.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.afinal.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import vn.zalopay.sdk.ZaloPaySDK;

/**
 * @noinspection ALL
 */
public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);
        bottomNavigationView = findViewById(R.id.user_bottom_nav);

        viewPager = findViewById(R.id.view_pager);
        setUpViewPager();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_user_homepage) {
                    viewPager.setCurrentItem(0);
                } else if (item.getItemId() == R.id.nav_user_rent_vehicle) {
                    viewPager.setCurrentItem(1);
                } else if (item.getItemId() == R.id.nav_user_history) {
                    viewPager.setCurrentItem(2);
                } else if (item.getItemId() == R.id.nav_user_setting) {
                    viewPager.setCurrentItem(3);
                }
                return true;
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("ZaloPay", "onNewIntent called");
        ZaloPaySDK.getInstance().onResult(intent);
    }

    private void setUpViewPager() {
        UserViewPagerAdapter userViewPagerAdapter = new UserViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(userViewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.nav_user_homepage).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.nav_user_rent_vehicle).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.nav_user_history).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.nav_user_setting).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}