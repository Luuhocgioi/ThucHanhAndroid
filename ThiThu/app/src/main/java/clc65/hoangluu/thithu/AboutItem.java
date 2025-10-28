package clc65.hoangluu.thithu;

public class AboutItem {
    private String title;
    private String content;
    private Integer iconResId; // Dùng Integer để có thể là null

    public AboutItem(String title, String content, Integer iconResId) {
        this.title = title;
        this.content = content;
        this.iconResId = iconResId;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Integer getIconResId() {
        return iconResId;
    }
}
