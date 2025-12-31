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
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import clc65.hoangluu.duan.R;
import clc65.hoangluu.duan.adapters.TableAdapter;
import clc65.hoangluu.duan.databinding.FragmentTableStatusBinding;
import clc65.hoangluu.duan.models.Table;

public class TableStatusFragment extends Fragment implements TableAdapter.OnTableClickListener {

    private FragmentTableStatusBinding binding;
    private TableAdapter tableAdapter;
    private FirebaseFirestore db;
    private ListenerRegistration tableListener; // Dùng để đóng lắng nghe khi thoát

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTableStatusBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Kiểm tra quyền truy cập (Admin/Staff)
        if (!checkPermission()) return;

        // 2. Thiết lập giao diện
        setupRecyclerView();
        setupBackButton();

        // 3. Lắng nghe dữ liệu Realtime từ Firestore
        startListeningTableStatus();
    }

    private boolean checkPermission() {
        SharedPreferences sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = sharedPref.getString("userRole", "Guest");

        boolean isAuthenticated = FirebaseAuth.getInstance().getCurrentUser() != null;
        boolean hasPermission = "Admin".equalsIgnoreCase(userRole) || "Staff".equalsIgnoreCase(userRole);

        if (!isAuthenticated || !hasPermission) {
            Toast.makeText(getContext(), "Bạn không có quyền truy cập sơ đồ bàn.", Toast.LENGTH_SHORT).show();
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
        // Sử dụng 2 cột để hiển thị thông tin bàn rõ ràng hơn (như đã thảo luận ở XML)
        binding.rvTableStatus.setLayoutManager(new GridLayoutManager(getContext(), 2));

        tableAdapter = new TableAdapter(getContext(), new ArrayList<>());
        tableAdapter.setOnTableClickListener(this);
        binding.rvTableStatus.setAdapter(tableAdapter);
    }

    /**
     * LẮNG NGHE SỰ THAY ĐỔI TRẠNG THÁI BÀN TỪ FIRESTORE
     */
    private void startListeningTableStatus() {
        // Truy vấn collection "tables", sắp xếp theo tên bàn
        Query query = db.collection("tables").orderBy("name", Query.Direction.ASCENDING);

        tableListener = query.addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu bàn", Toast.LENGTH_SHORT).show();
                return;
            }

            if (value != null) {
                List<Table> tables = new ArrayList<>();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Table table = doc.toObject(Table.class);
                    if (table != null) {
                        // Gán ID từ Firestore vào Model để quản lý
                        table.setId(doc.getId());
                        tables.add(table);
                    }
                }
                // Cập nhật vào Adapter để đổi màu Trắng/Đỏ
                tableAdapter.setTableList(tables);
            }
        });
    }

    @Override
    public void onTableClick(Table table) {
        NavController navController = Navigation.findNavController(requireView());

        // Kiểm tra trạng thái để điều hướng
        if ("Available".equalsIgnoreCase(table.getStatus())) {
            // Bàn TRỐNG: Sang màn hình Giỏ hàng để đặt món
            Bundle bundle = new Bundle();
            bundle.putString("targetId", table.getId());
            bundle.putString("orderType", "Table");

            navController.navigate(R.id.cartFragment, bundle);
        } else {
            // Bàn CÓ KHÁCH: Sang màn hình chi tiết để xem các món đang order
            if (table.getCurrentOrderId() != null && !table.getCurrentOrderId().isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putString("orderId", table.getCurrentOrderId());
                bundle.putString("tableName", table.getName());
                navController.navigate(R.id.orderDetailFragment, bundle); // Đảm bảo đã khai báo ID này trong nav_graph
            } else {
                Toast.makeText(getContext(), "Không tìm thấy dữ liệu đơn hàng!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Hủy lắng nghe để tránh lãng phí tài nguyên và lỗi memory leak
        if (tableListener != null) {
            tableListener.remove();
        }
        binding = null;
    }
}