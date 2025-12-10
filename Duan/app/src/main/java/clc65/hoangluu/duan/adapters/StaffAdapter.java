package clc65.hoangluu.duan.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import clc65.hoangluu.duan.databinding.ItemStaffBinding;
import clc65.hoangluu.duan.models.User;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffViewHolder> {

    private List<User> userList;
    private OnStaffActionListener listener;

    public interface OnStaffActionListener {
        void onStaffClick(User user);
        void onActionClick(User user);
    }

    public void setOnStaffActionListener(OnStaffActionListener listener) {
        this.listener = listener;
    }

    public StaffAdapter(List<User> userList) {
        this.userList = userList;
    }

    public void setUserList(List<User> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStaffBinding binding = ItemStaffBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new StaffViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);

        // Click vào toàn bộ item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStaffClick(user);
            }
        });

        // Click vào nút hành động (Sửa/Xóa)
        holder.binding.btnStaffActions.setOnClickListener(v -> {
            if (listener != null) {
                listener.onActionClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class StaffViewHolder extends RecyclerView.ViewHolder {
        public final ItemStaffBinding binding;

        public StaffViewHolder(ItemStaffBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(User user) {
            binding.tvStaffName.setText(user.getName());
            binding.tvStaffEmail.setText(user.getEmail());
            binding.tvStaffRole.setText(user.getRole());

            // Đặt màu sắc cho vai trò Admin
            if (user.getRole().equals("Admin")) {
                binding.tvStaffRole.setTextColor(Color.RED);
            } else {
                binding.tvStaffRole.setTextColor(Color.BLACK);
            }
        }
    }
}