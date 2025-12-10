package clc65.hoangluu.duan.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.navigation.Navigation; // Import Navigation
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;
import java.util.ArrayList; // Import ArrayList
import java.util.List;

import clc65.hoangluu.duan.R;
import clc65.hoangluu.duan.adapters.CartItemAdapter;
import clc65.hoangluu.duan.data.LocalCartRepository;
import clc65.hoangluu.duan.data.OrderRepository;
import clc65.hoangluu.duan.databinding.FragmentCartBinding;
import clc65.hoangluu.duan.models.Order;
import clc65.hoangluu.duan.models.OrderItem;

public class CartFragment extends Fragment implements CartItemAdapter.OnCartActionListener {

    private FragmentCartBinding binding;
    private LocalCartRepository cartRepository;
    private OrderRepository orderRepository;
    private CartItemAdapter cartAdapter;
    private FirebaseAuth mAuth;
    private static final DecimalFormat priceFormat = new DecimalFormat("#,### VNĐ");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        cartRepository = new LocalCartRepository(getContext());
        orderRepository = new OrderRepository(getContext());
        mAuth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupListeners(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartItems();
    }

    private void setupRecyclerView() {
        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartItemAdapter(new ArrayList<>());
        cartAdapter.setOnCartActionListener(this);
        binding.rvCartItems.setAdapter(cartAdapter);
    }

    private void setupListeners(View view) {
        binding.btnConfirmOrder.setOnClickListener(v -> {
            // Lấy target ID (ví dụ: số bàn)
            String targetId = binding.etTableNumber.getText().toString().trim(); // SỬ DỤNG ID ĐÃ SỬA
            if (targetId.isEmpty() || cartRepository.getCartItems().isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn bàn/nhập số bàn và thêm sản phẩm.", Toast.LENGTH_SHORT).show();
                return;
            }
            confirmOrder(targetId, view);
        });

        // SỬA LỖI: btnBackToMenu
        binding.btnBackToMenu.setOnClickListener(v -> {
            // Quay về HomeFragment
            Navigation.findNavController(v).popBackStack();
        });
    }

    // PHƯƠNG THỨC BỊ THIẾU ĐÃ ĐƯỢC BỔ SUNG (Fix lỗi "Cannot resolve method 'loadCartItems()'")
    private void loadCartItems() {
        List<OrderItem> items = cartRepository.getCartItems();
        cartAdapter.setCartItemList(items);
        double total = cartRepository.getTotalPrice();

        // SỬ DỤNG ID ĐÃ SỬA: tvTotalPrice
        binding.tvTotalPrice.setText(priceFormat.format(total));

        // SỬ DỤNG ID ĐÃ SỬA: tvEmptyCart
        if (items.isEmpty()) {
            binding.tvEmptyCart.setVisibility(View.VISIBLE);
            binding.rvCartItems.setVisibility(View.GONE);
            binding.btnConfirmOrder.setEnabled(false);
        } else {
            binding.tvEmptyCart.setVisibility(View.GONE);
            binding.rvCartItems.setVisibility(View.VISIBLE);
            binding.btnConfirmOrder.setEnabled(true);
        }
    }

    private void confirmOrder(String targetId, View view) {
        String orderType = binding.radioTableOrder.isChecked() ? "Table" : "TakeAway";
        List<OrderItem> items = cartRepository.getCartItems();
        double totalAmount = cartRepository.getTotalPrice();

        Order newOrder = new Order();
        newOrder.setType(orderType);
        newOrder.setTargetId(targetId);
        newOrder.setItems(items);
        newOrder.setTotalAmount(totalAmount);

        orderRepository.createNewOrder(newOrder, cartRepository, new OrderRepository.OnOrderCreationListener() {
            @Override
            public void onSuccess(String orderId) {
                loadCartItems();
                Toast.makeText(getContext(), "Đã tạo Order thành công! ID: " + orderId, Toast.LENGTH_LONG).show();
                // Quay về HomeFragment, xóa stack trước đó
                Navigation.findNavController(view).popBackStack(R.id.homeFragment, false);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Lỗi khi tạo đơn hàng: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // TRIỂN KHAI INTERFACE (Fix lỗi "Cannot resolve method 'updateQuantity'/'deleteItem'")
    @Override
    public void onQuantityChange(OrderItem item, int newQuantity) {
        cartRepository.updateQuantity(item.getSQLiteId(), newQuantity);
        loadCartItems();
    }

    @Override
    public void onDeleteItem(OrderItem item) {
        cartRepository.deleteItem(item.getSQLiteId());
        Toast.makeText(getContext(), "Đã xóa: " + item.getName(), Toast.LENGTH_SHORT).show();
        loadCartItems();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}