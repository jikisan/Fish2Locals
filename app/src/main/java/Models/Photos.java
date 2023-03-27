package Models;

public class Photos {

    private String productId;
    private String link;
    private String photoName;

    public Photos() {
    }

    public Photos(String productId, String link, String photoName) {
        this.productId = productId;
        this.link = link;
        this.photoName = photoName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }
}
