package Models;

public class Bookmark {


    private String storeUrl;
    private String storeName;
    private String storeLat;
    private String storeLang;
    private long ratings;
    private String userId;
    private String storeId;

    public Bookmark() {
    }

    public Bookmark(String storeUrl, String storeName, String storeLat, String storeLang,
                    long ratings, String userId, String storeId) {
        this.storeUrl = storeUrl;
        this.storeName = storeName;
        this.storeLat = storeLat;
        this.storeLang = storeLang;
        this.ratings = ratings;
        this.userId = userId;
        this.storeId = storeId;
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

    public String getStoreLat() {
        return storeLat;
    }

    public void setStoreLat(String storeLat) {
        this.storeLat = storeLat;
    }

    public String getStoreLang() {
        return storeLang;
    }

    public void setStoreLang(String storeLang) {
        this.storeLang = storeLang;
    }

    public long getRatings() {
        return ratings;
    }

    public void setRatings(long ratings) {
        this.ratings = ratings;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
