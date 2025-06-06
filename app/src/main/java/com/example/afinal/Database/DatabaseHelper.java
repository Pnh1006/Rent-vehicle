package com.example.afinal.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ThueXeDB.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo các bảng
        createTables(db);
        // Thêm dữ liệu mẫu
        insertSampleData(db);
    }

    private void createTables(SQLiteDatabase db) {
        // Bảng NGUOIDUNG
        db.execSQL("CREATE TABLE NGUOIDUNG (" +
                "MaND INTEGER PRIMARY KEY AUTOINCREMENT," +
                "HoTen TEXT," +
                "SDT TEXT," +
                "MatKhau TEXT," +
                "CCCD TEXT," +
                "NgaySinh TEXT," +
                "GioiTinh TEXT," +
                "NgayDangKy TEXT," +
                "VaiTro INTEGER," +
                "TrangThai INTEGER" +
                ");");

        // Bảng XE
        db.execSQL("CREATE TABLE XE (" +
                "MaXe INTEGER PRIMARY KEY AUTOINCREMENT," +
                "TenXe TEXT," +
                "BienSo TEXT," +
                "HinhAnh TEXT," +
                "GiaThue INTEGER," +
                "NamSX INTEGER," +
                "MauSac TEXT," +
                "LoaiXe TEXT," +
                "TrangThai INTEGER" +
                ");");

        // Bảng THUEXE
        db.execSQL("CREATE TABLE THUEXE (" +
                "MaThueXe INTEGER PRIMARY KEY AUTOINCREMENT," +
                "MaND INTEGER," +
                "NgayDat TEXT," +
                "TrangThai INTEGER," +
                "FOREIGN KEY(MaND) REFERENCES NGUOIDUNG(MaND)" +
                ");");

        // Bảng CHITIETTHUEXE
        db.execSQL("CREATE TABLE CHITIETTHUEXE (" +
                "MaChiTiet INTEGER PRIMARY KEY AUTOINCREMENT," +
                "MaThueXe INTEGER," +
                "MaXe INTEGER," +
                "TienCoc INTEGER," +
                "NgayBatDauDK TEXT," +
                "NgayKetThucDK TEXT," +
                "NgayBatDauTT TEXT," +
                "NgayKetThucTT TEXT," +
                "ThanhTien INTEGER," +
                "GhiChu TEXT," +
                "FOREIGN KEY(MaThueXe) REFERENCES THUEXE(MaThueXe)," +
                "FOREIGN KEY(MaXe) REFERENCES XE(MaXe)" +
                ");");

        // Bảng THANHTOAN
        db.execSQL("CREATE TABLE THANHTOAN (" +
                "MaThanhToan INTEGER PRIMARY KEY AUTOINCREMENT," +
                "MaThueXe INTEGER," +
                "MaND INTEGER," +
                "SoTien INTEGER," +
                "NoiDung TEXT," +
                "NgayThucHien TEXT," +
                "NgayThanhCong TEXT," +
                "PhuongThuc TEXT," +
                "MaGiaoDich TEXT," +
                "GhiChu TEXT," +
                "TrangThai INTEGER," +
                "FOREIGN KEY(MaThueXe) REFERENCES THUEXE(MaThueXe)," +
                "FOREIGN KEY(MaND) REFERENCES NGUOIDUNG(MaND)" +
                ");");
    }

    private void insertSampleData(SQLiteDatabase db) {
        // Thêm dữ liệu mẫu cho bảng NGUOIDUNG
        String[] hoTen = {"Admin System", "Nguyễn Văn A", "Trần Thị B", "Lê Văn C", "Phạm Thị D",
                "Hoàng Văn E", "Đỗ Thị F", "Ngô Văn G", "Vũ Thị H", "Đặng Văn I"};
        String[] sdt = {"01006200310", "0901234567", "0912345678", "0923456789", "0934567890",
                "0945678901", "0956789012", "0967890123", "0978901234", "0989012345"};
        String[] matKhau = {"06102003", "password1", "password2", "password3", "password4",
                "password5", "password6", "password7", "password8", "password9"};
        String[] cccd = {"000000000000", "123456789012", "234567890123", "345678901234", "456789012345",
                "567890123456", "678901234567", "789012345678", "890123456789", "901234567890"};

        for (int i = 0; i < 10; i++) {
            String gioiTinh = i % 3 == 0 ? "Nam" : (i % 3 == 1 ? "Nữ" : "Khác");
            int vaiTro = i == 0 ? 1 : 0; // Admin cho user đầu tiên
            int trangThai = 1; // Mặc định tất cả tài khoản đều hoạt động

            db.execSQL("INSERT INTO NGUOIDUNG (HoTen, SDT, MatKhau, CCCD, NgaySinh, GioiTinh, NgayDangKy, VaiTro, TrangThai) " +
                            "VALUES (?, ?, ?, ?, '2000-01-01', ?, '2024-01-01', ?, ?)",
                    new Object[]{hoTen[i], sdt[i], matKhau[i], cccd[i], gioiTinh, vaiTro, trangThai});
        }

        // Thêm dữ liệu mẫu cho bảng XE
        String[] tenXe = {"Honda Wave Alpha", "Yamaha Sirius", "Honda Vision", "Yamaha Janus", "Honda Air Blade",
                "Yamaha Exciter", "Honda Winner X", "Yamaha NVX", "Honda SH", "Yamaha Grande"};
        String[] bienSo = {"59P1-12345", "59P1-23456", "59P1-34567", "59P1-45678", "59P1-56789",
                "59P1-67890", "59P1-78901", "59P1-89012", "59P1-90123", "59P1-01234"};

        for (int i = 0; i < 10; i++) {
            String loaiXe = i % 2 == 0 ? "Xe số" : "Xe ga";
            String mauSac = i % 5 == 0 ? "Đỏ" : (i % 5 == 1 ? "Xanh dương" : (i % 5 == 2 ? "Đen" : (i % 5 == 3 ? "Trắng" : "Xám")));
            int namSX = 2019 + (i % 6); // Từ 2019 đến 2024
            int giaThue = 100000 + (i * 50000); // Từ 100,000 đến 550,000
            int trangThai = 0; // Mặc định tất cả xe đều hiện còn

            db.execSQL("INSERT INTO XE (TenXe, BienSo, HinhAnh, GiaThue, NamSX, MauSac, LoaiXe, TrangThai) " +
                            "VALUES (?, ?, 'xe_" + (i + 1) + ".jpg', ?, ?, ?, ?, ?)",
                    new Object[]{tenXe[i], bienSo[i], giaThue, namSX, mauSac, loaiXe, trangThai});
        }

        // Thêm dữ liệu mẫu cho bảng THUEXE và CHITIETTHUEXE
        // Tạo các đơn thuê xe với các trạng thái khác nhau cho người dùng thứ 2 (Nguyễn Văn A)
        int[] trangThaiDon = {0, 1, 2, 3}; // Đang thuê, Đang xử lý, Đã trả, Đã hủy
        for (int i = 0; i < 4; i++) {
            String ngayDat = String.format("2024-03-%02d", i + 1);
            
            // Insert vào bảng THUEXE
            db.execSQL("INSERT INTO THUEXE (MaND, NgayDat, TrangThai) VALUES (2, ?, ?)",
                    new Object[]{ngayDat, trangThaiDon[i]});

            // Lấy MaThueXe vừa insert
            int maThueXe = i + 1;
            int maXe = i + 1;
            int tienCoc = 500000;
            int thanhTien = 1000000;

            // Tạo ngày thuê dựa vào trạng thái
            String ngayBatDauDK = String.format("01/03/2024");
            String ngayKetThucDK = String.format("05/03/2024");
            String ngayBatDauTT = trangThaiDon[i] == 0 || trangThaiDon[i] == 1 ? 
                                String.format("01/03/2024") : "";
            String ngayKetThucTT = trangThaiDon[i] == 2 ? 
                                String.format("05/03/2024") : "";

            // Insert vào bảng CHITIETTHUEXE
            db.execSQL("INSERT INTO CHITIETTHUEXE (MaThueXe, MaXe, TienCoc, NgayBatDauDK, NgayKetThucDK, " +
                            "NgayBatDauTT, NgayKetThucTT, ThanhTien, GhiChu) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    new Object[]{maThueXe, maXe, tienCoc, ngayBatDauDK, ngayKetThucDK, 
                               ngayBatDauTT, ngayKetThucTT, thanhTien, "Ghi chú đơn " + (i + 1)});

            // Cập nhật trạng thái xe nếu đang được thuê
            if (trangThaiDon[i] == 0 || trangThaiDon[i] == 1) {
                db.execSQL("UPDATE XE SET TrangThai = 1 WHERE MaXe = ?", new Object[]{maXe});
            }

            // Insert vào bảng THANHTOAN
            String phuongThuc = i % 2 == 0 ? "ZaloPay" : "Tiền mặt";
            int trangThaiThanhToan;
            switch (trangThaiDon[i]) {
                case 0: // Đang thuê
                    trangThaiThanhToan = 0; // Đã thanh toán
                    break;
                case 1: // Đang xử lý
                    trangThaiThanhToan = 2; // Đang xử lý
                    break;
                case 2: // Đã trả
                    trangThaiThanhToan = 0; // Đã thanh toán
                    break;
                default: // Đã hủy
                    trangThaiThanhToan = 3; // Thất bại
                    break;
            }

            db.execSQL("INSERT INTO THANHTOAN (MaThueXe, MaND, SoTien, NoiDung, NgayThucHien, " +
                            "NgayThanhCong, PhuongThuc, MaGiaoDich, GhiChu, TrangThai) " +
                            "VALUES (?, 2, ?, 'Thanh toán tiền cọc', ?, ?, ?, ?, ?, ?)",
                    new Object[]{maThueXe, tienCoc, ngayDat, 
                               trangThaiThanhToan == 0 ? ngayDat : null,
                               phuongThuc, "GD" + (i + 1), 
                               "Thanh toán tiền cọc đơn " + (i + 1),
                               trangThaiThanhToan});
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS THANHTOAN");
        db.execSQL("DROP TABLE IF EXISTS CHITIETTHUEXE");
        db.execSQL("DROP TABLE IF EXISTS THUEXE");
        db.execSQL("DROP TABLE IF EXISTS XE");
        db.execSQL("DROP TABLE IF EXISTS NGUOIDUNG");
        onCreate(db);
    }
}
