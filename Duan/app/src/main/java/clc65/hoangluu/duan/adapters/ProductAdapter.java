package clc65.hoangluu.duan.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import clc65.hoangluu.duan.R;
import clc65.hoangluu.duan.databinding.ItemProductCardBinding;
import clc65.hoangluu.duan.models.Product;
import java.text.DecimalFormat;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private OnItemClickListener listener;
    // Format tiền tệ
    private static final DecimalFormat priceFormat = new DecimalFormat("#,### VNĐ");

    // Interface để xử lý sự kiện click (OnClick XML)
    public interface OnItemClickListener {
        void onAddToCartClick(Product product);
        void onItemDetailClick(Product product);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    // Phương thức cập nhật dữ liệu
    public void setProductList(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng ViewBinding cho item layout
        ItemProductCardBinding binding = ItemProductCardBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product currentProduct = productList.get(position);

        holder.bind(currentProduct);

        // KHẮC PHỤC LỖI: Sử dụng ID đúng từ XML: btnAddToCart (hoặc btn_add_to_cart trong XML)
        holder.binding.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAddToCartClick(currentProduct);
                }
            }
        });

        // Xử lý sự kiện click trên toàn bộ item (Activity is listener)
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemDetailClick(currentProduct);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ViewHolder Class
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        // Khai báo binding của item layout
        public ItemProductCardBinding binding;

        public ProductViewHolder(ItemProductCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Product product) {
            // Lưu ý: Các ID trong Binding được chuyển thành camelCase
            binding.tvProductName.setText(product.getName());
            binding.tvProductPrice.setText(priceFormat.format(product.getPrice()));

            // TODO: Load ảnh bằng Glide hoặc Picasso
            // Glide.with(binding.imgProduct.getContext()).load(product.getImageUrl()).into(binding.imgProduct);
        }
    }
}