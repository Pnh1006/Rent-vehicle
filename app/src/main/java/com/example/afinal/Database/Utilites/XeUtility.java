package com.example.afinal.Database.Utilites;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.afinal.Database.DAO.XeDAO;
import com.example.afinal.Database.Model.Xe;
import com.example.afinal.R;

import java.util.List;

public class XeUtility {
    private final Context context;
    private final XeDAO xeDAO;

    public XeUtility(Context context) {
        this.context = context;
        this.xeDAO = new XeDAO(context);
    }

    public List<Xe> getAllXe() {
        return xeDAO.getAll();
    }

    public List<Xe> searchXe(String searchText, String loaiXe, Integer trangThai) {
        // Nếu không có điều kiện tìm kiếm, trả về tất cả
        if ((searchText == null || searchText.isEmpty()) &&
            (loaiXe == null || loaiXe.equals("Tất cả")) && 
            trangThai == null) {
            return getAllXe();
        }

        // Sử dụng phương thức của XeDAO để thực hiện tìm kiếm
        return xeDAO.searchXeWithConditions(searchText, loaiXe, trangThai);
    }

    public String getTrangThaiText(int trangThai) {
        String[] trangThaiArray = context.getResources().getStringArray(R.array.trang_thai_xe);
        if (trangThai >= 0 && trangThai < trangThaiArray.length) {
            return trangThaiArray[trangThai];
        }
        return "Không xác định";
    }

    public int getTrangThaiIndex(String trangThaiText) {
        String[] trangThaiArray = context.getResources().getStringArray(R.array.trang_thai_xe);
        for (int i = 0; i < trangThaiArray.length; i++) {
            if (trangThaiArray[i].equals(trangThaiText)) {
                return i;
            }
        }
        return -1;
    }

    public String[] getLoaiXe() {
        return context.getResources().getStringArray(R.array.loai_xe);
    }

    public static boolean isValidBienSo(String bienSo) {
        String pattern = "^\\d{2}[A-Z]-\\d{3}\\.\\d{2}$";
        return bienSo.matches(pattern);
    }

    @SuppressLint("DefaultLocale")
    public static String formatGiaThue(int giaThue) {
        return String.format("%,d VNĐ", giaThue);
    }

    public long insertXe(Xe xe) {
        return xeDAO.insert(xe);
    }

    public int updateXe(Xe xe) {
        return xeDAO.update(xe);
    }

    public int deleteXe(int maXe) {
        return xeDAO.delete(maXe);
    }

    public void close() {
        xeDAO.close();
    }
}
