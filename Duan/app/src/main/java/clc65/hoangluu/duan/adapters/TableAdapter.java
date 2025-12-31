package clc65.hoangluu.duan.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

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
    }

    @Override
    public int getItemCount() {
        return tableList != null ? tableList.size() : 0;
    }

    public class TableViewHolder extends RecyclerView.ViewHolder {
        private final ItemTableBinding binding;

        public TableViewHolder(ItemTableBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Table table) {
            // Hiển thị tên bàn (VD: Bàn 01)
            binding.tvTableName.setText(table.getName());

            // Xử lý màu sắc dựa trên trạng thái
            if ("Occupied".equalsIgnoreCase(table.getStatus()) || "Serving".equalsIgnoreCase(table.getStatus())) {

                // TRẠNG THÁI CÓ MÓN (Đang có khách)
                binding.cardTable.setCardBackgroundColor(ContextCompat.getColor(context, R.color.status_error)); // Màu đỏ
                binding.tvTableName.setTextColor(Color.BLACK);

                // Hiển thị văn bản "Đã có món"
                binding.tvTableStatus.setText("Đã có món");
                binding.tvTableStatus.setTextColor(Color.RED);

            } else {

                // TRẠNG THÁI CHƯA CÓ MÓN (Bàn trống)
                binding.cardTable.setCardBackgroundColor(Color.WHITE); // Màu trắng
                binding.tvTableName.setTextColor(Color.BLACK);

                // Hiển thị văn bản "Bàn trống"
                binding.tvTableStatus.setText("Bàn trống");
                binding.tvTableStatus.setTextColor(Color.GRAY);
            }

            // Sự kiện click
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTableClick(table);
                }
            });
        }
    }
}