package clc65.hoangluu.duan.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Cần dùng thư viện Glide hoặc Picasso
import java.util.List;

import clc65.hoangluu.duan.R;
import clc65.hoangluu.duan.models.SliderItem;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<SliderItem> sliderItems;

    public SliderAdapter(List<SliderItem> sliderItems) {
        this.sliderItems = sliderItems;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        SliderItem item = sliderItems.get(position);

        // SỬ DỤNG GLIDE ĐỂ LOAD ẢNH TỪ URL
        // BẠN CẦN THÊM DEPENDENCY GLIDE VÀO BUILD.GRADLE
        Glide.with(holder.imageView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.ic_slider_placeholder) // Ảnh placeholder
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    // Phương thức cập nhật dữ liệu (nếu cần)
    public void updateData(List<SliderItem> newItems) {
        this.sliderItems = newItems;
        notifyDataSetChanged();
    }

    // ViewHolder Class
    public static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_slide);
            // Không dùng View Binding vì item_slider rất đơn giản
        }
    }
}