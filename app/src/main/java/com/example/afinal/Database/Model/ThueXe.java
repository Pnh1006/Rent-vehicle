package com.example.afinal.Database.Model;

import java.io.Serializable;

public class ThueXe implements Serializable {
    private int maThueXe;
    private int maND;
    private String ngayDat;
    private int trangThai;

    public ThueXe(int maThueXe, int maND, String ngayDat, int trangThai) {
        this.maThueXe = maThueXe;
        this.maND = maND;
        this.ngayDat = ngayDat;
        this.trangThai = trangThai;
    }

    public ThueXe() {
    }

    public int getMaThueXe() {
        return maThueXe;
    }

    public void setMaThueXe(int maThueXe) {
        this.maThueXe = maThueXe;
    }

    public int getMaND() {
        return maND;
    }

    public void setMaND(int maND) {
        this.maND = maND;
    }

    public String getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(String ngayDat) {
        this.ngayDat = ngayDat;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }
}
