package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.nex3z.notificationbadge.NotificationBadge;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.messaging.FirebaseMessaging;
public class Dish_Detail extends AppCompatActivity {

    TextView tensp, giasp, mota;
    Button btnthem;
    ImageView imghinhanh;
    Spinner spinner;
    Toolbar toolbar;
    Dish dish;
    NotificationBadge badge;
    DatabaseHelper databaseHelper;
    public static List<GioHang> magiohang = new ArrayList<>();

    private int customerId; // Added customerId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_detail);

        initview();
        initData();
        initControl();
        ActionToolBar();

    }

    private void initview() {
        tensp = findViewById(R.id.txttensp);
        giasp = findViewById(R.id.txtgia);
        mota = findViewById(R.id.txtmotachitiet);
        btnthem = findViewById(R.id.btnthemvaogiohang);
        spinner = findViewById(R.id.spinner);
        imghinhanh = findViewById(R.id.imgchitiet);
        toolbar = findViewById(R.id.toolbar);
        badge = findViewById(R.id.menu_sl);


        FrameLayout frameLayoutgiohang = findViewById(R.id.framegiohang);
        frameLayoutgiohang.setOnClickListener(v -> {
            Intent giohang = new Intent(Dish_Detail.this, GioHangActivity.class);
            startActivity(giohang);
        });
    }

    private void initData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String dishId = extras.getString("dish_id");
            String dishName = extras.getString("dish_name");
            int gia = extras.getInt("dish_gia");
            String mota1 = extras.getString("dish_mota");
            String imageName = extras.getString("dish_image");
            int quantity = extras.getInt("dish_quantity");

            dish = new Dish(dishId, dishName, mota1, gia, quantity, imageName);

            Integer[] so = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
            ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, so);
            spinner.setAdapter(adapter);

            tensp.setText(dishName);
            giasp.setText(String.valueOf(gia));
            mota.setText(mota1);

            int imageResId = getDrawableResIdByName(imageName);
            if (imageResId != 0) {
                imghinhanh.setImageResource(imageResId);
            }
        }
    }

    private void initControl() {
        btnthem.setOnClickListener(v -> themgiohang());
    }

    private void themgiohang() {
        if (dish == null) {
            return;
        }

        int soluong = Integer.parseInt(spinner.getSelectedItem().toString());
        boolean exists = false;

        for (GioHang gioHang : magiohang) {
            if (gioHang.getIdgh().equals(dish.getDishId())) {
                gioHang.setSoluonggh(gioHang.getSoluonggh() + soluong);
                gioHang.setGiagh(dish.getPrice());
                exists = true;
                break;
            }
        }

        if (!exists) {
            GioHang gioHang = new GioHang(dish.getDishId(), dish.getName(), dish.getPrice(), dish.getImage(), soluong);
            magiohang.add(gioHang);
        }

        badge.setText(String.valueOf(magiohang.size()));
    }

    private int getDrawableResIdByName(String resName) {
        String pkgName = getPackageName();
        return getResources().getIdentifier(resName, "drawable", pkgName);
    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
    }



}
