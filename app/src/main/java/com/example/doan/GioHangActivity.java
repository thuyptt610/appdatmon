package com.example.doan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import java.util.List;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GioHangActivity extends AppCompatActivity {

    private TextView gioHangTrong, tongTien;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private GioHangAdapter adapter;
    private Button btnMuaHang;
    private List<GioHang> magiohang;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private int customerId; // Declare customerId variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);

        databaseHelper = new DatabaseHelper(this);
        initView();
        initControl();

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        customerId = sharedPreferences.getInt("customer_id", -1);

        if (customerId == -1) {
            Log.e("GioHangActivity", "Failed to retrieve customerId from SharedPreferences.");
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        magiohang = Dish_Detail.magiohang;
        updateCart();
    }

    private void initView() {
        gioHangTrong = findViewById(R.id.txtGiohangtrong);
        tongTien = findViewById(R.id.txtTongTien);
        recyclerView = findViewById(R.id.recycleviewGioHang);
        toolbar = findViewById(R.id.toolbar);
        btnMuaHang = findViewById(R.id.btnmuahang);
    }

    private void initControl() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        btnMuaHang.setOnClickListener(view -> handleOrder());
    }

    public void updateCart() {
        if (magiohang != null && !magiohang.isEmpty()) {
            adapter = new GioHangAdapter(this, magiohang, this);
            recyclerView.setAdapter(adapter);
            gioHangTrong.setVisibility(View.GONE);

            long totalPrice = 0;
            for (GioHang gioHang : magiohang) {
                totalPrice += gioHang.getSoluonggh() * gioHang.getGiagh();
            }
            tongTien.setText(String.format("%,d đ", totalPrice));
        } else {
            gioHangTrong.setVisibility(View.VISIBLE);
            tongTien.setText("0 đ");
        }
    }

    public void handleOrder() {
        db = databaseHelper.getWritableDatabase();

        if (db == null) {
            Log.e("Database Error", "Unable to get writable database.");
            Toast.makeText(this, "Database error. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("OrderDebug", "customerId: " + customerId);

        if (customerId != -1 && magiohang != null && !magiohang.isEmpty()) {
            db.beginTransaction();
            try {
                for (GioHang gioHang : magiohang) {
                    String orderId = UUID.randomUUID().toString();
                    String dishId = gioHang.getIdgh();
                    int quantity = gioHang.getSoluonggh();
                    long timestamp = System.currentTimeMillis();
                    long totalPrice = gioHang.getGiagh() * gioHang.getSoluonggh();

                    boolean success = databaseHelper.insertOrder(orderId, customerId, dishId, quantity, timestamp, totalPrice);

                    if (!success) {
                        db.endTransaction();
                        Toast.makeText(this, "Failed to place order for dish: " + dishId, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Cập nhật số lượng sản phẩm trong bảng Dishes
                    boolean updateSuccess = databaseHelper.decrementDishQuantity(dishId, quantity);
                    if (!updateSuccess) {
                        db.endTransaction();
                        Toast.makeText(this, "Failed to update dish quantity for dish: " + dishId, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                db.setTransactionSuccessful();
                magiohang.clear();
                updateCart();

                Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GioHangActivity.this, Fragment_trangchu.class);
                startActivity(intent);

            } catch (Exception e) {
                db.endTransaction();
                Log.e("OrderPlacement", "Error placing order: " + e.getMessage());
                // Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show();
            } finally {
                db.endTransaction();
            }
        } else {
            Toast.makeText(this, "Failed to place order, invalid customer or dish", Toast.LENGTH_SHORT).show();
        }
    }

}
