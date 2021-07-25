package com.example.apnainsta.models;

public class Comment {
    public  String commentText;
    public  String createdBy;
    public  String imageUrl;
    public  String Uid;
    public  Long createdAt;



    public Comment(String commentText, String createdBy,String imageUrl ,String Uid,Long createdAt){
        this.commentText = commentText;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.imageUrl = imageUrl;
        this.Uid=Uid;

    }
 public Comment(){}



}
