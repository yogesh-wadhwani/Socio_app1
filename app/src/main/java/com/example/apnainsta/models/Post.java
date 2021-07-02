package com.example.apnainsta.models;

import android.graphics.Bitmap;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class Post {

   public  String text;
   public User createdBy;
    public Long createdAt;
    public  ArrayList<String> LikedBy = new ArrayList<>();
    public String  postImage ;

    public void like(String id){
        LikedBy.add(id);
    }
    public void unlike(String id){
        LikedBy.remove(id);
    }




    public Post(   String text,
                      User createdBy,
                      Long createdAt,
                      ArrayList<String> likedBy,
                     String postImage){
        this.createdBy=createdBy;
        this.createdAt = createdAt;
        this.text=text;
      this.LikedBy = likedBy;
      this.postImage=postImage;

    }
    public  Post(){}

}
