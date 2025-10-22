package clc65.hoangluu.intent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {
    Button btnBack;
    TextView tvName;
    void Tim() {
        btnBack = findViewById(R.id.btnBack);
        tvName = findViewById(R.id.tvName);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Tim();
        //Nhận về Intent
        Intent intentback = getIntent();
        //Bóc ra
        String nameNhan = intentback.getStringExtra("name");
        //Xử lý
        tvName.setText(nameNhan);

        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
            startActivity(intent);
        });
    }


    }
