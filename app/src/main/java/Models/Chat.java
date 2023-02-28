package Models;

public class Chat {

    String userIdOne;
    String userIdTwo;
    String chatID;

    public Chat() {
    }

    public Chat(String userIdOne, String userIdTwo, String chatID) {
        this.userIdOne = userIdOne;
        this.userIdTwo = userIdTwo;
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

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

}
