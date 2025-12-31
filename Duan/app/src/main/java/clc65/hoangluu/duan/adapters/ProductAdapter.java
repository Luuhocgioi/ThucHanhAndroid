package clc65.hoangluu.duan.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import clc65.hoangluu.duan.R;
import clc65.hoangluu.duan.databinding.ItemProductCardBinding;
import clc65.hoangluu.duan.models.Product;
import java.text.DecimalFormat;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private OnItemClickListener listener;
    private static final DecimalFormat priceFormat = new DecimalFormat("#,### VNĐ");

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

    public ProductAdapter(List<Product> productList, OnItemClickListener listener) {
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
        ItemProductCardBinding binding = ItemProductCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product currentProduct = productList.get(position);
        holder.bind(currentProduct, listener);
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public ItemProductCardBinding binding;

        public ProductViewHolder(ItemProductCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Product product, OnItemClickListener listener) {
            binding.tvProductName.setText(product.getName());
            binding.tvProductPrice.setText(priceFormat.format(product.getPrice()));

            binding.btnAddToCart.setOnClickListener(v -> {
                if (listener != null) listener.onAddToCartClick(product);
            });

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onItemDetailClick(product);
            });
            binding.getRoot().setOnClickListener(v -> {
                // Tạo Bundle và đóng gói đối tượng product
                android.os.Bundle bundle = new android.os.Bundle();
                bundle.putSerializable("product", product); // Đảm bảo class Product đã "implements Serializable"

                // Thực hiện chuyển màn hình kèm theo dữ liệu
                androidx.navigation.Navigation.findNavController(v)
                        .navigate(R.id.detailFragment, bundle);

                // Vẫn giữ lại listener cũ của bạn nếu cần dùng thêm logic khác
                if (listener != null) listener.onItemDetailClick(product);
            });
            if (product.getImageUrl() != null && !product.getImageUrl().trim().isEmpty()) {
                Glide.with(binding.imgProduct.getContext())
                        .load(product.getImageUrl().trim())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_coffee_placeholder)
                        .error(R.drawable.ic_coffee_placeholder)
                        .centerCrop()
                        .into(binding.imgProduct);
            } else {
                binding.imgProduct.setImageResource(R.drawable.ic_coffee_placeholder);
            }
        }
    }
}