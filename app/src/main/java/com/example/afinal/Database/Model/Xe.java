package com.example.afinal.Database.Model;

import java.io.Serializable;

public class Xe implements Serializable {
    private int maXe;
    private String tenXe;
    private String bienSo;
    private String hinhAnh;
    private int giaThue;
    private int namSX;
    private String mauSac;
    private String loaiXe;
    private int trangThai;

    public Xe(int maXe, String tenXe, String bienSo, String hinhAnh, int giaThue, int namSX, String mauSac, String loaiXe, int trangThai) {
        this.maXe = maXe;
        this.tenXe = tenXe;
        this.bienSo = bienSo;
        this.hinhAnh = hinhAnh;
        this.giaThue = giaThue;
        this.namSX = namSX;
        this.mauSac = mauSac;
        this.loaiXe = loaiXe;
        this.trangThai = trangThai;
    }

    public Xe() {
    }

    public int getMaXe() {
        return maXe;
    }

    public void setMaXe(int maXe) {
        this.maXe = maXe;
    }

    public String getTenXe() {
        return tenXe;
    }

    public void setTenXe(String tenXe) {
        this.tenXe = tenXe;
    }

    public String getBienSo() {
        return bienSo;
    }

    public void setBienSo(String bienSo) {
        this.bienSo = bienSo;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public int getGiaThue() {
        return giaThue;
    }

    public void setGiaThue(int giaThue) {
        this.giaThue = giaThue;
    }

    public int getNamSX() {
        return namSX;
    }

    public void setNamSX(int namSX) {
        this.namSX = namSX;
    }

    public String getMauSac() {
        return mauSac;
    }

    public void setMauSac(String mauSac) {
        this.mauSac = mauSac;
    }

    public String getLoaiXe() {
        return loaiXe;
    }

    public void setLoaiXe(String loaiXe) {
        this.loaiXe = loaiXe;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }
}
