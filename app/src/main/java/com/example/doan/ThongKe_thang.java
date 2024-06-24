package com.example.doan;

public class ThongKe_thang {
    private int tongtienthang;
    private int thang;
    public ThongKe_thang() {
        // Constructor mặc định
    }

    public ThongKe_thang(int thang, int tongtienthang) {
        this.tongtienthang = tongtienthang;
        this.thang = thang;
    }

    public int getTongtienthang() {
        return tongtienthang;
    }

    public void setTongtienthang(int tongtienthang) {
        this.tongtienthang = tongtienthang;
    }

    public int getThang() {
        return thang;
    }

    public void setThang(int thang) {
        this.thang = thang;
    }
}
