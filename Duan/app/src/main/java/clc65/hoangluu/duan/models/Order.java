package clc65.hoangluu.duan.models;

import java.util.List;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Order {
    private String id;
    private String type; // "Table" hoặc "TakeAway"
    private String targetId; // Tên khách hàng/Số bàn
    private String status; // "Pending", "Preparing", "Ready", "Completed", "Cancelled"
    private double totalAmount;
    private String staffId;
    private List<OrderItem> items; // Danh sách các món trong đơn

    @ServerTimestamp
    private Date timestamp;

    public Order() {}

    // Constructor, Getters, Setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}