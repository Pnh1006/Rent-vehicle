package com.example.afinal.Database.Model;

public class ChiTietThueXe {
    private int maChiTiet;
    private int maThueXe;
    private int maXe;
    private int tienCoc;
    private String ngayBatDauDK;
    private String ngayKetThucDK;
    private String ngayBatDauTT;
    private String ngayKetThucTT;
    private int thanhTien;
    private String ghiChu;

    public ChiTietThueXe(int maChiTiet, int maThueXe, int maXe, int tienCoc, String ngayBatDauDK, String ngayKetThucDK, String ngayBatDauTT, String ngayKetThucTT, int thanhTien, String ghiChu) {
        this.maChiTiet = maChiTiet;
        this.maThueXe = maThueXe;
        this.maXe = maXe;
        this.tienCoc = tienCoc;
        this.ngayBatDauDK = ngayBatDauDK;
        this.ngayKetThucDK = ngayKetThucDK;
        this.ngayBatDauTT = ngayBatDauTT;
        this.ngayKetThucTT = ngayKetThucTT;
        this.thanhTien = thanhTien;
        this.ghiChu = ghiChu;
    }

    public ChiTietThueXe() {
    }

    public int getMaChiTiet() {
        return maChiTiet;
    }

    public void setMaChiTiet(int maChiTiet) {
        this.maChiTiet = maChiTiet;
    }

    public int getMaThueXe() {
        return maThueXe;
    }

    public void setMaThueXe(int maThueXe) {
        this.maThueXe = maThueXe;
    }

    public int getMaXe() {
        return maXe;
    }

    public void setMaXe(int maXe) {
        this.maXe = maXe;
    }

    public int getTienCoc() {
        return tienCoc;
    }

    public void setTienCoc(int tienCoc) {
        this.tienCoc = tienCoc;
    }

    public String getNgayBatDauDK() {
        return ngayBatDauDK;
    }

    public void setNgayBatDauDK(String ngayBatDauDK) {
        this.ngayBatDauDK = ngayBatDauDK;
    }

    public String getNgayKetThucDK() {
        return ngayKetThucDK;
    }

    public void setNgayKetThucDK(String ngayKetThucDK) {
        this.ngayKetThucDK = ngayKetThucDK;
    }

    public String getNgayBatDauTT() {
        return ngayBatDauTT;
    }

    public void setNgayBatDauTT(String ngayBatDauTT) {
        this.ngayBatDauTT = ngayBatDauTT;
    }

    public String getNgayKetThucTT() {
        return ngayKetThucTT;
    }

    public void setNgayKetThucTT(String ngayKetThucTT) {
        this.ngayKetThucTT = ngayKetThucTT;
    }

    public int getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(int thanhTien) {
        this.thanhTien = thanhTien;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
