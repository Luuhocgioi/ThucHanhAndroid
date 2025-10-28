package clc65.hoangluu.landscape_rv;

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
    ArrayList<LandScape> dsList;

    public LandScapeAdapter(Context context, ArrayList<LandScape> dsList) {
        this.context = context;
        this.dsList = dsList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.landscape_item, parent, false);
        return new ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        LandScape landScape = dsList.get(position);
        String tvCaption = landScape.getLandScapeName();
        String image = landScape.getLandScapeImage();
        holder.tvName.setText(tvCaption);
        String packageName = holder.itemView.getContext().getPackageName();
        int resId = holder.itemView.getContext().getResources().getIdentifier(image, "mipmap", packageName);
        holder.imgAnh1.setImageResource(resId);
    }

    @Override
    public int getItemCount() {
        return dsList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAnh1 ;
        TextView tvName;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAnh1 = itemView.findViewById(R.id.imgAnh1);
            tvName = itemView.findViewById(R.id.tvName);

        }
    }


}
