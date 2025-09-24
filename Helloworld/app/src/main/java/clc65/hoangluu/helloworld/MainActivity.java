package clc65.hoangluu.helloworld;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    EditText edtNumberA,edtNumberB;
    RadioButton rb_cong,rb_tru,rb_nhan,rb_chia;
    Button btn_tinh;
    TextView txtKetQua;
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
    edtNumberA = findViewById(R.id.edtNumberA);
    edtNumberB = findViewById(R.id.edtNumberB);
    btn_tinh = findViewById(R.id.btn_tinh);
    rb_cong = findViewById(R.id.rb_cong);
    rb_tru = findViewById(R.id.rb_tru);
    rb_nhan = findViewById(R.id.rb_nhan);
    rb_chia = findViewById(R.id.rb_chia);
    txtKetQua = findViewById(R.id.txtKetQua);
        btn_tinh.setOnClickListener(v -> {
            double a = Double.parseDouble(edtNumberA.getText().toString());
            double b = Double.parseDouble(edtNumberB.getText().toString());
            double ketqua = 0;
            if(rb_cong.isChecked()){
                ketqua = a + b;
            } else if (rb_tru.isChecked()) {
                ketqua = a-b;
            } else if (rb_nhan.isChecked()) {
                ketqua = a*b;
            } else if (rb_chia.isChecked()) {
                if(b==0){
                    Toast.makeText( MainActivity.this,"Không thể chia cho 0",Toast.LENGTH_SHORT).show();
                }
                else ketqua = a/b;

            }
            txtKetQua.setText(String.valueOf(ketqua));
        });

    }
}