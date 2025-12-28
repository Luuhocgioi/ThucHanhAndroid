package clc65.hoangluu.duan.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;
import clc65.hoangluu.duan.adapters.OrderAdapter;
import clc65.hoangluu.duan.data.OrderRepository;
import clc65.hoangluu.duan.databinding.FragmentOrderListBinding;
import clc65.hoangluu.duan.models.Order;

public class OrderListFragment extends Fragment implements OrderAdapter.OnOrderClickListener {

    private FragmentOrderListBinding binding;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private FirebaseFirestore db;

    private static final String ARG_STATUS = "status_filter";
    private String statusFilter;

    // Sử dụng để khởi tạo Fragment từ PagerAdapter với tham số lọc trạng thái
    public static OrderListFragment newInstance(String status) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            statusFilter = getArguments().getString(ARG_STATUS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        loadOrdersFromFirestore();
    }

    private void setupRecyclerView() {
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList);
        orderAdapter.setOnOrderClickListener(this);
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvOrders.setAdapter(orderAdapter);
    }

    private void loadOrdersFromFirestore() {
        // Sắp xếp đơn hàng mới nhất lên đầu
        Query query = db.collection("orders").orderBy("timestamp", Query.Direction.DESCENDING);

        // Lọc dữ liệu dựa trên tab hiện tại (Khớp với tab "All" trong OrdersFragment)
        if (statusFilter != null && !statusFilter.equalsIgnoreCase("All")) {
            query = query.whereEqualTo("status", statusFilter);
        }

        query.addSnapshotListener((value, error) -> {
            if (error != null || value == null) return;

            for (DocumentChange dc : value.getDocumentChanges()) {
                Order order = dc.getDocument().toObject(Order.class);
                order.setId(dc.getDocument().getId());

                switch (dc.getType()) {
                    case ADDED:
                        orderList.add(order);
                        break;
                    case MODIFIED:
                        updateOrderInList(order);
                        break;
                    case REMOVED:
                        removeOrderFromList(order);
                        break;
                }
            }
            orderAdapter.notifyDataSetChanged();

            // Cập nhật trạng thái hiển thị nếu danh sách trống
            if (binding != null) {
                binding.emptyStateLayout.setVisibility(orderList.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void updateOrderInList(Order order) {
        for (int i = 0; i < orderList.size(); i++) {
            if (orderList.get(i).getId().equals(order.getId())) {
                orderList.set(i, order);
                break;
            }
        }
    }

    private void removeOrderFromList(Order order) {
        orderList.removeIf(item -> item.getId().equals(order.getId()));
    }

    @Override
    public void onOrderClick(Order order) {
        // Logic xem chi tiết đơn hàng (OrderDetail) có thể triển khai tại đây
        Toast.makeText(getContext(), "Đơn hàng: " + order.getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAcceptOrder(Order order) {
        // Logic chuyển trạng thái theo quy trình: Pending -> Preparing -> Ready -> Completed
        String nextStatus = "Preparing";
        if ("Preparing".equalsIgnoreCase(order.getStatus())) {
            nextStatus = "Ready";
        } else if ("Ready".equalsIgnoreCase(order.getStatus())) {
            nextStatus = "Completed";
        }

        updateStatus(order.getId(), nextStatus);
    }

    @Override
    public void onRejectOrder(Order order) {
        // Chuyển trạng thái thành Đã hủy
        updateStatus(order.getId(), "Cancelled");
    }

    private void updateStatus(String id, String status) {
        Order currentOrder = null;
        for (Order o : orderList) {
            if (o.getId().equals(id)) {
                currentOrder = o;
                break;
            }
        }

        if (currentOrder != null) {
            // Sử dụng OrderRepository để cập nhật đồng bộ cả đơn hàng và trạng thái bàn
            OrderRepository repository = new OrderRepository(getContext());
            repository.updateOrderStatus(
                    currentOrder.getId(),
                    status,
                    currentOrder.getType(),
                    currentOrder.getTargetId()
            );
            Toast.makeText(getContext(), "Đang cập nhật: " + status, Toast.LENGTH_SHORT).show();
        } else {
            // Fallback: Cập nhật trực tiếp lên Firestore nếu không tìm thấy trong list tạm
            db.collection("orders").document(id).update("status", status);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}