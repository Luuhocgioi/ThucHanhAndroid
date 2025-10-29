package thigk1.nguyenhoangluu.thigk;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.NumberFormat;
import java.util.Locale;

public class DuToanChiPhi extends AppCompatActivity {

    private TextInputLayout tilArea, tilUnitPrice;
    private TextInputEditText edtArea, edtUnitPrice;
    private Spinner spinnerMaterials;
    private Button btnCalculateEstimate;
    private TextView tvEstimateResult;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_du_toan_chi_phi);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tilArea = findViewById(R.id.tilArea);
        edtArea = findViewById(R.id.edtArea);
        tilUnitPrice = findViewById(R.id.tilUnitPrice);
        edtUnitPrice = findViewById(R.id.edtUnitPrice);
        spinnerMaterials = findViewById(R.id.spinnerMaterials);
        btnCalculateEstimate = findViewById(R.id.btnCalculateEstimate);
        tvEstimateResult = findViewById(R.id.tvEstimateResult);
        btnBack = findViewById(R.id.btnBack);

        setupSpinner();

        btnBack.setOnClickListener(v -> onBackPressed());

        btnCalculateEstimate.setOnClickListener(v -> calculateEstimate());
    }

    private void setupSpinner() {
        String[] materials = new String[]{"Gạch men", "Đá hoa cương", "Sàn gỗ", "Thảm lót"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, materials);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerMaterials.setAdapter(adapter);
    }

    private void calculateEstimate() {
        String areaStr = edtArea.getText().toString();
        String unitPriceStr = edtUnitPrice.getText().toString();

        if (areaStr.isEmpty()) {
            tilArea.setError("Vui lòng nhập diện tích");
            return;
        } else {
            tilArea.setError(null);
        }
    }
}