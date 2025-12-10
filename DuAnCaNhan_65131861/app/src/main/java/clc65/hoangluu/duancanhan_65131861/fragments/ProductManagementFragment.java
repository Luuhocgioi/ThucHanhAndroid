package clc65.hoangluu.duancanhan_65131861.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import clc65.hoangluu.duancanhan_65131861.R;
import clc65.hoangluu.duancanhan_65131861.adapter.ProductManagementAdapter;
import clc65.hoangluu.duancanhan_65131861.model.Category;
import clc65.hoangluu.duancanhan_65131861.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductManagementFragment extends Fragment {

    private RecyclerView rvProducts;
    private EditText edtProductName, edtProductPrice, edtProductDesc, edtProductImage;
    private Spinner spinnerCategory;
    private CheckBox cbStatus;
    private Button btnAddProduct;
    private ProductManagementAdapter productAdapter;
    private List<Product> productList;
    private DatabaseReference mProductRef, mCategoryRef;
    private String productToEditId = null;
    private List<Category> categoryList;

    public ProductManagementFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_management, container, false);

        rvProducts = view.findViewById(R.id.rvProducts);
        edtProductName = view.findViewById(R.id.edtProductName);
        edtProductPrice = view.findViewById(R.id.edtProductPrice);
        edtProductDesc = view.findViewById(R.id.edtProductDesc);
        edtProductImage = view.findViewById(R.id.edtProductImage);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        cbStatus = view.findViewById(R.id.cbStatus);
        btnAddProduct = view.findViewById(R.id.btnAddProduct);

        mProductRef = FirebaseDatabase.getInstance().getReference("products");
        mCategoryRef = FirebaseDatabase.getInstance().getReference("categories");
        productList = new ArrayList<>();
        categoryList = new ArrayList<>();

        setupCategorySpinner();
        setupRecyclerView();
        loadProducts();

        btnAddProduct.setOnClickListener(v -> saveProduct());

        return view;
    }

    private void setupCategorySpinner() {
        mCategoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                List<String> categoryNames = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Category category = postSnapshot.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                        categoryNames.add(category.getName());
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item, categoryNames);
                spinnerCategory.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải danh mục: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        productAdapter = new ProductManagementAdapter(getContext(), productList, new ProductManagementAdapter.ProductActionListener() {
            @Override
            public void onEdit(Product product) {
                productToEditId = product.getId();
                edtProductName.setText(String.valueOf(product.getName()));
                edtProductPrice.setText(String.valueOf(product.getPrice()));
                edtProductDesc.setText(String.valueOf(product.getDescription()));
                edtProductImage.setText(String.valueOf(product.getImageUrl()));
                cbStatus.setChecked(product.isStatus());
                btnAddProduct.setText("Lưu Thay Đổi");

                for (int i = 0; i < categoryList.size(); i++) {
                    if (categoryList.get(i).getId().equals(product.getCategoryId())) {
                        spinnerCategory.setSelection(i);
                        break;
                    }
                }
            }

            @Override
            public void onDelete(String productId) {
                mProductRef.child(productId).removeValue();
                Toast.makeText(getContext(), "Đã xóa sản phẩm.", Toast.LENGTH_SHORT).show();
            }
        });
        rvProducts.setAdapter(productAdapter);
    }

    private void loadProducts() {
        mProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }
                productAdapter.updateList(productList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải sản phẩm: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProduct() {
        String name = edtProductName.getText().toString().trim();
        String priceStr = edtProductPrice.getText().toString().trim();
        String description = edtProductDesc.getText().toString().trim();
        String imageUrl = edtProductImage.getText().toString().trim();
        boolean status = cbStatus.isChecked();

        if (name.isEmpty() || priceStr.isEmpty() || spinnerCategory.getSelectedItem() == null) {
            Toast.makeText(getContext(), "Vui lòng điền đủ Tên, Giá và chọn Danh mục.", Toast.LENGTH_SHORT).show();
            return;
        }

        long price;
        try {
            price = Long.parseLong(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Giá không hợp lệ.", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedCategoryName = spinnerCategory.getSelectedItem().toString();
        String categoryId = categoryList.stream()
                .filter(c -> c.getName().equals(selectedCategoryName))
                .findFirst()
                .map(Category::getId)
                .orElse(null);

        if (categoryId == null) {
            Toast.makeText(getContext(), "Lỗi: Không tìm thấy ID danh mục.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (productToEditId != null) {
            Product updatedProduct = new Product(productToEditId, categoryId, name, price, description, imageUrl, status);
            mProductRef.child(productToEditId).setValue(updatedProduct)
                    .addOnSuccessListener(aVoid -> clearInput(true))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi sửa: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            String newId = mProductRef.push().getKey();
            Product newProduct = new Product(newId, categoryId, name, price, description, imageUrl, status);
            mProductRef.child(newId).setValue(newProduct)
                    .addOnSuccessListener(aVoid -> clearInput(false))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi thêm: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void clearInput(boolean isEditMode) {
        edtProductName.setText("");
        edtProductPrice.setText("");
        edtProductDesc.setText("");
        edtProductImage.setText("");
        cbStatus.setChecked(true);
        if (isEditMode) {
            btnAddProduct.setText("Thêm Sản phẩm mới");
            productToEditId = null;
        }
        Toast.makeText(getContext(), isEditMode ? "Lưu thành công!" : "Thêm thành công!", Toast.LENGTH_SHORT).show();
    }
}