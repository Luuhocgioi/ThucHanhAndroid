package clc65.hoangluu.duan.models;

public class OrderItem {
    private int sqLiteId; // ID này chỉ dùng cho SQLite, không liên quan đến Firebase
    private String productId;
    private String name;
    private int quantity;
    private double price;
    private String imageUrl;
    private String note;

    public OrderItem() {}

    public OrderItem(String productId, String name, int quantity, double price, String imageUrl, String note) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl = imageUrl;
        this.note = note;
    }

    // ==========================================================
    // KHẮC PHỤC LỖI: GETTER VÀ SETTER CHO sqLiteId
    // ==========================================================
    public int getSQLiteId() {
        return sqLiteId;
    }
    public void setSQLiteId(int sqLiteId) {
        this.sqLiteId = sqLiteId;
    }

    // Getters and Setters cho các trường khác
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}