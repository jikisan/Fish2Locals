package Models;

public class InTransitOrders {

    private String orderId;
    private String timeInTransit;
    private String buyerId;
    private String sellerId;

    public InTransitOrders() {
    }

    public InTransitOrders(String orderId, String timeInTransit, String buyerId, String sellerId) {
        this.orderId = orderId;
        this.timeInTransit = timeInTransit;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTimeInTransit() {
        return timeInTransit;
    }

    public void setTimeInTransit(String timeInTransit) {
        this.timeInTransit = timeInTransit;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
}
