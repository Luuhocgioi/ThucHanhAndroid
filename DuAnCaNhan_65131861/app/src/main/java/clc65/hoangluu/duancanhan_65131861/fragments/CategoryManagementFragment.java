package clc65.hoangluu.duancanhan_65131861.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import clc65.hoangluu.duancanhan_65131861.R;
import clc65.hoangluu.duancanhan_65131861.adapter.CategoryAdapter;
import clc65.hoangluu.duancanhan_65131861.model.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryManagementFragment extends Fragment {

    private RecyclerView rvCategories;
    private EditText edtCategoryName, edtImageUrl;
    private Button btnAddCategory;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private DatabaseReference mDatabase;
    private String categoryToEditId = null;

    public CategoryManagementFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_management, container, false);

        rvCategories = view.findViewById(R.id.rvCategories);
        edtCategoryName = view.findViewById(R.id.edtCategoryName);
        edtImageUrl = view.findViewById(R.id.edtImageUrl);
        btnAddCategory = view.findViewById(R.id.btnAddCategory);

        mDatabase = FirebaseDatabase.getInstance().getReference("categories");
        categoryList = new ArrayList<>();

        setupRecyclerView();
        loadCategories();

        btnAddCategory.setOnClickListener(v -> saveCategory());

        return view;
    }

    private void setupRecyclerView() {
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryAdapter = new CategoryAdapter(getContext(), categoryList, new CategoryAdapter.CategoryActionListener() {
            @Override
            public void onEdit(Category category) {
                edtCategoryName.setText(category.getName());
                edtImageUrl.setText(category.getImageUrl());
                btnAddCategory.setText("Lưu Thay Đổi");
                categoryToEditId = category.getId();
            }

            @Override
            public void onDelete(String categoryId) {
                mDatabase.child(categoryId).removeValue()
                        .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Đã xóa danh mục.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
        rvCategories.setAdapter(categoryAdapter);
    }

    private void loadCategories() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Category category = postSnapshot.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }
                categoryAdapter.updateList(categoryList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải danh mục: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCategory() {
        String name = edtCategoryName.getText().toString().trim();
        String imageUrl = edtImageUrl.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập tên danh mục.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (categoryToEditId != null) {
            Category updatedCategory = new Category(categoryToEditId, name, imageUrl);
            mDatabase.child(categoryToEditId).setValue(updatedCategory)
                    .addOnSuccessListener(aVoid -> clearInput(true))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi sửa: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            String newId = mDatabase.push().getKey();
            Category newCategory = new Category(newId, name, imageUrl);
            mDatabase.child(newId).setValue(newCategory)
                    .addOnSuccessListener(aVoid -> clearInput(false))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi thêm: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void clearInput(boolean isEditMode) {
        edtCategoryName.setText("");
        edtImageUrl.setText("");
        if (isEditMode) {
            btnAddCategory.setText("Thêm Danh mục");
            categoryToEditId = null;
        }
        Toast.makeText(getContext(), isEditMode ? "Lưu thành công!" : "Thêm thành công!", Toast.LENGTH_SHORT).show();
    }
}