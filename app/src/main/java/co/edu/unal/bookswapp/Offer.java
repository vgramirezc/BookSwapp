package co.edu.unal.bookswapp;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vr on 11/25/17.
 */

//state values:
//0 -> Open
//1 -> Reserved
//2 -> Closed

public class Offer{
    private String id;
    private String ownerName;
    private String ownerId;
    private String title;
    private String author;
    private String description;
    private String photoUrl;
    private int state;
    private String userReservedId;
    private Object timestamp;

    public Offer(){}

    public Offer(String id, String ownerName, String ownerId, String title, String author, String description, String photoUrl, int state, String userReservedId, Object timestamp) {
        this.id = id;
        this.ownerName = ownerName;
        this.ownerId = ownerId;
        this.title = title;
        this.author = author;
        this.description = description;
        this.photoUrl = photoUrl;
        this.state = state;
        this.userReservedId = userReservedId;
        this.timestamp = timestamp;
    }



    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getUserReservedId() {
        return userReservedId;
    }

    public void setUserReservedId(String userReservedId) {
        this.userReservedId = userReservedId;
    }

    public Object getTimestamp(){ return timestamp; }

    public void setTimestamp(long timestamp){ this.timestamp = timestamp; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "id='" + id + '\'' +
                "ownerName='" + ownerName + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", photoUrl=" + photoUrl +
                ", state=" + state +
                ", userReservedId='" + userReservedId + '\'' +
                '}';
    }

}
