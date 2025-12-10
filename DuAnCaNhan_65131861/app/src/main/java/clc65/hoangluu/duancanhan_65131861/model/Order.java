package clc65.hoangluu.duancanhan_65131861.model;

import java.util.List;

public class Order {
    private String id;
    private String userId;
    private long orderTime;
    private double totalAmount;
    private String orderType;
    private String tableNumber;
    private String status;
    private List<CartItem> orderItems;

    public Order() {
    }

    public Order(String id, String userId, long orderTime, double totalAmount, String orderType, String tableNumber, String status, List<CartItem> orderItems) {
        this.id = id;
        this.userId = userId;
        this.orderTime = orderTime;
        this.totalAmount = totalAmount;
        this.orderType = orderType;
        this.tableNumber = tableNumber;
        this.status = status;
        this.orderItems = orderItems;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getOrderTime() { return orderTime; }
    public void setOrderTime(long orderTime) { this.orderTime = orderTime; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }

    public String getTableNumber() { return tableNumber; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<CartItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<CartItem> orderItems) { this.orderItems = orderItems; }
}
