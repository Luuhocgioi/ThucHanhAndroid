package clc65.hoangluu.duan.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

import clc65.hoangluu.duan.R;
import clc65.hoangluu.duan.databinding.ItemCartItemBinding;
import clc65.hoangluu.duan.models.OrderItem;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {

    private List<OrderItem> cartItemList;
    private OnCartActionListener listener;
    private static final DecimalFormat priceFormat = new DecimalFormat("#,### VNĐ");

    public interface OnCartActionListener {
        void onQuantityChange(OrderItem item, int newQuantity);
        void onDeleteItem(OrderItem item);
    }

    public void setOnCartActionListener(OnCartActionListener listener) {
        this.listener = listener;
    }

    public CartItemAdapter(List<OrderItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    public void setCartItemList(List<OrderItem> newCartItemList) {
        this.cartItemList = newCartItemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartItemBinding binding = ItemCartItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CartItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        OrderItem item = cartItemList.get(position);
        holder.bind(item);

        // Xử lý sự kiện TĂNG số lượng
        holder.binding.btnIncrementCart.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuantityChange(item, item.getQuantity() + 1);
            }
        });

        // Xử lý sự kiện GIẢM số lượng
        holder.binding.btnDecrementCart.setOnClickListener(v -> {
            if (listener != null && item.getQuantity() > 1) {
                listener.onQuantityChange(item, item.getQuantity() - 1);
            }
        });

        // Xử lý sự kiện XÓA item
        holder.binding.btnDeleteItem.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteItem(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartItemViewHolder extends RecyclerView.ViewHolder {
        public final ItemCartItemBinding binding;

        public CartItemViewHolder(ItemCartItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OrderItem item) {
            // Tên và Ghi chú
            binding.tvCartItemName.setText(item.getName());
            binding.tvCartItemNote.setText(item.getNote().isEmpty() ? "Không có ghi chú" : "Ghi chú: " + item.getNote());

            // Giá tính toán
            double itemTotalPrice = item.getQuantity() * item.getPrice();
            binding.tvCartItemPrice.setText(priceFormat.format(itemTotalPrice));

            // Số lượng hiện tại
            binding.tvCartItemQuantity.setText(String.valueOf(item.getQuantity()));
        }
    }
}