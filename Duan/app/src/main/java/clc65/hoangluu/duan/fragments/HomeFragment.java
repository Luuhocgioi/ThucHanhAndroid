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
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayoutMediator;

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
    private LocalCartRepository cartRepository;
    private ProductAdapter productAdapter;
    private SliderAdapter sliderAdapter;

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_ROLE = "userRole";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        productRepository = new ProductRepository(getContext());
        cartRepository = new LocalCartRepository(getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Setup giao diện Slider mới (Modern Design)
        setupModernSlider();

        // 2. Setup danh sách sản phẩm
        setupMenuRecyclerView();

        // 3. Đồng bộ dữ liệu Firebase & SQLite
        productRepository.startListeningForProductUpdates();
        loadProductDataFromCache();

        // 4. Setup các nút chức năng quản lý
        setupManagementListeners(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật giao diện dựa trên quyền khi quay lại fragment
        SharedPreferences sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String userRole = sharedPref.getString(KEY_ROLE, "Guest");
        updateManagementMenuVisibility(userRole);

        loadProductDataFromCache();
    }

    /**
     * THIẾT LẬP SLIDER VỚI HIỆU ỨNG MODERN VÀ INDICATOR
     */
    private void setupModernSlider() {
        List<SliderItem> sliderItems = Arrays.asList(
                new SliderItem("https://img.freepik.com/free-vector/coffee-shop-banner-template_23-2148887754.jpg"),
                new SliderItem("https://img.freepik.com/free-psd/delicious-coffee-social-media-template_23-2148405374.jpg"),
                new SliderItem("https://img.freepik.com/free-vector/coffee-horizontal-banner-design_23-2148892557.jpg")
        );

        sliderAdapter = new SliderAdapter(sliderItems);
        binding.viewPagerSlider.setAdapter(sliderAdapter);

        // Hiệu ứng Transformer (Thu nhỏ 2 bên, tạo chiều sâu)
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        binding.viewPagerSlider.setPageTransformer(compositePageTransformer);
        binding.viewPagerSlider.setOffscreenPageLimit(3);
        binding.viewPagerSlider.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        // Kết nối ViewPager2 với TabLayout để làm dấu chấm (Indicator)
        new TabLayoutMediator(binding.tabLayoutIndicator, binding.viewPagerSlider,
                (tab, position) -> {
                    // Để trống để TabLayout chỉ hiển thị các dấu chấm từ selector drawable
                }).attach();
    }

    private void setupMenuRecyclerView() {
        binding.rvMenuItems.setLayoutManager(new LinearLayoutManager(getContext()));
        productAdapter = new ProductAdapter(new ArrayList<>());
        productAdapter.setOnItemClickListener(this);
        binding.rvMenuItems.setAdapter(productAdapter);
    }

    private void loadProductDataFromCache() {
        List<Product> cachedProducts = productRepository.getAllProductsFromCache();
        if (cachedProducts != null) {
            productAdapter.setProductList(cachedProducts);
        }
    }

    private void setupManagementListeners(View view) {
        NavController navController = Navigation.findNavController(view);

        binding.cardOrders.setOnClickListener(v -> navController.navigate(R.id.ordersFragment));
        binding.cardTables.setOnClickListener(v -> navController.navigate(R.id.tableStatusFragment));
        binding.cardTakeaway.setOnClickListener(v -> navController.navigate(R.id.takeAwayFragment));
        binding.cardDrinks.setOnClickListener(v -> navController.navigate(R.id.drinkManagementFragment));
        binding.cardStaff.setOnClickListener(v -> navController.navigate(R.id.staffManagementFragment));
        binding.cardRevenue.setOnClickListener(v -> navController.navigate(R.id.revenueReportFragment));

        // Nút lọc sản phẩm (Nếu bạn có làm tính năng Filter)
        binding.btnFilter.setOnClickListener(v ->
                Toast.makeText(getContext(), "Tính năng lọc đang phát triển", Toast.LENGTH_SHORT).show());

        // Nút thông báo
        binding.btnNotifications.setOnClickListener(v ->
                Toast.makeText(getContext(), "Bạn không có thông báo mới", Toast.LENGTH_SHORT).show());
    }

    public void updateManagementMenuVisibility(String userRole) {
        if (binding == null) return;

        boolean isStaffOrAdmin = userRole.equals("Staff") || userRole.equals("Admin");
        boolean isAdmin = userRole.equals("Admin");

        if (isStaffOrAdmin) {
            binding.tvManagementTitle.setVisibility(View.VISIBLE);
            binding.layoutManagementMenu.setVisibility(View.VISIBLE);
            binding.cardStaff.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
            binding.cardRevenue.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        } else {
            binding.tvManagementTitle.setVisibility(View.GONE);
            binding.layoutManagementMenu.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAddToCartClick(Product product) {
        OrderItem item = new OrderItem(product.getId(), product.getName(), 1, product.getPrice(), "");
        boolean success = cartRepository.addItemToCart(item);

        if (success) {
            Toast.makeText(getContext(), "Đã thêm " + product.getName() + " vào giỏ!", Toast.LENGTH_SHORT).show();
            // Gọi activity để cập nhật badge trên FAB
            if (getActivity() instanceof clc65.hoangluu.duan.MainActivity) {
                ((clc65.hoangluu.duan.MainActivity) getActivity()).updateCartBadge();
            }
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
        productRepository.stopListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}