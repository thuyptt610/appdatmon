package com.example.doan;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Fragment_ThongKe_Month extends Fragment {

    private Toolbar toolbar;
    private EditText edtdaymonth;
    private Button btnSearch;
    private BarChart barChart;
    private DatabaseHelper databaseHelper;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private List<ThongKe_thangnam> thongKeThangnams;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__thong_ke__month, container, false);
        initView(view);
        loadDataChart();
        setupBarChart();

        btnSearch.setOnClickListener(v -> {
            String inputDate = edtdaymonth.getText().toString();
            if (!inputDate.isEmpty()) {
                searchAndUpdateChart(inputDate);
            }
        });

        return view;
    }

    private void initView(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        edtdaymonth = view.findViewById(R.id.edtdaymonth);
        btnSearch = view.findViewById(R.id.btnSearch);
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
                    loadThongKeThang(); // after
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
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

    private void updateBarChart(List<ThongKe_thangnam> thongKeThangnams) {
        if (thongKeThangnams != null && !thongKeThangnams.isEmpty()) {
            ArrayList<BarEntry> entries = new ArrayList<>();

            for (ThongKe_thangnam thongKe_thangnam : thongKeThangnams) {
                String[] parts = thongKe_thangnam.getThangNam().split("/");
                int thang = Integer.parseInt(parts[0]) ;
                entries.add(new BarEntry(thang, thongKe_thangnam.getTongtienthang()));
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
        thongKeThangnams = new ArrayList<>();
        String sql = "SELECT strftime('%m', substr(orderdate, 7, 4) || '-' || substr(orderdate, 4, 2) || '-' || substr(orderdate, 1, 2)) || '/' || substr(orderdate, 7, 4) AS thangNam, SUM(total_price) AS tongtienthang FROM Orders GROUP BY thangNam";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String thangNam = cursor.getString(cursor.getColumnIndexOrThrow("thangNam"));
                int tongtienthang = cursor.getInt(cursor.getColumnIndexOrThrow("tongtienthang"));
                thongKeThangnams.add(new ThongKe_thangnam(thangNam, tongtienthang));
            } while (cursor.moveToNext());
            cursor.close();
        }

        updateBarChart(thongKeThangnams);
        database.close();
    }

    private void searchAndUpdateChart(String inputDate) {
        String[] parts = inputDate.split("/");
        String month = parts[0]; // Lấy tháng
        String year = parts[1]; // Lấy năm

        List<ThongKe_thangnam> filteredData = new ArrayList<>();
        for (ThongKe_thangnam data : thongKeThangnams) {
            if (data.getThangNam().startsWith(month) && data.getThangNam().endsWith(year)) {
                filteredData.add(data);
            }
        }

        if (filteredData.isEmpty()) {
            showNoResultsDialog();
        } else {
            updateBarChart(filteredData);
        }
    }

    private void showNoResultsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Kết quả tìm kiếm");
        builder.setMessage("Không tìm thấy kết quả phù hợp.");
        builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
