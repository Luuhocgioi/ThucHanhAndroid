package clc65.hoangluu.vidulistview;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView dsName;
    ArrayList<String> lstName = new ArrayList<String>();
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstName);
    void TimTen() {
        dsName = findViewById(R.id.dsName);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TimTen();

        //Chuẩn bị ngườn dữ liệu hiển thị
        //------------Khai báo



        //Laasy dữ liu đưa vào lstName

        lstName = getData(); //1.1

        //3
        dsName.setAdapter(adapter);
        //4
        dsName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Xử lý việc chọn Item ở đây
                // biến position chứa vị trí của Item được chọn
                // 4.1 Lấy giá trị của phần tử được chọn
                //Cách 1: Lấy gián tiếp qua Adapter
                String ten = adapter.getItem(position).toString();
                //Cách 2: lấy trực tiếp từ biến chứa danh sách
                //String ten2 = adapter.getItem(position);
                //4.2 Làm  gì với nó thì tùy bắn toán
                String thongBao = "Ban da chon: " + ten;
                Toast.makeText(MainActivity.this, thongBao, Toast.LENGTH_SHORT).show();
            }
        });
    }
    //1.1

    ArrayList<String> getData() {
        //Code đọc dữ liệu và cất vào biến tạm, trước khi return
        ArrayList<String> lstTam = new ArrayList<String>();
        //code ở đây
        //Bi này, ta hard-code, ta fake dữ liệu tại đây cho nhanh
        lstTam.add("Nguyễn Văn A");
        lstTam.add("Nguyễn Văn B");
        lstTam.add("Nguyễn Văn C");
        lstTam.add("Nguyễn Văn D");
        lstTam.add("Nguyễn Văn E");
        lstTam.add("Nguyễn Văn F");
        lstTam.add("Nguyễn Văn G");
        lstTam.add("Nguyễn Văn H");
        lstTam.add("Nguyễn Văn I");
        lstTam.add("Nguyễn Văn K");
        return lstTam;
    }
}