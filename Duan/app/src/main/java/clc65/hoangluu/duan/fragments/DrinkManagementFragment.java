package clc65.hoangluu.duan.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import clc65.hoangluu.duan.R;
import clc65.hoangluu.duan.adapters.ProductAdapter;
import clc65.hoangluu.duan.database.DatabaseHelper;
import clc65.hoangluu.duan.databinding.FragmentDrinkManagementBinding;
import clc65.hoangluu.duan.models.Product;

public class DrinkManagementFragment extends Fragment implements ProductAdapter.OnItemClickListener {

    private FragmentDrinkManagementBinding binding;
    private ProductAdapter productAdapter;
    private FirebaseFirestore db;
    private ListenerRegistration productsListener;
    private DatabaseHelper dbHelper;

    public DrinkManagementFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        dbHelper = new DatabaseHelper(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDrinkManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupAddButton();
        setupBackButton(view);
        startListeningForDrinks();
    }

    private void setupBackButton(View view) {
        binding.btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack();
        });
    }

    private void setupRecyclerView() {
        binding.rvDrinks.setLayoutManager(new LinearLayoutManager(getContext()));
        productAdapter = new ProductAdapter(new ArrayList<>());
        productAdapter.setOnItemClickListener(this);
        binding.rvDrinks.setAdapter(productAdapter);
    }

    private void setupAddButton() {
        binding.fabAddDrink.setOnClickListener(v -> showProductDialog(null));
    }

    private void showProductDialog(@Nullable Product product) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_drink, null);
        EditText etName = dialogView.findViewById(R.id.et_drink_name);
        EditText etPrice = dialogView.findViewById(R.id.et_drink_price);
        EditText etImageUrl = dialogView.findViewById(R.id.et_drink_image_url);
        EditText etCategory = dialogView.findViewById(R.id.et_drink_category);

        if (product != null) {
            etName.setText(product.getName());
            etPrice.setText(String.valueOf((int) product.getPrice()));
            etImageUrl.setText(product.getImageUrl());
            etCategory.setText(product.getCategory());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        builder.setTitle(product == null ? "Thêm thức uống" : "Sửa thức uống");

        builder.setPositiveButton(product == null ? "Thêm" : "Cập nhật", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String imageUrl = etImageUrl.getText().toString().trim();
            String category = etCategory.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr)) {
                Toast.makeText(getContext(), "Vui lòng nhập tên và giá", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double price = Double.parseDouble(priceStr);
                if (product == null) {
                    saveDrinkToFirestore(name, price, imageUrl, category);
                } else {
                    updateDrinkInFirestore(product.getId(), name, price, imageUrl, category);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void saveDrinkToFirestore(String name, double price, String imageUrl, String category) {
        Product newProduct = new Product();
        newProduct.setName(name);
        newProduct.setPrice(price);
        newProduct.setImageUrl(imageUrl);
        newProduct.setCategory(category);

        db.collection("products")
                .add(newProduct)
                .addOnSuccessListener(documentReference -> Toast.makeText(getContext(), "Thêm thành công!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi thêm: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateDrinkInFirestore(String id, String name, double price, String imageUrl, String category) {
        Product updatedProduct = new Product();
        updatedProduct.setName(name);
        updatedProduct.setPrice(price);
        updatedProduct.setImageUrl(imageUrl);
        updatedProduct.setCategory(category);

        db.collection("products").document(id)
                .set(updatedProduct)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi sửa: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void startListeningForDrinks() {
        Query query = db.collection("products").orderBy("name", Query.Direction.ASCENDING);

        productsListener = query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshots != null) {
                List<Product> products = new ArrayList<>();
                for (com.google.firebase.firestore.DocumentSnapshot doc : snapshots.getDocuments()) {
                    Product product = doc.toObject(Product.class);
                    if (product != null) {
                        product.setId(doc.getId());
                        products.add(product);
                    }
                }
                syncToSQLite(products);
                productAdapter.updateList(products);
            }
        });
    }

    private void syncToSQLite(List<Product> products) {
        SQLiteDatabase sqlDb = dbHelper.getWritableDatabase();
        sqlDb.beginTransaction();
        try {
            for (Product p : products) {
                ContentValues values = new ContentValues();
                values.put("id", p.getId());
                values.put("name", p.getName());
                values.put("price", p.getPrice());
                values.put("imageUrl", p.getImageUrl());
                values.put("category", p.getCategory());
                sqlDb.insertWithOnConflict("products", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            sqlDb.setTransactionSuccessful();
        } finally {
            sqlDb.endTransaction();
            sqlDb.close();
        }
    }

    @Override
    public void onAddToCartClick(Product product) {
        String[] options = {"Sửa", "Xóa"};
        new AlertDialog.Builder(getContext())
                .setTitle("Tùy chọn cho " + product.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showProductDialog(product);
                    } else {
                        showDeleteConfirmation(product);
                    }
                })
                .show();
    }

    private void showDeleteConfirmation(Product product) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa " + product.getName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteProduct(product))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteProduct(Product product) {
        db.collection("products").document(product.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    deleteFromSQLite(product.getId());
                    Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteFromSQLite(String id) {
        SQLiteDatabase sqlDb = dbHelper.getWritableDatabase();
        sqlDb.delete("products", "id = ?", new String[]{id});
        sqlDb.close();
    }

    @Override
    public void onItemDetailClick(Product product) {
        Toast.makeText(getContext(), "Xem chi tiết: " + product.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (productsListener != null) {
            productsListener.remove();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}