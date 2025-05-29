package com.example.afinal.Database.Model;

import java.io.Serializable;

public class ThanhToan implements Serializable {
    private int maThanhToan;
    private int maThueXe;
    private int maND;
    private int soTien;
    private String noiDung;
    private String ngayThucHien;
    private String ngayThanhCong;
    private String phuongThuc;
    private String maGiaoDich;
    private String ghiChu;
    private int trangThai;

    public ThanhToan(int maThanhToan, int maThueXe, int maND, int soTien, String noiDung, String ngayThucHien, String ngayThanhCong, String phuongThuc, String maGiaoDich, String ghiChu, int trangThai) {
        this.maThanhToan = maThanhToan;
        this.maThueXe = maThueXe;
        this.maND = maND;
        this.soTien = soTien;
        this.noiDung = noiDung;
        this.ngayThucHien = ngayThucHien;
        this.ngayThanhCong = ngayThanhCong;
        this.phuongThuc = phuongThuc;
        this.maGiaoDich = maGiaoDich;
        this.ghiChu = ghiChu;
        this.trangThai = trangThai;
    }

    public ThanhToan() {
    }

    public int getMaThanhToan() {
        return maThanhToan;
    }

    public void setMaThanhToan(int maThanhToan) {
        this.maThanhToan = maThanhToan;
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

    public int getSoTien() {
        return soTien;
    }

    public void setSoTien(int soTien) {
        this.soTien = soTien;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getNgayThucHien() {
        return ngayThucHien;
    }

    public void setNgayThucHien(String ngayThucHien) {
        this.ngayThucHien = ngayThucHien;
    }

    public String getNgayThanhCong() {
        return ngayThanhCong;
    }

    public void setNgayThanhCong(String ngayThanhCong) {
        this.ngayThanhCong = ngayThanhCong;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public String getMaGiaoDich() {
        return maGiaoDich;
    }

    public void setMaGiaoDich(String maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }
}
