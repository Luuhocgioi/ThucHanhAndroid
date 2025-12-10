package clc65.hoangluu.duan.models;

public class Product {
    private String id;
    private String name;
    private double price;
    private String imageUrl;
    private String description; // TRƯỜNG MỚI ĐÃ ĐƯỢC THÊM VÀO

    public Product() {}

    // Constructor đầy đủ
    public Product(String id, String name, double price, String imageUrl, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description; // Gán giá trị description
    }

    // Constructor cũ (giữ lại để tương thích)
    public Product(String id, String name, double price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = ""; // Mặc định là chuỗi rỗng
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // PHƯƠNG THỨC MỚI ĐÃ ĐƯỢC THÊM VÀO ĐỂ KHẮC PHỤC LỖI
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}