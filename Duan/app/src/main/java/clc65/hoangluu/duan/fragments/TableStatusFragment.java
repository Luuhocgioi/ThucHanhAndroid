package clc65.hoangluu.duan.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import clc65.hoangluu.duan.R;
import clc65.hoangluu.duan.adapters.TableAdapter;
import clc65.hoangluu.duan.databinding.FragmentTableStatusBinding;
import clc65.hoangluu.duan.models.Table;

public class TableStatusFragment extends Fragment implements TableAdapter.OnTableClickListener {

    private FragmentTableStatusBinding binding;
    private TableAdapter tableAdapter;

    // TODO: Cần một TableRepository để lấy dữ liệu trạng thái bàn từ Firebase

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTableStatusBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // LOGIC KIỂM TRA QUYỀN TRUY CẬP (ADMIN HOẶC STAFF)
        SharedPreferences sharedPref = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = sharedPref.getString("userRole", "Guest");
        boolean isAuthenticated = FirebaseAuth.getInstance().getCurrentUser() != null;
        boolean hasPermission = userRole.equalsIgnoreCase("Admin") || userRole.equalsIgnoreCase("Staff");

        if (!isAuthenticated || !hasPermission) {
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack();
            return;
        }

        setupRecyclerView();
        loadTableStatus();
        setupBackButton(view);
    }

    private void setupBackButton(View view) {
        binding.btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack();
        });
    }

    private void setupRecyclerView() {
        // Thiết lập GridLayoutManager (3 cột)
        binding.rvTableStatus.setLayoutManager(new GridLayoutManager(getContext(), 3));

        tableAdapter = new TableAdapter(getContext(), new ArrayList<>());
        tableAdapter.setOnTableClickListener(this); // Gán Fragment làm Listener
        binding.rvTableStatus.setAdapter(tableAdapter);
    }

    private void loadTableStatus() {
        // Dữ liệu mẫu (Thay thế bằng dữ liệu thực tế từ Firebase/TableRepository)
        List<Table> sampleTables = new ArrayList<>();
        sampleTables.add(new Table("T01", "Bàn 01", "Occupied", "ORD123"));
        sampleTables.add(new Table("T02", "Bàn 02", "Available", null));
        sampleTables.add(new Table("T03", "Bàn 03", "Serving", "ORD456"));
        sampleTables.add(new Table("T04", "Bàn 04", "Available", null));
        sampleTables.add(new Table("T05", "Bàn 05", "Occupied", "ORD789"));

        tableAdapter.setTableList(sampleTables);

        // TODO: Thực hiện logic lắng nghe trạng thái bàn từ Firebase Realtime Database hoặc Firestore
    }

    // *** TỐI ƯU HÓA: Xử lý khi nhân viên nhấn vào một bàn ***
    @Override
    public void onTableClick(Table table) {
        NavController navController = Navigation.findNavController(requireView());

        if (table.getStatus().equalsIgnoreCase("Available")) {
            // 1. Bàn trống: Chuyển sang CartFragment để tạo Order mới
            Bundle bundle = new Bundle();
            bundle.putString("targetId", table.getId()); // Truyền ID bàn
            bundle.putString("orderType", "Table"); // Loại đơn hàng

            // Navigate đến CartFragment (giả sử bạn đã có ID này trong Nav Graph)
            navController.navigate(R.id.cartFragment, bundle);

        } else {
            // 2. Bàn đang sử dụng: Chuyển sang màn hình chi tiết đơn hàng
            Toast.makeText(getContext(), "Xem Đơn hàng " + table.getCurrentOrderId() + " của " + table.getName(), Toast.LENGTH_SHORT).show();
            // TODO: Chuyển sang màn hình chi tiết đơn hàng đang diễn ra (OrderDetailFragment)
        }
    }
    // ************************************************************

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        // TODO: Dừng lắng nghe Firebase nếu có
    }
}