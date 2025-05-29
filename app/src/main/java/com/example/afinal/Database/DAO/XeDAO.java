package com.example.afinal.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.afinal.Database.DatabaseHelper;
import com.example.afinal.Database.Model.Xe;
import com.example.afinal.Database.Utilites.XeUtility;

import java.util.ArrayList;

public class XeDAO {
    private static final String TAG = "XeDAO";
    private final SQLiteDatabase db;
    private final Context context;

    public XeDAO(Context context) {
        this.context = context;
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    // Create operations
    public long insert(Xe xe) {
        try {
            ContentValues values = getContentValuesFromXe(xe);
            return db.insertOrThrow("XE", null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error inserting xe: " + e.getMessage());
            return -1;
        }
    }

    // Read operations
    public ArrayList<Xe> getAll() {
        ArrayList<Xe> list = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT * FROM XE", null)) {
            if (c.moveToFirst()) {
                do {
                    list.add(getXeFromCursor(c));
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting all xe: " + e.getMessage());
        }
        return list;
    }

    public Xe getById(int maXe) {
        try (Cursor c = db.rawQuery("SELECT * FROM XE WHERE MaXe = ?",
                new String[]{String.valueOf(maXe)})) {
            if (c.moveToFirst()) {
                return getXeFromCursor(c);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting xe by id: " + e.getMessage());
        }
        return null;
    }

    // Update operations
    public int update(Xe xe) {
        try {
            ContentValues values = getContentValuesFromXe(xe);
            return db.update("XE", values, "MaXe = ?",
                    new String[]{String.valueOf(xe.getMaXe())});
        } catch (SQLiteException e) {
            Log.e(TAG, "Error updating xe: " + e.getMessage());
            return -1;
        }
    }

    public int updateStatus(int maXe, int newStatus) {
        try {
            ContentValues values = new ContentValues();
            values.put("TrangThai", newStatus);
            return db.update("XE", values, "MaXe = ?",
                    new String[]{String.valueOf(maXe)});
        } catch (SQLiteException e) {
            Log.e(TAG, "Error updating xe status: " + e.getMessage());
            return -1;
        }
    }

    // Delete operations
    public int delete(int maXe) {
        try {
            return db.delete("XE", "MaXe = ?",
                    new String[]{String.valueOf(maXe)});
        } catch (SQLiteException e) {
            Log.e(TAG, "Error deleting xe: " + e.getMessage());
            return -1;
        }
    }

    public ArrayList<Xe> searchXeWithConditions(String searchText, String loaiXe, Integer trangThai) {
        ArrayList<Xe> list = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM XE WHERE 1=1");
        ArrayList<String> args = new ArrayList<>();

        // Thêm điều kiện tìm kiếm text
        if (searchText != null && !searchText.isEmpty()) {
            query.append(" AND (TenXe LIKE ? OR BienSo LIKE ?)");
            args.add("%" + searchText + "%");
            args.add("%" + searchText + "%");
        }

        // Thêm điều kiện loại xe
        if (loaiXe != null && !loaiXe.equals("Tất cả")) {
            query.append(" AND LoaiXe = ?");
            args.add(loaiXe);
        }

        // Thêm điều kiện trạng thái
        if (trangThai != null && trangThai >= 0) {
            query.append(" AND TrangThai = ?");
            args.add(String.valueOf(trangThai));
        }

        try (Cursor c = db.rawQuery(query.toString(), args.toArray(new String[0]))) {
            if (c.moveToFirst()) {
                do {
                    list.add(getXeFromCursor(c));
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error searching xe with conditions: " + e.getMessage());
        }
        return list;
    }

    // Utility methods
    private ContentValues getContentValuesFromXe(Xe xe) {
        ContentValues values = new ContentValues();
        values.put("TenXe", xe.getTenXe());
        values.put("LoaiXe", xe.getLoaiXe());
        values.put("BienSo", xe.getBienSo());
        values.put("GiaThue", xe.getGiaThue());
        values.put("HinhAnh", xe.getHinhAnh());
        values.put("TrangThai", xe.getTrangThai());
        values.put("NamSX", xe.getNamSX());
        values.put("MauSac", xe.getMauSac());
        return values;
    }

    private Xe getXeFromCursor(Cursor c) {
        Xe xe = new Xe();
        xe.setMaXe(c.getInt(c.getColumnIndex("MaXe")));
        xe.setTenXe(c.getString(c.getColumnIndex("TenXe")));
        xe.setLoaiXe(c.getString(c.getColumnIndex("LoaiXe")));
        xe.setBienSo(c.getString(c.getColumnIndex("BienSo")));
        xe.setGiaThue(c.getInt(c.getColumnIndex("GiaThue")));
        xe.setHinhAnh(c.getString(c.getColumnIndex("HinhAnh")));
        xe.setTrangThai(c.getInt(c.getColumnIndex("TrangThai")));
        xe.setNamSX(c.getInt(c.getColumnIndex("NamSX")));
        xe.setMauSac(c.getString(c.getColumnIndex("MauSac")));
        return xe;
    }

    public boolean isValidBienSo(String bienSo) {
        return XeUtility.isValidBienSo(bienSo);
    }

    public String formatGiaThue(int giaThue) {
        return XeUtility.formatGiaThue(giaThue);
    }

    // Check methods
    public boolean isXeAvailable(int maXe) {
        try (Cursor c = db.rawQuery("SELECT TrangThai FROM XE WHERE MaXe = ?",
                new String[]{String.valueOf(maXe)})) {
            if (c.moveToFirst()) {
                int status = c.getInt(0);
                return status == 0; // Assuming 0 means available
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error checking xe availability: " + e.getMessage());
        }
        return false;
    }

    public boolean existsBienSo(String bienSo) {
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM XE WHERE BienSo = ?",
                new String[]{bienSo})) {
            if (c.moveToFirst()) {
                return c.getInt(0) > 0;
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error checking bien so existence: " + e.getMessage());
        }
        return false;
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public SQLiteDatabase getDatabase() {
        return db;
    }
}
