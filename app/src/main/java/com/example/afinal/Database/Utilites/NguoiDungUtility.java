package com.example.afinal.Database.Utilites;

import android.content.Context;
import android.widget.Toast;
import android.util.Patterns;

import com.example.afinal.Database.DAO.NguoiDungDAO;
import com.example.afinal.Database.Model.NguoiDung;
import com.example.afinal.R;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NguoiDungUtility {
    private static NguoiDungUtility instance;
    private final NguoiDungDAO nguoiDungDAO;
    private final Context context;
    private NguoiDung currentUser;

    private NguoiDungUtility(Context context) {
        this.context = context.getApplicationContext();
        this.nguoiDungDAO = new NguoiDungDAO(context);
    }

    public static synchronized NguoiDungUtility getInstance(Context context) {
        if (instance == null) {
            instance = new NguoiDungUtility(context);
        }
        return instance;
    }

    // Validation methods
    public static boolean validateInput(String hoTen, String sdt, String matKhau, String cccd) {
        if (hoTen == null || hoTen.trim().length() < 2) {
            return false;
        }

        if (sdt == null || !Patterns.PHONE.matcher(sdt).matches()) {
            return false;
        }

        if (matKhau == null || matKhau.length() < 6) {
            return false;
        }

        if (cccd == null || cccd.length() != 12 || !cccd.matches("\\d+")) {
            return false;
        }

        return true;
    }

    public static boolean validatePassword(String matKhau) {
        return matKhau != null && matKhau.length() >= 6;
    }

    public static boolean validatePhone(String sdt) {
        return sdt != null && Patterns.PHONE.matcher(sdt).matches();
    }

    public static boolean validateCCCD(String cccd) {
        return cccd != null && cccd.length() == 12 && cccd.matches("\\d+");
    }

    // Status and role text methods
    public static String getTrangThaiText(Context context, int trangThai) {
        String[] trangThaiArray = context.getResources().getStringArray(R.array.trang_thai_tai_khoan);
        if (trangThai >= 0 && trangThai < trangThaiArray.length) {
            return trangThaiArray[trangThai];
        }
        return "Không xác định";
    }

    public static String getVaiTroText(Context context, int vaiTro) {
        String[] vaiTroArray = context.getResources().getStringArray(R.array.vai_tro_tai_khoan);
        if (vaiTro >= 0 && vaiTro < vaiTroArray.length) {
            return vaiTroArray[vaiTro];
        }
        return "Không xác định";
    }

    // Date formatting methods
    public static String formatNgaySinh(String ngaySinh) {
        try {
            LocalDate date = LocalDate.parse(ngaySinh);
            return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return ngaySinh;
        }
    }

    public static String formatNgayDangKy(String ngayDangKy) {
        try {
            LocalDate date = LocalDate.parse(ngayDangKy);
            return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        } catch (Exception e) {
            return ngayDangKy;
        }
    }

    public static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Registration and login methods
    public boolean dangKyNguoiDung(String hoTen, String sdt, String matKhau, String cccd,
                                   String ngaySinh, String gioiTinh) {
        try {
            if (!validateInput(hoTen, sdt, matKhau, cccd)) {
                showToast("Thông tin không hợp lệ!");
                return false;
            }

            if (nguoiDungDAO.checkExistSDT(sdt)) {
                showToast("Số điện thoại đã được đăng ký!");
                return false;
            }

            NguoiDung nguoiDung = new NguoiDung();
            nguoiDung.setHoTen(hoTen.trim());
            nguoiDung.setSdt(sdt.trim());
            nguoiDung.setMatKhau(matKhau);
            nguoiDung.setCccd(cccd.trim());
            nguoiDung.setNgaySinh(ngaySinh);
            nguoiDung.setGioiTinh(gioiTinh);
            nguoiDung.setNgayDangKy(getCurrentDateTime());
            nguoiDung.setVaiTro(0); // 0: Người dùng thường
            nguoiDung.setTrangThai(1); // 1: Hoạt động

            long result = nguoiDungDAO.insert(nguoiDung);
            if (result > 0) {
                showToast("Đăng ký thành công!");
                return true;
            } else {
                showToast("Đăng ký thất bại!");
                return false;
            }
        } catch (Exception e) {
            showToast("Lỗi: " + e.getMessage());
            return false;
        }
    }

    public boolean dangNhap(String sdt, String matKhau) {
        try {
            if (sdt == null || matKhau == null) {
                showToast("Vui lòng nhập đầy đủ thông tin!");
                return false;
            }

            NguoiDung nguoiDung = nguoiDungDAO.Login(sdt, matKhau);
            if (nguoiDung != null) {
                if (nguoiDung.getTrangThai() == 0) {
                    showToast("Tài khoản đã bị khóa!");
                    return false;
                }
                currentUser = nguoiDung;
                showToast("Đăng nhập thành công!");
                return true;
            } else {
                showToast("Số điện thoại hoặc mật khẩu không đúng!");
                return false;
            }
        } catch (Exception e) {
            showToast("Lỗi: " + e.getMessage());
            return false;
        }
    }

    public boolean capNhatThongTin(NguoiDung nguoiDung) {
        try {
            if (!validateInput(nguoiDung.getHoTen(), nguoiDung.getSdt(),
                    nguoiDung.getMatKhau(), nguoiDung.getCccd())) {
                return false;
            }

            int result = nguoiDungDAO.update(nguoiDung);
            if (result > 0) {
                if (currentUser != null && currentUser.getMaND() == nguoiDung.getMaND()) {
                    currentUser = nguoiDung; // Cập nhật thông tin người dùng hiện tại
                }
                showToast("Cập nhật thành công!");
                return true;
            } else {
                showToast("Cập nhật thất bại!");
                return false;
            }
        } catch (Exception e) {
            showToast("Lỗi: " + e.getMessage());
            return false;
        }
    }

    public boolean xoaNguoiDung(int maND) {
        try {
            int result = nguoiDungDAO.delete(maND);
            if (result > 0) {
                showToast("Xóa thành công!");
                return true;
            } else {
                showToast("Xóa thất bại!");
                return false;
            }
        } catch (Exception e) {
            showToast("Lỗi: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<NguoiDung> layDanhSachNguoiDung() {
        return nguoiDungDAO.getAll();
    }

    public NguoiDung getCurrentUser() {
        return currentUser;
    }

    public void dangXuat() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getVaiTro() == 1;
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public boolean doiMatKhau(String matKhauCu, String matKhauMoi) {
        if (currentUser == null) {
            showToast("Vui lòng đăng nhập!");
            return false;
        }

        if (currentUser.getMatKhau().equals(matKhauCu)) {
            currentUser.setMatKhau(matKhauMoi);
            if (capNhatThongTin(currentUser)) {
                showToast("Đổi mật khẩu thành công!");
                return true;
            }
        } else {
            showToast("Mật khẩu cũ không đúng!");
        }
        return false;
    }

    public boolean kiemTraSDTTonTai(String sdt) {
        return nguoiDungDAO.checkExistSDT(sdt);
    }

    public NguoiDung timNguoiDungTheoSDT(String sdt) {
        ArrayList<NguoiDung> danhSach = nguoiDungDAO.getAll();
        for (NguoiDung nd : danhSach) {
            if (nd.getSdt().equals(sdt)) {
                return nd;
            }
        }
        return null;
    }

    public ArrayList<NguoiDung> getAllNguoiDung() {
        return nguoiDungDAO.getAll();
    }

    public long insertNguoiDung(NguoiDung nguoiDung) {
        try {
            if (!validateInput(nguoiDung.getHoTen(), nguoiDung.getSdt(),
                    nguoiDung.getMatKhau(), nguoiDung.getCccd())) {
                showToast("Thông tin không hợp lệ!");
                return -1;
            }

            if (nguoiDungDAO.checkExistSDT(nguoiDung.getSdt())) {
                showToast("Số điện thoại đã được đăng ký!");
                return -1;
            }

            nguoiDung.setNgayDangKy(getCurrentDateTime());
            return nguoiDungDAO.insert(nguoiDung);
        } catch (Exception e) {
            showToast("Lỗi: " + e.getMessage());
            return -1;
        }
    }

    public int updateNguoiDung(NguoiDung nguoiDung) {
        try {
            if (!validateInput(nguoiDung.getHoTen(), nguoiDung.getSdt(),
                    nguoiDung.getMatKhau(), nguoiDung.getCccd())) {
                showToast("Thông tin không hợp lệ!");
                return -1;
            }

            int result = nguoiDungDAO.update(nguoiDung);
            if (result > 0) {
                if (currentUser != null && currentUser.getMaND() == nguoiDung.getMaND()) {
                    currentUser = nguoiDung;
                }
                showToast("Cập nhật thành công!");
            } else {
                showToast("Cập nhật thất bại!");
            }
            return result;
        } catch (Exception e) {
            showToast("Lỗi: " + e.getMessage());
            return -1;
        }
    }

    public int deleteNguoiDung(int maND) {
        try {
            int result = nguoiDungDAO.delete(maND);
            if (result > 0) {
                showToast("Xóa thành công!");
            } else {
                showToast("Xóa thất bại!");
            }
            return result;
        } catch (Exception e) {
            showToast("Lỗi: " + e.getMessage());
            return -1;
        }
    }
}
