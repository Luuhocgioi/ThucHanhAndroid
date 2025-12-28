package clc65.hoangluu.duan;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import clc65.hoangluu.duan.databinding.ActivityMainBinding;
import clc65.hoangluu.duan.fragments.HomeFragment;
import clc65.hoangluu.duan.data.LocalCartRepository;

// Giả định RoleUpdateListener.java đã được tạo riêng
public class MainActivity extends AppCompatActivity implements RoleUpdateListener {

    private ActivityMainBinding binding;
    private NavController navController;
    private LocalCartRepository cartRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartRepository = new LocalCartRepository(this); // Khởi tạo Repository

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(binding.navHostFragment.getId());

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // KHẮC PHỤC LỖI VIEW BINDING: SỬ DỤNG ID bottomNav
            BottomNavigationView bottomNav = binding.bottomNav;

            // 1. Setup BottomNavigationView với NavController
            NavigationUI.setupWithNavController(bottomNav, navController);

            // *** KHẮC PHỤC LỖI: THIẾT LẬP LISTENER CHO FAB (Nút Giỏ hàng) ***
            binding.fabCart.setOnClickListener(v -> {
                // Chuyển đến CartFragment
                navController.navigate(R.id.cartFragment);
            });
            // ***************************************************************

            // 2. Logic Ẩn/hiện Fab Container và cập nhật Badge
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                // Hiển thị FAB khi ở màn hình chính (Home) hoặc tìm kiếm (Search)
                if (destination.getId() == R.id.homeFragment || destination.getId() == R.id.searchFragment) {
                    binding.layoutFabContainer.setVisibility(View.VISIBLE);
                    updateCartBadge(); // Cập nhật Badge khi quay lại
                } else {
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

    // ==========================================================
    // LOGIC BADGE (ĐẾM SỐ LƯỢNG MỤC TRONG GIỎ HÀNG)
    // ==========================================================
    public void updateCartBadge() {
        if (cartRepository == null || binding == null) return; // Bảo vệ Null Pointer

        int itemCount = cartRepository.getCartItems().size();

        if (itemCount > 0) {
            binding.tvCartBadge.setText(String.valueOf(itemCount));
            binding.tvCartBadge.setVisibility(View.VISIBLE);
        } else {
            binding.tvCartBadge.setVisibility(View.GONE);
        }
    }

    // TRIỂN KHAI PHƯƠNG THỨC TỪ INTERFACE (Cập nhật vai trò)
    @Override
    public void onRoleUpdated(String newRole) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(binding.navHostFragment.getId());
        if (navHostFragment != null) {
            Fragment homeFragment = navHostFragment.getChildFragmentManager().findFragmentById(R.id.homeFragment);

            if (homeFragment instanceof HomeFragment) {
                ((HomeFragment) homeFragment).updateManagementMenuVisibility(newRole);
            }
        }
    }
}