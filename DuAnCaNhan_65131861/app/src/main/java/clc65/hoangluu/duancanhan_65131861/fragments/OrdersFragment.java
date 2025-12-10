package clc65.hoangluu.duancanhan_65131861.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import clc65.hoangluu.duancanhan_65131861.R;

public class OrdersFragment extends Fragment {

    private RecyclerView rvOrders;
    private TextView tvFilterStatus;

    public OrdersFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        rvOrders = view.findViewById(R.id.rvOrders);
        tvFilterStatus = view.findViewById(R.id.tvFilterStatus);

        // TODO: Tải danh sách đơn hàng từ Firebase RTDB, hiển thị bằng RecyclerView
        // Chức năng này sẽ hiển thị tất cả đơn hàng cho Admin/Staff

        return view;
    }
}