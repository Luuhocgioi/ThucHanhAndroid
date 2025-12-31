package clc65.hoangluu.duan.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import clc65.hoangluu.duan.adapters.OrdersPagerAdapter;
import clc65.hoangluu.duan.databinding.FragmentOrdersBinding;

public class OrdersFragment extends Fragment {

    private FragmentOrdersBinding binding;
    private final String[] tabTitles = new String[]{"All", "Preparing", "Ready", "Completed"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() == null) return;

        SharedPreferences sharedPref = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = sharedPref.getString("userRole", "Guest");

        boolean isAuthenticated = FirebaseAuth.getInstance().getCurrentUser() != null;
        boolean hasPermission = userRole.equalsIgnoreCase("Admin") || userRole.equalsIgnoreCase("Staff");

        if (!isAuthenticated || !hasPermission) {
            Toast.makeText(getContext(), "Quyền truy cập bị từ chối!", Toast.LENGTH_LONG).show();
            Navigation.findNavController(view).popBackStack();
            return;
        }

        OrdersPagerAdapter adapter = new OrdersPagerAdapter(getChildFragmentManager(), getLifecycle(), tabTitles);
        binding.viewPagerOrders.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayoutOrderStatus, binding.viewPagerOrders,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(view).popBackStack());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}