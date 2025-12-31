package clc65.hoangluu.duan.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import clc65.hoangluu.duan.adapters.ProductAdapter;
import clc65.hoangluu.duan.database.DatabaseHelper;
import clc65.hoangluu.duan.databinding.FragmentSearchBinding;
import clc65.hoangluu.duan.models.Product;

public class SearchFragment extends Fragment implements ProductAdapter.OnItemClickListener {

    private FragmentSearchBinding binding;
    private ProductAdapter searchAdapter;
    private DatabaseHelper dbHelper;
    private String currentCategory = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupSearchLogic();
        setupCategoryFilters();
        loadProductsFromSQLite("");
    }

    private void setupRecyclerView() {
        binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        searchAdapter = new ProductAdapter(new ArrayList<>());
        searchAdapter.setOnItemClickListener(this);
        binding.rvSearchResults.setAdapter(searchAdapter);
    }

    private void setupSearchLogic() {
        binding.etSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadProductsFromSQLite(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupCategoryFilters() {
        binding.chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentCategory = "";
            } else {
                Chip chip = group.findViewById(checkedIds.get(0));
                String chipText = chip.getText().toString();

                if (chipText.contains("Cà phê")) {
                    currentCategory = "Coffee";
                } else if (chipText.contains("Trà sữa")) {
                    currentCategory = "MilkTea";
                } else if (chipText.contains("Trà")) {
                    currentCategory = "Tea";
                } else {
                    currentCategory = "";
                }
            }
            loadProductsFromSQLite(binding.etSearchQuery.getText().toString());
        });
    }
    private void loadProductsFromSQLite(String query) {
        List<Product> filteredList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1");
        List<String> selectionArgs = new ArrayList<>();

        if (!query.isEmpty()) {
            sql.append(" AND name LIKE ?");
            selectionArgs.add("%" + query + "%");
        }

        if (!currentCategory.isEmpty()) {
            sql.append(" AND category = ?");
            selectionArgs.add(currentCategory);
        }

        Cursor cursor = db.rawQuery(sql.toString(), selectionArgs.toArray(new String[0]));

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(cursor.getString(0));
                product.setName(cursor.getString(1));
                product.setPrice(cursor.getDouble(2));
                product.setImageUrl(cursor.getString(3));
                product.setCategory(cursor.getString(4));
                filteredList.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        updateUI(filteredList, query);
    }

    private void updateUI(List<Product> filteredList, String query) {
        searchAdapter.updateList(filteredList);

        if (filteredList.isEmpty()) {
            showState("no_results");
        } else {
            showState("results");
            String title = query.isEmpty() ? "Tất cả sản phẩm" : "Kết quả cho \"" + query + "\"";
            binding.tvSearchResultsTitle.setText(title + " (" + filteredList.size() + ")");
        }
    }

    private void showState(String state) {
        boolean isNoResults = state.equals("no_results");
        boolean isResults = state.equals("results");

        binding.noResultsState.setVisibility(isNoResults ? View.VISIBLE : View.GONE);
        binding.resultsHeader.setVisibility(isResults ? View.VISIBLE : View.GONE);
        binding.rvSearchResults.setVisibility(isResults ? View.VISIBLE : View.GONE);

        if (binding.emptyState != null) {
            binding.emptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAddToCartClick(Product product) {
        addToCart(product);
    }

    private void addToCart(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_id", product.getId());
        values.put("name", product.getName());
        values.put("quantity", 1);
        values.put("price", product.getPrice());
        values.put("note", "");

        long result = db.insert("cart", null, values);
        if (result != -1) {
            Toast.makeText(getContext(), "Đã thêm " + product.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemDetailClick(Product product) {
        Toast.makeText(getContext(), "Chi tiết: " + product.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}