package clc65.hoangluu.duan.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import clc65.hoangluu.duan.databinding.DialogLoginBinding;

public class LoginDialogFragment extends DialogFragment {

    private DialogLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "LoginDialog";

    // Interface để truyền dữ liệu (vai trò) về SettingsFragment (Activity is Listener)
    public interface LoginListener {
        void onLoginSuccess(String role);
    }

    private LoginListener loginListener;

    // Gán Listener khi Dialog được attach vào Context
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            // Đảm bảo Fragment gọi Dialog (SettingsFragment) implement interface này
            loginListener = (LoginListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement LoginListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Sử dụng View Binding cho layout dialog_login.xml
        binding = DialogLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Xử lý sự kiện click nút Đăng nhập (Inline Anonymous Listener)
        binding.btnDialogLogin.setOnClickListener(v -> {
            String email = binding.etLoginEmail.getText().toString().trim();
            String password = binding.etLoginPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ Email và Mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            // Thực hiện đăng nhập Firebase
            attemptLogin(email, password);
        });
    }

    private void attemptLogin(String email, String password) {
        showProgress(true);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Firebase Auth Success.");
                        // Bước quan trọng: Đăng nhập thành công, lấy vai trò từ Firestore
                        fetchUserRole(mAuth.getCurrentUser().getUid());
                    } else {
                        // Đăng nhập thất bại
                        showProgress(false);
                        Toast.makeText(getContext(), "Đăng nhập thất bại: Kiểm tra Email/Mật khẩu.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Auth Failed", task.getException());
                    }
                });
    }

    private void fetchUserRole(String uid) {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    showProgress(false);
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        if (role != null) {
                            // Truyền vai trò về SettingsFragment
                            loginListener.onLoginSuccess(role);
                            dismiss(); // Đóng dialog
                        } else {
                            Toast.makeText(getContext(), "Không tìm thấy vai trò người dùng.", Toast.LENGTH_SHORT).show();
                            mAuth.signOut(); // Đăng xuất nếu không có vai trò hợp lệ
                        }
                    } else {
                        Toast.makeText(getContext(), "Tài khoản không được phân quyền quản lý.", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(getContext(), "Lỗi kết nối Firestore.", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    Log.e(TAG, "Firestore Fetch Failed", e);
                });
    }

    private void showProgress(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnDialogLogin.setEnabled(!show);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}