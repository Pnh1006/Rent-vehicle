package com.example.afinal.Database.Utilites;

import android.content.Context;

import com.example.afinal.Database.DAO.ThueXeDAO;
import com.example.afinal.Database.Model.ThueXe;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ThueXeUtility {
    private static ThueXeUtility instance;
    private final ThueXeDAO thueXeDAO;

    private ThueXeUtility(Context context) {
        thueXeDAO = new ThueXeDAO(context);
    }

    public static synchronized ThueXeUtility getInstance(Context context) {
        if (instance == null) {
            instance = new ThueXeUtility(context.getApplicationContext());
        }
        return instance;
    }

    // Lấy tất cả đơn thuê xe
    public List<ThueXe> getAllThueXe() {
        return thueXeDAO.getAllThueXe();
    }

    // Thêm đơn thuê xe mới
    public long insertThueXe(ThueXe thueXe) {
        long result = thueXeDAO.insertThueXe(thueXe);
        return result;
    }

    // Cập nhật đơn thuê xe
    public long updateThueXe(ThueXe thueXe) {
        // Lấy trạng thái cũ của đơn thuê xe
        ThueXe oldThueXe = thueXeDAO.getThueXeById(thueXe.getMaThueXe());
        long result = thueXeDAO.updateThueXe(thueXe);

        if (result > 0) {
            // Cập nhật trạng thái xe dựa trên trạng thái mới của đơn thuê
            int trangThaiXe;
            switch (thueXe.getTrangThai()) {
                case 0: // Đang thuê
                case 2: // Đang xử lý
                    trangThaiXe = 1; // Đang được thuê
                    break;
                case 1: // Đã trả
                case 3: // Hủy
                    trangThaiXe = 0; // Hiện còn
                    break;
                default:
                    trangThaiXe = oldThueXe.getTrangThai();
            }
        }
        return result;
    }

    // Xóa đơn thuê xe
    public long deleteThueXe(int maThueXe) {
        // Lấy thông tin đơn thuê xe trước khi xóa
        ThueXe thueXe = thueXeDAO.getThueXeById(maThueXe);
        long result = thueXeDAO.deleteThueXe(maThueXe);

        return result;
    }

    // Lấy tên người dùng từ mã người dùng
    public String getTenNguoiDung(int maND) {
        return thueXeDAO.getTenNguoiDung(maND);
    }

    // Lấy tên xe từ mã xe
    public String getTenXe(int maXe) {
        return thueXeDAO.getTenXe(maXe);
    }

    // Chuyển đổi trạng thái số thành text
    public String getTrangThaiText(Context context, int trangThai) {
        switch (trangThai) {
            case 0:
                return "Chờ xác nhận";
            case 1:
                return "Đang thuê";
            case 2:
                return "Đã hoàn thành";
            case 3:
                return "Đã hủy";
            default:
                return "Không xác định";
        }
    }

    public static boolean isValidNgayDat(String ngayDat) {
        try {
            LocalDate.parse(ngayDat);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getCurrentDate() {
        return LocalDate.now().toString();
    }

    public static String formatNgayDat(String ngayDat) {
        try {
            LocalDate date = LocalDate.parse(ngayDat);
            return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return ngayDat;
        }
    }

    public static boolean canCancel(int trangThai) {
        // Chỉ có thể hủy đơn khi đang ở trạng thái chờ xác nhận
        return trangThai == 0;
    }

    public static boolean canComplete(int trangThai) {
        // Chỉ có thể hoàn thành đơn khi đang ở trạng thái đang thuê
        return trangThai == 1;
    }
}
