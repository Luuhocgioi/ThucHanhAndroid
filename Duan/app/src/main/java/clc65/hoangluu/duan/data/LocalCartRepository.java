package clc65.hoangluu.duan.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import clc65.hoangluu.duan.models.OrderItem;

public class LocalCartRepository {

    private ProductDbHelper dbHelper;
    private static final String TAG = "LocalCartRepo";

    public LocalCartRepository(Context context) {
        dbHelper = new ProductDbHelper(context);
    }

    public boolean addItemToCart(OrderItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Kiểm tra xem mặt hàng đã tồn tại chưa (cùng ProductId và Note)
        OrderItem existingItem = getExistingItem(db, item.getProductId(), item.getNote());

        if (existingItem != null) {
            // CẬP NHẬT số lượng
            int newQuantity = existingItem.getQuantity() + item.getQuantity();

            values.put(ProductContract.CartEntry.COLUMN_NAME_QUANTITY, newQuantity);

            String selection = ProductContract.CartEntry._ID + " = ?";
            String[] selectionArgs = { String.valueOf(existingItem.getSQLiteId()) };

            int count = db.update(
                    ProductContract.CartEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs
            );
            return count > 0;

        } else {
            // THÊM mới
            values.put(ProductContract.CartEntry.COLUMN_NAME_PRODUCT_ID, item.getProductId());
            values.put(ProductContract.CartEntry.COLUMN_NAME_NAME, item.getName());
            values.put(ProductContract.CartEntry.COLUMN_NAME_QUANTITY, item.getQuantity());
            values.put(ProductContract.CartEntry.COLUMN_NAME_UNIT_PRICE, item.getPrice());
            values.put(ProductContract.CartEntry.COLUMN_NAME_NOTE, item.getNote());

            long newRowId = db.insert(ProductContract.CartEntry.TABLE_NAME, null, values);
            return newRowId != -1;
        }
    }

    private OrderItem getExistingItem(SQLiteDatabase db, String productId, String note) {
        String selection = ProductContract.CartEntry.COLUMN_NAME_PRODUCT_ID + " = ? AND " +
                ProductContract.CartEntry.COLUMN_NAME_NOTE + " = ?";
        String[] selectionArgs = { productId, note };

        Cursor cursor = db.query(
                ProductContract.CartEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null, null, null, "1"
        );

        OrderItem item = null;
        if (cursor.moveToFirst()) {
            int sqliteId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.CartEntry._ID));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.CartEntry.COLUMN_NAME_QUANTITY));

            item = new OrderItem();
            item.setSQLiteId(sqliteId);
            item.setQuantity(quantity);
        }
        cursor.close();
        return item;
    }

    // PHƯƠNG THỨC BỊ THIẾU ĐÃ ĐƯỢC BỔ SUNG
    public boolean updateQuantity(int sqLiteId, int newQuantity) {
        if (newQuantity <= 0) {
            return deleteItem(sqLiteId);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProductContract.CartEntry.COLUMN_NAME_QUANTITY, newQuantity);

        String selection = ProductContract.CartEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(sqLiteId) };

        int count = db.update(
                ProductContract.CartEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        return count > 0;
    }

    // PHƯƠNG THỨC BỊ THIẾU ĐÃ ĐƯỢC BỔ SUNG
    public boolean deleteItem(int sqLiteId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = ProductContract.CartEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(sqLiteId) };

        int deletedRows = db.delete(
                ProductContract.CartEntry.TABLE_NAME,
                selection,
                selectionArgs
        );
        return deletedRows > 0;
    }

    public List<OrderItem> getCartItems() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<OrderItem> cartItems = new ArrayList<>();

        Cursor cursor = db.query(
                ProductContract.CartEntry.TABLE_NAME,
                null,
                null, null, null, null, null
        );

        while (cursor.moveToNext()) {
            int sqliteId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.CartEntry._ID));
            String productId = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.CartEntry.COLUMN_NAME_PRODUCT_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.CartEntry.COLUMN_NAME_NAME));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.CartEntry.COLUMN_NAME_QUANTITY));
            double unitPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(ProductContract.CartEntry.COLUMN_NAME_UNIT_PRICE));
            String note = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.CartEntry.COLUMN_NAME_NOTE));

            OrderItem item = new OrderItem(productId, name, quantity, unitPrice, note);
            item.setSQLiteId(sqliteId);
            cartItems.add(item);
        }
        cursor.close();
        return cartItems;
    }

    public double getTotalPrice() {
        List<OrderItem> items = getCartItems();
        double total = 0.0;
        for (OrderItem item : items) {
            total += item.getQuantity() * item.getPrice();
        }
        return total;
    }

    public void clearCart() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.clearCart(db);
    }
}