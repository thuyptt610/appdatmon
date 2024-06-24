package com.example.doan;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class MonthValueFormatter extends ValueFormatter {

    private final String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    @Override
    public String getFormattedValue(float value) {
        int monthIndex = (int) value - 1;
        if (monthIndex >= 0 && monthIndex < months.length) {
            return months[monthIndex];
        } else {
            return "";
        }
    }
}

