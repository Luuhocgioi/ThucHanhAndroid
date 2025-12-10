package clc65.hoangluu.duan.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.tabs.TabLayoutMediator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import clc65.hoangluu.duan.adapters.OrdersPagerAdapter;
import clc65.hoangluu.duan.databinding.FragmentOrdersBinding;

public class OrdersFragment extends Fragment {

    private FragmentOrdersBinding binding;
    private String[] tabTitles = new String[]{"All", "Pending", "Preparing", "Ready", "Completed"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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