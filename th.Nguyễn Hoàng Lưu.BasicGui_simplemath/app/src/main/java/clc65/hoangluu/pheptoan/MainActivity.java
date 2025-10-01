package clc65.hoangluu.pheptoan;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    EditText edtSoA, edtSoB;
    Button btnCong, btnTru, btnNhan, btnChia;
    TextView txtKetqua;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtSoA = findViewById(R.id.edtSoA);
        edtSoB = findViewById(R.id.edtSoB);
        btnCong = findViewById(R.id.btnCong);
        btnTru = findViewById(R.id.btnTru);
        btnNhan = findViewById(R.id.btnNhan);
        btnChia = findViewById(R.id.btnChia);
        txtKetqua = findViewById(R.id.edtKetqua);
        btnCong.setOnClickListener(v -> {
            int soA = Integer.parseInt(edtSoA.getText().toString());
            int soB = Integer.parseInt(edtSoB.getText().toString());
            int ketqua = soA + soB;
            txtKetqua.setText(String.valueOf(ketqua));
        });
        btnTru.setOnClickListener(v -> {
            int soA = Integer.parseInt(edtSoA.getText().toString());
            int soB = Integer.parseInt(edtSoB.getText().toString());
            int ketqua = soA - soB;
            txtKetqua.setText(String.valueOf(ketqua));
        });
        btnNhan.setOnClickListener(v -> {
            int soA = Integer.parseInt(edtSoA.getText().toString());
            int soB = Integer.parseInt(edtSoB.getText().toString());
            int ketqua = soA * soB;
            txtKetqua.setText(String.valueOf(ketqua));
        });
        btnChia.setOnClickListener(v -> {
            int soA = Integer.parseInt(edtSoA.getText().toString());
            int soB = Integer.parseInt(edtSoB.getText().toString());
            int ketqua = soA / soB;
            txtKetqua.setText(String.valueOf(ketqua));
        });

    }
}