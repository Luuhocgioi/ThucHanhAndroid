package clc65.hoangluu.duancanhan_65131861.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import clc65.hoangluu.duancanhan_65131861.R;

public class HomeFragment extends Fragment {

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView tvWelcome = view.findViewById(R.id.tvWelcome);
        Button btnQuickOrder = view.findViewById(R.id.btnQuickOrder);

        // Kiểm tra an toàn trước khi sử dụng View
        if (tvWelcome != null) {
            tvWelcome.setText("Chào mừng đến với hệ thống Order!");
        }

        if (btnQuickOrder != null) {
            btnQuickOrder.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Chuyển sang tab Tìm kiếm (Menu Order) để đặt món.", Toast.LENGTH_SHORT).show();
            });
        }

        return view;
    }
}