package clc65.hoangluu.thithu; // <-- Gói của bạn

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat; // <-- SỬA LỖI FULL MÀN HÌNH
import androidx.core.view.WindowInsetsControllerCompat; // <-- SỬA LỖI ICON MÀU TỐI
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import java.util.ArrayList;
import java.util.List;

// Đảm bảo bạn có file AboutItem.java và AboutItemAdapter.java trong cùng package

public class AboutMeCollapsingActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private RecyclerView recyclerViewAboutItems;
    private AboutItemAdapter aboutItemAdapter; // <-- Sẽ dùng Adapter của chúng ta
    private TextView textName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ===== SỬA LỖI 1: FULL MÀN HÌNH & ICON STATUS BAR MÀU TỐI =====
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        // "false" = dùng icon màu SÁNG (màu trắng)
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);
        // ===========================================

        // Dùng file XML đã sửa lỗi cú pháp
        setContentView(R.layout.activity_about);

        // Ánh xạ các view
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        toolbar = findViewById(R.id.toolbar);
        recyclerViewAboutItems = findViewById(R.id.recyclerViewAboutItems);
        textName = findViewById(R.id.textName);

        // Cài đặt Toolbar (nút back)
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Cài đặt CollapsingToolbar
        collapsingToolbar.setTitle(textName.getText().toString());
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));

        // Cài đặt cuộn mượt
        recyclerViewAboutItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAboutItems.setNestedScrollingEnabled(false);


        // ===== SỬA LỖI 2: DANH SÁCH "Item 0, Item 1..." =====
        // Tạo dữ liệu "Bio", "Ngày sinh" thật
        List<AboutItem> aboutItems = new ArrayList<>();
        aboutItems.add(new AboutItem("Bio", "Tôi là một lập trình viên Android", R.drawable.ic_info));
        aboutItems.add(new AboutItem("Ngày sinh", "02/07/2005", R.drawable.ic_cake));
        aboutItems.add(new AboutItem("Sở thích", "Đi giao lưu bạn bè", R.drawable.ic_heart));
        aboutItems.add(new AboutItem("Kỹ năng", "Java, Android", R.drawable.ic_code));
        aboutItems.add(new AboutItem("Học vấn", "Đại học Nha Trang", R.drawable.ic_school));
        aboutItems.add(new AboutItem("Email", "Nguyenluwu@gmail.com", R.drawable.ic_email));
        aboutItems.add(new AboutItem("GitHub", "https://github.com/Luuhocgioi", R.drawable.ic_github));
        // ===========================================

        // Dùng Adapter của chúng ta, không phải Adapter mặc định
        aboutItemAdapter = new AboutItemAdapter(aboutItems);
        recyclerViewAboutItems.setAdapter(aboutItemAdapter);
    }
}