package Models;

public class TempStoreData {

    private String storeUrl;
    private String storeName;
    private double distance;
    private double ratings;
    private int ratingsCount;
    private String storeId;
    private String storeOwnersUserId;

    public TempStoreData() {
    }

    public TempStoreData(String storeUrl, String storeName, double distance, double ratings,
                         int ratingsCount, String storeId, String storeOwnersUserId) {
        this.storeUrl = storeUrl;
        this.storeName = storeName;
        this.distance = distance;
        this.ratings = ratings;
        this.ratingsCount = ratingsCount;
        this.storeId = storeId;
        this.storeOwnersUserId = storeOwnersUserId;
    }

    public String getStoreUrl() {
        return storeUrl;
    }

    public void setStoreUrl(String storeUrl) {
        this.storeUrl = storeUrl;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getRatings() {
        return ratings;
    }

    public void setRatings(double ratings) {
        this.ratings = ratings;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(int ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreOwnersUserId() {
        return storeOwnersUserId;
    }

    public void setStoreOwnersUserId(String storeOwnersUserId) {
        this.storeOwnersUserId = storeOwnersUserId;
    }
}
