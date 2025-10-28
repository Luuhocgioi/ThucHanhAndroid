package clc65.hoangluu.thithu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LandScapeAdapter extends RecyclerView.Adapter<LandScapeAdapter.ItemViewHolder> {
    Context context;
    ArrayList<Landscape> dsList;

    public LandScapeAdapter(Context context, ArrayList<Landscape> dsList) {
        this.context = context;
        this.dsList = dsList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        // Sử dụng layout landscape_item.xml
        View view = inflater.inflate(R.layout.landscape_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Landscape landScape = dsList.get(position);

        // Lấy dữ liệu từ model
        String tvCaption = landScape.getLandScapeName();
        String tvTime = landScape.getLandScapeTime(); // Lấy dữ liệu thời gian
        String image = landScape.getLandScapeImage();

        // Gán dữ liệu vào View
        holder.textViewTitle.setText(tvCaption);
        holder.textViewTime.setText(tvTime); // Gán thời gian

        // Lấy và gán ảnh
        String packageName = holder.itemView.getContext().getPackageName();
        int resId = holder.itemView.getContext().getResources().getIdentifier(image, "mipmap", packageName);
        holder.imgView.setImageResource(resId);
    }

    @Override
    public int getItemCount() {
        return dsList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView;
        TextView textViewTitle, textViewTime; // Đổi tên biến cho khớp với ID mới

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ View theo ID mới
            imgView = itemView.findViewById(R.id.imgView);
            textViewTitle = itemView.findViewById(R.id.tvTitle);
            textViewTime = itemView.findViewById(R.id.tvTime);
        }
    }
}