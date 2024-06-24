package com.example.doan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;
    private String nameC, phone, email, username, password, imageResName;
    private int customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Lấy các thông tin từ Intent
        nameC = getIntent().getStringExtra("nameC");
        phone = getIntent().getStringExtra("phone");
        email = getIntent().getStringExtra("email");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        imageResName = getIntent().getStringExtra("imageResName");
        customerId = getIntent().getIntExtra("customerid", -1);

        Log.d("MainActivity", "nameC: " + nameC); // Thêm log

        addControls();
        addEvents();
        loadFragment(new Fragment_trangchu(), false);
    }

    void addControls() {
        bottomNav = findViewById(R.id.bottomNav);
    }

    void addEvents() {
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menuTrangChu) {
                loadFragment(new Fragment_trangchu(), false);
            } else if (itemId == R.id.menuDonHang) {
                //loadFragment(new Fragment_Order_User(), false);
                loadFragment(Fragment_Order_User.newInstance(nameC), false); // Sửa ở đây
            } else if (itemId == R.id.menuPiechart) {
                loadFragment(new Fragment_ThongKe(), false);
            }else if (itemId == R.id.menuBarchart) {
                loadFragment(new Fragment_ThongKe_Month(), false);

            } else {
                Fragment_user_info fragment = Fragment_user_info.newInstance(nameC, phone, email, username, password, imageResName, customerId);
                loadFragment(fragment, false);
            }

            return true;
        });
    }

    void loadFragment(Fragment fragment, boolean isAppInit) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInit) {
            fragmentTransaction.add(R.id.frameLayout, fragment);
        } else {
            fragmentTransaction.replace(R.id.frameLayout, fragment);
        }

        fragmentTransaction.commit();
    }
}
