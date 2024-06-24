package com.example.doan;

public class ThongKe {
    private String tensp;
    private int tong;

    public ThongKe(){

    }

    public String getTensp() {
        return tensp;
    }

    public void setTensp(String tensp) {
        this.tensp = tensp;
    }

    public int getTong() {
        return tong;
    }

    public ThongKe(String tensp, int tong) {
        this.tensp = tensp;
        this.tong = tong;
    }

    public void setTong(int tong) {
        this.tong = tong;
    }
}
