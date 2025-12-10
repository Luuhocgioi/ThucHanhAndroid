package clc65.hoangluu.duancanhan_65131861.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import clc65.hoangluu.duancanhan_65131861.R;
import clc65.hoangluu.duancanhan_65131861.adapter.ProductAdapter;
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

public class SearchFragment extends Fragment {

    private EditText edtSearch;
    private ImageView ivCart;
    private Spinner spinnerCategory;
    private RecyclerView rvProductList;
    private ProductAdapter productAdapter;
    private List<Product> allProducts;
    private List<Category> categoryList;
    private DatabaseReference mProductRef, mCategoryRef;

    public SearchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        edtSearch = view.findViewById(R.id.edtSearch);
        ivCart = view.findViewById(R.id.ivCart);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        rvProductList = view.findViewById(R.id.rvProductList);

        mProductRef = FirebaseDatabase.getInstance().getReference("products");
        mCategoryRef = FirebaseDatabase.getInstance().getReference("categories");
        allProducts = new ArrayList<>();
        categoryList = new ArrayList<>();

        setupRecyclerView();
        loadCategoriesAndProducts();

        // Chuyển sang giỏ hàng (nếu có nav_cart)
        ivCart.setOnClickListener(v -> {
            if (getActivity() != null) {
                // Tạm thời hiển thị Toast, sau này bạn có thể chuyển Fragment
                Toast.makeText(getContext(), "Nhấn tab Giỏ hàng để xem chi tiết.", Toast.LENGTH_SHORT).show();
            }
        });

        setupListeners();

        return view;
    }

    private void setupRecyclerView() {
        // Sử dụng GridLayoutManager 2 cột (như trong fragment_search.xml)
        rvProductList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new ProductAdapter(getContext(), allProducts);
        rvProductList.setAdapter(productAdapter);
    }

    private void loadCategoriesAndProducts() {
        // 1. Tải Danh mục
        mCategoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                List<String> categoryNames = new ArrayList<>();
                categoryNames.add("Tất cả"); // Thêm mục "Tất cả"

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
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // 2. Tải Sản phẩm
        mProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allProducts.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    if (product != null) {
                        allProducts.add(product);
                    }
                }
                // Khởi tạo hiển thị ban đầu
                filterProducts(edtSearch.getText().toString(), spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "Tất cả");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải Menu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        // Lắng nghe sự kiện Tìm kiếm (Mục tiêu 2.1.3 - Tìm kiếm)
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString(), spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "Tất cả");
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Lắng nghe sự kiện Lọc (Mục tiêu 2.1.3 - Lọc)
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterProducts(edtSearch.getText().toString(), parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void filterProducts(String keyword, String categoryName) {
        if (allProducts == null || allProducts.isEmpty()) {
            productAdapter.updateList(new ArrayList<>());
            return;
        }

        List<Product> filteredList = allProducts.stream()
                // Lọc theo Danh mục
                .filter(product -> {
                    if ("Tất cả".equals(categoryName)) {
                        return true;
                    }
                    String categoryId = categoryList.stream()
                            .filter(c -> c.getName().equals(categoryName))
                            .findFirst()
                            .map(Category::getId)
                            .orElse(null);
                    return product.getCategoryId().equals(categoryId);
                })
                // Lọc theo Từ khóa (Tên sản phẩm)
                .filter(product -> product.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());

        productAdapter.updateList(filteredList);
    }
}