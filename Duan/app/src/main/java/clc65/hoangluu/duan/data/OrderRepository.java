package clc65.hoangluu.duan.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clc65.hoangluu.duan.models.Order;
import clc65.hoangluu.duan.models.OrderItem;

public class OrderRepository {

    private static final String TAG = "OrderRepository";
    private FirebaseFirestore db;
    private CollectionReference ordersRef;
    private CollectionReference tablesRef;
    private FirebaseAuth mAuth;
    private Context context;

    public OrderRepository(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.ordersRef = db.collection("orders");
        this.tablesRef = db.collection("tables");
        this.mAuth = FirebaseAuth.getInstance();
    }

    // ==========================================================
    // LOGIC TẠO ĐƠN HÀNG MỚI (CHÍNH THỨC)
    // ==========================================================

    public void createNewOrder(Order order, LocalCartRepository localCartRepository, OnOrderCreationListener listener) {

        // 1. Chuẩn bị dữ liệu
        String staffId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "anonymous";
        order.setStaffId(staffId);

        // *** TỐI ƯU HÓA: MẶC ĐỊNH LÀ "PREPARING" (Đang Chuẩn bị) ***
        order.setStatus("Preparing");
        // ************************************************************

        DocumentReference newOrderRef = ordersRef.document(); // Tạo tham chiếu mới để lấy ID trước
        order.setId(newOrderRef.getId()); // Gán ID cho đối tượng Order

        WriteBatch batch = db.batch();

        // 2. Thêm đơn hàng vào collection 'orders'
        batch.set(newOrderRef, order);

        // 3. Nếu là đơn "Table", cập nhật trạng thái bàn (Sử dụng ID bàn từ TargetId)
        if ("Table".equals(order.getType())) {
            String tableId = order.getTargetId();
            DocumentReference tableDocRef = tablesRef.document(tableId);

            Map<String, Object> tableUpdates = new HashMap<>();
            tableUpdates.put("status", "Occupied"); // Đổi trạng thái bàn thành Đang sử dụng
            tableUpdates.put("currentOrderId", newOrderRef.getId()); // Lưu ID đơn hàng hiện tại

            // *** SỬ DỤNG batch.update() AN TOÀN HƠN SET ***
            batch.update(tableDocRef, tableUpdates);
        }

        // 4. Commit tất cả các thay đổi
        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Order created successfully. ID: " + newOrderRef.getId());
                    localCartRepository.clearCart(); // Xóa giỏ hàng local sau khi thành công
                    listener.onSuccess(newOrderRef.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating order", e);
                    listener.onFailure(e.getMessage());
                });
    }

    // ==========================================================
    // LOGIC CẬP NHẬT TRẠNG THÁI (CHUNG)
    // ==========================================================

    public void updateOrderStatus(String orderId, String newStatus, String orderType, String targetId) {
        ordersRef.document(orderId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Order status updated to: " + newStatus);

                    // *** LOGIC TỐI ƯU HÓA: XỬ LÝ TRẠNG THÁI BÀN KHI ORDER ĐƯỢC CẬP NHẬT ***
                    if ("Table".equals(orderType)) {
                        DocumentReference tableDocRef = tablesRef.document(targetId);
                        Map<String, Object> tableUpdates = new HashMap<>();

                        if ("Completed".equals(newStatus)) {
                            // Order hoàn thành -> Bàn trở về Available
                            tableUpdates.put("status", "Available");
                            tableUpdates.put("currentOrderId", null);
                        } else if ("Ready".equals(newStatus)) {
                            // Order sẵn sàng -> Bàn đang được phục vụ
                            tableUpdates.put("status", "Serving");
                        }

                        if (!tableUpdates.isEmpty()) {
                            db.runTransaction(transaction -> {
                                transaction.update(tableDocRef, tableUpdates);
                                return null;
                            }).addOnFailureListener(e -> Log.e(TAG, "Failed to update table status", e));
                        }
                    }
                    // *******************************************************************
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error updating status", e));
    }

    public interface OnOrderCreationListener {
        void onSuccess(String orderId);
        void onFailure(String errorMessage);
    }
}