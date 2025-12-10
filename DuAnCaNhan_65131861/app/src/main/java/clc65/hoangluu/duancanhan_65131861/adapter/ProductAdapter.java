package clc65.hoangluu.duancanhan_65131861.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import clc65.hoangluu.duancanhan_65131861.R;
import clc65.hoangluu.duancanhan_65131861.database.SQLiteHelper;
import clc65.hoangluu.duancanhan_65131861.model.CartItem;
import clc65.hoangluu.duancanhan_65131861.model.Product;

import java.util.List;
import java.util.Locale;
import java.text.NumberFormat;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context context;
    private List<Product> productList;
    private final SQLiteHelper dbHelper;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.dbHelper = new SQLiteHelper(context);
    }

    public void updateList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText(NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(product.getPrice()));

        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            // Placeholder: Không dùng Picasso vì bạn chưa học
        }

        // SỬA LỖI: Kiểm tra an toàn xem ic_coffee có tồn tại không
        try {
            holder.ivImage.setImageResource(R.drawable.ic_coffee);
        } catch (Exception e) {
            holder.ivImage.setImageResource(android.R.drawable.ic_menu_gallery); // Dùng icon Android mặc định
        }

        if (!product.isStatus()) {
            holder.btnAdd.setEnabled(false);
            holder.btnAdd.setText("Hết hàng");
        } else {
            holder.btnAdd.setEnabled(true);
            holder.btnAdd.setText("Thêm vào giỏ");
        }

        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (product.isStatus()) {
                    CartItem cartItem = new CartItem(
                            product.getId(),
                            product.getName(),
                            (double) product.getPrice(),
                            1,
                            product.getImageUrl()
                    );

                    dbHelper.addToCart(cartItem);
                    Toast.makeText(context, product.getName() + " đã được thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvProductName, tvProductPrice;
        Button btnAdd;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }
}