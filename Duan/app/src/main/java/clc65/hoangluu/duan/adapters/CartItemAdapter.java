package clc65.hoangluu.duan.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DecimalFormat;
import java.util.List;

import clc65.hoangluu.duan.R;
import clc65.hoangluu.duan.databinding.ItemCartItemBinding;
import clc65.hoangluu.duan.models.OrderItem;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {

    private List<OrderItem> cartItemList;
    private OnCartActionListener listener;
    private static final DecimalFormat priceFormat = new DecimalFormat("#,###đ");
    private boolean isReadOnly = false;

    public void setReadOnly(boolean readOnly) {
        this.isReadOnly = readOnly;
        notifyDataSetChanged();
    }

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
        // Gọi hàm bind đã bao gồm logic hiển thị ảnh và xử lý Read-Only
        holder.bind(item, isReadOnly, listener);
    }

    @Override
    public int getItemCount() {
        return cartItemList != null ? cartItemList.size() : 0;
    }

    public static class CartItemViewHolder extends RecyclerView.ViewHolder {
        public final ItemCartItemBinding binding;

        public CartItemViewHolder(ItemCartItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OrderItem item, boolean isReadOnly, OnCartActionListener listener) {
            binding.tvCartItemName.setText(item.getName());
            binding.tvCartItemNote.setText(item.getNote() == null || item.getNote().isEmpty() ?
                    "Không có ghi chú" : "Ghi chú: " + item.getNote());

            double itemTotalPrice = item.getQuantity() * item.getPrice();
            binding.tvCartItemPrice.setText(priceFormat.format(itemTotalPrice));

            // --- ĐỒNG BỘ HÌNH ẢNH TỪ PRODUCT QUA URL LƯU TRONG ORDERITEM ---
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                Glide.with(binding.imgCartItem.getContext())
                        .load(item.getImageUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_coffee_placeholder)
                        .error(R.drawable.ic_coffee_placeholder)
                        .centerCrop()
                        .into(binding.imgCartItem);
            } else {
                binding.imgCartItem.setImageResource(R.drawable.ic_coffee_placeholder);
            }

            // --- XỬ LÝ LOGIC HIỂN THỊ DỰA TRÊN TRẠNG THÁI READ-ONLY ---
            if (isReadOnly) {
                binding.btnIncrementCart.setVisibility(View.GONE);
                binding.btnDecrementCart.setVisibility(View.GONE);
                binding.btnDeleteItem.setVisibility(View.GONE);
                binding.tvCartItemQuantity.setText("x" + item.getQuantity());
            } else {
                binding.btnIncrementCart.setVisibility(View.VISIBLE);
                binding.btnDecrementCart.setVisibility(View.VISIBLE);
                binding.btnDeleteItem.setVisibility(View.VISIBLE);
                binding.tvCartItemQuantity.setText(String.valueOf(item.getQuantity()));

                binding.btnIncrementCart.setOnClickListener(v -> {
                    if (listener != null) listener.onQuantityChange(item, item.getQuantity() + 1);
                });

                binding.btnDecrementCart.setOnClickListener(v -> {
                    if (listener != null && item.getQuantity() > 1) {
                        listener.onQuantityChange(item, item.getQuantity() - 1);
                    }
                });

                binding.btnDeleteItem.setOnClickListener(v -> {
                    if (listener != null) listener.onDeleteItem(item);
                });
            }
        }
    }
}