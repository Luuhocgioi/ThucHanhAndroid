package clc65.hoangluu.duancanhan_65131861.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import clc65.hoangluu.duancanhan_65131861.model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CoffeeShopDB";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CART = "CartItem";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PRODUCT_ID = "productId";
    public static final String COLUMN_PRODUCT_NAME = "productName";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_IMAGE_URL = "imageUrl";

    private static final String CREATE_TABLE_CART = "CREATE TABLE " + TABLE_CART + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PRODUCT_ID + " TEXT UNIQUE,"
            + COLUMN_PRODUCT_NAME + " TEXT,"
            + COLUMN_PRICE + " REAL,"
            + COLUMN_QUANTITY + " INTEGER,"
            + COLUMN_IMAGE_URL + " TEXT"
            + ")";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CART);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }

    public void addToCart(CartItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_ID, item.getProductId());
        values.put(COLUMN_PRODUCT_NAME, item.getProductName());
        values.put(COLUMN_PRICE, item.getPrice());
        values.put(COLUMN_IMAGE_URL, item.getImageUrl());

        CartItem existingItem = getCartItemByProductId(item.getProductId());

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + item.getQuantity();
            values.put(COLUMN_QUANTITY, newQuantity);
            db.update(TABLE_CART, values, COLUMN_PRODUCT_ID + " = ?", new String[]{item.getProductId()});
            Log.d("SQLiteHelper", "Đã cập nhật số lượng sản phẩm: " + item.getProductName());
        } else {
            values.put(COLUMN_QUANTITY, item.getQuantity());
            db.insert(TABLE_CART, null, values);
            Log.d("SQLiteHelper", "Đã thêm sản phẩm mới vào giỏ hàng: " + item.getProductName());
        }
        db.close();
    }

    public CartItem getCartItemByProductId(String productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CART,
                new String[]{COLUMN_ID, COLUMN_PRODUCT_ID, COLUMN_PRODUCT_NAME, COLUMN_PRICE, COLUMN_QUANTITY, COLUMN_IMAGE_URL},
                COLUMN_PRODUCT_ID + " = ?",
                new String[]{productId}, null, null, null, null);

        CartItem item = null;
        if (cursor != null && cursor.moveToFirst()) {
            item = new CartItem();
            item.setId(cursor.getInt(0));
            item.setProductId(cursor.getString(1));
            item.setProductName(cursor.getString(2));
            item.setPrice(cursor.getDouble(3));
            item.setQuantity(cursor.getInt(4));
            item.setImageUrl(cursor.getString(5));
            cursor.close();
        }
        db.close();
        return item;
    }

    public List<CartItem> getAllCartItems() {
        List<CartItem> cartList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CART;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                CartItem item = new CartItem();
                item.setId(cursor.getInt(0));
                item.setProductId(cursor.getString(1));
                item.setProductName(cursor.getString(2));
                item.setPrice(cursor.getDouble(3));
                item.setQuantity(cursor.getInt(4));
                item.setImageUrl(cursor.getString(5));
                cartList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cartList;
    }

    public int updateQuantity(String productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUANTITY, quantity);
        int result = db.update(TABLE_CART, values, COLUMN_PRODUCT_ID + " = ?", new String[]{productId});
        db.close();
        return result;
    }

    public void deleteCartItem(String productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, COLUMN_PRODUCT_ID + " = ?", new String[]{productId});
        db.close();
    }

    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, null, null);
        db.close();
        Log.d("SQLiteHelper", "Đã xóa sạch giỏ hàng.");
    }

    public double getTotalAmount() {
        double total = 0;
        List<CartItem> items = getAllCartItems();
        for (CartItem item : items) {
            total += item.getItemTotal();
        }
        return total;
    }
}
