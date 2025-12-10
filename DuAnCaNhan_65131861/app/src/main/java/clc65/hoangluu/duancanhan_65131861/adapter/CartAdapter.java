package clc65.hoangluu.duancanhan_65131861.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import clc65.hoangluu.duancanhan_65131861.R;
import clc65.hoangluu.duancanhan_65131861.database.SQLiteHelper;
import clc65.hoangluu.duancanhan_65131861.model.CartItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final Context context;
    private List<CartItem> cartList;
    private final SQLiteHelper dbHelper;
    private final CartUpdateListener listener;

    public interface CartUpdateListener {
        void onCartUpdated();
    }

    public CartAdapter(Context context, List<CartItem> cartList, CartUpdateListener listener) {
        this.context = context;
        this.cartList = cartList;
        this.dbHelper = new SQLiteHelper(context);
        this.listener = listener;
    }

    public void updateList(List<CartItem> newList) {
        this.cartList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartList.get(position);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        holder.tvName.setText(item.getProductName());
        holder.tvPrice.setText(currencyFormat.format(item.getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvTotal.setText(currencyFormat.format(item.getItemTotal()));

        holder.ivImage.setImageResource(R.drawable.ic_coffee);

        holder.btnIncrease.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            updateItemQuantity(item.getProductId(), newQuantity);
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() - 1;
            if (newQuantity >= 1) {
                updateItemQuantity(item.getProductId(), newQuantity);
            } else {
                deleteItem(item.getProductId(), position);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            deleteItem(item.getProductId(), position);
        });
    }

    private void updateItemQuantity(String productId, int newQuantity) {
        dbHelper.updateQuantity(productId, newQuantity);
        cartList = dbHelper.getAllCartItems();
        notifyDataSetChanged();
        listener.onCartUpdated();
    }

    private void deleteItem(String productId, int position) {
        dbHelper.deleteCartItem(productId);
        cartList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cartList.size());
        listener.onCartUpdated();
        Toast.makeText(context, "Đã xóa món khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvPrice, tvQuantity, tvTotal;
        ImageButton btnIncrease, btnDecrease, btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            // Các ID đã được liên kết với item_cart.xml
            ivImage = itemView.findViewById(R.id.ivImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}