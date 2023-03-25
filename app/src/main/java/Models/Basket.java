package Models;

public class Basket {

    private String imageName;
    private String fishName;
    private double pricePerKilo;
    private boolean pickup;
    private boolean ownDelivery;
    private boolean thirdPartyDelivery;
    private int quantityByKilo;
    private String storeId;
    private String sellerUserId;
    private String buyerUserId;
    private String productId;

    public Basket() {
    }

    public Basket(String imageName, String fishName, double pricePerKilo, boolean pickup,
                  boolean ownDelivery, boolean thirdPartyDelivery, int quantityByKilo,
                  String storeId, String sellerUserId, String buyerUserId, String productId)
    {
        this.imageName = imageName;
        this.fishName = fishName;
        this.pricePerKilo = pricePerKilo;
        this.pickup = pickup;
        this.ownDelivery = ownDelivery;
        this.thirdPartyDelivery = thirdPartyDelivery;
        this.quantityByKilo = quantityByKilo;
        this.storeId = storeId;
        this.sellerUserId = sellerUserId;
        this.buyerUserId = buyerUserId;
        this.productId = productId;
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

    public boolean isPickup() {
        return pickup;
    }

    public void setPickup(boolean pickup) {
        this.pickup = pickup;
    }

    public boolean isOwnDelivery() {
        return ownDelivery;
    }

    public void setOwnDelivery(boolean ownDelivery) {
        this.ownDelivery = ownDelivery;
    }

    public boolean isThirdPartyDelivery() {
        return thirdPartyDelivery;
    }

    public void setThirdPartyDelivery(boolean thirdPartyDelivery) {
        this.thirdPartyDelivery = thirdPartyDelivery;
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

    public String getSellerUserId() {
        return sellerUserId;
    }

    public void setSellerUserId(String sellerUserId) {
        this.sellerUserId = sellerUserId;
    }

    public String getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(String buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
