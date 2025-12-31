package clc65.hoangluu.duan;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import clc65.hoangluu.duan.databinding.ActivityMainBinding;
import clc65.hoangluu.duan.fragments.HomeFragment;
import clc65.hoangluu.duan.data.LocalCartRepository;

public class MainActivity extends AppCompatActivity implements RoleUpdateListener {

    private ActivityMainBinding binding;
    private NavController navController;
    private LocalCartRepository cartRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartRepository = new LocalCartRepository(this);

        // Thiết lập NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // SỬ DỤNG BINDING ĐỂ GỌI BOTTOM NAV (Đảm bảo ID trong XML là bottom_nav)
            BottomNavigationView bottomNav = binding.bottomNav;

            // Kết nối BottomNavigationView với NavController
            NavigationUI.setupWithNavController(bottomNav, navController);

            // FAB Giỏ hàng
            binding.fabCart.setOnClickListener(v -> navController.navigate(R.id.cartFragment));

            // LOGIC QUAN TRỌNG: Quản lý ẩn/hiện BottomNav và FAB
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int id = destination.getId();

                // 1. Kiểm tra các màn hình chính để hiện Menu và FAB
                if (id == R.id.homeFragment || id == R.id.searchFragment || id == R.id.settingsFragment) {
                    bottomNav.setVisibility(View.VISIBLE);
                    binding.layoutFabContainer.setVisibility(View.VISIBLE);
                    updateCartBadge();
                }
                // 2. Ẩn toàn bộ khi vào Chi tiết đơn hoặc Giỏ hàng để tránh đè giao diện
                else if (id == R.id.orderDetailFragment || id == R.id.cartFragment) {
                    bottomNav.setVisibility(View.GONE);
                    binding.layoutFabContainer.setVisibility(View.GONE);
                }
                // 3. Các trường hợp khác (như Sơ đồ bàn, Hóa đơn) hiện menu nhưng ẩn FAB
                else {
                    bottomNav.setVisibility(View.VISIBLE);
                    binding.layoutFabContainer.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    public void updateCartBadge() {
        if (cartRepository == null || binding == null) return;
        int itemCount = cartRepository.getCartItems().size();
        if (itemCount > 0) {
            binding.tvCartBadge.setText(String.valueOf(itemCount));
            binding.tvCartBadge.setVisibility(View.VISIBLE);
        } else {
            binding.tvCartBadge.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRoleUpdated(String newRole) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            Fragment homeFragment = navHostFragment.getChildFragmentManager().findFragmentById(R.id.homeFragment);
            if (homeFragment instanceof HomeFragment) {
                ((HomeFragment) homeFragment).updateManagementMenuVisibility(newRole);
            }
        }
    }
}