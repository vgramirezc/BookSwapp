package co.edu.unal.bookswapp;

/**
 * Created by vr on 11/29/17.
 */

public class ChatInfo {
    String user;
    String lastMessage;
    String userImageUri;

    public ChatInfo(){}

    public ChatInfo(String user, String lastMessage, String userImageUri) {
        this.user = user;
        this.lastMessage = lastMessage;
        this.userImageUri = userImageUri;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getUserImageUri() {
        return userImageUri;
    }

    public void setUserImageUri(String userImageUri) {
        this.userImageUri = userImageUri;
    }
}
