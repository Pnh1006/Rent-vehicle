package com.example.afinal.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.afinal.Database.DatabaseHelper;
import com.example.afinal.Database.Model.ThanhToan;
import com.example.afinal.Database.Utilites.ThanhToanUtility;

import java.util.ArrayList;
import java.util.List;

public class ThanhToanDAO {
    private static final String TAG = "ThanhToanDAO";
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public ThanhToanDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public long insert(ThanhToan thanhToan) {
        open();
        ContentValues values = new ContentValues();
        values.put("MaND", thanhToan.getMaND());
        values.put("MaThueXe", thanhToan.getMaThueXe());
        values.put("SoTien", thanhToan.getSoTien());
        values.put("NoiDung", thanhToan.getNoiDung());
        values.put("NgayThucHien", thanhToan.getNgayThucHien());
        values.put("NgayThanhCong", thanhToan.getNgayThanhCong());
        values.put("PhuongThuc", thanhToan.getPhuongThuc());
        values.put("MaGiaoDich", thanhToan.getMaGiaoDich());
        values.put("TrangThai", thanhToan.getTrangThai());
        values.put("GhiChu", thanhToan.getGhiChu());

        long result = db.insert("ThanhToan", null, values);
        close();
        return result;
    }

    public int update(ThanhToan thanhToan) {
        open();
        ContentValues values = new ContentValues();
        values.put("MaND", thanhToan.getMaND());
        values.put("MaThueXe", thanhToan.getMaThueXe());
        values.put("SoTien", thanhToan.getSoTien());
        values.put("NoiDung", thanhToan.getNoiDung());
        values.put("NgayThucHien", thanhToan.getNgayThucHien());
        values.put("NgayThanhCong", thanhToan.getNgayThanhCong());
        values.put("PhuongThuc", thanhToan.getPhuongThuc());
        values.put("MaGiaoDich", thanhToan.getMaGiaoDich());
        values.put("TrangThai", thanhToan.getTrangThai());
        values.put("GhiChu", thanhToan.getGhiChu());

        int result = db.update("ThanhToan", values, "MaThanhToan = ?",
                new String[]{String.valueOf(thanhToan.getMaThanhToan())});
        close();
        return result;
    }

    public int delete(int maThanhToan) {
        open();
        int result = db.delete("ThanhToan", "MaThanhToan = ?",
                new String[]{String.valueOf(maThanhToan)});
        close();
        return result;
    }

    public ArrayList<ThanhToan> getAll() {
        open();
        ArrayList<ThanhToan> list = new ArrayList<>();
        Cursor cursor = db.query("ThanhToan", null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ThanhToan thanhToan = new ThanhToan();
                thanhToan.setMaThanhToan(cursor.getInt(cursor.getColumnIndex("MaThanhToan")));
                thanhToan.setMaND(cursor.getInt(cursor.getColumnIndex("MaND")));
                thanhToan.setMaThueXe(cursor.getInt(cursor.getColumnIndex("MaThueXe")));
                thanhToan.setSoTien(cursor.getInt(cursor.getColumnIndex("SoTien")));
                thanhToan.setNoiDung(cursor.getString(cursor.getColumnIndex("NoiDung")));
                thanhToan.setNgayThucHien(cursor.getString(cursor.getColumnIndex("NgayThucHien")));
                thanhToan.setNgayThanhCong(cursor.getString(cursor.getColumnIndex("NgayThanhCong")));
                thanhToan.setPhuongThuc(cursor.getString(cursor.getColumnIndex("PhuongThuc")));
                thanhToan.setMaGiaoDich(cursor.getString(cursor.getColumnIndex("MaGiaoDich")));
                thanhToan.setTrangThai(cursor.getInt(cursor.getColumnIndex("TrangThai")));
                thanhToan.setGhiChu(cursor.getString(cursor.getColumnIndex("GhiChu")));
                list.add(thanhToan);
            } while (cursor.moveToNext());
            cursor.close();
        }
        close();
        return list;
    }

    public ThanhToan getById(int maThanhToan) {
        open();
        ThanhToan thanhToan = null;
        Cursor cursor = db.query("ThanhToan", null, "MaThanhToan = ?",
                new String[]{String.valueOf(maThanhToan)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            thanhToan = new ThanhToan();
            thanhToan.setMaThanhToan(cursor.getInt(cursor.getColumnIndex("MaThanhToan")));
            thanhToan.setMaND(cursor.getInt(cursor.getColumnIndex("MaND")));
            thanhToan.setMaThueXe(cursor.getInt(cursor.getColumnIndex("MaThueXe")));
            thanhToan.setSoTien(cursor.getInt(cursor.getColumnIndex("SoTien")));
            thanhToan.setNoiDung(cursor.getString(cursor.getColumnIndex("NoiDung")));
            thanhToan.setNgayThucHien(cursor.getString(cursor.getColumnIndex("NgayThucHien")));
            thanhToan.setNgayThanhCong(cursor.getString(cursor.getColumnIndex("NgayThanhCong")));
            thanhToan.setPhuongThuc(cursor.getString(cursor.getColumnIndex("PhuongThuc")));
            thanhToan.setMaGiaoDich(cursor.getString(cursor.getColumnIndex("MaGiaoDich")));
            thanhToan.setTrangThai(cursor.getInt(cursor.getColumnIndex("TrangThai")));
            thanhToan.setGhiChu(cursor.getString(cursor.getColumnIndex("GhiChu")));
            cursor.close();
        }
        close();
        return thanhToan;
    }

    public ArrayList<ThanhToan> getByMaND(int maND) {
        open();
        ArrayList<ThanhToan> list = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT * FROM THANHTOAN WHERE MaND = ?",
                new String[]{String.valueOf(maND)})) {
            if (c.moveToFirst()) {
                do {
                    list.add(getThanhToanFromCursor(c));
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting thanh toan by MaND: " + e.getMessage());
        }
        close();
        return list;
    }

    public ThanhToan getThanhToanById(int maThanhToan) {
        open();
        ThanhToan thanhToan = null;
        try (Cursor c = db.rawQuery("SELECT * FROM THANHTOAN WHERE MaThanhToan = ?",
                new String[]{String.valueOf(maThanhToan)})) {
            if (c.moveToFirst()) {
                thanhToan = getThanhToanFromCursor(c);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting thanh toan by id: " + e.getMessage());
        }
        close();
        return thanhToan;
    }

    public int updateStatus(int maThanhToan, int newStatus) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put("TrangThai", newStatus);
            int result = db.update("THANHTOAN", values, "MaThanhToan = ?",
                    new String[]{String.valueOf(maThanhToan)});
            close();
            return result;
        } catch (SQLiteException e) {
            Log.e(TAG, "Error updating thanh toan status: " + e.getMessage());
            close();
            return -1;
        }
    }

    public int updateTransactionSuccess(int maThanhToan, String ngayThanhCong, String maGiaoDich) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put("NgayThanhCong", ngayThanhCong);
            values.put("MaGiaoDich", maGiaoDich);
            values.put("TrangThai", 1); // Assuming 1 means success
            int result = db.update("THANHTOAN", values, "MaThanhToan = ?",
                    new String[]{String.valueOf(maThanhToan)});
            close();
            return result;
        } catch (SQLiteException e) {
            Log.e(TAG, "Error updating transaction success: " + e.getMessage());
            close();
            return -1;
        }
    }

    public ArrayList<ThanhToan> getByStatus(int trangThai) {
        open();
        ArrayList<ThanhToan> list = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT * FROM THANHTOAN WHERE TrangThai = ?",
                new String[]{String.valueOf(trangThai)})) {
            if (c.moveToFirst()) {
                do {
                    list.add(getThanhToanFromCursor(c));
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting thanh toan by status: " + e.getMessage());
        }
        close();
        return list;
    }

    public ArrayList<ThanhToan> getByDateRange(String startDate, String endDate) {
        open();
        ArrayList<ThanhToan> list = new ArrayList<>();
        try (Cursor c = db.rawQuery(
                "SELECT * FROM THANHTOAN WHERE NgayThucHien BETWEEN ? AND ?",
                new String[]{startDate, endDate})) {
            if (c.moveToFirst()) {
                do {
                    list.add(getThanhToanFromCursor(c));
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting thanh toan by date range: " + e.getMessage());
        }
        close();
        return list;
    }

    public ArrayList<ThanhToan> searchAdvanced(Integer maND, Integer trangThai,
                                               String startDate, String endDate, String phuongThuc) {
        open();
        ArrayList<ThanhToan> list = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM THANHTOAN WHERE 1=1");
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
            query.append(" AND NgayThucHien >= ?");
            args.add(startDate);
        }
        if (endDate != null) {
            query.append(" AND NgayThucHien <= ?");
            args.add(endDate);
        }
        if (phuongThuc != null) {
            query.append(" AND PhuongThuc = ?");
            args.add(phuongThuc);
        }

        try (Cursor c = db.rawQuery(query.toString(), args.toArray(new String[0]))) {
            if (c.moveToFirst()) {
                do {
                    list.add(getThanhToanFromCursor(c));
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error searching thanh toan: " + e.getMessage());
        }
        close();
        return list;
    }

    public int getTotalAmount(int maND) {
        open();
        try (Cursor c = db.rawQuery(
                "SELECT SUM(SoTien) FROM THANHTOAN WHERE MaND = ? AND TrangThai = 1",
                new String[]{String.valueOf(maND)})) {
            if (c.moveToFirst()) {
                int result = c.getInt(0);
                close();
                return result;
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting total amount: " + e.getMessage());
        }
        close();
        return 0;
    }

    public int getCountByStatus(int trangThai) {
        open();
        try (Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM THANHTOAN WHERE TrangThai = ?",
                new String[]{String.valueOf(trangThai)})) {
            if (c.moveToFirst()) {
                int result = c.getInt(0);
                close();
                return result;
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error counting thanh toan by status: " + e.getMessage());
        }
        close();
        return 0;
    }

    private ContentValues getContentValuesFromThanhToan(ThanhToan tt) {
        ContentValues values = new ContentValues();
        values.put("MaND", tt.getMaND());
        values.put("MaThueXe", tt.getMaThueXe());
        values.put("SoTien", tt.getSoTien());
        values.put("NoiDung", tt.getNoiDung());
        values.put("NgayThucHien", tt.getNgayThucHien());
        values.put("NgayThanhCong", tt.getNgayThanhCong());
        values.put("PhuongThuc", tt.getPhuongThuc());
        values.put("MaGiaoDich", tt.getMaGiaoDich());
        values.put("TrangThai", tt.getTrangThai());
        values.put("GhiChu", tt.getGhiChu());
        return values;
    }

    private ThanhToan getThanhToanFromCursor(Cursor c) {
        ThanhToan tt = new ThanhToan();
        tt.setMaThanhToan(c.getInt(c.getColumnIndex("MaThanhToan")));
        tt.setMaND(c.getInt(c.getColumnIndex("MaND")));
        tt.setMaThueXe(c.getInt(c.getColumnIndex("MaThueXe")));
        tt.setSoTien(c.getInt(c.getColumnIndex("SoTien")));
        tt.setNoiDung(c.getString(c.getColumnIndex("NoiDung")));
        tt.setNgayThucHien(c.getString(c.getColumnIndex("NgayThucHien")));
        tt.setNgayThanhCong(c.getString(c.getColumnIndex("NgayThanhCong")));
        tt.setPhuongThuc(c.getString(c.getColumnIndex("PhuongThuc")));
        tt.setMaGiaoDich(c.getString(c.getColumnIndex("MaGiaoDich")));
        tt.setTrangThai(c.getInt(c.getColumnIndex("TrangThai")));
        tt.setGhiChu(c.getString(c.getColumnIndex("GhiChu")));
        return tt;
    }

    public String getTrangThaiText(int trangThai) {
        return ThanhToanUtility.getTrangThaiText(trangThai);
    }

    public String formatSoTien(int soTien) {
        return ThanhToanUtility.formatSoTien(soTien);
    }

    public String formatNgayThanhToan(String ngay) {
        return ThanhToanUtility.formatNgayThanhToan(ngay);
    }

    public boolean isValidPhuongThuc(String phuongThuc) {
        return ThanhToanUtility.isValidPhuongThuc(phuongThuc);
    }

    public String generateMaGiaoDich() {
        return ThanhToanUtility.generateMaGiaoDich();
    }

    public boolean canRefund(ThanhToan tt) {
        return ThanhToanUtility.canRefund(tt.getTrangThai());
    }

    public int calculateRefundAmount(ThanhToan tt) {
        return ThanhToanUtility.calculateRefundAmount(tt.getSoTien());
    }

    // Check methods
    public boolean hasSuccessfulPayment(int maThueXe) {
        open();
        try (Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM THANHTOAN WHERE MaThueXe = ? AND TrangThai = 1",
                new String[]{String.valueOf(maThueXe)})) {
            if (c.moveToFirst()) {
                boolean result = c.getInt(0) > 0;
                close();
                return result;
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error checking successful payment: " + e.getMessage());
        }
        close();
        return false;
    }

    public boolean isValidTransaction(String maGiaoDich) {
        open();
        try (Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM THANHTOAN WHERE MaGiaoDich = ?",
                new String[]{maGiaoDich})) {
            if (c.moveToFirst()) {
                boolean result = c.getInt(0) == 1;
                close();
                return result;
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error checking transaction validity: " + e.getMessage());
        }
        close();
        return false;
    }

    public ThanhToan getByMaGiaoDich(String maGiaoDich) {
        open();
        ThanhToan thanhToan = null;
        try (Cursor c = db.rawQuery("SELECT * FROM THANHTOAN WHERE MaGiaoDich = ?",
                new String[]{maGiaoDich})) {
            if (c.moveToFirst()) {
                thanhToan = getThanhToanFromCursor(c);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting thanh toan by maGiaoDich: " + e.getMessage());
        }
        close();
        return thanhToan;
    }

    public ThanhToan getByMaThueXe(int maThueXe) {
        open();
        ThanhToan thanhToan = null;
        try (Cursor c = db.rawQuery("SELECT * FROM THANHTOAN WHERE MaThueXe = ?",
                new String[]{String.valueOf(maThueXe)})) {
            if (c.moveToFirst()) {
                thanhToan = getThanhToanFromCursor(c);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting thanh toan by ma thue xe: " + e.getMessage());
        }
        close();
        return thanhToan;
    }
}
