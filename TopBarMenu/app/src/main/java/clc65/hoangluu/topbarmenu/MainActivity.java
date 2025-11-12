package clc65.hoangluu.topbarmenu;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private MaterialToolbar topAppBar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views
        drawerLayout = findViewById(R.id.drawer_layout);
        topAppBar = findViewById(R.id.topAppBar);
        navigationView = findViewById(R.id.nav_view);

        setSupportActionBar(topAppBar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                topAppBar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        topAppBar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_search) {
                Toast.makeText(this, "Tìm kiếm...", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.action_shop) {
                Toast.makeText(this, "Mở shop", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.action_settings) {
                Toast.makeText(this, "Mở cài đặt", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        Button btnChangeTitle = findViewById(R.id.btnChangeTitle);
        btnChangeTitle.setOnClickListener(v -> topAppBar.setTitle("Tiêu đề mới"));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Đơn giản: đổi tiêu đề theo item và đóng drawer
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            topAppBar.setTitle("Trang chính");
        } else if (id == R.id.nav_profile) {
            topAppBar.setTitle("Hồ sơ");
        }
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}
