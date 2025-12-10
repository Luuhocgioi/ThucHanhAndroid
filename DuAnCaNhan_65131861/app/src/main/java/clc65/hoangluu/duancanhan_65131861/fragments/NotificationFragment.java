package clc65.hoangluu.duancanhan_65131861.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import clc65.hoangluu.duancanhan_65131861.R;

public class NotificationFragment extends Fragment {

    private RecyclerView rvNotifications;
    private TextView tvNoNoti;

    public NotificationFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // ĐÃ SỬA LỖI: Layout phải là fragment_notifications
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        rvNotifications = view.findViewById(R.id.rvNotifications);
        tvNoNoti = view.findViewById(R.id.tvNoNotifications);

        if (tvNoNoti != null) {
            tvNoNoti.setVisibility(View.VISIBLE);
        }
        if (rvNotifications != null) {
            rvNotifications.setVisibility(View.GONE);
        }

        return view;
    }
}