package clc65.hoangluu.duan.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import clc65.hoangluu.duan.RoleUpdateListener; // Import RoleUpdateListener
import clc65.hoangluu.duan.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment implements LoginDialogFragment.LoginListener {

    private FragmentSettingsBinding binding;
    private FirebaseAuth mAuth;
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_ROLE = "userRole";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        updateUIBasedOnLoginStatus();

        binding.btnLoginLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    // Đang đăng nhập -> Đăng xuất
                    mAuth.signOut();
                    clearUserSession(); // Xóa vai trò đã lưu
                    updateUIBasedOnLoginStatus();
                    Toast.makeText(getContext(), "Đã đăng xuất thành công.", Toast.LENGTH_SHORT).show();

                    // Gửi tín hiệu chung đến Activity để cập nhật giao diện Quản lý sau khi đăng xuất
                    if (getActivity() instanceof RoleUpdateListener) {
                        ((RoleUpdateListener) getActivity()).onRoleUpdated("Guest");
                    }
                } else {
                    // Chưa đăng nhập -> Mở Dialog
                    showLoginDialog();
                }
            }
        });
    }

    private void updateUIBasedOnLoginStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String userRole = sharedPref.getString(KEY_ROLE, "Guest");

        if (currentUser != null) {
            binding.btnLoginLogout.setText("Đăng Xuất (" + currentUser.getEmail() + ")");
            binding.tvUserRole.setText("Vai trò: " + userRole);
        } else {
            binding.btnLoginLogout.setText("Đăng Nhập");
            binding.tvUserRole.setText("Vai trò: Khách (Guest)");
        }
    }

    private void showLoginDialog() {
        LoginDialogFragment dialog = new LoginDialogFragment();
        dialog.setTargetFragment(SettingsFragment.this, 0);
        dialog.show(getParentFragmentManager(), "LoginDialogTag");
    }

    private void saveRoleToSharedPreferences(String role) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPref.edit().putString(KEY_ROLE, role).apply();
    }

    private void clearUserSession() {
        getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().remove(KEY_ROLE).apply();
    }

    @Override
    public void onLoginSuccess(String role) {
        saveRoleToSharedPreferences(role);

        updateUIBasedOnLoginStatus();

        Toast.makeText(getContext(), "Đăng nhập thành công! Vai trò: " + role, Toast.LENGTH_LONG).show();

        // **KHẮC PHỤC LỖI: Gửi tín hiệu chung đến Activity để cập nhật giao diện Quản lý**
        // MainActivity đã implement RoleUpdateListener
        if (getActivity() instanceof RoleUpdateListener) {
            ((RoleUpdateListener) getActivity()).onRoleUpdated(role);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}