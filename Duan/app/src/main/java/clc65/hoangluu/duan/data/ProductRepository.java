package clc65.hoangluu.duan.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.List;
import clc65.hoangluu.duan.database.DatabaseHelper;
import clc65.hoangluu.duan.models.Product;

public class ProductRepository {
    private DatabaseHelper dbHelper;
    private FirebaseFirestore firestoreDb;
    private CollectionReference productsRef;
    private ListenerRegistration firestoreListener;

    public ProductRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
        this.firestoreDb = FirebaseFirestore.getInstance();
        this.productsRef = firestoreDb.collection("products");
    }

    public void startListeningForProductUpdates() {
        if (firestoreListener == null) {
            firestoreListener = productsRef.addSnapshotListener((snapshots, e) -> {
                if (e != null || snapshots == null) return;
                syncProductsToSQLite(snapshots.getDocuments());
            });
        }
    }

    private void syncProductsToSQLite(List<DocumentSnapshot> documents) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("products", null, null);
            for (DocumentSnapshot doc : documents) {
                Product p = doc.toObject(Product.class);
                if (p != null) {
                    ContentValues v = new ContentValues();
                    v.put("id", doc.getId());
                    v.put("name", p.getName());
                    v.put("price", p.getPrice());
                    v.put("imageUrl", p.getImageUrl());
                    v.put("category", p.getCategory());
                    v.put("description", p.getDescription());
                    db.insert("products", null, v);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<Product> getAllProductsFromCache() {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query("products", null, null, null, null, null, "name ASC");
        if (c != null && c.moveToFirst()) {
            do {
                Product p = new Product();
                p.setId(c.getString(0));
                p.setName(c.getString(1));
                p.setPrice(c.getDouble(2));
                p.setImageUrl(c.getString(3));
                p.setCategory(c.getString(4));
                p.setDescription(c.getString(5));
                list.add(p);
            } while (c.moveToNext());
            c.close();
        }
        return list;
    }

    public void stopListening() {
        if (firestoreListener != null) firestoreListener.remove();
    }
}