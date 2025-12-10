package clc65.hoangluu.duan.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot; // Cần thiết
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import clc65.hoangluu.duan.models.Product;

public class ProductRepository {

    private ProductDbHelper dbHelper;
    private FirebaseFirestore firestoreDb;
    private CollectionReference productsRef;
    private ListenerRegistration firestoreListener;
    private Context context;
    // Tên Tag cho Log
    private static final String TAG = "ProductRepository";

    public ProductRepository(Context context) {
        this.context = context;
        dbHelper = new ProductDbHelper(context);
        firestoreDb = FirebaseFirestore.getInstance();
        productsRef = firestoreDb.collection("products");
    }

    // ==========================================================
    // A. LOGIC ĐỒNG BỘ TỪ FIREBASE SANG SQLITE (CACHE)
    // ==========================================================

    public void startListeningForProductUpdates() {
        if (firestoreListener == null) {
            // Lắng nghe thay đổi trên collection "products"
            firestoreListener = productsRef.addSnapshotListener((snapshots, e) -> {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshots != null && !snapshots.isEmpty()) {
                    Log.d(TAG, "Firebase updated. Starting SQLite synchronization.");

                    // Truyền List<DocumentSnapshot> (là kết quả từ getDocuments())
                    // Phương thức syncProductsToSQLite đã được sửa để chấp nhận kiểu này.
                    syncProductsToSQLite(snapshots.getDocuments());
                }
            });
        }
    }

    // PHƯƠNG THỨC ĐÃ ĐƯỢC SỬA: Chấp nhận List<DocumentSnapshot>
    private void syncProductsToSQLite(List<DocumentSnapshot> documents) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();

        try {
            // 1. Xóa dữ liệu cũ
            dbHelper.clearAllProducts(db);

            // 2. Ghi dữ liệu mới
            for (DocumentSnapshot doc : documents) {
                // toObject() hoạt động tốt với cả DocumentSnapshot và QueryDocumentSnapshot
                Product product = doc.toObject(Product.class);

                if (product != null) {
                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_NAME_FIREBASE_ID, doc.getId());
                    values.put(ProductContract.ProductEntry.COLUMN_NAME_NAME, product.getName());
                    values.put(ProductContract.ProductEntry.COLUMN_NAME_PRICE, product.getPrice());
                    values.put(ProductContract.ProductEntry.COLUMN_NAME_IMAGE_URL, product.getImageUrl());

                    db.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);
                }
            }
            db.setTransactionSuccessful();
            Log.d(TAG, "SQLite synchronization successful. Items: " + documents.size());

            // TODO: Thông báo cho các Fragment (ví dụ: HomeFragment) rằng dữ liệu đã thay đổi
            // Nếu bạn dùng LiveData/ViewModel, đây là nơi bạn gửi thông báo.

        } finally {
            db.endTransaction();
        }
    }

    public void stopListening() {
        if (firestoreListener != null) {
            firestoreListener.remove();
            firestoreListener = null;
            Log.d(TAG, "Firebase listener stopped.");
        }
    }

    // ==========================================================
    // B. LOGIC TRUY VẤN SQLITE TỪ CÁC FRAGMENT (READ)
    // ==========================================================

    public List<Product> getAllProductsFromCache() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Product> productList = new ArrayList<>();

        String[] projection = {
                ProductContract.ProductEntry.COLUMN_NAME_FIREBASE_ID,
                ProductContract.ProductEntry.COLUMN_NAME_NAME,
                ProductContract.ProductEntry.COLUMN_NAME_PRICE,
                ProductContract.ProductEntry.COLUMN_NAME_IMAGE_URL
        };

        Cursor cursor = db.query(
                ProductContract.ProductEntry.TABLE_NAME,
                projection,
                null, null, null, null, null
        );

        while (cursor.moveToNext()) {
            // Dùng getColumnIndexOrThrow để tránh lỗi nếu tên cột sai
            String firebaseId = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_NAME_FIREBASE_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_NAME_NAME));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_NAME_PRICE));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_NAME_IMAGE_URL));

            Product product = new Product(firebaseId, name, price, imageUrl);
            productList.add(product);
        }
        cursor.close();
        return productList;
    }

    public List<Product> searchProductsInCache(String query) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Product> productList = new ArrayList<>();

        // Tìm kiếm không phân biệt chữ hoa/chữ thường (sử dụng LIKE)
        String selection = ProductContract.ProductEntry.COLUMN_NAME_NAME + " LIKE ?";
        String[] selectionArgs = { "%" + query + "%" };

        Cursor cursor = db.query(
                ProductContract.ProductEntry.TABLE_NAME,
                null, // Lấy tất cả cột
                selection,
                selectionArgs,
                null, null, null
        );

        while (cursor.moveToNext()) {
            String firebaseId = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_NAME_FIREBASE_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_NAME_NAME));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_NAME_PRICE));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_NAME_IMAGE_URL));

            Product product = new Product(firebaseId, name, price, imageUrl);
            productList.add(product);
        }
        cursor.close();
        return productList;
    }
}