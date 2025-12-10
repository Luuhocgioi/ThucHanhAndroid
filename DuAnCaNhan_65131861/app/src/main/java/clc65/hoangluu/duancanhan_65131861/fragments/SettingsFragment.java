package clc65.hoangluu.duancanhan_65131861.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import clc65.hoangluu.duancanhan_65131861.R;
import clc65.hoangluu.duancanhan_65131861.MainActivity;
import clc65.hoangluu.duancanhan_65131861.AdminProductActivity;
import android.content.Intent;

public class SettingsFragment extends Fragment {

    private LinearLayout adminPanel;
    private Button btnManageProducts, btnManageOrders, btnLogout, btnLoginRedirect;
    private TextView tvRole;
    private String userRole;
    private boolean isUserLoggedIn;

    public SettingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        adminPanel = view.findViewById(R.id.adminPanel);
        btnManageProducts = view.findViewById(R.id.btnManageProducts);
        btnManageOrders = view.findViewById(R.id.btnManageOrders);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLoginRedirect = view.findViewById(R.id.btnLoginRedirect);
        tvRole = view.findViewById(R.id.tvRole);

        if (getArguments() != null) {
            userRole = getArguments().getString("USER_ROLE");
            isUserLoggedIn = getArguments().getBoolean("IS_LOGGED_IN", false);
        } else {
            userRole = "Guest";
            isUserLoggedIn = false;
        }

        tvRole.setText("Vai trò: " + userRole);

        if (isUserLoggedIn) {
            if(btnLogout != null) btnLogout.setVisibility(View.VISIBLE);
            if(btnLoginRedirect != null) btnLoginRedirect.setVisibility(View.GONE);
            if ("Admin".equals(userRole) && adminPanel != null) {
                adminPanel.setVisibility(View.VISIBLE);
            } else if(adminPanel != null) {
                adminPanel.setVisibility(View.GONE);
            }
        } else {
            if(btnLogout != null) btnLogout.setVisibility(View.GONE);
            if(btnLoginRedirect != null) btnLoginRedirect.setVisibility(View.VISIBLE);
            if(adminPanel != null) adminPanel.setVisibility(View.GONE);
            tvRole.setText("Vai trò: Khách (Chưa đăng nhập)");
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).logoutUser();
                }
            });
        }

        if (btnLoginRedirect != null) {
            btnLoginRedirect.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).redirectToLogin();
                }
            });
        }

        if (btnManageProducts != null) {
            btnManageProducts.setOnClickListener(v -> {
                if (isUserLoggedIn && "Admin".equals(userRole)) {
                    startActivity(new Intent(getActivity(), AdminProductActivity.class));
                } else {
                    Toast.makeText(getContext(), "Chỉ Admin mới có quyền truy cập.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnManageOrders != null) {
            btnManageOrders.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Quản lý đơn hàng được truy cập qua tab QL Đơn hàng.", Toast.LENGTH_SHORT).show();
            });
        }

        return view;
    }
}