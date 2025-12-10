package clc65.hoangluu.duancanhan_65131861;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import clc65.hoangluu.duancanhan_65131861.fragments.HomeFragment;
import clc65.hoangluu.duancanhan_65131861.fragments.SearchFragment;
import clc65.hoangluu.duancanhan_65131861.fragments.NotificationFragment;
import clc65.hoangluu.duancanhan_65131861.fragments.SettingsFragment;
import clc65.hoangluu.duancanhan_65131861.fragments.CartFragment;
import clc65.hoangluu.duancanhan_65131861.fragments.OrdersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userRole = "Guest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        loadMenuAndFragment(savedInstanceState);
    }

    private void loadMenuAndFragment(Bundle savedInstanceState) {
        if (mAuth.getCurrentUser() != null) {
            checkRoleFromFirebase(savedInstanceState);
        } else {
            userRole = "Guest";
            bottomNavigationView.getMenu().clear(); // SỬA LỖI: Xóa menu cũ
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_v2);
            if (savedInstanceState == null) {
                loadFragment(new HomeFragment());
            }
            setupNavigationListener();
        }
    }

    private void checkRoleFromFirebase(Bundle savedInstanceState) {
        String uid = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(uid).child("role")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            userRole = snapshot.getValue(String.class);
                        } else {
                            userRole = "Guest";
                        }

                        bottomNavigationView.getMenu().clear(); // SỬA LỖI: Xóa menu cũ
                        if ("Admin".equals(userRole) || "Staff".equals(userRole)) {
                            bottomNavigationView.inflateMenu(R.menu.bottom_nav_staff);
                            if (savedInstanceState == null) {
                                loadFragment(new SearchFragment());
                            }
                        } else {
                            bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_v2);
                            if (savedInstanceState == null) {
                                loadFragment(new HomeFragment());
                            }
                        }

                        setupNavigationListener();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Lỗi đọc vai trò (Firebase): " + error.getMessage() + ". Tải Menu Khách.", Toast.LENGTH_LONG).show();

                        userRole = "Guest";
                        bottomNavigationView.getMenu().clear(); // SỬA LỖI: Xóa menu cũ
                        bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_v2);
                        if (savedInstanceState == null) {
                            loadFragment(new HomeFragment());
                        }
                        setupNavigationListener();
                    }
                });
    }

    private void setupNavigationListener() {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                boolean isLoggedIn = mAuth.getCurrentUser() != null;

                if (itemId == R.id.nav_cart || itemId == R.id.nav_orders) {
                    if (!isLoggedIn) {
                        Toast.makeText(MainActivity.this, "Vui lòng đăng nhập để truy cập chức năng này.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }

                if (itemId == R.id.nav_search) {
                    selectedFragment = new SearchFragment();
                } else if (itemId == R.id.nav_cart) {
                    selectedFragment = new CartFragment();
                } else if (itemId == R.id.nav_orders) {
                    if (!"Admin".equals(userRole) && !"Staff".equals(userRole)) {
                        Toast.makeText(MainActivity.this, "Bạn không có quyền truy cập Quản lý Đơn hàng.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    selectedFragment = new OrdersFragment();
                } else if (itemId == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_notifications) {
                    selectedFragment = new NotificationFragment();
                } else if (itemId == R.id.nav_settings) {
                    selectedFragment = new SettingsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("USER_ROLE", userRole);
                    bundle.putBoolean("IS_LOGGED_IN", isLoggedIn);
                    selectedFragment.setArguments(bundle);
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        try {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.commit();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi hiển thị màn hình: Vui lòng kiểm tra Layout XML của Fragment " + fragment.getClass().getSimpleName() + " (Lỗi: " + e.getMessage() + ")", Toast.LENGTH_LONG).show();
        }
    }

    public void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void redirectToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public boolean isAdmin() {
        return "Admin".equals(userRole);
    }

}