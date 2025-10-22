package clc65.hoangluu.intent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button btnSend;
    EditText edtName;
    void Tim(){
        btnSend = findViewById(R.id.btnSend);
        edtName = findViewById(R.id.edtName);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Tim();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);

                String name = edtName.getText().toString();

                //Gói vào intent, dùng putExtra (key,value)
                intent.putExtra("name", name);
                intent.putExtra("age",20 );
                startActivity(intent);
            }
        });
        }
    ;}