package clc65.hoangluu.duan.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "OrderDrink.db";
    // Tăng phiên bản database lên 2 vì chúng ta thêm bảng mới
    public static final int DATABASE_VERSION = 2;

    private static final String SQL_CREATE_PRODUCTS_CACHE =
            "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + " (" +
                    ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY," +
                    ProductContract.ProductEntry.COLUMN_NAME_FIREBASE_ID + " TEXT UNIQUE," +
                    ProductContract.ProductEntry.COLUMN_NAME_NAME + " TEXT NOT NULL," +
                    ProductContract.ProductEntry.COLUMN_NAME_PRICE + " REAL," +
                    ProductContract.ProductEntry.COLUMN_NAME_IMAGE_URL + " TEXT)";

    // Lệnh SQL MỚI để tạo bảng Giỏ hàng
    private static final String SQL_CREATE_LOCAL_CART =
            "CREATE TABLE " + ProductContract.CartEntry.TABLE_NAME + " (" +
                    ProductContract.CartEntry._ID + " INTEGER PRIMARY KEY," +
                    ProductContract.CartEntry.COLUMN_NAME_PRODUCT_ID + " TEXT NOT NULL," +
                    ProductContract.CartEntry.COLUMN_NAME_NAME + " TEXT NOT NULL," +
                    ProductContract.CartEntry.COLUMN_NAME_QUANTITY + " INTEGER," +
                    ProductContract.CartEntry.COLUMN_NAME_UNIT_PRICE + " REAL," +
                    ProductContract.CartEntry.COLUMN_NAME_NOTE + " TEXT)";

    private static final String SQL_DELETE_PRODUCTS_CACHE =
            "DROP TABLE IF EXISTS " + ProductContract.ProductEntry.TABLE_NAME;

    private static final String SQL_DELETE_LOCAL_CART =
            "DROP TABLE IF EXISTS " + ProductContract.CartEntry.TABLE_NAME;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo cả hai bảng
        db.execSQL(SQL_CREATE_PRODUCTS_CACHE);
        db.execSQL(SQL_CREATE_LOCAL_CART);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa và tạo lại cả hai bảng
        db.execSQL(SQL_DELETE_PRODUCTS_CACHE);
        db.execSQL(SQL_DELETE_LOCAL_CART);
        onCreate(db);
    }

    // Phương thức xóa tất cả dữ liệu trong bảng cache sản phẩm
    public void clearAllProducts(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + ProductContract.ProductEntry.TABLE_NAME);
    }

    // Phương thức MỚI để xóa Giỏ hàng
    public void clearCart(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + ProductContract.CartEntry.TABLE_NAME);
    }
}