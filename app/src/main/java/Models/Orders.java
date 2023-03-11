package Models;

public class Orders {

    private String orderId;
    private String storeId;
    private String sellerUserId;
    private String buyerUserId;
    private String timeCreated;
    private String dateCreated;

    private String imageName;
    private String fishName;
    private double pricePerKilo;
    private boolean pickup;
    private boolean ownDelivery;
    private boolean thirdPartyDelivery;
    private int quantity;

    private String deliveryAddress;
    private String deliveryLat;
    private String deliveryLong;
    private String buyerContactNum;
    private double totalPrice;

    public Orders() {
    }

    public Orders(String orderId, String storeId, String sellerUserId, String buyerUserId,
                  String timeCreated, String dateCreated, String imageName, String fishName,
                  double pricePerKilo, boolean pickup, boolean ownDelivery,
                  boolean thirdPartyDelivery, int quantity, String deliveryAddress,
                  String deliveryLat, String deliveryLong, String buyerContactNum,
                  double totalPrice) {

        this.orderId = orderId;
        this.storeId = storeId;
        this.sellerUserId = sellerUserId;
        this.buyerUserId = buyerUserId;
        this.timeCreated = timeCreated;
        this.dateCreated = dateCreated;
        this.imageName = imageName;
        this.fishName = fishName;
        this.pricePerKilo = pricePerKilo;
        this.pickup = pickup;
        this.ownDelivery = ownDelivery;
        this.thirdPartyDelivery = thirdPartyDelivery;
        this.quantity = quantity;
        this.deliveryAddress = deliveryAddress;
        this.deliveryLat = deliveryLat;
        this.deliveryLong = deliveryLong;
        this.buyerContactNum = buyerContactNum;
        this.totalPrice = totalPrice;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryLat() {
        return deliveryLat;
    }

    public void setDeliveryLat(String deliveryLat) {
        this.deliveryLat = deliveryLat;
    }

    public String getDeliveryLong() {
        return deliveryLong;
    }

    public void setDeliveryLong(String deliveryLong) {
        this.deliveryLong = deliveryLong;
    }

    public String getBuyerContactNum() {
        return buyerContactNum;
    }

    public void setBuyerContactNum(String buyerContactNum) {
        this.buyerContactNum = buyerContactNum;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
