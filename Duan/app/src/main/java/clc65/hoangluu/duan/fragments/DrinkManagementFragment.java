package clc65.hoangluu.duan.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import clc65.hoangluu.duan.adapters.ProductAdapter; // Sử dụng lại Adapter của Product
import clc65.hoangluu.duan.databinding.FragmentDrinkManagementBinding;
import clc65.hoangluu.duan.models.Product;

public class DrinkManagementFragment extends Fragment implements ProductAdapter.OnItemClickListener {

    private FragmentDrinkManagementBinding binding;
    private ProductAdapter productAdapter;
    private FirebaseFirestore db;
    private ListenerRegistration productsListener;

    public DrinkManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
        // Sử dụng lại ProductAdapter
        productAdapter = new ProductAdapter(new ArrayList<>());
        productAdapter.setOnItemClickListener(this); // Thiết lập Listener
        binding.rvDrinks.setAdapter(productAdapter);
    }

    private void setupAddButton() {
        binding.fabAddDrink.setOnClickListener(v -> {
            // TODO: Mở Dialog hoặc Fragment để thêm thức uống mới
            Toast.makeText(getContext(), "Chức năng thêm thức uống đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void startListeningForDrinks() {
        // Giả sử tất cả sản phẩm đều là thức uống (hoặc lọc theo category nếu có)
        Query query = db.collection("products")
                        .orderBy("name", Query.Direction.ASCENDING);

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
                productAdapter.setProductList(products);
            }
        });
    }

    // Implement các phương thức của ProductAdapter.OnItemClickListener
    @Override
    public void onAddToCartClick(Product product) {
        // Trong màn hình quản lý, có thể dùng nút này để "Sửa" hoặc "Xóa"
        // Hoặc ẩn nút này đi trong Adapter nếu cần (cần sửa Adapter để hỗ trợ mode quản lý)
        Toast.makeText(getContext(), "Chỉnh sửa: " + product.getName(), Toast.LENGTH_SHORT).show();
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