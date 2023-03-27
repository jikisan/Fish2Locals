package Models;

public class Notifications {

    private long transactionDateInMillis;
    private String transactionDate;
    private String transactionTime;
    private String notificationType;
    private String notificationMessage;
    private String notificationUserId;

    public Notifications() {
    }

    public Notifications(long transactionDateInMillis, String transactionDate,
                         String transactionTime, String notificationType,
                         String notificationMessage, String notificationUserId) {
        this.transactionDateInMillis = transactionDateInMillis;
        this.transactionDate = transactionDate;
        this.transactionTime = transactionTime;
        this.notificationType = notificationType;
        this.notificationMessage = notificationMessage;
        this.notificationUserId = notificationUserId;
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

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getNotificationUserId() {
        return notificationUserId;
    }

    public void setNotificationUserId(String notificationUserId) {
        this.notificationUserId = notificationUserId;
    }
}
