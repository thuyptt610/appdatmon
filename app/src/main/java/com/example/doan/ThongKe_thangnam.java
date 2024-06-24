package com.example.doan;

public class ThongKe_thangnam {
    private int tongtienthang;
    private String thangNam;


    public ThongKe_thangnam() {}


    public ThongKe_thangnam(String thangNam, int tongtienthang) {
        this.tongtienthang = tongtienthang;
        this.thangNam = thangNam;
    }

    public int getTongtienthang() {
        return tongtienthang;
    }


    public void setTongtienthang(int tongtienthang) {
        this.tongtienthang = tongtienthang;
    }


    public String getThangNam() {
        return thangNam;
    }

    public void setThangNam(String thangNam) {
        this.thangNam = thangNam;
    }


    @Override
    public String toString() {
        return "ThongKe_thangnam{" +
                "tongtienthang=" + tongtienthang +
                ", thangNam='" + thangNam + '\'' +
                '}';
    }
}
