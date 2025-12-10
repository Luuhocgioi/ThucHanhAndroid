package clc65.hoangluu.duancanhan_65131861.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import clc65.hoangluu.duancanhan_65131861.R;
import clc65.hoangluu.duancanhan_65131861.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final Context context;
    private List<Category> categoryList;
    private final CategoryActionListener listener;

    public interface CategoryActionListener {
        void onEdit(Category category);
        void onDelete(String categoryId);
    }

    public CategoryAdapter(Context context, List<Category> categoryList, CategoryActionListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    public void updateList(List<Category> newList) {
        this.categoryList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Cần tạo file item_category_management.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_management, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);

        holder.tvName.setText(category.getName());
        holder.tvId.setText("ID: " + category.getId().substring(0, 8));

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(category));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(category.getId()));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvId;
        Button btnEdit, btnDelete;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Cần tạo các ID này trong item_category_management.xml
            // tvName = itemView.findViewById(R.id.tvCategoryName);
            // tvId = itemView.findViewById(R.id.tvCategoryId);
            // btnEdit = itemView.findViewById(R.id.btnEdit);
            // btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}