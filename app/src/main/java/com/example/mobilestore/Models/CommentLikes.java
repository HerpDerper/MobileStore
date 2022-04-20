package com.example.mobilestore.Models;

public class CommentLikes {

    private String userName, commentName;

    public CommentLikes() {
    }

    public CommentLikes(String userName, String commentName) {
        this.userName = userName;
        this.commentName = commentName;
    }

    public String getUserName() {
        return userName;
    }

    public String getCommentName() {
        return commentName;
    }
}