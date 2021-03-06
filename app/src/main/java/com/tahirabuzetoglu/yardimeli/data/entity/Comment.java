package com.tahirabuzetoglu.yardimeli.data.entity;

import com.google.firebase.database.Exclude;

public class Comment {

    private String username;
    private String comment;
    private String userID;
    private String documentID;
    private double date;
    @Exclude
    private boolean success;

    public Comment(){}

    public Comment(String username, String comment, String userID, double date, String documentID) {
        this.username = username;
        this.comment = comment;
        this.userID = userID;
        this.date = date;
        this.documentID = documentID;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public double getDate() {
        return date;
    }

    public void setDate(double date) {
        this.date = date;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
