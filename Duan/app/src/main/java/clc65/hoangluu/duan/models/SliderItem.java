package clc65.hoangluu.duan.models;

public class SliderItem {
    private String imageUrl; // URL ảnh từ Firebase Storage/Internet

    public SliderItem(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}