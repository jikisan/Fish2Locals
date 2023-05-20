package Models;

public class TempBestSellerData {

    private String imageName;
    private String fishName;
    private double pricePerKilo;
    private int quantityByKilo;
    private String storeName;
    private double distance;
    private double ratings;
    private String storeId;
    private String storeOwnersUserId;

    public TempBestSellerData() {
    }

    public TempBestSellerData(String imageName, String fishName, double pricePerKilo,
                              int quantityByKilo, String storeName, double distance,
                              double ratings, String storeId, String storeOwnersUserId) {
        this.imageName = imageName;
        this.fishName = fishName;
        this.pricePerKilo = pricePerKilo;
        this.quantityByKilo = quantityByKilo;
        this.storeName = storeName;
        this.distance = distance;
        this.ratings = ratings;
        this.storeId = storeId;
        this.storeOwnersUserId = storeOwnersUserId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getFishName() {
        return fishName;
    }

    public void setFishName(String fishName) {
        this.fishName = fishName;
    }

    public double getPricePerKilo() {
        return pricePerKilo;
    }

    public void setPricePerKilo(double pricePerKilo) {
        this.pricePerKilo = pricePerKilo;
    }

    public int getQuantityByKilo() {
        return quantityByKilo;
    }

    public void setQuantityByKilo(int quantityByKilo) {
        this.quantityByKilo = quantityByKilo;
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
