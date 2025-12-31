package clc65.hoangluu.duan.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import clc65.hoangluu.duan.database.DatabaseHelper;
import clc65.hoangluu.duan.models.OrderItem;

public class LocalCartRepository {
    private DatabaseHelper dbHelper;

    public LocalCartRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public boolean addItemToCart(OrderItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Kiểm tra xem món đã có trong giỏ chưa (khớp ID và Ghi chú)
        Cursor c = db.query("cart", null, "product_id = ? AND note = ?",
                new String[]{item.getProductId(), item.getNote() == null ? "" : item.getNote()}, null, null, null);

        ContentValues v = new ContentValues();
        if (c != null && c.moveToFirst()) {
            int newQty = c.getInt(c.getColumnIndexOrThrow("quantity")) + item.getQuantity();
            v.put("quantity", newQty);
            int row = db.update("cart", v, "cart_id = ?", new String[]{String.valueOf(c.getInt(0))});
            c.close();
            return row > 0;
        } else {
            v.put("product_id", item.getProductId());
            v.put("name", item.getName());
            v.put("price", item.getPrice());
            v.put("quantity", item.getQuantity());
            v.put("note", item.getNote());
            // --- THÊM DÒNG NÀY ĐỂ LƯU ẢNH VÀO SQLITE ---
            v.put("image_url", item.getImageUrl());

            if (c != null) c.close();
            return db.insert("cart", null, v) != -1;
        }
    }

    public List<OrderItem> getCartItems() {
        List<OrderItem> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query("cart", null, null, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                // SỬA Ở ĐÂY: Truyền đủ 6 tham số vào Constructor bao gồm image_url
                OrderItem item = new OrderItem(
                        c.getString(c.getColumnIndexOrThrow("product_id")),
                        c.getString(c.getColumnIndexOrThrow("name")),
                        c.getInt(c.getColumnIndexOrThrow("quantity")),
                        c.getDouble(c.getColumnIndexOrThrow("price")),
                        c.getString(c.getColumnIndexOrThrow("image_url")), // Thêm tham số này
                        c.getString(c.getColumnIndexOrThrow("note"))
                );
                item.setSQLiteId(c.getInt(c.getColumnIndexOrThrow("cart_id")));
                list.add(item);
            } while (c.moveToNext());
            c.close();
        }
        return list;
    }

    // Các hàm clearCart, getTotalPrice, updateQuantity giữ nguyên của bạn...
    public void clearCart() {
        dbHelper.getWritableDatabase().delete("cart", null, null);
    }

    public double getTotalPrice() {
        double total = 0;
        for (OrderItem i : getCartItems()) total += i.getQuantity() * i.getPrice();
        return total;
    }

    public boolean updateQuantity(int id, int qty) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (qty <= 0) return db.delete("cart", "cart_id = ?", new String[]{String.valueOf(id)}) > 0;
        ContentValues v = new ContentValues();
        v.put("quantity", qty);
        return db.update("cart", v, "cart_id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteItem(int id) {
        return dbHelper.getWritableDatabase().delete("cart", "cart_id = ?", new String[]{String.valueOf(id)}) > 0;
    }
}