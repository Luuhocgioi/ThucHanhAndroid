package clc65.hoangluu.duancanhan_65131861.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import clc65.hoangluu.duancanhan_65131861.R;
import clc65.hoangluu.duancanhan_65131861.model.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductManagementAdapter extends RecyclerView.Adapter<ProductManagementAdapter.ProductViewHolder> {

    private final Context context;
    private List<Product> productList;
    private final ProductActionListener listener;

    public interface ProductActionListener {
        void onEdit(Product product);
        void onDelete(String productId);
    }

    public ProductManagementAdapter(Context context, List<Product> productList, ProductActionListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    public void updateList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Cần tạo file item_product_management.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_management, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(currencyFormat.format(product.getPrice()));
        holder.tvStatus.setText(product.isStatus() ? "Còn hàng" : "Hết hàng");
        holder.tvStatus.setTextColor(product.isStatus() ? 0xFF4CAF50 : 0xFFF44336); // Màu xanh/đỏ

        // Ảnh (Placeholder)
        holder.ivImage.setImageResource(R.drawable.ic_coffee);

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(product));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(product.getId()));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvPrice, tvStatus;
        Button btnEdit, btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            // Cần tạo các ID này trong item_product_management.xml
            // ivImage = itemView.findViewById(R.id.ivImage);
            // tvName = itemView.findViewById(R.id.tvProductName);
            // tvPrice = itemView.findViewById(R.id.tvProductPrice);
            // tvStatus = itemView.findViewById(R.id.tvProductStatus);
            // btnEdit = itemView.findViewById(R.id.btnEdit);
            // btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}