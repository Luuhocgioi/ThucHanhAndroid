package clc65.hoangluu.unitconverter;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    EditText edtVND, edtCYN;
    MaterialButton btnConvert;

    final double VND_TO_CYN = 1.0 / 3500.0;

    void init() {
        edtVND = findViewById(R.id.edtVND);
        edtCYN = findViewById(R.id.edtCYN);
        btnConvert = findViewById(R.id.btnConvert);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        edtCYN.setEnabled(false);
        edtVND.setText("0");

        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vndText = edtVND.getText().toString().trim();

                if (vndText.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập số tiền VND", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double vnd = Double.parseDouble(vndText);
                    double cny = vnd * VND_TO_CYN;

                    edtCYN.setText(String.format("%.2f", cny));
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Giá trị không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
