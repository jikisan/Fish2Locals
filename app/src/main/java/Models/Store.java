package Models;

public class Store {

    private String storeUrl;
    private String storeImageName;
    private String storeName;
    private String storeAddress;
    private String storeLat;
    private String storeLang;
    private String storeContactNum;
    private String storeContactPerson;
    private String storeBizDocsUrl;
    private String storeBizDocsImageName;
    private String storeOwnersUserId;
    private long ratings;

    public Store() {
    }

    public Store(String storeUrl, String storeImageName, String storeName, String storeAddress,
                 String storeLat, String storeLang, String storeContactNum,
                 String storeContactPerson, String storeBizDocsUrl, String storeBizDocsImageName,
                 String storeOwnersUserId, long ratings) {
        this.storeUrl = storeUrl;
        this.storeImageName = storeImageName;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.storeLat = storeLat;
        this.storeLang = storeLang;
        this.storeContactNum = storeContactNum;
        this.storeContactPerson = storeContactPerson;
        this.storeBizDocsUrl = storeBizDocsUrl;
        this.storeBizDocsImageName = storeBizDocsImageName;
        this.storeOwnersUserId = storeOwnersUserId;
        this.ratings = ratings;
    }

    public String getStoreUrl() {
        return storeUrl;
    }

    public void setStoreUrl(String storeUrl) {
        this.storeUrl = storeUrl;
    }

    public String getStoreImageName() {
        return storeImageName;
    }

    public void setStoreImageName(String storeImageName) {
        this.storeImageName = storeImageName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
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

    public String getStoreContactNum() {
        return storeContactNum;
    }

    public void setStoreContactNum(String storeContactNum) {
        this.storeContactNum = storeContactNum;
    }

    public String getStoreContactPerson() {
        return storeContactPerson;
    }

    public void setStoreContactPerson(String storeContactPerson) {
        this.storeContactPerson = storeContactPerson;
    }

    public String getStoreBizDocsUrl() {
        return storeBizDocsUrl;
    }

    public void setStoreBizDocsUrl(String storeBizDocsUrl) {
        this.storeBizDocsUrl = storeBizDocsUrl;
    }

    public String getStoreBizDocsImageName() {
        return storeBizDocsImageName;
    }

    public void setStoreBizDocsImageName(String storeBizDocsImageName) {
        this.storeBizDocsImageName = storeBizDocsImageName;
    }

    public String getStoreOwnersUserId() {
        return storeOwnersUserId;
    }

    public void setStoreOwnersUserId(String storeOwnersUserId) {
        this.storeOwnersUserId = storeOwnersUserId;
    }

    public long getRatings() {
        return ratings;
    }

    public void setRatings(long ratings) {
        this.ratings = ratings;
    }
}
