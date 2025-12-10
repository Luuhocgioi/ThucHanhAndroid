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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import clc65.hoangluu.duan.adapters.OrderAdapter;
import clc65.hoangluu.duan.databinding.FragmentTakeAwayBinding;
import clc65.hoangluu.duan.models.Order;

public class TakeAwayFragment extends Fragment implements OrderAdapter.OnOrderClickListener {

    private FragmentTakeAwayBinding binding;
    private OrderAdapter orderAdapter;
    private FirebaseFirestore db;
    private ListenerRegistration ordersListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTakeAwayBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
        orderAdapter.setOnOrderClickListener(this);
        binding.rvTakeawayOrders.setAdapter(orderAdapter);
    }

    private void startListeningForTakeAwayOrders() {
        Query query = db.collection("orders")
                .whereEqualTo("type", "TakeAway")
                .whereIn("status", List.of("Pending", "Preparing", "Ready"))
                .orderBy("timestamp", Query.Direction.DESCENDING);

        ordersListener = query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Toast.makeText(getContext(), "Lỗi tải đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshots != null) {
                List<Order> orders = new ArrayList<>();
                for (com.google.firebase.firestore.DocumentSnapshot doc : snapshots.getDocuments()) {
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

    // Xử lý khi nhấn vào đơn hàng (chuyển sang màn hình chi tiết)
    @Override
    public void onOrderClick(Order order) {
        Toast.makeText(getContext(), "Xem chi tiết đơn hàng mang về #" + order.getId(), Toast.LENGTH_SHORT).show();
        // TODO: Chuyển sang màn hình chi tiết đơn hàng (OrderDetailFragment)
    }

    // TRIỂN KHAI PHƯƠNG THỨC BỊ THIẾU (Khắc phục lỗi "is not abstract")
    @Override
    public void onStatusActionClick(Order order) {
        Toast.makeText(getContext(), "Cập nhật trạng thái cho đơn hàng mang về #" + order.getId(), Toast.LENGTH_SHORT).show();
        // TODO: Logic chuyển trạng thái (Ví dụ: Pending -> Preparing -> Ready)
    }

    @Override
    public void onStop() {
        super.onStop();
        if (ordersListener != null) {
            ordersListener.remove();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}