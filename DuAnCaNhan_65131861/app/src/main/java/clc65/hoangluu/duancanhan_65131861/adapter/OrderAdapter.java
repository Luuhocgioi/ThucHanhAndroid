package clc65.hoangluu.duancanhan_65131861.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import clc65.hoangluu.duancanhan_65131861.R;
import clc65.hoangluu.duancanhan_65131861.model.Order;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final Context context;
    private List<Order> orderList;
    private final boolean isAdminView; // Xác định xem có phải giao diện Admin/Staff không

    public OrderAdapter(Context context, List<Order> orderList, boolean isAdminView) {
        this.context = context;
        this.orderList = orderList;
        this.isAdminView = isAdminView;
    }

    public void updateList(List<Order> newList) {
        this.orderList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());

        // SỬA LỖI: Kiểm tra độ dài ID trước khi substring để tránh lỗi StringIndexOutOfBoundsException
        String orderIdDisplay = order.getId();
        if (orderIdDisplay != null && orderIdDisplay.length() > 8) {
            orderIdDisplay = orderIdDisplay.substring(0, 8);
        }
        holder.tvOrderId.setText("Mã Đơn: " + orderIdDisplay);
        
        holder.tvOrderType.setText("Loại: " + order.getOrderType());
        holder.tvOrderTime.setText(dateFormat.format(order.getOrderTime()));
        holder.tvOrderTotal.setText(currencyFormat.format(order.getTotalAmount()));
        holder.tvOrderStatus.setText(order.getStatus());

        if ("Hoàn thành".equals(order.getStatus())) {
            holder.btnUpdateStatus.setVisibility(View.GONE);
        } else {
            holder.btnUpdateStatus.setVisibility(isAdminView ? View.VISIBLE : View.GONE);
        }

        holder.btnUpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic cập nhật trạng thái đơn hàng (Mục tiêu 2.1.4)
                if (isAdminView) {
                    updateOrderStatus(order.getId(), "Hoàn thành");
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Chuyển sang OrderDetailActivity (để xem chi tiết CartItem)
            }
        });
    }

    private void updateOrderStatus(String orderId, String newStatus) {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders").child(orderId);
        orderRef.child("status").setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Cập nhật trạng thái thành công!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderType, tvOrderTime, tvOrderTotal, tvOrderStatus;
        Button btnUpdateStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

             tvOrderId = itemView.findViewById(R.id.tvOrderId);
             tvOrderType = itemView.findViewById(R.id.tvOrderType);
             tvOrderTime = itemView.findViewById(R.id.tvOrderTime);
             tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
             tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
             btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
        }
    }
}