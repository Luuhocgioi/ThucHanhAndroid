package clc65.hoangluu.listviewjson;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView dsName;
    ArrayList<String> lstName; // Chỉ khai báo, chưa khởi tạo
    ArrayAdapter<String> adapter; // Chỉ khai báo

    void TimTen() {
        dsName = findViewById(R.id.dsName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TimTen();

        // Bước 1: Lấy dữ liệu từ file JSON
        lstName = getDataJson();

        // Bước 2: Tạo Adapter VỚI DỮ LIỆU ĐÃ CÓ
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lstName);

        // Bước 3: Gắn Adapter vào ListView
        dsName.setAdapter(adapter);

        // Bước 4: Bắt sự kiện click
        dsName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ten = lstName.get(position); // Lấy trực tiếp từ list cho an toàn
                String thongBao = "Ban da chon: " + ten;
                Toast.makeText(MainActivity.this, thongBao, Toast.LENGTH_SHORT).show();
            }
        });
    }

    ArrayList<String> getDataJson() {
        ArrayList<String> lstTam = new ArrayList<>();
        String jsonString;
        try {
            InputStream is = getAssets().open("danhsach.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String ten = jsonObject.getString("ten");
                lstTam.add(ten);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lstTam;
    }
}