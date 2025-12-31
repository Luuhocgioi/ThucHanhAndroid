package clc65.hoangluu.duan.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;

import clc65.hoangluu.duan.adapters.CartItemAdapter;
import clc65.hoangluu.duan.databinding.FragmentOrderDetailBinding;
import clc65.hoangluu.duan.models.Order;

public class OrderDetailFragment extends Fragment {
    private FragmentOrderDetailBinding binding;
    private FirebaseFirestore db;
    private CartItemAdapter adapter;
    private final DecimalFormat priceFormat = new DecimalFormat("#,###đ");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String orderId = getArguments() != null ? getArguments().getString("orderId") : null;
        String tableName = getArguments() != null ? getArguments().getString("tableName") : "Thông tin đơn hàng";

        // Gán dữ liệu lên View (Binding tự chuyển snake_case sang camelCase)
        binding.tvTableNameDetail.setText(tableName);

        setupRecyclerView();

        if (orderId != null) {
            loadOrderItems(orderId);
            setupDeleteButton(orderId);
        }

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(view).popBackStack());
    }

    private void setupRecyclerView() {
        binding.rvOrderItemsDetail.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartItemAdapter(new ArrayList<>());
        adapter.setReadOnly(true);
        binding.rvOrderItemsDetail.setAdapter(adapter);
    }

    private void loadOrderItems(String orderId) {
        db.collection("orders").document(orderId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Order order = documentSnapshot.toObject(Order.class);
                        if (order != null) {
                            String displayId = documentSnapshot.getId().length() > 8 ?
                                    documentSnapshot.getId().substring(0, 8).toUpperCase() :
                                    documentSnapshot.getId().toUpperCase();

                            binding.tvOrderId.setText("Mã đơn: #" + displayId);
                            binding.tvTotalPriceDetail.setText(priceFormat.format(order.getTotalAmount()));
                            adapter.setCartItemList(order.getItems());
                        }
                    }
                });
    }

    private void setupDeleteButton(String orderId) {
        binding.btnDeleteOrder.setOnClickListener(v -> {
            EditText etReason = new EditText(requireContext());
            etReason.setHint("Lý do hủy (VD: Nhập sai món...)");

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Xác nhận hủy đơn")
                    .setView(etReason)
                    .setPositiveButton("Hủy đơn", (dialog, which) -> {
                        String reason = etReason.getText().toString().trim();
                        if (!reason.isEmpty()) {
                            deleteOrder(orderId, reason);
                        } else {
                            Toast.makeText(getContext(), "Vui lòng nhập lý do", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Quay lại", null)
                    .show();
        });
    }

    private void deleteOrder(String orderId, String reason) {
        db.collection("orders").document(orderId)
                .update("status", "Cancelled", "cancelReason", reason)
                .addOnSuccessListener(aVoid -> {
                    // Trả trạng thái bàn về Available (màu trắng)
                    db.collection("tables").whereEqualTo("currentOrderId", orderId).get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                    doc.getReference().update("status", "Available", "currentOrderId", "");
                                }
                                Toast.makeText(getContext(), "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(requireView()).popBackStack();
                            });
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}