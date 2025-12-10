package clc65.hoangluu.duan.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import clc65.hoangluu.duan.databinding.FragmentOrderListBinding;
import clc65.hoangluu.duan.models.Order;

public class OrderListFragment extends Fragment implements OrderAdapter.OnOrderClickListener {

    private FragmentOrderListBinding binding;
    private OrderAdapter orderAdapter;
    private FirebaseFirestore db;
    private ListenerRegistration ordersListener;
    private String statusFilter;
    private static final String ARG_STATUS_FILTER = "status_filter";

    public static OrderListFragment newInstance(String statusFilter) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS_FILTER, statusFilter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            statusFilter = getArguments().getString(ARG_STATUS_FILTER);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrderListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        startListeningForOrders();
    }

    private void setupRecyclerView() {
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter(new ArrayList<>());
        orderAdapter.setOnOrderClickListener(this);
        binding.rvOrders.setAdapter(orderAdapter);
    }

    private void startListeningForOrders() {
        Query query = db.collection("orders");

        if (statusFilter != null && !statusFilter.equals("All")) {
            query = query.whereEqualTo("status", statusFilter);
        }

        query = query.orderBy("timestamp", Query.Direction.DESCENDING);

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

                if (orders.isEmpty()) {
                    binding.tvEmptyState.setVisibility(View.VISIBLE);
                } else {
                    binding.tvEmptyState.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onOrderClick(Order order) {
        Toast.makeText(getContext(), "Xem chi tiết đơn hàng #" + order.getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusActionClick(Order order) {
        Toast.makeText(getContext(), "Chuyển trạng thái cho đơn hàng #" + order.getId(), Toast.LENGTH_SHORT).show();
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