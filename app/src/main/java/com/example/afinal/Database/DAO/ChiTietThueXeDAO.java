package com.example.afinal.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.afinal.Database.DatabaseHelper;
import com.example.afinal.Database.Model.ChiTietThueXe;
import com.example.afinal.Database.Utilites.ChiTietThueXeUtility;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ChiTietThueXeDAO {
    private static final String TAG = "ChiTietThueXeDAO";
    private final SQLiteDatabase db;
    private final Context context;
    private final DatabaseHelper dbHelper;

    public ChiTietThueXeDAO(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Lấy mã xe từ mã thuê xe
    public int getMaXe(int maThueXe) {
        try (Cursor c = db.rawQuery("SELECT MaXe FROM CHITIETTHUEXE WHERE MaThueXe = ?",
                new String[]{String.valueOf(maThueXe)})) {
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting MaXe from ChiTietThueXe: " + e.getMessage());
        }
        return -1;
    }

    // Thêm chi tiết thuê xe mới
    public long insert(ChiTietThueXe chiTiet) {
        try {
            ContentValues values = getContentValuesFromChiTiet(chiTiet);
            return db.insertOrThrow("CHITIETTHUEXE", null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error inserting chi tiet thue xe: " + e.getMessage());
            return -1;
        }
    }

    // Cập nhật chi tiết thuê xe
    public int update(ChiTietThueXe chiTiet) {
        try {
            ContentValues values = getContentValuesFromChiTiet(chiTiet);
            return db.update("CHITIETTHUEXE", values, "MaChiTiet = ?",
                    new String[]{String.valueOf(chiTiet.getMaChiTiet())});
        } catch (SQLiteException e) {
            Log.e(TAG, "Error updating chi tiet thue xe: " + e.getMessage());
            return -1;
        }
    }

    // Xóa chi tiết thuê xe
    public int delete(int maChiTiet) {
        try {
            return db.delete("CHITIETTHUEXE", "MaChiTiet = ?",
                    new String[]{String.valueOf(maChiTiet)});
        } catch (SQLiteException e) {
            Log.e(TAG, "Error deleting chi tiet thue xe: " + e.getMessage());
            return -1;
        }
    }

    // Lấy chi tiết thuê xe theo mã thuê xe
    public List<ChiTietThueXe> getByMaThueXe(int maThueXe) {
        List<ChiTietThueXe> list = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT * FROM CHITIETTHUEXE WHERE MaThueXe = ?",
                new String[]{String.valueOf(maThueXe)})) {
            if (c.moveToFirst()) {
                do {
                    list.add(getChiTietFromCursor(c));
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting chi tiet thue xe by MaThueXe: " + e.getMessage());
        }
        return list;
    }

    private ContentValues getContentValuesFromChiTiet(ChiTietThueXe chiTiet) {
        ContentValues values = new ContentValues();
        values.put("MaThueXe", chiTiet.getMaThueXe());
        values.put("MaXe", chiTiet.getMaXe());
        values.put("TienCoc", chiTiet.getTienCoc());
        values.put("NgayBatDauDK", chiTiet.getNgayBatDauDK());
        values.put("NgayKetThucDK", chiTiet.getNgayKetThucDK());
        values.put("NgayBatDauTT", chiTiet.getNgayBatDauTT());
        values.put("NgayKetThucTT", chiTiet.getNgayKetThucTT());
        values.put("ThanhTien", chiTiet.getThanhTien());
        values.put("GhiChu", chiTiet.getGhiChu());
        return values;
    }

    private ChiTietThueXe getChiTietFromCursor(Cursor c) {
        ChiTietThueXe chiTiet = new ChiTietThueXe();
        chiTiet.setMaChiTiet(c.getInt(c.getColumnIndex("MaChiTiet")));
        chiTiet.setMaThueXe(c.getInt(c.getColumnIndex("MaThueXe")));
        chiTiet.setMaXe(c.getInt(c.getColumnIndex("MaXe")));
        chiTiet.setTienCoc(c.getInt(c.getColumnIndex("TienCoc")));
        chiTiet.setNgayBatDauDK(c.getString(c.getColumnIndex("NgayBatDauDK")));
        chiTiet.setNgayKetThucDK(c.getString(c.getColumnIndex("NgayKetThucDK")));
        chiTiet.setNgayBatDauTT(c.getString(c.getColumnIndex("NgayBatDauTT")));
        chiTiet.setNgayKetThucTT(c.getString(c.getColumnIndex("NgayKetThucTT")));
        chiTiet.setThanhTien(c.getInt(c.getColumnIndex("ThanhTien")));
        chiTiet.setGhiChu(c.getString(c.getColumnIndex("GhiChu")));
        return chiTiet;
    }

    public long insertMultiple(List<ChiTietThueXe> chiTietList) {
        List<Long> insertedIds = new ArrayList<>();
        db.beginTransaction();
        try {
            for (ChiTietThueXe ct : chiTietList) {
                long id = insert(ct);
                if (id != -1) {
                    insertedIds.add(id);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return insertedIds.get(0);
    }

    // Read operations
    public ArrayList<ChiTietThueXe> getAll() {
        ArrayList<ChiTietThueXe> list = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT * FROM CHITIETTHUEXE", null)) {
            if (c.moveToFirst()) {
                do {
                    list.add(getChiTietFromCursor(c));
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting all chi tiet thue xe: " + e.getMessage());
        }
        return list;
    }

    public ChiTietThueXe getById(int maChiTiet) {
        try (Cursor c = db.rawQuery("SELECT * FROM CHITIETTHUEXE WHERE MaChiTiet = ?",
                new String[]{String.valueOf(maChiTiet)})) {
            if (c.moveToFirst()) {
                return getChiTietFromCursor(c);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting chi tiet thue xe by id: " + e.getMessage());
        }
        return null;
    }

    public int updateActualDates(int maChiTiet, String ngayBatDauTT, String ngayKetThucTT) {
        try {
            ContentValues values = new ContentValues();
            values.put("NgayBatDauTT", ngayBatDauTT);
            values.put("NgayKetThucTT", ngayKetThucTT);
            return db.update("CHITIETTHUEXE", values, "MaChiTiet = ?",
                    new String[]{String.valueOf(maChiTiet)});
        } catch (SQLiteException e) {
            Log.e(TAG, "Error updating actual dates: " + e.getMessage());
            return -1;
        }
    }

    public ArrayList<ChiTietThueXe> getByDateRange(String startDate, String endDate) {
        ArrayList<ChiTietThueXe> list = new ArrayList<>();
        try (Cursor c = db.rawQuery(
                "SELECT * FROM CHITIETTHUEXE WHERE NgayBatDauDK >= ? AND NgayKetThucDK <= ?",
                new String[]{startDate, endDate})) {
            if (c.moveToFirst()) {
                do {
                    list.add(getChiTietFromCursor(c));
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting chi tiet thue xe by date range: " + e.getMessage());
        }
        return list;
    }

    public ArrayList<ChiTietThueXe> searchByVehicle(int maXe) {
        ArrayList<ChiTietThueXe> list = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT * FROM CHITIETTHUEXE WHERE MaXe = ?",
                new String[]{String.valueOf(maXe)})) {
            if (c.moveToFirst()) {
                do {
                    list.add(getChiTietFromCursor(c));
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error searching chi tiet thue xe by vehicle: " + e.getMessage());
        }
        return list;
    }

    public ArrayList<ChiTietThueXe> searchAdvanced(Integer maThueXe, Integer maXe,
                                                   String startDate, String endDate) {
        ArrayList<ChiTietThueXe> list = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM CHITIETTHUEXE WHERE 1=1");
        ArrayList<String> args = new ArrayList<>();

        if (maThueXe != null) {
            query.append(" AND MaThueXe = ?");
            args.add(String.valueOf(maThueXe));
        }
        if (maXe != null) {
            query.append(" AND MaXe = ?");
            args.add(String.valueOf(maXe));
        }
        if (startDate != null) {
            query.append(" AND NgayBatDauDK >= ?");
            args.add(startDate);
        }
        if (endDate != null) {
            query.append(" AND NgayKetThucDK <= ?");
            args.add(endDate);
        }

        try (Cursor c = db.rawQuery(query.toString(), args.toArray(new String[0]))) {
            if (c.moveToFirst()) {
                do {
                    list.add(getChiTietFromCursor(c));
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error searching chi tiet thue xe: " + e.getMessage());
        }
        return list;
    }

    public int getTotalRevenue(String startDate, String endDate) {
        try (Cursor c = db.rawQuery(
                "SELECT SUM(ThanhTien) FROM CHITIETTHUEXE WHERE NgayBatDauTT >= ? AND NgayKetThucTT <= ?",
                new String[]{startDate, endDate})) {
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error calculating total revenue: " + e.getMessage());
        }
        return 0;
    }

    public int getActiveRentalsCount() {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        try (Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM CHITIETTHUEXE WHERE NgayBatDauTT <= ? AND NgayKetThucTT >= ?",
                new String[]{currentDate, currentDate})) {
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting active rentals count: " + e.getMessage());
        }
        return 0;
    }

    public String formatNgay(String ngay) {
        return ChiTietThueXeUtility.formatNgay(ngay);
    }

    public String formatTienCoc(int tienCoc) {
        return ChiTietThueXeUtility.formatTien(tienCoc);
    }

    public String formatThanhTien(int thanhTien) {
        return ChiTietThueXeUtility.formatTien(thanhTien);
    }

    public int calculateThanhTien(int giaThueNgay, String ngayBatDau, String ngayKetThuc) {
        return ChiTietThueXeUtility.calculateThanhTien(giaThueNgay, ngayBatDau, ngayKetThuc);
    }

    public int calculateTienCoc(int thanhTien) {
        return ChiTietThueXeUtility.calculateTienCoc(thanhTien);
    }

    public boolean isValidDateRange(String ngayBatDau, String ngayKetThuc) {
        return ChiTietThueXeUtility.isValidDateRange(ngayBatDau, ngayKetThuc);
    }

    public int calculateSoNgayThue(String ngayBatDau, String ngayKetThuc) {
        return ChiTietThueXeUtility.calculateSoNgayThue(ngayBatDau, ngayKetThuc);
    }

    public boolean isRentalStarted(ChiTietThueXe ct) {
        return ChiTietThueXeUtility.isRentalStarted(ct.getNgayBatDauTT());
    }

    public boolean isRentalEnded(ChiTietThueXe ct) {
        return ChiTietThueXeUtility.isRentalEnded(ct.getNgayKetThucTT());
    }

    public boolean isVehicleAvailable(int maXe, String startDate, String endDate) {
        try (Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM CHITIETTHUEXE WHERE MaXe = ? " +
                        "AND ((NgayBatDauDK <= ? AND NgayKetThucDK >= ?) OR " +
                        "(NgayBatDauDK <= ? AND NgayKetThucDK >= ?))",
                new String[]{String.valueOf(maXe), endDate, startDate, startDate, endDate})) {
            if (c.moveToFirst()) {
                return c.getInt(0) == 0;
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error checking vehicle availability: " + e.getMessage());
        }
        return false;
    }

    public boolean hasOverlappingRental(int maXe, String startDate, String endDate, Integer excludeMaChiTiet) {
        StringBuilder query = new StringBuilder(
                "SELECT COUNT(*) FROM CHITIETTHUEXE WHERE MaXe = ? " +
                        "AND ((NgayBatDauDK <= ? AND NgayKetThucDK >= ?) OR " +
                        "(NgayBatDauDK <= ? AND NgayKetThucDK >= ?))");
        ArrayList<String> args = new ArrayList<>();
        args.add(String.valueOf(maXe));
        args.add(endDate);
        args.add(startDate);
        args.add(startDate);
        args.add(endDate);

        if (excludeMaChiTiet != null) {
            query.append(" AND MaChiTiet != ?");
            args.add(String.valueOf(excludeMaChiTiet));
        }

        try (Cursor c = db.rawQuery(query.toString(), args.toArray(new String[0]))) {
            if (c.moveToFirst()) {
                return c.getInt(0) > 0;
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error checking overlapping rental: " + e.getMessage());
        }
        return true;
    }

    public ChiTietThueXe getSingleByMaThueXe(int maThueXe) {
        ChiTietThueXe chiTietThueXe = null;
        try (Cursor c = db.rawQuery("SELECT * FROM CHITIETTHUEXE WHERE MaThueXe = ? LIMIT 1",
                new String[]{String.valueOf(maThueXe)})) {
            if (c.moveToFirst()) {
                chiTietThueXe = getChiTietFromCursor(c);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting chi tiet thue xe by maThueXe: " + e.getMessage());
        }
        return chiTietThueXe;
    }
}
