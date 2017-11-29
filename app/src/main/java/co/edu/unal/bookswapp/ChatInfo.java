package co.edu.unal.bookswapp;

/**
 * Created by vr on 11/29/17.
 */

public class ChatInfo {
    String user;
    String email;
    String userImageUri;

    public ChatInfo(String user, String email, String userImageUri) {
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
}
