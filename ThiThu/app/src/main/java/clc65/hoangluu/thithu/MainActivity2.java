package clc65.hoangluu.thithu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    EditText edtGK, edtCK, edtKQ;
    Button btnTB;
    void Tim(){
        edtGK = findViewById(R.id.edtGK);
        edtCK = findViewById(R.id.edtCK);
        edtKQ = findViewById(R.id.edtKQ);
        btnTB = findViewById(R.id.btnTB);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Tim();
        btnTB.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                TinhDiemTB();
            }
        });

    }
    public void TinhDiemTB()
    {
        String diemGK = edtGK.getText().toString();
        String diemCK = edtCK.getText().toString();
        if(diemGK.isEmpty()||diemCK.isEmpty())
        {
            edtKQ.setText("Vui lòng nhập đầy đủ điểm");
            return;
        }
        else {
            float diemGK1 = Float.parseFloat(diemGK);
            float diemCK1 = Float.parseFloat(diemCK);
            float diemTB = (diemGK1 + diemCK1) / 2;
            edtKQ.setText(String.valueOf(diemTB));

        }
    }
}