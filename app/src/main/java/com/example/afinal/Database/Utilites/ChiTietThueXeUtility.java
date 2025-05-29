package com.example.afinal.Database.Utilites;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ChiTietThueXeUtility {
    public static String formatNgay(String ngay) {
        try {
            LocalDate date = LocalDate.parse(ngay);
            return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return ngay;
        }
    }

    public static String formatTien(int tien) {
        return String.format("%,d VNĐ", tien);
    }

    public static int calculateThanhTien(int giaThueNgay, String ngayBatDau, String ngayKetThuc) {
        try {
            LocalDate startDate = LocalDate.parse(ngayBatDau);
            LocalDate endDate = LocalDate.parse(ngayKetThuc);
            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            return (int) (giaThueNgay * days);
        } catch (Exception e) {
            return 0;
        }
    }

    public static int calculateTienCoc(int thanhTien) {
        // Tiền cọc là 30% tổng tiền thuê
        return (int) (thanhTien * 0.3);
    }

    public static boolean isValidDateRange(String ngayBatDau, String ngayKetThuc) {
        try {
            LocalDate startDate = LocalDate.parse(ngayBatDau);
            LocalDate endDate = LocalDate.parse(ngayKetThuc);
            LocalDate today = LocalDate.now();

            // Ngày bắt đầu phải từ hôm nay trở đi
            // Ngày kết thúc phải sau ngày bắt đầu
            return !startDate.isBefore(today) && !endDate.isBefore(startDate);
        } catch (Exception e) {
            return false;
        }
    }

    public static int calculateSoNgayThue(String ngayBatDau, String ngayKetThuc) {
        try {
            LocalDate startDate = LocalDate.parse(ngayBatDau);
            LocalDate endDate = LocalDate.parse(ngayKetThuc);
            return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isRentalStarted(String ngayBatDauTT) {
        try {
            if (ngayBatDauTT == null) return false;
            LocalDate startDate = LocalDate.parse(ngayBatDauTT);
            LocalDate today = LocalDate.now();
            return !startDate.isAfter(today);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isRentalEnded(String ngayKetThucTT) {
        try {
            if (ngayKetThucTT == null) return false;
            LocalDate endDate = LocalDate.parse(ngayKetThucTT);
            LocalDate today = LocalDate.now();
            return !endDate.isAfter(today);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasDateOverlap(String start1, String end1, String start2, String end2) {
        try {
            LocalDate s1 = LocalDate.parse(start1);
            LocalDate e1 = LocalDate.parse(end1);
            LocalDate s2 = LocalDate.parse(start2);
            LocalDate e2 = LocalDate.parse(end2);

            return !(e1.isBefore(s2) || s1.isAfter(e2));
        } catch (Exception e) {
            return true; // Trả về true để an toàn, tránh trùng lịch
        }
    }
}