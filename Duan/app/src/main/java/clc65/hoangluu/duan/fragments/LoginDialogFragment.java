package clc65.hoangluu.duan.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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

import clc65.hoangluu.duan.database.DatabaseHelper;
import clc65.hoangluu.duan.databinding.DialogLoginBinding;

public class LoginDialogFragment extends DialogFragment {

    private DialogLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DatabaseHelper dbHelper; // Thêm dbHelper để thao tác SQLite
    private static final String TAG = "LoginDialog";

    public interface LoginListener {
        void onLoginSuccess(String role);
    }

    private LoginListener loginListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            // Kiểm tra target fragment hoặc activity để gán listener
            if (getTargetFragment() instanceof LoginListener) {
                loginListener = (LoginListener) getTargetFragment();
            } else if (context instanceof LoginListener) {
                loginListener = (LoginListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling UI must implement LoginListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        dbHelper = new DatabaseHelper(getContext()); // Khởi tạo dbHelper
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
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");

                        if (role != null) {
                            // *** CHUẨN HÓA VAI TRÒ ĐỂ KHỚP VỚI LOGIC APP ***
                            role = role.trim();
                            if (role.length() > 0) {
                                role = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
                            }

                            // *** LƯU VÀO SQLITE ĐỂ DUY TRÌ PHIÊN LÀM VIỆC ***
                            saveStaffToSQLite(uid, name, email, role);

                            if (loginListener != null) {
                                loginListener.onLoginSuccess(role);
                            }
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

    // Hàm thực hiện lưu thông tin nhân viên/admin vào SQLite cục bộ
    private void saveStaffToSQLite(String uid, String name, String email, String role) {
        try {
            SQLiteDatabase dbSqlite = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("uid", uid);
            values.put("name", name != null ? name : "Unknown");
            values.put("email", email != null ? email : "");
            values.put("role", role);

            // Sử dụng REPLACE để cập nhật nếu UID đã tồn tại
            dbSqlite.insertWithOnConflict("staff", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            Log.d(TAG, "Saved staff to SQLite: " + role);
        } catch (Exception e) {
            Log.e(TAG, "Error saving to SQLite", e);
        }
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