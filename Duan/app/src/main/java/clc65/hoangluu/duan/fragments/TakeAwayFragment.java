package clc65.hoangluu.duan.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import clc65.hoangluu.duan.R;
import clc65.hoangluu.duan.adapters.OrderAdapter;
import clc65.hoangluu.duan.data.OrderRepository;
import clc65.hoangluu.duan.databinding.FragmentTakeAwayBinding;
import clc65.hoangluu.duan.models.Order;

public class TakeAwayFragment extends Fragment implements OrderAdapter.OnOrderClickListener {

    private FragmentTakeAwayBinding binding;
    private OrderAdapter orderAdapter;
    private FirebaseFirestore db;
    private ListenerRegistration ordersListener;
    private OrderRepository orderRepository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTakeAwayBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        orderRepository = new OrderRepository(getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Kiểm tra quyền truy cập
        if (!checkUserPermission()) return;

        // 2. Cài đặt giao diện
        setupRecyclerView();
        setupBackButton();

        // 3. Bắt đầu lắng nghe dữ liệu Realtime sử dụng Index đã tạo
        startListeningForTakeAwayOrders();
    }

    private boolean checkUserPermission() {
        SharedPreferences sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = sharedPref.getString("userRole", "Guest");
        boolean isAuthenticated = FirebaseAuth.getInstance().getCurrentUser() != null;
        boolean hasPermission = "Admin".equalsIgnoreCase(userRole) || "Staff".equalsIgnoreCase(userRole);

        if (!isAuthenticated || !hasPermission) {
            Toast.makeText(getContext(), "Bạn không có quyền truy cập.", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).popBackStack();
            return false;
        }
        return true;
    }

    private void setupBackButton() {
        binding.btnBack.setOnClickListener(v ->
                Navigation.findNavController(v).popBackStack());
    }

    private void setupRecyclerView() {
        binding.rvTakeawayOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter(new ArrayList<>());
        orderAdapter.setOnOrderClickListener(this);
        binding.rvTakeawayOrders.setAdapter(orderAdapter);
    }

    private void startListeningForTakeAwayOrders() {
        // Truy vấn sử dụng các trường đã được đánh Index: status, type, timestamp
        Query query = db.collection("orders")
                .whereEqualTo("type", "TakeAway")
                .whereIn("status", Arrays.asList("Pending", "Preparing", "Ready"))
                .orderBy("timestamp", Query.Direction.DESCENDING);

        ordersListener = query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Toast.makeText(getContext(), "Lỗi tải đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            if (snapshots != null) {
                List<Order> orders = new ArrayList<>();
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    Order order = doc.toObject(Order.class);
                    if (order != null) {
                        order.setId(doc.getId());
                        orders.add(order);
                    }
                }

                // Xử lý hiển thị danh sách hoặc thông báo trống
                if (orders.isEmpty()) {
                    binding.rvTakeawayOrders.setVisibility(View.GONE);
                    binding.emptyStateLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.rvTakeawayOrders.setVisibility(View.VISIBLE);
                    binding.emptyStateLayout.setVisibility(View.GONE);
                    orderAdapter.setOrderList(orders);
                }
            }
        });
    }

    @Override
    public void onOrderClick(Order order) {
        // Chuyển sang màn hình chi tiết món ăn đồng bộ với Sơ đồ bàn
        Bundle bundle = new Bundle();
        bundle.putString("orderId", order.getId());
        bundle.putString("tableName", "Khách mang về: " + order.getTargetId());
        Navigation.findNavController(requireView()).navigate(R.id.orderDetailFragment, bundle);
    }

    @Override
    public void onAcceptOrder(Order order) {
        String currentStatus = order.getStatus();
        String nextStatus = "Preparing";

        if ("Preparing".equalsIgnoreCase(currentStatus)) nextStatus = "Ready";
        else if ("Ready".equalsIgnoreCase(currentStatus)) nextStatus = "Completed";

        updateStatus(order, nextStatus);
    }

    @Override
    public void onRejectOrder(Order order) {
        // Hủy đơn hàng mang về
        updateStatus(order, "Cancelled");
    }

    private void updateStatus(Order order, String newStatus) {
        // Cập nhật lên Firestore thông qua Repository
        orderRepository.updateOrderStatus(
                order.getId(),
                newStatus,
                order.getType(),
                order.getTargetId()
        );
        Toast.makeText(getContext(), "Đã chuyển sang: " + newStatus, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Gỡ bỏ listener để tránh rò rỉ bộ nhớ
        if (ordersListener != null) {
            ordersListener.remove();
        }
        binding = null;
    }
}