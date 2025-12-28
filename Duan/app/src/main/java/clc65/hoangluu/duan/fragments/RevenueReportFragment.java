package clc65.hoangluu.duan.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicReference;

import clc65.hoangluu.duan.databinding.FragmentRevenueReportBinding;

public class RevenueReportFragment extends Fragment {

    private FragmentRevenueReportBinding binding;
    private FirebaseFirestore db;
    private static final DecimalFormat priceFormat = new DecimalFormat("#,### VNĐ");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRevenueReportBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState); // [SỬA] Nên gọi super trước để đảm bảo vòng đời Fragment ổn định

        // **********************************************************
        // LOGIC KIỂM TRA QUYỀN TRUY CẬP (CHỈ ADMIN)
        // **********************************************************
        SharedPreferences sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE); // [SỬA] Dùng requireContext() an toàn hơn
        String userRole = sharedPref.getString("userRole", "Guest");

        boolean isAuthenticated = FirebaseAuth.getInstance().getCurrentUser() != null;

        // [SỬA] Đảo ngược chuỗi để tránh NullPointerException nếu userRole bị null
        boolean hasPermission = "Admin".equalsIgnoreCase(userRole);

        if (!isAuthenticated || !hasPermission) {
            Toast.makeText(getContext(), "Chức năng này chỉ dành cho Admin.", Toast.LENGTH_LONG).show();

            // [SỬA] Kiểm tra an toàn trước khi popBackStack
            try {
                NavController navController = Navigation.findNavController(view);
                navController.popBackStack();
            } catch (Exception e) {
                // Xử lý trường hợp không tìm thấy NavController (hiếm gặp nhưng an toàn)
                e.printStackTrace();
            }
            return; // Dừng code tại đây
        }
        // **********************************************************
        // KẾT THÚC KIỂM TRA
        // **********************************************************

        setupBackButton(view);
        loadTotalRevenue();
        loadDailyRevenueReport();
    }

    private void setupBackButton(View view) {
        // [SỬA] Kiểm tra null để tránh crash nếu file XML chưa có ID btnBack
        if (binding.btnBack != null) {
            binding.btnBack.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(view);
                navController.popBackStack();
            });
        }
    }

    private void loadTotalRevenue() {
        try {
            AtomicReference<Double> totalRevenue = new AtomicReference<>(0.0);

            db.collection("orders")
                    .whereEqualTo("status", "Completed")
                    .get()
                    .addOnCompleteListener(task -> {
                        // [SỬA QUAN TRỌNG] Kiểm tra binding != null trước khi cập nhật giao diện
                        // Vì Firebase chạy bất đồng bộ, có thể View đã bị hủy khi dữ liệu tải xong.
                        if (binding == null) return;

                        if (task.isSuccessful() && task.getResult() != null) {
                            for (com.google.firebase.firestore.DocumentSnapshot doc : task.getResult().getDocuments()) {
                                Double amount = doc.getDouble("totalAmount");
                                if (amount != null) {
                                    totalRevenue.updateAndGet(v -> v + amount);
                                }
                            }
                            // Cập nhật giao diện
                            // [SỬA] Đảm bảo tvTotalRevenueAmount tồn tại trong XML
                            if (binding.tvTotalRevenueAmount != null) {
                                binding.tvTotalRevenueAmount.setText(priceFormat.format(totalRevenue.get()));
                            }
                        } else {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Lỗi tải doanh thu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception ex) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "Lỗi query: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadDailyRevenueReport() {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Đang tải báo cáo chi tiết...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Đây là lý do cần kiểm tra binding != null trong các hàm callback của Firebase
    }
}