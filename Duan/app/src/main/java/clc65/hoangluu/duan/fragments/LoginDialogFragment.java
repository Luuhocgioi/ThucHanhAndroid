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

    public interface LoginListener {
        void onLoginSuccess(String role);
    }

    private LoginListener loginListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            loginListener = (LoginListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement LoginListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnDialogLogin.setOnClickListener(v -> {
            String email = binding.etLoginEmail.getText().toString().trim();
            String password = binding.etLoginPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ Email và Mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            attemptLogin(email, password);
        });
    }

    private void attemptLogin(String email, String password) {
        showProgress(true);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Firebase Auth Success.");
                        fetchUserRole(mAuth.getCurrentUser().getUid());
                    } else {
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

                            // *** SỬA LỖI VÀ CHUẨN HÓA VAI TRÒ ***
                            role = role.trim();
                            if (role.length() > 0) {
                                role = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
                            }
                            // *** KẾT THÚC CHUẨN HÓA ***

                            loginListener.onLoginSuccess(role);
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), "Không tìm thấy vai trò người dùng.", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
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