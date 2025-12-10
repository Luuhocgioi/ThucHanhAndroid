package clc65.hoangluu.duan.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import clc65.hoangluu.duan.R;
import clc65.hoangluu.duan.databinding.ItemTableBinding;
import clc65.hoangluu.duan.models.Table;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private List<Table> tableList;
    private OnTableClickListener listener;
    private Context context;

    public interface OnTableClickListener {
        void onTableClick(Table table);
    }

    public void setOnTableClickListener(OnTableClickListener listener) {
        this.listener = listener;
    }

    public TableAdapter(Context context, List<Table> tableList) {
        this.context = context;
        this.tableList = tableList;
    }

    public void setTableList(List<Table> newTableList) {
        this.tableList = newTableList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTableBinding binding = ItemTableBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TableViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        Table table = tableList.get(position);
        holder.bind(table);

        // Xử lý sự kiện click (Inline Anonymous Listener)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTableClick(table);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    public class TableViewHolder extends RecyclerView.ViewHolder {
        private final ItemTableBinding binding;

        public TableViewHolder(ItemTableBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Table table) {
            binding.tvTableName.setText(table.getName());
            binding.tvTableStatus.setText(table.getStatus());

            // Tùy chỉnh màu sắc dựa trên trạng thái
            if (table.getStatus().equals("Occupied") || table.getStatus().equals("Serving")) {
                // Đang sử dụng -> Màu đỏ hoặc cam (Occupied)
                binding.tableStatusIndicator.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
                binding.tvTableStatus.setTextColor(Color.WHITE);
                binding.tvTableName.setTextColor(Color.WHITE);
            } else {
                // Trống (Available) -> Màu xanh lá cây hoặc trắng
                binding.tableStatusIndicator.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
                binding.tvTableStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
                binding.tvTableName.setTextColor(Color.BLACK);
            }
        }
    }
}