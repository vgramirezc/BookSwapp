package co.edu.unal.bookswapp;

/**
 * Created by vr on 11/29/17.
 */

public class ChatInfo {
    String userId;
    String user;
    String email;
    String userImageUri;

    public ChatInfo(String userId, String user, String email, String userImageUri) {
        this.userId = userId;
        this.user = user;
        this.email = email;
        this.userImageUri = userImageUri;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserImageUri() {
        return userImageUri;
    }

    public void setUserImageUri(String userImageUri) {
        this.userImageUri = userImageUri;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
