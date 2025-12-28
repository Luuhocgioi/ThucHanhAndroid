package clc65.hoangluu.duan.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

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
        // Giữ nguyên logic kiểm tra quyền truy cập của bạn
        SharedPreferences sharedPref = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = sharedPref.getString("userRole", "Guest");
        boolean isAuthenticated = FirebaseAuth.getInstance().getCurrentUser() != null;
        boolean hasPermission = userRole.equalsIgnoreCase("Admin") || userRole.equalsIgnoreCase("Staff");

        if (!isAuthenticated || !hasPermission) {
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack();
            return;
        }

        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupBackButton(view);
        startListeningForTakeAwayOrders();
    }

    private void setupBackButton(View view) {
        binding.btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack();
        });
    }

    private void setupRecyclerView() {
        binding.rvTakeawayOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter(new ArrayList<>());
        orderAdapter.setOnOrderClickListener(this); // Lắng nghe sự kiện tại đây
        binding.rvTakeawayOrders.setAdapter(orderAdapter);
    }

    private void startListeningForTakeAwayOrders() {
        // Truy vấn lấy các đơn mang về đang chờ xử lý hoặc đang làm
        Query query = db.collection("orders")
                .whereEqualTo("type", "TakeAway")
                .whereIn("status", List.of("Pending", "Preparing", "Ready"))
                .orderBy("timestamp", Query.Direction.DESCENDING);

        ordersListener = query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Toast.makeText(getContext(), "Lỗi tải: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                orderAdapter.setOrderList(orders);
            }
        });
    }

    // --- TRIỂN KHAI CÁC PHƯƠNG THỨC INTERFACE ĐỂ HẾT LỖI ---

    @Override
    public void onOrderClick(Order order) {
        Toast.makeText(getContext(), "Chi tiết đơn #" + order.getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAcceptOrder(Order order) {
        // Khi bấm nút "Xác nhận" (Dấu tích xanh trên Card)
        String nextStatus = "Preparing";
        if (order.getStatus().equalsIgnoreCase("Preparing")) nextStatus = "Ready";
        else if (order.getStatus().equalsIgnoreCase("Ready")) nextStatus = "Completed";

        updateStatus(order, nextStatus);
    }

    @Override
    public void onRejectOrder(Order order) {
        // Khi bấm nút "Từ chối" (Dấu X đỏ trên Card)
        updateStatus(order, "Cancelled");
    }

    // Phương thức phụ trợ để cập nhật qua Repository
    private void updateStatus(Order order, String status) {
        orderRepository.updateOrderStatus(
                order.getId(),
                status,
                order.getType(),
                order.getTargetId()
        );
        Toast.makeText(getContext(), "Cập nhật đơn hàng thành: " + status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (ordersListener != null) ordersListener.remove();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}