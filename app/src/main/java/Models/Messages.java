package Models;

public class Messages {

    String senderUid;
    String receiverUid;
    String chatUid;
    String message;

    public Messages() {
    }

    public Messages(String senderUid, String receiverUid, String chatUid, String message) {
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.chatUid = chatUid;
        this.message = message;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getChatUid() {
        return chatUid;
    }

    public void setChatUid(String chatUid) {
        this.chatUid = chatUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
