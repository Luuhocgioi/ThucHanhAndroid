package clc65.hoangluu.bmi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    EditText edtCanNang, edtChieuCao;
    Button btnTinh;
    EditText edtKetQua;
    void init() {
        edtCanNang = findViewById(R.id.edtCanNang);
        edtChieuCao = findViewById(R.id.edtChieuCao);
        btnTinh = findViewById(R.id.btnTinh);
        edtKetQua = findViewById(R.id.edtKetQua);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        btnTinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String canNang = edtCanNang.getText().toString();
                String chieuCao = edtChieuCao.getText().toString();
                if (canNang.isEmpty() || chieuCao.isEmpty()) {
                    edtKetQua.setText("Vui lòng nhập đầy đủ thông tin");
                    return;
                }
                double Height = Double.parseDouble(chieuCao) / 100; // đổi cm sang m
                double Weight = Double.parseDouble(canNang);
                double BMI = Weight / (Height * Height);
                int BMIint = (int) BMI;

                String category = new String();
                if(BMI < 16)
                    category = "Gầy cấp độ 3 : Thiếu năng lượng trầm trọng";
                else if(BMI < 17 && BMI >= 16)
                    category = "Gầy cấp độ 2 : Thiếu năng lượng vừa";
                else if(BMI < 18.5 && BMI >= 17)
                    category = "Gầy cấp độ 1 : Thiếu năng lượng nhẹ";
                else if(BMI < 25 && BMI >= 18.5)
                    category = "Bình thường : Cân nặng lý tưởng";
                else if(BMI < 30 && BMI >= 25)
                    category = "Nguy cơ béo phì";
                else if(BMI < 35 && BMI >= 30)
                    category = "Béo phì cấp độ 1";
                else if(BMI < 40 && BMI >= 35)
                    category = "Béo phì cấp độ 2";
                else if(BMI >= 40)
                    category = "Béo phì nghiêm trọng";
                edtKetQua.setText("Chỉ số BMI của bạn là: " + BMIint + "\n" + category);



            };
        });
    }
}


