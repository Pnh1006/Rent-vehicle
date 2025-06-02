package com.example.afinal.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.afinal.Database.DatabaseHelper;
import com.example.afinal.Database.Model.ThueXe;
import com.example.afinal.Database.Utilites.ThueXeUtility;

import java.util.ArrayList;
import java.util.List;

public class ThueXeDAO {
    private static final String TAG = "ThueXeDAO";
    private final SQLiteDatabase db;
    private final Context context;
    private final DatabaseHelper dbHelper;

    public ThueXeDAO(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Create operations
    public long insert(ThueXe tx) {
        try {
            ContentValues values = getContentValuesFromThueXe(tx);
            return db.insertOrThrow("THUEXE", null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error inserting thue xe: " + e.getMessage());
            return -1;
        }
    }

    public ArrayList<ThueXe> getAll() {
        ArrayList<ThueXe> list = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT * FROM THUEXE", null)) {
            if (c.moveToFirst()) {
                do {
                    list.add(getThueXeFromCursor(c));
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting all thue xe: " + e.getMessage());
        }
        return list;
    }

    public ArrayList<ThueXe> getByMaND(int maND) {
        ArrayList<ThueXe> list = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT * FROM THUEXE WHERE MaND = ?",
                new String[]{String.valueOf(maND)})) {
            if (c.moveToFirst()) {
                do {
                    list.add(getThueXeFromCursor(c));
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting thue xe by MaND: " + e.getMessage());
        }
        return list;
    }

    public ThueXe getById(int maThueXe) {
        try (Cursor c = db.rawQuery("SELECT * FROM THUEXE WHERE MaThueXe = ?",
                new String[]{String.valueOf(maThueXe)})) {
            if (c.moveToFirst()) {
                return getThueXeFromCursor(c);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting thue xe by id: " + e.getMessage());
        }
        return null;
    }

    public int update(ThueXe tx) {
        try {
            ContentValues values = getContentValuesFromThueXe(tx);
            return db.update("THUEXE", values, "MaThueXe = ?",
                    new String[]{String.valueOf(tx.getMaThueXe())});
        } catch (SQLiteException e) {
            Log.e(TAG, "Error updating thue xe: " + e.getMessage());
            return -1;
        }
    }

    public int updateStatus(int maThueXe, int newStatus) {
        try {
            ContentValues values = new ContentValues();
            values.put("TrangThai", newStatus);
            return db.update("THUEXE", values, "MaThueXe = ?",
                    new String[]{String.valueOf(maThueXe)});
        } catch (SQLiteException e) {
            Log.e(TAG, "Error updating thue xe status: " + e.getMessage());
            return -1;
        }
    }

    public int delete(int maThueXe) {
        try {
            return db.delete("THUEXE", "MaThueXe = ?",
                    new String[]{String.valueOf(maThueXe)});
        } catch (SQLiteException e) {
            Log.e(TAG, "Error deleting thue xe: " + e.getMessage());
            return -1;
        }
    }

    public ArrayList<ThueXe> getByStatus(int trangThai) {
        ArrayList<ThueXe> list = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT * FROM THUEXE WHERE TrangThai = ?",
                new String[]{String.valueOf(trangThai)})) {
            if (c.moveToFirst()) {
                do {
                    list.add(getThueXeFromCursor(c));
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting thue xe by status: " + e.getMessage());
        }
        return list;
    }

    public ArrayList<ThueXe> getByDateRange(String startDate, String endDate) {
        ArrayList<ThueXe> list = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT * FROM THUEXE WHERE NgayDat BETWEEN ? AND ?",
                new String[]{startDate, endDate})) {
            if (c.moveToFirst()) {
                do {
                    list.add(getThueXeFromCursor(c));
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting thue xe by date range: " + e.getMessage());
        }
        return list;
    }

    public ArrayList<ThueXe> searchAdvanced(Integer maND, Integer trangThai, String startDate, String endDate) {
        ArrayList<ThueXe> list = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM THUEXE WHERE 1=1");
        ArrayList<String> args = new ArrayList<>();

        if (maND != null) {
            query.append(" AND MaND = ?");
            args.add(String.valueOf(maND));
        }
        if (trangThai != null) {
            query.append(" AND TrangThai = ?");
            args.add(String.valueOf(trangThai));
        }
        if (startDate != null) {
            query.append(" AND NgayDat >= ?");
            args.add(startDate);
        }
        if (endDate != null) {
            query.append(" AND NgayDat <= ?");
            args.add(endDate);
        }

        try (Cursor c = db.rawQuery(query.toString(), args.toArray(new String[0]))) {
            if (c.moveToFirst()) {
                do {
                    list.add(getThueXeFromCursor(c));
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error searching thue xe: " + e.getMessage());
        }
        return list;
    }

    public int getCountByStatus(int trangThai) {
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM THUEXE WHERE TrangThai = ?",
                new String[]{String.valueOf(trangThai)})) {
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error counting thue xe by status: " + e.getMessage());
        }
        return 0;
    }

    public int getCountByUserAndStatus(int maND, int trangThai) {
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM THUEXE WHERE MaND = ? AND TrangThai = ?",
                new String[]{String.valueOf(maND), String.valueOf(trangThai)})) {
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error counting thue xe by user and status: " + e.getMessage());
        }
        return 0;
    }

    private ContentValues getContentValuesFromThueXe(ThueXe tx) {
        ContentValues values = new ContentValues();
        values.put("MaND", tx.getMaND());
        values.put("NgayDat", tx.getNgayDat());
        values.put("TrangThai", tx.getTrangThai());
        return values;
    }

    private ThueXe getThueXeFromCursor(Cursor c) {
        ThueXe tx = new ThueXe();
        tx.setMaThueXe(c.getInt(c.getColumnIndex("MaThueXe")));
        tx.setMaND(c.getInt(c.getColumnIndex("MaND")));
        tx.setNgayDat(c.getString(c.getColumnIndex("NgayDat")));
        tx.setTrangThai(c.getInt(c.getColumnIndex("TrangThai")));
        return tx;
    }

    public String getTrangThaiText(int trangThai) {
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

    public boolean isValidNgayDat(String ngayDat) {
        return ThueXeUtility.isValidNgayDat(ngayDat);
    }

    public String getCurrentDate() {
        return ThueXeUtility.getCurrentDate();
    }

    public String formatNgayDat(String ngayDat) {
        return ThueXeUtility.formatNgayDat(ngayDat);
    }

    public boolean canCancel(ThueXe tx) {
        return ThueXeUtility.canCancel(tx.getTrangThai());
    }

    public boolean canComplete(ThueXe tx) {
        return ThueXeUtility.canComplete(tx.getTrangThai());
    }

    public boolean hasActiveRental(int maND) {
        try (Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM THUEXE WHERE MaND = ? AND TrangThai IN (0, 1)", // 0: pending, 1: active
                new String[]{String.valueOf(maND)})) {
            if (c.moveToFirst()) {
                return c.getInt(0) > 0;
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error checking active rental: " + e.getMessage());
        }
        return false;
    }

    public boolean isValidRental(int maThueXe) {
        try (Cursor c = db.rawQuery(
                "SELECT TrangThai FROM THUEXE WHERE MaThueXe = ?",
                new String[]{String.valueOf(maThueXe)})) {
            if (c.moveToFirst()) {
                int status = c.getInt(0);
                return status != 3; // Assuming 3 means cancelled
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error checking rental validity: " + e.getMessage());
        }
        return false;
    }

    public List<ThueXe> getAllThueXe() {
        List<ThueXe> thueXeList = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT * FROM THUEXE", null)) {
            if (c.moveToFirst()) {
                do {
                    ThueXe thueXe = new ThueXe();
                    thueXe.setMaThueXe(c.getInt(c.getColumnIndex("MaThueXe")));
                    thueXe.setMaND(c.getInt(c.getColumnIndex("MaND")));
                    thueXe.setNgayDat(c.getString(c.getColumnIndex("NgayDat")));
                    thueXe.setTrangThai(c.getInt(c.getColumnIndex("TrangThai")));
                    thueXeList.add(thueXe);
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting all thue xe: " + e.getMessage());
        }
        return thueXeList;
    }

    public long insertThueXe(ThueXe thueXe) {
        try {
            ContentValues values = new ContentValues();
            values.put("MaND", thueXe.getMaND());
            values.put("NgayDat", thueXe.getNgayDat());
            values.put("TrangThai", thueXe.getTrangThai());
            return db.insertOrThrow("THUEXE", null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error inserting thue xe: " + e.getMessage());
            return -1;
        }
    }

    public long updateThueXe(ThueXe thueXe) {
        try {
            ContentValues values = new ContentValues();
            values.put("MaND", thueXe.getMaND());
            values.put("NgayDat", thueXe.getNgayDat());
            values.put("TrangThai", thueXe.getTrangThai());
            long result = db.update("THUEXE", values, "MaThueXe = ?",
                    new String[]{String.valueOf(thueXe.getMaThueXe())});
            return result;
        } catch (SQLiteException e) {
            Log.e(TAG, "Error updating thue xe: " + e.getMessage());
            return -1;
        }
    }

    public long deleteThueXe(int maThueXe) {
        // Xóa chi tiết thuê xe trước
        db.delete("CHITIETTHUEXE", "MaThueXe = ?", new String[]{String.valueOf(maThueXe)});
        // Sau đó xóa đơn thuê xe
        return db.delete("THUEXE", "MaThueXe = ?", new String[]{String.valueOf(maThueXe)});
    }

    public String getTenNguoiDung(int maND) {
        String query = "SELECT HoTen FROM NGUOIDUNG WHERE MaND = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(maND)});
        String tenNguoiDung = "";
        if (cursor.moveToFirst()) {
            tenNguoiDung = cursor.getString(0);
        }
        cursor.close();
        return tenNguoiDung;
    }

    public String getTenXe(int maXe) {
        String query = "SELECT TenXe FROM XE WHERE MaXe = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(maXe)});
        String tenXe = "";
        if (cursor.moveToFirst()) {
            tenXe = cursor.getString(0);
        }
        cursor.close();
        return tenXe;
    }

    public int updateTrangThaiXe(int maXe, int trangThai) {
        ContentValues values = new ContentValues();
        values.put("TrangThai", trangThai);
        return db.update("XE", values, "MaXe = ?", new String[]{String.valueOf(maXe)});
    }

    // Lấy đơn thuê xe theo mã
    public ThueXe getThueXeById(int maThueXe) {
        try (Cursor c = db.rawQuery("SELECT * FROM THUEXE WHERE MaThueXe = ?",
                new String[]{String.valueOf(maThueXe)})) {
            if (c.moveToFirst()) {
                return getThueXeFromCursor(c);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting thue xe by id: " + e.getMessage());
        }
        return null;
    }

    public ThueXe getLatestRentalByUser(int maND) {
        try (Cursor c = db.rawQuery(
                "SELECT * FROM THUEXE WHERE MaND = ? ORDER BY MaThueXe DESC LIMIT 1",
                new String[]{String.valueOf(maND)})) {
            if (c.moveToFirst()) {
                return getThueXeFromCursor(c);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting latest rental for user: " + e.getMessage());
        }
        return null;
    }
}
