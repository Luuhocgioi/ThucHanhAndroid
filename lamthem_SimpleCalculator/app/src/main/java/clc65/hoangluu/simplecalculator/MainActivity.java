package clc65.hoangluu.simplecalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tvDisplay;
    double firstNum = 0;
    double secondNum = 0;
    String operator = "";
    boolean isNewOp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDisplay = findViewById(R.id.tvDisplay);

        Button btn0 = findViewById(R.id.btn0);
        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
        Button btn5 = findViewById(R.id.btn5);
        Button btn6 = findViewById(R.id.btn6);
        Button btn7 = findViewById(R.id.btn7);
        Button btn8 = findViewById(R.id.btn8);
        Button btn9 = findViewById(R.id.btn9);

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnSub = findViewById(R.id.btnSub);
        Button btnMul = findViewById(R.id.btnMul);
        Button btnDiv = findViewById(R.id.btnDiv);
        Button btnC = findViewById(R.id.btnC);
        Button btnEqual = findViewById(R.id.btnEqual);

        View.OnClickListener numberClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                String value = b.getText().toString();
                if (isNewOp) {
                    tvDisplay.setText(value);
                    isNewOp = false;
                } else {
                    tvDisplay.append(value);
                }
            }
        };

        btn0.setOnClickListener(numberClick);
        btn1.setOnClickListener(numberClick);
        btn2.setOnClickListener(numberClick);
        btn3.setOnClickListener(numberClick);
        btn4.setOnClickListener(numberClick);
        btn5.setOnClickListener(numberClick);
        btn6.setOnClickListener(numberClick);
        btn7.setOnClickListener(numberClick);
        btn8.setOnClickListener(numberClick);
        btn9.setOnClickListener(numberClick);

        btnAdd.setOnClickListener(v -> setOperator("+"));
        btnSub.setOnClickListener(v -> setOperator("-"));
        btnMul.setOnClickListener(v -> setOperator("*"));
        btnDiv.setOnClickListener(v -> setOperator("/"));

        btnC.setOnClickListener(v -> {
            tvDisplay.setText("0");
            firstNum = 0;
            secondNum = 0;
            operator = "";
            isNewOp = true;
        });

        btnEqual.setOnClickListener(v -> calculateResult());
    }

    private void setOperator(String op) {
        firstNum = Double.parseDouble(tvDisplay.getText().toString());
        operator = op;
        isNewOp = true;
    }

    private void calculateResult() {
        secondNum = Double.parseDouble(tvDisplay.getText().toString());
        double result = 0;
        switch (operator) {
            case "+": result = firstNum + secondNum; break;
            case "-": result = firstNum - secondNum; break;
            case "*": result = firstNum * secondNum; break;
            case "/":
                if (secondNum != 0) result = firstNum / secondNum;
                else {
                    tvDisplay.setText("Lỗi");
                    isNewOp = true;
                    return;
                }
                break;
        }
        tvDisplay.setText(removeTrailingZero(result));
        isNewOp = true;
    }

    private String removeTrailingZero(double number) {
        if (number == (long) number)
            return String.format("%d", (long) number);
        else
            return String.format("%s", number);
    }
}