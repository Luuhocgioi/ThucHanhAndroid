package clc65.hoangluu.thithu;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity3 extends AppCompatActivity {
    ListView lsMon;
    ArrayList<String> dsMon ;
    ArrayAdapter<String> adapter;
    ImageButton btnBackToHome;

    void Tim() {
        lsMon = findViewById(R.id.lsMon);
        btnBackToHome = findViewById(R.id.btnBackToHome);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Tim();
        dsMon = getData();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dsMon);
        lsMon.setAdapter(adapter);
        lsMon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String mon = adapter.getItem(i).toString();
            String thongBao ="Bạn đã chọn "+ mon;
            Toast.makeText(MainActivity3.this, thongBao, Toast.LENGTH_SHORT).show();
            }
        });
        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng màn hình hiện tại và quay về
            }
        });
    }
    ArrayList<String> getData(){
        ArrayList<String> lstTam = new  ArrayList<String>();
        lstTam.add("Tin học đại cương");
        lstTam.add("Lập trình Java");
        lstTam.add("Phát triển ứng dụng web");
        lstTam.add("Khai phá dữ liệu lớn");
        lstTam.add("Kinh tế chính trị Mác - Lê nin");
        return lstTam;
    }
}