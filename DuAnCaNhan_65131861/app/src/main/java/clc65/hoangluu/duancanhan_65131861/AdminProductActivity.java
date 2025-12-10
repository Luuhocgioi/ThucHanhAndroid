package clc65.hoangluu.duancanhan_65131861;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import clc65.hoangluu.duancanhan_65131861.R;
import clc65.hoangluu.duancanhan_65131861.fragments.CategoryManagementFragment;
import clc65.hoangluu.duancanhan_65131861.fragments.ProductManagementFragment;
import com.google.android.material.tabs.TabLayout;

public class AdminProductActivity extends AppCompatActivity {

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product);

        tabLayout = findViewById(R.id.tabLayout);

        // Thiết lập Fragment mặc định (Quản lý Sản phẩm)
        loadFragment(new ProductManagementFragment());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                androidx.fragment.app.Fragment selectedFragment = null;
                if (tab.getPosition() == 0) {
                    selectedFragment = new ProductManagementFragment();
                } else if (tab.getPosition() == 1) {
                    selectedFragment = new CategoryManagementFragment();
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadFragment(androidx.fragment.app.Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container_admin, fragment);
        ft.commit();
    }
}