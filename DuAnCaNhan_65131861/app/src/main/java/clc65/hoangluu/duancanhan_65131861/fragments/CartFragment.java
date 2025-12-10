package clc65.hoangluu.duancanhan_65131861.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import clc65.hoangluu.duancanhan_65131861.R;
import clc65.hoangluu.duancanhan_65131861.adapter.CartAdapter;
import clc65.hoangluu.duancanhan_65131861.database.SQLiteHelper;
import clc65.hoangluu.duancanhan_65131861.model.CartItem;
import clc65.hoangluu.duancanhan_65131861.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment implements CartAdapter.CartUpdateListener {

    private RecyclerView rvCartItems;
    private TextView tvTotalAmount;
    private Button btnCheckout;
    private EditText edtTableNumber;
    private SQLiteHelper dbHelper;
    private CartAdapter cartAdapter;
    private List<CartItem> cartList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private NumberFormat currencyFormat;

    public CartFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        rvCartItems = view.findViewById(R.id.rvCartItems);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        edtTableNumber = view.findViewById(R.id.edtTableNumber);

        dbHelper = new SQLiteHelper(getContext());
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("orders");
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        setupRecyclerView();
        loadCartData();

        btnCheckout.setOnClickListener(v -> attemptCheckout());

        return view;
    }

    private void setupRecyclerView() {
        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        cartList = new ArrayList<>();
        cartAdapter = new CartAdapter(getContext(), cartList, this);
        rvCartItems.setAdapter(cartAdapter);
    }

    private void loadCartData() {
        cartList = dbHelper.getAllCartItems();
        cartAdapter.updateList(cartList);
        updateCartTotal();
    }

    @Override
    public void onCartUpdated() {
        loadCartData();
    }

    private void updateCartTotal() {
        double total = dbHelper.getTotalAmount();
        tvTotalAmount.setText("Tổng tiền: " + currencyFormat.format(total));

        if (total > 0) {
            btnCheckout.setEnabled(true);
            btnCheckout.setText("Thanh toán & Đặt hàng");
        } else {
            btnCheckout.setEnabled(false);
            btnCheckout.setText("Giỏ hàng trống");
        }
    }

    private void attemptCheckout() {
        if (cartList.isEmpty()) {
            Toast.makeText(getContext(), "Không có món nào trong giỏ hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        String tableNumber = edtTableNumber.getText().toString().trim();
        String orderType = tableNumber.isEmpty() ? "Mang về" : "Tại bàn";

        String orderId = mDatabase.push().getKey();
        if (orderId == null) {
            Toast.makeText(getContext(), "Lỗi hệ thống: Không thể tạo Order ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        Order newOrder = new Order(
                orderId,
                mAuth.getCurrentUser().getUid(),
                System.currentTimeMillis(),
                dbHelper.getTotalAmount(),
                orderType,
                orderType.equals("Tại bàn") ? tableNumber : null,
                "Chờ",
                cartList
        );

        mDatabase.child(orderId).setValue(newOrder)
                .addOnSuccessListener(aVoid -> {
                    dbHelper.clearCart();
                    loadCartData();
                    Toast.makeText(getContext(), "Đặt hàng thành công! Loại: " + orderType, Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi Thanh toán: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}