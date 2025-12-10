package clc65.hoangluu.duan.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation; // Cần import Navigation
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import clc65.hoangluu.duan.R;
import clc65.hoangluu.duan.data.ProductRepository;
import clc65.hoangluu.duan.data.LocalCartRepository; // ĐÃ KHẮC PHỤC LỖI IMPORT
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
        // KHỞI TẠO REPOSITORY GIỎ HÀNG
        cartRepository = new LocalCartRepository(getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String productId = getArguments() != null ? getArguments().getString("productId") : null;

        if (productId != null) {
            loadProductDetails(productId);
        } else {
            Toast.makeText(getContext(), "Không tìm thấy ID sản phẩm.", Toast.LENGTH_SHORT).show();
            // Quay lại nếu không có ID
            Navigation.findNavController(view).popBackStack();
        }

        setupQuantityListeners();
        setupAddToCartListener(view);
    }

    private void loadProductDetails(String productId) {
        // TODO: Viết logic lấy chi tiết sản phẩm từ SQLite Cache

        // Dữ liệu mẫu tạm thời:
        currentProduct = new Product(productId, "Trà Sữa Trân Châu", 50000.0, "url_placeholder");
        currentProduct.setDescription("Trà sữa thơm béo đặc trưng với trân châu dai ngon. Xin đừng quên thêm trân châu đường đen!");

        if (currentProduct != null) {
            binding.tvProductNameDetail.setText(currentProduct.getName());
            binding.tvProductPriceDetail.setText(String.format("%,.0f VNĐ", currentProduct.getPrice()));
            binding.tvProductDescriptionDetail.setText(currentProduct.getDescription());
            // TODO: Load ảnh bằng Glide
        }
    }

    private void setupQuantityListeners() {
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

                // Tạo đối tượng OrderItem
                OrderItem item = new OrderItem(
                        currentProduct.getId(),
                        currentProduct.getName(),
                        quantity,
                        currentProduct.getPrice(),
                        note
                );

                // Ghi item vào bảng SQLite LocalCart
                boolean success = cartRepository.addItemToCart(item);

                if (success) {
                    Toast.makeText(getContext(), "Đã thêm " + quantity + " x " + currentProduct.getName() + " vào Giỏ hàng!", Toast.LENGTH_SHORT).show();

                    // Quay lại màn hình trước đó sau khi thêm thành công
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