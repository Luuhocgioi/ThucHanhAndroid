package clc65.hoangluu.thithu;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity4 extends AppCompatActivity {

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
        setContentView(R.layout.activity_main5);
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
        dsList.add(new Landscape("Tiêu đề hoạt động 1", "Thời gian 1", "anhnhatrang1"));
        dsList.add(new Landscape("Tiêu đề hoạt động 2", "Thời gian 2", "anhnhatrang2"));
        dsList.add(new Landscape("Tiêu đề hoạt động 3", "Thời gian 3", "anhnhatrang3"));
        dsList.add(new Landscape("Tiêu đề hoạt động 4", "Thời gian 4", "anhnhatrang4"));
        return dsList;
    }
}