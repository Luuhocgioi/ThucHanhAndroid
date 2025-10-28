package clc65.hoangluu.thithu;

public class Landscape {
    private String landScapeName;
    private String landScapeTime; // Thêm trường này
    private String landScapeImage;

    public Landscape(String landScapeName, String landScapeTime, String landScapeImage) {
        this.landScapeName = landScapeName;
        this.landScapeTime = landScapeTime; // Thêm vào constructor
        this.landScapeImage = landScapeImage;
    }

    public String getLandScapeName() {
        return landScapeName;
    }

    public void setLandScapeName(String landScapeName) {
        this.landScapeName = landScapeName;
    }

    public String getLandScapeTime() { // Thêm getter
        return landScapeTime;
    }

    public void setLandScapeTime(String landScapeTime) { // Thêm setter
        this.landScapeTime = landScapeTime;
    }

    public String getLandScapeImage() {
        return landScapeImage;
    }

    public void setLandScapeImage(String landScapeImage) {
        this.landScapeImage = landScapeImage;
    }
}