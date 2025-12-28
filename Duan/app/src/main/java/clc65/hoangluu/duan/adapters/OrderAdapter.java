package clc65.hoangluu.duan.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

import clc65.hoangluu.duan.R;
import clc65.hoangluu.duan.databinding.ItemOrderCardBinding;
import clc65.hoangluu.duan.models.Order;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private OnOrderClickListener listener;
    private static final DecimalFormat priceFormat = new DecimalFormat("#,###đ");

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
        void onAcceptOrder(Order order);
        void onRejectOrder(Order order);
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    public void setOrderList(List<Order> newOrderList) {
        this.orderList = newOrderList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderCardBinding binding = ItemOrderCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);

        // Xem chi tiết khi bấm vào nút hoặc card
        holder.binding.btnViewDetail.setOnClickListener(v -> {
            if (listener != null) listener.onOrderClick(order);
        });

        // Xử lý các nút bấm nhanh (Xác nhận/Từ chối)
        holder.binding.btnAccept.setOnClickListener(v -> {
            if (listener != null) listener.onAcceptOrder(order);
        });

        holder.binding.btnReject.setOnClickListener(v -> {
            if (listener != null) listener.onRejectOrder(order);
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        public final ItemOrderCardBinding binding;

        public OrderViewHolder(ItemOrderCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Order order) {
            // 1. Mã đơn hàng (Lấy 5 ký tự cuối cho đẹp)
            binding.tvOrderId.setText("#" + (order.getId() != null && order.getId().length() > 5
                    ? order.getId().substring(order.getId().length() - 5).toUpperCase()
                    : "000"));

            // 2. Trạng thái (Chip)
            binding.chipStatus.setText(order.getStatus());
            setupStatusStyle(order.getStatus());

            // 3. Loại đơn & Thông tin (Bàn hoặc Tên khách)
            String info = order.getType().equalsIgnoreCase("Takeaway")
                    ? "🥤 Mang về: " + order.getTargetId()
                    : "🪑 Bàn: " + order.getTargetId();
            binding.tvOrderType.setText(info);

            // 4. Tổng tiền
            binding.tvOrderTotal.setText(priceFormat.format(order.getTotalAmount()));

            // 5. Hiển thị danh sách món tóm tắt (Nếu có)
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                StringBuilder summary = new StringBuilder();
                for (int i = 0; i < order.getItems().size(); i++) {
                    summary.append(order.getItems().get(i).getQuantity())
                            .append("x ")
                            .append(order.getItems().get(i).getName());
                    if (i < order.getItems().size() - 1) summary.append(", ");
                }
                binding.tvOrderItems.setText(summary.toString());
            }

            // 6. Tối ưu nút bấm chuyển trạng thái
            // Hiển thị quickActions ở tab Pending (để Xác nhận) và Preparing (để báo Pha xong)
            if ("Pending".equalsIgnoreCase(order.getStatus())) {
                binding.quickActions.setVisibility(View.VISIBLE);
                binding.btnAccept.setText("✓ Xác nhận");
                binding.btnReject.setVisibility(View.VISIBLE);
            } else if ("Preparing".equalsIgnoreCase(order.getStatus())) {
                binding.quickActions.setVisibility(View.VISIBLE);
                binding.btnAccept.setText("☕ Pha xong");
                binding.btnReject.setVisibility(View.GONE); // Đã làm thì không cho từ chối
            } else if ("Ready".equalsIgnoreCase(order.getStatus())) {
                binding.quickActions.setVisibility(View.VISIBLE);
                binding.btnAccept.setText("✅ Hoàn tất");
                binding.btnReject.setVisibility(View.GONE);
            } else {
                binding.quickActions.setVisibility(View.GONE);
            }
        }

        private void setupStatusStyle(String status) {
            int colorRes = R.color.status_pending; // Mặc định xanh dương (Pending)

            if ("Preparing".equalsIgnoreCase(status)) {
                colorRes = R.color.status_warning; // Vàng (Đang pha)
            } else if ("Ready".equalsIgnoreCase(status)) {
                colorRes = R.color.status_success; // Xanh lá (Sẵn sàng)
            } else if ("Completed".equalsIgnoreCase(status)) {
                colorRes = R.color.text_tertiary; // Xám (Hoàn thành)
            } else if ("Cancelled".equalsIgnoreCase(status)) {
                colorRes = R.color.status_error; // Đỏ (Hủy)
            }

            binding.chipStatus.setChipBackgroundColorResource(colorRes);
        }
    }
}