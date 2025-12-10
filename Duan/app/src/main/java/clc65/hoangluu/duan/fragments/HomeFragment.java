package clc65.hoangluu.duan.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import clc65.hoangluu.duan.R;
import clc65.hoangluu.duan.adapters.ProductAdapter;
import clc65.hoangluu.duan.adapters.SliderAdapter;
import clc65.hoangluu.duan.data.LocalCartRepository;
import clc65.hoangluu.duan.data.ProductRepository;
import clc65.hoangluu.duan.databinding.FragmentHomeBinding;
import clc65.hoangluu.duan.models.OrderItem;
import clc65.hoangluu.duan.models.Product;
import clc65.hoangluu.duan.models.SliderItem;

public class HomeFragment extends Fragment implements ProductAdapter.OnItemClickListener {

    private FragmentHomeBinding binding;
    private ProductRepository productRepository;
    private LocalCartRepository cartRepository; // Đã thêm: Repository cho giỏ hàng
    private ProductAdapter productAdapter;
    private SliderAdapter sliderAdapter;

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_ROLE = "userRole";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        productRepository = new ProductRepository(getContext());
        cartRepository = new LocalCartRepository(getContext()); // Đã thêm: Khởi tạo
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupSlider();
        setupMenuRecyclerView();

        // BẮT ĐẦU LẮNG NGHE FIREBASE VÀ ĐỒNG BỘ SQLITE
        productRepository.startListeningForProductUpdates();

        // TẢI SẢN PHẨM TỪ SQLITE CACHE CỤC BỘ
        loadProductDataFromCache();

        setupManagementListeners(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String userRole = sharedPref.getString(KEY_ROLE, "Guest");
        updateManagementMenuVisibility(userRole);

        // Cập nhật lại dữ liệu từ cache mỗi khi Fragment quay lại foreground
        loadProductDataFromCache();
    }

    private void setupSlider() {
        List<SliderItem> sliderItems = Arrays.asList(
                new SliderItem("https://via.placeholder.com/600/92c952?text=Coffee+Deal"),
                new SliderItem("https://via.placeholder.com/600/771796?text=New+Juice"),
                new SliderItem("https://via.placeholder.com/600/24f355?text=Summer+Promo")
        );

        sliderAdapter = new SliderAdapter(sliderItems);
        binding.viewPagerSlider.setAdapter(sliderAdapter);
    }

    private void setupMenuRecyclerView() {
        // Đã sửa lại thành LinearLayoutManager cho Menu List
        binding.rvMenuItems.setLayoutManager(new LinearLayoutManager(getContext()));
        productAdapter = new ProductAdapter(new ArrayList<>());
        productAdapter.setOnItemClickListener(this);
        binding.rvMenuItems.setAdapter(productAdapter);
    }

    // PHƯƠNG THỨC TẢI DỮ LIỆU TỪ SQLITE CACHE
    private void loadProductDataFromCache() {
        List<Product> cachedProducts = productRepository.getAllProductsFromCache();
        productAdapter.setProductList(cachedProducts);

        if (cachedProducts.isEmpty()) {
            Toast.makeText(getContext(), "Đang tải dữ liệu từ Firebase lần đầu...", Toast.LENGTH_SHORT).show();
            // Có thể thêm logic hiển thị loading nếu cần
        }
    }

    private void setupManagementListeners(View view) {

        NavController navController = Navigation.findNavController(view);

        binding.cardOrders.setOnClickListener(v -> {
            // Toast.makeText(getContext(), "Chuyển đến màn hình Hóa Đơn", Toast.LENGTH_SHORT).show(); // Bỏ Toast
            navController.navigate(R.id.ordersFragment);
        });

        binding.cardTables.setOnClickListener(v -> {
            // Toast.makeText(getContext(), "Chuyển đến màn hình Quản lý Bàn", Toast.LENGTH_SHORT).show(); // Bỏ Toast
            navController.navigate(R.id.tableStatusFragment);
        });

        binding.cardTakeaway.setOnClickListener(v -> {
            // Toast.makeText(getContext(), "Chuyển đến màn hình Đơn Mang Về", Toast.LENGTH_SHORT).show(); // Bỏ Toast
            navController.navigate(R.id.takeAwayFragment);
        });

        // Xử lý sự kiện click cho thẻ Thức uống
        binding.cardDrinks.setOnClickListener(v -> {
            // Toast.makeText(getContext(), "Chuyển đến màn hình Quản lý Thức uống", Toast.LENGTH_SHORT).show(); // Bỏ Toast
            navController.navigate(R.id.drinkManagementFragment);
        });

        binding.cardStaff.setOnClickListener(v -> {
            // Toast.makeText(getContext(), "Chuyển đến màn hình Quản lý Nhân Viên", Toast.LENGTH_SHORT).show(); // Bỏ Toast
            navController.navigate(R.id.staffManagementFragment);
        });

        binding.cardRevenue.setOnClickListener(v -> {
            // Toast.makeText(getContext(), "Chuyển đến màn hình Doanh Thu", Toast.LENGTH_SHORT).show(); // Bỏ Toast
            navController.navigate(R.id.revenueReportFragment);
        });
    }

    public void updateManagementMenuVisibility(String userRole) {
        boolean isStaffOrAdmin = userRole.equals("Staff") || userRole.equals("Admin");
        boolean isAdmin = userRole.equals("Admin");

        if (isStaffOrAdmin) {
            binding.tvManagementTitle.setVisibility(View.VISIBLE);
            binding.layoutManagementMenu.setVisibility(View.VISIBLE);

            binding.cardOrders.setVisibility(View.VISIBLE);
            binding.cardTables.setVisibility(View.VISIBLE);
            binding.cardTakeaway.setVisibility(View.VISIBLE);
            
            // Hiển thị Card Thức uống cho cả Staff và Admin
            binding.cardDrinks.setVisibility(View.VISIBLE);

            if (isAdmin) {
                binding.cardStaff.setVisibility(View.VISIBLE);
                binding.cardRevenue.setVisibility(View.VISIBLE);
            } else {
                binding.cardStaff.setVisibility(View.GONE);
                binding.cardRevenue.setVisibility(View.GONE);
            }
        } else {
            binding.tvManagementTitle.setVisibility(View.GONE);
            binding.layoutManagementMenu.setVisibility(View.GONE);

            binding.cardOrders.setVisibility(View.GONE);
            binding.cardTables.setVisibility(View.GONE);
            binding.cardTakeaway.setVisibility(View.GONE);
            binding.cardDrinks.setVisibility(View.GONE);
            binding.cardStaff.setVisibility(View.GONE);
            binding.cardRevenue.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAddToCartClick(Product product) {
        // Đã sửa: Lưu sản phẩm vào SQLite thông qua LocalCartRepository
        OrderItem item = new OrderItem();
        item.setProductId(product.getId()); // ID từ Firebase
        item.setName(product.getName());
        item.setPrice(product.getPrice());
        item.setQuantity(1); // Mặc định thêm 1
        item.setNote(""); // Mặc định không ghi chú

        boolean success = cartRepository.addItemToCart(item);

        if (success) {
            Toast.makeText(getContext(), "Đã thêm " + product.getName() + " vào giỏ hàng!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Lỗi khi thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemDetailClick(Product product) {
        NavController navController = Navigation.findNavController(requireView());

        Bundle bundle = new Bundle();
        bundle.putString("productId", product.getId());

        navController.navigate(R.id.detailFragment, bundle);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Dừng lắng nghe Firebase khi Fragment không còn hiển thị
        productRepository.stopListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}