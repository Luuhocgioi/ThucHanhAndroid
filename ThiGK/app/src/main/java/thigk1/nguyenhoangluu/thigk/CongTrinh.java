package thigk1.nguyenhoangluu.thigk;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CongTrinh extends AppCompatActivity {

    LandScapeAdapter landscapeAdapter; // Sửa tên biến
    ArrayList<Landscape> dsList;
    RecyclerView rvLandscape;
    ImageButton btnBackToHome;


    void Tim() {
        rvLandscape = findViewById(R.id.rvLandscape);
        btnBackToHome = findViewById(R.id.btnBackToHome);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sử dụng layout có chứa RecyclerView của bạn (activity_main5.xml)
        setContentView(R.layout.activity_cong_trinh);
        Tim();
        dsList = getData();

        // 1. Thay đổi LayoutManager thành DỌC (VERTICAL)
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvLandscape.setLayoutManager(layoutManager);

        landscapeAdapter = new LandScapeAdapter(this, dsList); // Sửa tên biến
        rvLandscape.setAdapter(landscapeAdapter);
        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng màn hình hiện tại và quay về
            }
        });
    }

    ArrayList<Landscape> getData() {
        ArrayList<Landscape> dsList = new ArrayList<>();
        // Cung cấp dữ liệu mới (với 3 tham số)
        dsList.add(new Landscape("Công Trình Xây Dựng", "4 năm", "congtrinh1"));
        dsList.add(new Landscape("Công Trình Hạ Tầng Kỹ Thuật", "6 năm", "congtrinh2"));
        dsList.add(new Landscape("Nền Bắn Cần Cẩu Xây Dựng Trên Bầu Trời", "10 năm", "congtrinh3"));
        dsList.add(new Landscape("Xây Dựng", "5 năm", "congtrinh4"));
        return dsList;
    }
}