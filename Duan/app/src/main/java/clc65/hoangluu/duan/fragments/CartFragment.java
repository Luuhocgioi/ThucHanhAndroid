package clc65.hoangluu.duan.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
    private static final DecimalFormat priceFormat = new DecimalFormat("#,###đ");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        cartRepository = new LocalCartRepository(getContext());
        orderRepository = new OrderRepository(getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupListeners(view);
        handleArguments();
        setupOrderTypeLogic(); // Xử lý ẩn/hiện logic tại bàn và mang về
    }

    private void handleArguments() {
        if (getArguments() != null) {
            String targetId = getArguments().getString("targetId");
            String orderType = getArguments().getString("orderType");

            if ("Table".equals(orderType) && targetId != null) {
                binding.etTableNumber.setText(targetId);
                binding.etTableNumber.setEnabled(false);
                binding.radioTableOrder.setChecked(true);
            }
        }
    }

    private void setupOrderTypeLogic() {
        // Lắng nghe thay đổi loại đơn hàng để đổi Hint cho người dùng
        binding.radioGroupOrderType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_table_order) {
                binding.inputTargetId.setHint("Số bàn (VD: 05)");
                binding.etTableNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else {
                binding.inputTargetId.setHint("Tên khách hàng / SĐT");
                binding.etTableNumber.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });
    }

    private void setupRecyclerView() {
        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartItemAdapter(new ArrayList<>());
        cartAdapter.setOnCartActionListener(this);
        binding.rvCartItems.setAdapter(cartAdapter);
    }

    private void setupListeners(View view) {
        // Nút xác nhận đơn hàng
        binding.btnConfirmOrder.setOnClickListener(v -> {
            String targetId = binding.etTableNumber.getText().toString().trim();
            if (targetId.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập số bàn hoặc thông tin khách.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cartRepository.getCartItems().isEmpty()) {
                Toast.makeText(getContext(), "Giỏ hàng đang trống.", Toast.LENGTH_SHORT).show();
                return;
            }
            confirmOrder(targetId, view);
        });

        // Nút quay lại Menu
        binding.btnBackToMenu.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        // Nút xóa sạch giỏ hàng (nếu bạn có ID btn_clear_cart trong XML)
        if (binding.btnClearCart != null) {
            binding.btnClearCart.setOnClickListener(v -> {
                cartRepository.clearCart();
                loadCartItems();
                Toast.makeText(getContext(), "Đã làm trống giỏ hàng", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartItems();
    }

    private void loadCartItems() {
        List<OrderItem> items = cartRepository.getCartItems();
        cartAdapter.setCartItemList(items);

        double total = cartRepository.getTotalPrice();
        binding.tvTotalPrice.setText(priceFormat.format(total));

        // XỬ LÝ EMPTY STATE ĐỒNG BỘ VỚI XML
        if (items.isEmpty()) {
            // Nếu giỏ trống: Hiện logo, ẩn danh sách, ẩn phần nhập liệu cho đỡ rối
            binding.emptyStateLayout.setVisibility(View.VISIBLE);
            binding.rvCartItems.setVisibility(View.GONE);
            binding.checkoutContainer.setVisibility(View.GONE); // Thêm dòng này nếu muốn ẩn cả phần dưới khi trống
        } else {
            // Nếu có món: ẨN LOGO, hiện danh sách, hiện phần nhập liệu
            binding.emptyStateLayout.setVisibility(View.GONE);
            binding.rvCartItems.setVisibility(View.VISIBLE);
            binding.checkoutContainer.setVisibility(View.VISIBLE);
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
        newOrder.setStatus("Pending"); // Mặc định là chờ xử lý

        orderRepository.createNewOrder(newOrder, cartRepository, new OrderRepository.OnOrderCreationListener() {
            @Override
            public void onSuccess(String orderId) {
                cartRepository.clearCart(); // Xóa giỏ hàng SQLite sau khi tạo đơn thành công
                Toast.makeText(getContext(), "Đặt đơn thành công!", Toast.LENGTH_LONG).show();
                // Quay về màn hình Home và xóa stack
                Navigation.findNavController(view).popBackStack(R.id.homeFragment, false);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onQuantityChange(OrderItem item, int newQuantity) {
        cartRepository.updateQuantity(item.getSQLiteId(), newQuantity);
        loadCartItems();
    }

    @Override
    public void onDeleteItem(OrderItem item) {
        cartRepository.deleteItem(item.getSQLiteId());
        loadCartItems();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}