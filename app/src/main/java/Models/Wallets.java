package Models;

public class Wallets {

    String userID;
    double fundAmount;

    public Wallets() {
    }

    public Wallets(String userID, double fundAmount) {
        this.userID = userID;
        this.fundAmount = fundAmount;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public double getFundAmount() {
        return fundAmount;
    }

    public void setFundAmount(double fundAmount) {
        this.fundAmount = fundAmount;
    }


}
