package clc65.hoangluu.duancanhan_65131861.model;

/**
 * Lớp Model đại diện cho một Sản phẩm.
 * Được sử dụng để ánh xạ dữ liệu từ Firebase Realtime Database.
 */
public class Product {
    private String id;
    private String categoryId; // Khóa ngoại liên kết với Danh mục
    private String name;
    private long price; // Giá sản phẩm
    private String description;
    private String imageUrl;
    private boolean status; // Trạng thái (true: còn hàng, false: hết hàng)

    // Constructor mặc định không đối số (BẮT BUỘC cho Firebase)
    public Product() {
        // Cần có constructor rỗng
    }

    // Constructor đầy đủ
    public Product(String id, String categoryId, String name, long price, String description, String imageUrl, boolean status) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}