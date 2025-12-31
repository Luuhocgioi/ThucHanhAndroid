package clc65.hoangluu.duan.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide; // Cần thêm import Glide

import clc65.hoangluu.duan.R;
import clc65.hoangluu.duan.data.ProductRepository;
import clc65.hoangluu.duan.data.LocalCartRepository;
import clc65.hoangluu.duan.databinding.FragmentDetailBinding;
import clc65.hoangluu.duan.models.Product;
import clc65.hoangluu.duan.models.OrderItem;

public class DetailFragment extends Fragment {

    private FragmentDetailBinding binding;
    private Product currentProduct;
    private int quantity = 1;

    private ProductRepository productRepository;
    private LocalCartRepository cartRepository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        productRepository = new ProductRepository(getContext());
        cartRepository = new LocalCartRepository(getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. LẤY DỮ LIỆU TỪ BUNDLE (Thay vì chỉ lấy String ID)
        if (getArguments() != null) {
            currentProduct = (Product) getArguments().getSerializable("product");
        }

        // 2. HIỂN THỊ DỮ LIỆU LÊN GIAO DIỆN
        if (currentProduct != null) {
            displayProductDetails();
        } else {
            Toast.makeText(getContext(), "Không tìm thấy thông tin sản phẩm.", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).popBackStack();
        }
        binding.btnBack.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });
        setupQuantityListeners();
        setupAddToCartListener(view);
    }

    private void displayProductDetails() {
        // Đổ tên và giá
        binding.tvProductNameDetail.setText(currentProduct.getName());
        binding.tvProductPriceDetail.setText(String.format("%,.0f VNĐ", currentProduct.getPrice()));

        // Đổ mô tả (nếu có)
        if (currentProduct.getDescription() != null && !currentProduct.getDescription().isEmpty()) {
            binding.tvProductDescriptionDetail.setText(currentProduct.getDescription());
        }

        // ĐỔ ẢNH BẰNG GLIDE (Giải quyết vấn đề ảnh mặc định)
        if (currentProduct.getImageUrl() != null && !currentProduct.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(currentProduct.getImageUrl())
                    .placeholder(R.drawable.ic_coffee_placeholder)
                    .error(R.drawable.ic_coffee_placeholder)
                    .centerCrop()
                    .into(binding.imgProductDetail);
        }
    }

    private void setupQuantityListeners() {
        binding.tvQuantity.setText(String.valueOf(quantity)); // Khởi tạo giá trị 1

        binding.btnIncrement.setOnClickListener(v -> {
            quantity++;
            binding.tvQuantity.setText(String.valueOf(quantity));
        });

        binding.btnDecrement.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                binding.tvQuantity.setText(String.valueOf(quantity));
            }
        });
    }

    private void setupAddToCartListener(View view) {
        binding.btnAddToCartDetail.setOnClickListener(v -> {
            if (currentProduct != null) {
                String note = binding.etProductNote.getText().toString();

                // SỬA TẠI ĐÂY: Truyền thêm currentProduct.getImageUrl() vào tham số thứ 5
                OrderItem item = new OrderItem(
                        currentProduct.getId(),
                        currentProduct.getName(),
                        quantity,
                        currentProduct.getPrice(),
                        currentProduct.getImageUrl(), // Thêm link ảnh để đồng bộ
                        note
                );

                boolean success = cartRepository.addItemToCart(item);

                if (success) {
                    Toast.makeText(getContext(), "Đã thêm " + quantity + " x " + currentProduct.getName() + " vào Giỏ hàng!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).popBackStack();
                } else {
                    Toast.makeText(getContext(), "Lỗi khi thêm vào giỏ hàng.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}