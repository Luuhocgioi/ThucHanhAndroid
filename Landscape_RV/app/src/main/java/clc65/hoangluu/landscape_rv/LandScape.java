package clc65.hoangluu.landscape_rv;

public class LandScape {
    private String landScapeName;
    private String landScapeImage;

    public LandScape(String landScapeName, String landScapeImage) {
        this.landScapeName = landScapeName;
        this.landScapeImage = landScapeImage;
    }

    public String getLandScapeName() {
        return landScapeName;
    }

    public void setLandScapeName(String landScapeName) {
        this.landScapeName = landScapeName;
    }

    public String getLandScapeImage() {
        return landScapeImage;
    }

    public void setLandScapeImage(String landScapeImage) {
        this.landScapeImage = landScapeImage;
    }
}

