package com.example.doan;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Fragment_ThongKe extends Fragment {
    private Toolbar toolbar;
    private PieChart pieChart;
    private BarChart barChart;
    private DatabaseHelper databaseHelper;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private List<ThongKe_thang> thongKeThangList;
    private List<ThongKe> thongKeModels;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__thong_ke, container, false);
        initView(view);
        loadDataChart(); // Load dữ liệu chart
        setupBarChart();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_thongke, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.tkdoanhthu) {
            loadDishesFromDatabase(); // Load thống kê doanh thu
            return true;
        } else if (id == R.id.tkdoanhthumonth) {
            loadThongKeThang(); // Load thống kê theo tháng
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);
        databaseHelper = new DatabaseHelper(getContext());
        setupToolBar();
    }

    private void setupToolBar() {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
            }
        }
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    private void loadDataChart() {
        compositeDisposable.add(Completable.fromAction(() -> {

                     loadDishesFromDatabase(); // before
                    loadThongKeThang(); // after

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    private void loadDishesFromDatabase() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        thongKeModels = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT Dishes.nameD, SUM(Orders.quantity) as tong FROM Orders INNER JOIN Dishes ON Dishes.dishid = Orders.dishid GROUP BY Dishes.nameD", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String tensp = cursor.getString(cursor.getColumnIndexOrThrow("nameD"));
                int tong = cursor.getInt(cursor.getColumnIndexOrThrow("tong"));
                thongKeModels.add(new ThongKe(tensp, tong));
            } while (cursor.moveToNext());
            cursor.close();
            database.close();
            updatePieChart(thongKeModels);
        }
    }

    private void updatePieChart(List<ThongKe> thongKeModels) {
        if (thongKeModels != null && !thongKeModels.isEmpty()) {
            List<PieEntry> pieEntries = new ArrayList<>();
            for (ThongKe thongKe : thongKeModels) {
                pieEntries.add(new PieEntry(thongKe.getTong(), thongKe.getTensp()));
            }
            PieDataSet pieDataSet = new PieDataSet(pieEntries, "Thống kê");
            pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            PieData pieData = new PieData(pieDataSet);
            pieData.setValueTextSize(12f);
            pieChart.setData(pieData);
            pieChart.setUsePercentValues(true);
            pieChart.getDescription().setEnabled(false);
            pieChart.invalidate();
        }
    }
    /////////////////////////////////////// tháng

      private void setupBarChart() {
          barChart.getDescription().setEnabled(false);
          barChart.setDrawValueAboveBar(false);
          XAxis xAxis = barChart.getXAxis();
          xAxis.setGranularity(1f); // Đảm bảo các nhãn trên trục x được hiển thị cho mỗi tháng
          xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
          xAxis.setDrawGridLines(false);
          xAxis.setValueFormatter(new MonthValueFormatter()); // Sử dụng MonthValueFormatter

          YAxis yAxisRight = barChart.getAxisRight();
          yAxisRight.setAxisMinimum(0);
          YAxis yAxisLeft = barChart.getAxisLeft();
          yAxisLeft.setAxisMinimum(0);
      }
      private void updateBarChart() {
          if (thongKeThangList != null && !thongKeThangList.isEmpty()) {
              ArrayList<BarEntry> entries = new ArrayList<>();

              for (ThongKe_thang thongKe_thang : thongKeThangList) {
                  entries.add(new BarEntry(thongKe_thang.getThang(), thongKe_thang.getTongtienthang()));
              }

              BarDataSet barDataSet = new BarDataSet(entries, "Thống kê");
              barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
              BarData barData = new BarData(barDataSet);
              barData.setValueTextSize(12f);

              barChart.setData(barData);
              barChart.invalidate();
          }
      }
      private void loadThongKeThang() {
          SQLiteDatabase database = databaseHelper.getReadableDatabase();
          thongKeThangList = new ArrayList<>();
          String sql = "SELECT strftime('%m', substr(orderdate, 7, 4) || '-' || substr(orderdate, 4, 2) || '-' || substr(orderdate, 1, 2)) AS thang, SUM(total_price) AS tongtienthang FROM Orders GROUP BY thang";
          Cursor cursor = database.rawQuery(sql, null);

          if (cursor != null && cursor.moveToFirst()) {
              do {
                  int thang = cursor.getInt(cursor.getColumnIndexOrThrow("thang"));
                  int tongtienthang = cursor.getInt(cursor.getColumnIndexOrThrow("tongtienthang"));
                  thongKeThangList.add(new ThongKe_thang(thang, tongtienthang));
              } while (cursor.moveToNext());
              cursor.close();
          }

          updateBarChart();
          database.close();
      }


}
