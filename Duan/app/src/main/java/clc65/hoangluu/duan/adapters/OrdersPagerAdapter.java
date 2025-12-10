package clc65.hoangluu.duan.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import clc65.hoangluu.duan.fragments.OrderListFragment;

public class OrdersPagerAdapter extends FragmentStateAdapter {

    private String[] statuses; // Danh sách các trạng thái Tab (Ví dụ: "All", "Pending", "Completed")

    public OrdersPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String[] statuses) {
        super(fragmentManager, lifecycle);
        this.statuses = statuses;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String statusFilter = statuses[position];
        // Truyền trạng thái cần lọc vào Fragment con
        return OrderListFragment.newInstance(statusFilter);
    }

    @Override
    public int getItemCount() {
        return statuses.length;
    }
}