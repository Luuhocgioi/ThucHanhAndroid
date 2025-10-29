package thigk1.nguyenhoangluu.thigk;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class DienTichSan extends AppCompatActivity {

    TextInputLayout tilWidth, tilHeight;
    TextInputEditText edtWidth, edtHeight;
    Button btnCalculate;
    TextView tvResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        setContentView(R.layout.activity_dien_tich_san);

        tilWidth = findViewById(R.id.tilWidth);
        tilHeight = findViewById(R.id.tilHeight);
        edtWidth = findViewById(R.id.edtWidth);
        edtHeight = findViewById(R.id.edtHeight);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvResult = findViewById(R.id.tvResult);


        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateArea();
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void calculateArea() {
        tilWidth.setError(null);
        tilHeight.setError(null);

        String widthStr = edtWidth.getText().toString();
        String heightStr = edtHeight.getText().toString();

        if (widthStr.isEmpty()) {
            tilWidth.setError("Vui lòng nhập chiều rộng");
            return;
        }
        if (heightStr.isEmpty()) {
            tilHeight.setError("Vui lòng nhập chiều cao");
            return;
        }


        try {
            double width = Double.parseDouble(widthStr);
            double height = Double.parseDouble(heightStr);

            double area = width * height;
            String resultText = String.format(Locale.getDefault(), "Kết quả: %.2f m²", area);
            tvResult.setText(resultText);

        } catch (NumberFormatException e) {

            tvResult.setText("Lỗi: Dữ liệu nhập không hợp lệ");
            e.printStackTrace();
        }
    }
}
