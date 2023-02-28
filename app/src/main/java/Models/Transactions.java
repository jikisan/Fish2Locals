package Models;

public class Transactions {

    long transactionDateInMillis;
    String transactionDate;
    String transactionTime;
    String transactionType;
    String transactionNote;
    double transactionAmount;
    String ownerID;

    public Transactions() {
    }

    public Transactions(long transactionDateInMillis, String transactionDate,
                        String transactionTime, String transactionType,
                        String transactionNote, double transactionAmount, String ownerID) {
        this.transactionDateInMillis = transactionDateInMillis;
        this.transactionDate = transactionDate;
        this.transactionTime = transactionTime;
        this.transactionType = transactionType;
        this.transactionNote = transactionNote;
        this.transactionAmount = transactionAmount;
        this.ownerID = ownerID;
    }

    public long getTransactionDateInMillis() {
        return transactionDateInMillis;
    }

    public void setTransactionDateInMillis(long transactionDateInMillis) {
        this.transactionDateInMillis = transactionDateInMillis;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionNote() {
        return transactionNote;
    }

    public void setTransactionNote(String transactionNote) {
        this.transactionNote = transactionNote;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }
}
