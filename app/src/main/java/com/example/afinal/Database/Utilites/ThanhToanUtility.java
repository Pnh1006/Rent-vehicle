package com.example.afinal.Database.Utilites;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class ThanhToanUtility {
    public static String getTrangThaiText(int trangThai) {
        switch (trangThai) {
            case 0:
                return "Chờ thanh toán";
            case 1:
                return "Đã thanh toán";
            case 2:
                return "Đã hoàn tiền";
            case 3:
                return "Đã hủy";
            default:
                return "Không xác định";
        }
    }

    public static String formatSoTien(int soTien) {
        return String.format("%,d VNĐ", soTien);
    }

    public static String formatNgayThanhToan(String ngay) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(ngay);
            return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        } catch (Exception e) {
            return ngay;
        }
    }

    public static boolean isValidPhuongThuc(String phuongThuc) {
        String[] validMethods = {
                "Tiền mặt", "Chuyển khoản", "Ví điện tử", "Thẻ tín dụng"
        };
        return Arrays.asList(validMethods).contains(phuongThuc);
    }

    public static String generateMaGiaoDich() {
        // Tạo mã giao dịch theo format: TT + timestamp
        return "TT" + System.currentTimeMillis();
    }

    public static boolean canRefund(int trangThai) {
        // Chỉ có thể hoàn tiền cho giao dịch đã thanh toán
        return trangThai == 1;
    }

    public static int calculateRefundAmount(int soTien) {
        // Tính số tiền hoàn lại (ví dụ: hoàn 90% nếu hủy sớm)
        return (int)(soTien * 0.9);
    }
}
