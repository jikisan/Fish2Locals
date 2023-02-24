package Models;

public class Products {

    private String imageName;
    private String fishName;
    private boolean hasPickup;
    private boolean hasOwnDelivery;
    private boolean has3rdPartyDelivery;
    private double pricePerKilo;
    private int quantityByKilo;
    private String storeId;
    private String userId;

    public Products() {
    }

    public Products(String imageName, String fishName, boolean hasPickup, boolean hasOwnDelivery,
                    boolean has3rdPartyDelivery, double pricePerKilo, int quantityByKilo,
                    String storeId, String userId) {
        this.imageName = imageName;
        this.fishName = fishName;
        this.hasPickup = hasPickup;
        this.hasOwnDelivery = hasOwnDelivery;
        this.has3rdPartyDelivery = has3rdPartyDelivery;
        this.pricePerKilo = pricePerKilo;
        this.quantityByKilo = quantityByKilo;
        this.storeId = storeId;
        this.userId = userId;
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

    public boolean isHasPickup() {
        return hasPickup;
    }

    public void setHasPickup(boolean hasPickup) {
        this.hasPickup = hasPickup;
    }

    public boolean isHasOwnDelivery() {
        return hasOwnDelivery;
    }

    public void setHasOwnDelivery(boolean hasOwnDelivery) {
        this.hasOwnDelivery = hasOwnDelivery;
    }

    public boolean isHas3rdPartyDelivery() {
        return has3rdPartyDelivery;
    }

    public void setHas3rdPartyDelivery(boolean has3rdPartyDelivery) {
        this.has3rdPartyDelivery = has3rdPartyDelivery;
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

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
