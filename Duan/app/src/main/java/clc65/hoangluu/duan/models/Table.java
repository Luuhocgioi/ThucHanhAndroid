package clc65.hoangluu.duan.models;

public class Table {
    private String id;
    private String name; // Ví dụ: "Bàn 01", "Bàn Kế bên cửa sổ"
    private String status; // Ví dụ: "Available", "Occupied", "Serving"
    private String currentOrderId; // ID đơn hàng nếu bàn đang sử dụng

    public Table() {}

    public Table(String id, String name, String status, String currentOrderId) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.currentOrderId = currentOrderId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCurrentOrderId() { return currentOrderId; }
    public void setCurrentOrderId(String currentOrderId) { this.currentOrderId = currentOrderId; }
}