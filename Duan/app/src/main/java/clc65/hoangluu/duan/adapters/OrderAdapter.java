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
    private static final DecimalFormat priceFormat = new DecimalFormat("#,### VNĐ");

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
        void onStatusActionClick(Order order);
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

        // Click vào toàn bộ card để xem chi tiết
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });

        // Click vào nút hành động (Ví dụ: "Sẵn sàng" -> "Hoàn thành")
        holder.binding.btnStatusAction.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStatusActionClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        public final ItemOrderCardBinding binding;

        public OrderViewHolder(ItemOrderCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Order order) {
            binding.tvOrderId.setText("#" + order.getId());
            binding.tvCustomerName.setText("Khách hàng: " + order.getTargetId());
            binding.tvOrderStatus.setText(order.getStatus());
            binding.tvTotalAmount.setText("Tổng tiền: " + priceFormat.format(order.getTotalAmount()));

            // TODO: Thiết lập màu sắc và text cho nút hành động dựa trên order.getStatus()
        }
    }
}