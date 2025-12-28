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
        super.onViewCreated(view, savedInstanceState); // ✅ GỌI TRƯỚC

        // **********************************************************
        // LOGIC KIỂM TRA QUYỀN TRUY CẬP (CHỈ ADMIN)
        // **********************************************************
        if (getContext() == null) {
            return;
        }

        SharedPreferences sharedPref = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = sharedPref.getString("userRole", "Guest");

        boolean isAuthenticated = FirebaseAuth.getInstance().getCurrentUser() != null;
        boolean hasPermission = "Admin".equalsIgnoreCase(userRole);

        if (!isAuthenticated || !hasPermission) {
            Toast.makeText(getContext(), "Chức năng Quản lý Nhân viên chỉ dành cho Admin đã đăng nhập.", Toast.LENGTH_LONG).show();
            try {
                NavController navController = Navigation.findNavController(view);
                navController.popBackStack();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        // **********************************************************
        // KẾT THÚC KIỂM TRA
        // **********************************************************

        setupRecyclerView();
        setupBackButton(view);
        startListeningForStaffList();

        if (binding.btnAddStaff != null) {
            binding.btnAddStaff.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Mở Dialog Thêm tài khoản mới", Toast.LENGTH_SHORT).show();
                // TODO: Mở Dialog/Fragment để Admin nhập thông tin Email, Tên, Mật khẩu, Vai trò
            });
        }
    }

    private void setupBackButton(View view) {
        if (binding.btnBack != null) {
            binding.btnBack.setOnClickListener(v -> {
                try {
                    NavController navController = Navigation.findNavController(view);
                    navController.popBackStack();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void setupRecyclerView() {
        if (binding.rvStaffList != null) {
            binding.rvStaffList.setLayoutManager(new LinearLayoutManager(getContext()));
            staffAdapter = new StaffAdapter(new ArrayList<>());
            staffAdapter.setOnStaffActionListener(this);
            binding.rvStaffList.setAdapter(staffAdapter);
        }
    }

    private void startListeningForStaffList() {
        try {
            Query query = db.collection("users").orderBy("role", Query.Direction.DESCENDING);

            staffListener = query.addSnapshotListener((snapshots, e) -> {
                if (e != null) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Lỗi tải danh sách nhân viên: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    return;
                }

                if (snapshots != null && staffAdapter != null) {
                    List<User> users = new ArrayList<>();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : snapshots.getDocuments()) {
                        User user = doc.toObject(User.class);
                        if (user != null) {
                            user.setUid(doc.getId());
                            users.add(user);
                        }
                    }
                    staffAdapter.setUserList(users);
                }
            });
        } catch (Exception ex) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "Lỗi khởi tạo Staff Listener: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStaffClick(User user) {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Xem chi tiết hồ sơ: " + user.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActionClick(User user) {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Mở menu Sửa/Xóa cho: " + user.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (staffListener != null) {
            staffListener.remove();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}