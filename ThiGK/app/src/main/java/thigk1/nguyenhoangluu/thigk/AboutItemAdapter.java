package thigk1.nguyenhoangluu.thigk;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class AboutItemAdapter extends RecyclerView.Adapter<AboutItemAdapter.AboutItemViewHolder> {

    private List<AboutItem> items;

    public AboutItemAdapter(List<AboutItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public AboutItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_about_info, parent, false);
        return new AboutItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AboutItemViewHolder holder, int position) {
        AboutItem item = items.get(position);
        holder.titleView.setText(item.getTitle());
        holder.contentView.setText(item.getContent());

        if (item.getIconResId() != null) {
            holder.iconView.setImageResource(item.getIconResId());
            holder.iconView.setVisibility(View.VISIBLE);
        } else {
            holder.iconView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Lớp ViewHolder
    public static class AboutItemViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView iconView;
        TextView titleView;
        TextView contentView;

        public AboutItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewItem);
            iconView = itemView.findViewById(R.id.imageViewIcon);
            titleView = itemView.findViewById(R.id.textViewTitle);
            contentView = itemView.findViewById(R.id.textViewContent);
        }
    }
}
