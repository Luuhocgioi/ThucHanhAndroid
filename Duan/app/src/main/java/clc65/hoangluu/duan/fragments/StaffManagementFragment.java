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

import clc65.hoangluu.duan.R;
import clc65.hoangluu.duan.adapters.StaffAdapter;
import clc65.hoangluu.duan.databinding.FragmentStaffManagementBinding;
import clc65.hoangluu.duan.models.User;

public class StaffManagementFragment extends Fragment implements StaffAdapter.OnStaffActionListener {

    private FragmentStaffManagementBinding binding;
    private StaffAdapter staffAdapter;
    private FirebaseFirestore db;
    private ListenerRegistration staffListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStaffManagementBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupBackButton(view);
        startListeningForStaffList();

        binding.btnAddStaff.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Mở Dialog Thêm tài khoản mới", Toast.LENGTH_SHORT).show();
            // TODO: Mở Dialog/Fragment để Admin nhập thông tin Email, Tên, Mật khẩu, Vai trò
        });
    }

    private void setupBackButton(View view) {
        binding.btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack();
        });
    }

    private void setupRecyclerView() {
        binding.rvStaffList.setLayoutManager(new LinearLayoutManager(getContext()));
        staffAdapter = new StaffAdapter(new ArrayList<>());
        staffAdapter.setOnStaffActionListener(this);
        binding.rvStaffList.setAdapter(staffAdapter);
    }

    private void startListeningForStaffList() {
        // Lắng nghe tất cả các tài liệu trong collection "users" (Vai trò được lưu ở đây)
        Query query = db.collection("users").orderBy("role", Query.Direction.DESCENDING);

        staffListener = query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Toast.makeText(getContext(), "Lỗi tải danh sách nhân viên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshots != null) {
                List<User> users = new ArrayList<>();
                for (com.google.firebase.firestore.DocumentSnapshot doc : snapshots.getDocuments()) {
                    User user = doc.toObject(User.class);
                    if (user != null) {
                        user.setUid(doc.getId()); // UID chính là ID tài liệu Firestore
                        users.add(user);
                    }
                }
                staffAdapter.setUserList(users);
            }
        });
    }

    // Xử lý khi nhấn vào một nhân viên
    @Override
    public void onStaffClick(User user) {
        Toast.makeText(getContext(), "Xem chi tiết hồ sơ: " + user.getName(), Toast.LENGTH_SHORT).show();
    }

    // Xử lý khi nhấn nút Tùy chọn (Sửa/Xóa)
    @Override
    public void onActionClick(User user) {
        Toast.makeText(getContext(), "Mở menu Sửa/Xóa cho: " + user.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Mở Bottom Sheet Dialog hoặc Popup Menu cho các hành động Sửa, Đổi mật khẩu, Xóa
    }

    @Override
    public void onStop() {
        super.onStop();
        if (staffListener != null) {
            staffListener.remove(); // Dừng lắng nghe Firebase
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}