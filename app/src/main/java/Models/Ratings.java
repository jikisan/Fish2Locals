package Models;

public class Ratings {

    String ratingOfId;
    String ratedById;
    double ratingValue;
    String ratingMessage;
    String orderId;

    public Ratings() {
    }

    public Ratings(String ratingOfId, String ratedById, double ratingValue, String ratingMessage,
                   String orderId) {
        this.ratingOfId = ratingOfId;
        this.ratedById = ratedById;
        this.ratingValue = ratingValue;
        this.ratingMessage = ratingMessage;
        this.orderId = orderId;
    }

    public String getRatingOfId() {
        return ratingOfId;
    }

    public void setRatingOfId(String ratingOfId) {
        this.ratingOfId = ratingOfId;
    }

    public String getRatedById() {
        return ratedById;
    }

    public void setRatedById(String ratedById) {
        this.ratedById = ratedById;
    }

    public double getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(double ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getRatingMessage() {
        return ratingMessage;
    }

    public void setRatingMessage(String ratingMessage) {
        this.ratingMessage = ratingMessage;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
