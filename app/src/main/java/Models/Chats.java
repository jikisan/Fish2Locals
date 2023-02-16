package Models;

public class Chats {

    String userIdOne;
    String userIdTwo;
    String storeID;
    String chatID;

    public Chats() {
    }

    public Chats(String userIdOne, String userIdTwo, String storeID, String chatID) {
        this.userIdOne = userIdOne;
        this.userIdTwo = userIdTwo;
        this.storeID = storeID;
        this.chatID = chatID;
    }

    public String getUserIdOne() {
        return userIdOne;
    }

    public void setUserIdOne(String userIdOne) {
        this.userIdOne = userIdOne;
    }

    public String getUserIdTwo() {
        return userIdTwo;
    }

    public void setUserIdTwo(String userIdTwo) {
        this.userIdTwo = userIdTwo;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }
}
