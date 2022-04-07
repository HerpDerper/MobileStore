package com.example.mobilestore.Models;

public class Comment {
    private String text, productName, userName, timeComment;
    private int likeCount;
    private float rating;

    public Comment() {
    }

    public Comment(String text, String productName, String userName, String timeComment, int likeCount, float rating) {
        this.text = text;
        this.productName = productName;
        this.userName = userName;
        this.timeComment = timeComment;
        this.likeCount = likeCount;
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public String getProductName() {
        return productName;
    }

    public String getUserName() {
        return userName;
    }

    public String getTimeComment() {
        return timeComment;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public float getRating() {
        return rating;
    }
}