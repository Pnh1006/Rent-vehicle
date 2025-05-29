package com.example.afinal.Database.Model;

import java.io.Serializable;

public class NguoiDung implements Serializable {
    private int maND;
    private String hoTen;
    private String sdt;
    private String matKhau;
    private String cccd;
    private String ngaySinh;
    private String gioiTinh;
    private String ngayDangKy;
    private int vaiTro;
    private int trangThai;

    public NguoiDung(int maND, String hoTen, String sdt, String matKhau, String cccd, String ngaySinh, String gioiTinh, String ngayDangKy, int vaiTro, int trangThai) {
        this.maND = maND;
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.matKhau = matKhau;
        this.cccd = cccd;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.ngayDangKy = ngayDangKy;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
    }

    public NguoiDung() {

    }

    public int getMaND() {
        return maND;
    }

    public void setMaND(int maND) {
        this.maND = maND;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getNgayDangKy() {
        return ngayDangKy;
    }

    public void setNgayDangKy(String ngayDangKy) {
        this.ngayDangKy = ngayDangKy;
    }

    public int getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(int vaiTro) {
        this.vaiTro = vaiTro;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }
}
