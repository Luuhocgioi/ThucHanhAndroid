package clc65.hoangluu.duan.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
        super.onViewCreated(view, savedInstanceState);

        // TODO: Thiết lập RecyclerView cho báo cáo chi tiết

        loadTotalRevenue();
        loadDailyRevenueReport();
    }

    private void loadTotalRevenue() {
        AtomicReference<Double> totalRevenue = new AtomicReference<>(0.0);

        // Truy vấn tất cả các đơn hàng đã hoàn thành ("Completed")
        db.collection("orders")
                .whereEqualTo("status", "Completed")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (com.google.firebase.firestore.DocumentSnapshot doc : task.getResult().getDocuments()) {
                            Double amount = doc.getDouble("totalAmount");
                            if (amount != null) {
                                totalRevenue.updateAndGet(v -> v + amount);
                            }
                        }
                        // Cập nhật giao diện
                        binding.tvTotalRevenueAmount.setText(priceFormat.format(totalRevenue.get()));
                    } else {
                        Toast.makeText(getContext(), "Lỗi tải tổng doanh thu.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadDailyRevenueReport() {
        // TODO: Logic phức tạp hơn để tính tổng theo ngày, sau đó cập nhật RecyclerView
        // Query cần sắp xếp và group by ngày (thường được thực hiện ở Backend hoặc bằng truy vấn phức tạp)
        // Hiện tại, chúng ta chỉ hiển thị một thông báo
        Toast.makeText(getContext(), "Đang tải báo cáo chi tiết...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}