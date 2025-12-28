package clc65.hoangluu.duan.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.material.tabs.TabLayoutMediator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

import clc65.hoangluu.duan.R; // Cần import R
import clc65.hoangluu.duan.adapters.OrdersPagerAdapter;
import clc65.hoangluu.duan.databinding.FragmentOrdersBinding;

public class OrdersFragment extends Fragment {

    private FragmentOrdersBinding binding;
    // *** TỐI ƯU HÓA: RÚT GỌN TABS TỪ 5 XUỐNG 4 ***
    private String[] tabTitles = new String[]{"All", "Preparing", "Ready", "Completed"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // LOGIC KIỂM TRA QUYỀN TRUY CẬP (ADMIN HOẶC STAFF)
        if (getContext() == null) {
            return;
        }

        SharedPreferences sharedPref = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = sharedPref.getString("userRole", "Guest");

        boolean isAuthenticated = FirebaseAuth.getInstance().getCurrentUser() != null;
        boolean hasPermission = userRole.equalsIgnoreCase("Admin") || userRole.equalsIgnoreCase("Staff");

        if (!isAuthenticated || !hasPermission) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập với vai trò Quản lý/Nhân viên.", Toast.LENGTH_LONG).show();
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack();
            return;
        }
        // KẾT THÚC KIỂM TRA

        super.onViewCreated(view, savedInstanceState);

        // Thiết lập ViewPager2 Adapter
        OrdersPagerAdapter adapter = new OrdersPagerAdapter(
                getChildFragmentManager(),
                getLifecycle(),
                tabTitles
        );
        binding.viewPagerOrders.setAdapter(adapter);

        // Liên kết TabLayout với ViewPager2
        new TabLayoutMediator(binding.tabLayoutOrderStatus, binding.viewPagerOrders,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();

        // Thiết lập sự kiện nút Back
        setupBackButton(view);
    }

    private void setupBackButton(View view) {
        binding.btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}