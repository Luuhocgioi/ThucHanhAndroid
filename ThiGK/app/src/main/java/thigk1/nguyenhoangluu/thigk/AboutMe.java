package thigk1.nguyenhoangluu.thigk;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

public class AboutMe extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private RecyclerView recyclerViewAboutItems;
    private AboutItemAdapter aboutItemAdapter;
    private TextView textName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_about_me);

        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        toolbar = findViewById(R.id.toolbar);
        recyclerViewAboutItems = findViewById(R.id.recyclerViewAboutItems);
        textName = findViewById(R.id.textName);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        collapsingToolbar.setTitle(textName.getText().toString());
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));

        recyclerViewAboutItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAboutItems.setNestedScrollingEnabled(false);

        List<AboutItem> aboutItems = new ArrayList<>();
        aboutItems.add(new AboutItem("Bio", "Tôi là một lập trình viên Android", R.drawable.ic_info));
        aboutItems.add(new AboutItem("Ngày sinh", "02/07/2005", R.drawable.ic_cake));
        aboutItems.add(new AboutItem("Sở thích", "Đi giao lưu bạn bè", R.drawable.ic_heart));
        aboutItems.add(new AboutItem("Kỹ năng", "Java, Android", R.drawable.ic_code));
        aboutItems.add(new AboutItem("Học vấn", "Đại học Nha Trang", R.drawable.ic_school));
        aboutItems.add(new AboutItem("Email", "Nguyenluwu@gmail.com", R.drawable.ic_email));
        aboutItems.add(new AboutItem("GitHub", "https://github.com/Luuhocgioi", R.drawable.ic_github));

        aboutItemAdapter = new AboutItemAdapter(aboutItems);
        recyclerViewAboutItems.setAdapter(aboutItemAdapter);
    }
}
