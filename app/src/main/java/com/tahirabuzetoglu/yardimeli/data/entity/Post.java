package com.tahirabuzetoglu.yardimeli.data.entity;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Post implements Serializable {

    private String id;
    private String ownerID;
    private String ownerName;
    private String imageUrl;
    private String ownerImageUrl;
    private String description;
    private List<String> likes;
    private Map<String, Map<String, List<String>>> Comments;
    private Double date;
    @Exclude
    private boolean success;
    @Exclude
    private boolean isUserLiked;
    @Exclude
    private boolean isUserSaved;
    @Exclude
    private int likeCount;
    @Exclude
    private int commentCount;

    public Post() {
    }

    public Post(String id, String ownerID, String ownerName, String imageUrl, String ownerImageUrl, String description, List<String> likes, Double date) {
        this.id = id;
        this.ownerID = ownerID;
        this.ownerName = ownerName;
        this.imageUrl = imageUrl;
        this.ownerImageUrl = ownerImageUrl;
        this.description = description;
        this.likes = likes;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOwnerImageUrl() {
        return ownerImageUrl;
    }

    public void setOwnerImageUrl(String ownerImageUrl) {
        this.ownerImageUrl = ownerImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public Map<String, Map<String, List<String>>> getComments() {
        return Comments;
    }

    public void setComments(Map<String, Map<String, List<String>>> comments) {
        Comments = comments;
    }

    public Double getDate() {
        return date;
    }

    public void setDate(Double date) {
        this.date = date;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isUserLiked() {
        return isUserLiked;
    }

    public void setUserLiked(boolean userLiked) {
        isUserLiked = userLiked;
    }

    public boolean isUserSaved() {
        return isUserSaved;
    }

    public void setUserSaved(boolean userSaved) {
        isUserSaved = userSaved;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
