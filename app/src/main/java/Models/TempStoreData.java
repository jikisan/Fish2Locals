package Models;

public class TempStoreData {

    private String storeUrl;
    private String storeName;
    private double distance;
    private long ratings;
    private String storeId;
    private String storeOwnersUserId;

    public TempStoreData() {
    }

    public TempStoreData(String storeUrl, String storeName, double distance, long ratings,
                         String storeId, String storeOwnersUserId) {
        this.storeUrl = storeUrl;
        this.storeName = storeName;
        this.distance = distance;
        this.ratings = ratings;
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

    public long getRatings() {
        return ratings;
    }

    public void setRatings(long ratings) {
        this.ratings = ratings;
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
