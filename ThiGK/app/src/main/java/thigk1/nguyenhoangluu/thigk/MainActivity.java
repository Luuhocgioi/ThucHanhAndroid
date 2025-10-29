package thigk1.nguyenhoangluu.thigk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button btnCN2, btnCN3, btnCN4, btnAbout,btnCostEstimate;
    ImageView imageView;
    androidx.constraintlayout.widget.ConstraintLayout constraintLayout;
    void Tim(){
        btnCN2 = findViewById(R.id.btnCN2);
        btnCN3 = findViewById(R.id.btnCN3);
        btnCN4 = findViewById(R.id.btnCN4);
        btnAbout = findViewById(R.id.btnAbout);
        btnCostEstimate = findViewById(R.id.btnCostEstimate);
        constraintLayout = findViewById(R.id.main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Tim();
        btnCN2.setOnClickListener(view -> {
            Intent intent2 = new Intent(MainActivity.this, DienTichSan.class);
            startActivity(intent2);
        });
        btnCN3.setOnClickListener(view -> {
            Intent intent3 = new Intent(MainActivity.this, DsVatLieu.class);
            startActivity(intent3);
        });
        btnCN4.setOnClickListener(view -> {
            Intent intent4 = new Intent(MainActivity.this, CongTrinh.class);
            startActivity(intent4);
        });
        btnAbout.setOnClickListener(view -> {
            Intent intent5 = new Intent(MainActivity.this, AboutMe.class);
            startActivity(intent5);
        });
        btnCostEstimate.setOnClickListener(view -> {
            Intent intent5 = new Intent(MainActivity.this, DuToanChiPhi.class);
            startActivity(intent5);
        });

    }
}
