package clc65.hoangluu.duan.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

public class DatabaseHelper extends SQLiteOpenHelper {
    // ĐỔI TÊN Ở ĐÂY để ép tạo file mới hoàn toàn
    public static final String DATABASE_NAME = "coffee_shop.db";
    public static final int DATABASE_VERSION = 1;

    private static final String CREATE_PRODUCTS = "CREATE TABLE products (id TEXT PRIMARY KEY, name TEXT, price REAL, imageUrl TEXT, category TEXT, description TEXT, isPopular INTEGER DEFAULT 0)";
    private static final String CREATE_CART = "CREATE TABLE cart (cart_id INTEGER PRIMARY KEY AUTOINCREMENT, product_id TEXT, name TEXT, price REAL, quantity INTEGER,image_url TEXT ,note TEXT )";
    private static final String CREATE_STAFF = "CREATE TABLE staff (uid TEXT PRIMARY KEY, name TEXT, email TEXT, role TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PRODUCTS);
        db.execSQL(CREATE_CART);
        db.execSQL(CREATE_STAFF);

        db.execSQL("CREATE TABLE categories (cat_id INTEGER PRIMARY KEY AUTOINCREMENT, cat_name TEXT UNIQUE, cat_tag TEXT)");
        db.execSQL("INSERT INTO categories (cat_name, cat_tag) VALUES ('Cà phê', 'Coffee'), ('Trà sữa', 'MilkTea'), ('Trà', 'Tea')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int n) {
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS cart");
        db.execSQL("DROP TABLE IF EXISTS staff");
        db.execSQL("DROP TABLE IF EXISTS categories");
        onCreate(db);
    }
}