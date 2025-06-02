package com.example.afinal.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.afinal.Database.DatabaseHelper;
import com.example.afinal.Database.Model.NguoiDung;
import com.example.afinal.Database.Utilites.NguoiDungUtility;

import java.util.ArrayList;

public class NguoiDungDAO {
    private static final String TAG = "NguoiDungDAO";
    private final SQLiteDatabase db;
    private final Context context;

    public NguoiDungDAO(Context context) {
        this.context = context;
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    public long insert(NguoiDung nd) {
        try {
            ContentValues values = getContentValuesFromNguoiDung(nd);
            return db.insertOrThrow("NGUOIDUNG", null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error inserting nguoi dung: " + e.getMessage());
            return -1;
        }
    }

    public int update(NguoiDung nd) {
        try {
            ContentValues values = getContentValuesFromNguoiDung(nd);
            return db.update("NGUOIDUNG", values, "MaND = ?", new String[]{String.valueOf(nd.getMaND())});
        } catch (SQLiteException e) {
            Log.e(TAG, "Error updating nguoi dung: " + e.getMessage());
            return -1;
        }
    }

    public int delete(int maND) {
        try {
            return db.delete("NGUOIDUNG", "MaND = ?", new String[]{String.valueOf(maND)});
        } catch (SQLiteException e) {
            Log.e(TAG, "Error deleting nguoi dung: " + e.getMessage());
            return -1;
        }
    }

    public ArrayList<NguoiDung> getAll() {
        ArrayList<NguoiDung> list = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT * FROM NGUOIDUNG", null)) {
            if (c.moveToFirst()) {
                do {
                    list.add(getNguoiDungFromCursor(c));
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting all nguoi dung: " + e.getMessage());
        }
        return list;
    }

    public NguoiDung getById(int maND) {
        try (Cursor c = db.rawQuery("SELECT * FROM NGUOIDUNG WHERE MaND = ?", new String[]{String.valueOf(maND)})) {
            if (c.moveToFirst()) {
                return getNguoiDungFromCursor(c);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting nguoi dung by id: " + e.getMessage());
        }
        return null;
    }

    public NguoiDung getBySDT(String sdt) {
        try (Cursor c = db.rawQuery("SELECT * FROM NGUOIDUNG WHERE SDT = ?", new String[]{sdt})) {
            if (c.moveToFirst()) {
                return getNguoiDungFromCursor(c);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting nguoi dung by SDT: " + e.getMessage());
        }
        return null;
    }

    public NguoiDung getBySDTAndMatKhau(String sdt, String matKhau) {
        try (Cursor c = db.rawQuery("SELECT * FROM NGUOIDUNG WHERE SDT = ? AND MatKhau = ?", new String[]{sdt, matKhau})) {
            if (c.moveToFirst()) {
                return getNguoiDungFromCursor(c);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting nguoi dung by SDT and MatKhau: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<NguoiDung> getByVaiTro(int vaiTro) {
        ArrayList<NguoiDung> list = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT * FROM NGUOIDUNG WHERE VaiTro = ?", new String[]{String.valueOf(vaiTro)})) {
            if (c.moveToFirst()) {
                do {
                    list.add(getNguoiDungFromCursor(c));
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting nguoi dung by vai tro: " + e.getMessage());
        }
        return list;
    }

    public ArrayList<NguoiDung> getByTrangThai(int trangThai) {
        ArrayList<NguoiDung> list = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT * FROM NGUOIDUNG WHERE TrangThai = ?", new String[]{String.valueOf(trangThai)})) {
            if (c.moveToFirst()) {
                do {
                    list.add(getNguoiDungFromCursor(c));
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting nguoi dung by trang thai: " + e.getMessage());
        }
        return list;
    }

    public boolean checkExistSDT(String sdt) {
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM NGUOIDUNG WHERE SDT = ?", new String[]{sdt})) {
            if (c.moveToFirst()) {
                return c.getInt(0) > 0;
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error checking SDT existence: " + e.getMessage());
        }
        return false;
    }

    public boolean checkExistCCCD(String cccd) {
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM NGUOIDUNG WHERE CCCD = ?", new String[]{cccd})) {
            if (c.moveToFirst()) {
                return c.getInt(0) > 0;
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error checking CCCD existence: " + e.getMessage());
        }
        return false;
    }

    public NguoiDung Login(String sdt, String matKhau) {
        return getBySDTAndMatKhau(sdt, matKhau);
    }

    private ContentValues getContentValuesFromNguoiDung(NguoiDung nd) {
        ContentValues values = new ContentValues();
        values.put("HoTen", nd.getHoTen());
        values.put("SDT", nd.getSdt());
        values.put("MatKhau", nd.getMatKhau());
        values.put("CCCD", nd.getCccd());
        values.put("NgaySinh", nd.getNgaySinh());
        values.put("GioiTinh", nd.getGioiTinh());
        values.put("NgayDangKy", nd.getNgayDangKy());
        values.put("VaiTro", nd.getVaiTro());
        values.put("TrangThai", nd.getTrangThai());
        return values;
    }

    private NguoiDung getNguoiDungFromCursor(Cursor c) {
        NguoiDung nd = new NguoiDung();
        nd.setMaND(c.getInt(c.getColumnIndexOrThrow("MaND")));
        nd.setHoTen(c.getString(c.getColumnIndexOrThrow("HoTen")));
        nd.setSdt(c.getString(c.getColumnIndexOrThrow("SDT")));
        nd.setMatKhau(c.getString(c.getColumnIndexOrThrow("MatKhau")));
        nd.setCccd(c.getString(c.getColumnIndexOrThrow("CCCD")));
        nd.setNgaySinh(c.getString(c.getColumnIndexOrThrow("NgaySinh")));
        nd.setGioiTinh(c.getString(c.getColumnIndexOrThrow("GioiTinh")));
        nd.setNgayDangKy(c.getString(c.getColumnIndexOrThrow("NgayDangKy")));
        nd.setVaiTro(c.getInt(c.getColumnIndexOrThrow("VaiTro")));
        nd.setTrangThai(c.getInt(c.getColumnIndexOrThrow("TrangThai")));
        return nd;
    }

    // Utility methods now use NguoiDungUtility
    public String getTrangThaiText(int trangThai) {
        return NguoiDungUtility.getTrangThaiText(context, trangThai);
    }

    public String getVaiTroText(int vaiTro) {
        return NguoiDungUtility.getVaiTroText(context, vaiTro);
    }

    public String formatNgaySinh(String ngaySinh) {
        return NguoiDungUtility.formatNgaySinh(ngaySinh);
    }

    public String formatNgayDangKy(String ngayDangKy) {
        return NguoiDungUtility.formatNgayDangKy(ngayDangKy);
    }

    public boolean validatePhone(String sdt) {
        return NguoiDungUtility.validatePhone(sdt);
    }

    public boolean validateCCCD(String cccd) {
        return NguoiDungUtility.validateCCCD(cccd);
    }
}
